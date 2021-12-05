package nodomain.freeyourgadget.gadgetbridge.service.devices.hplus;

import android.content.Context;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusConstants;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusHealthSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivityOverlay;
import nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivityOverlayDao;
import nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivitySample;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.hplus.HPlusDataRecord;
import nodomain.freeyourgadget.gadgetbridge.service.serial.GBDeviceIoThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HPlusHandlerThread extends GBDeviceIoThread {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) HPlusHandlerThread.class);
    private int CURRENT_DAY_SYNC_PERIOD = 31536000;
    private int CURRENT_DAY_SYNC_RETRY_PERIOD = 10;
    private int DAY_SUMMARY_SYNC_PERIOD = 86400;
    private int DAY_SUMMARY_SYNC_RETRY_PERIOD = 30;
    private int HELLO_PERIOD = 120;
    private int SLEEP_SYNC_PERIOD = 43200;
    private int SLEEP_SYNC_RETRY_PERIOD = 30;
    private HPlusDataRecordDaySlot mCurrentDaySlot = null;
    List<HPlusDataRecordDaySlot> mDaySlotRecords = new ArrayList();
    private Calendar mGetDaySlotsTime = GregorianCalendar.getInstance();
    private Calendar mGetDaySummaryTime = GregorianCalendar.getInstance();
    private Calendar mGetSleepTime = GregorianCalendar.getInstance();
    private HPlusSupport mHPlusSupport;
    private Calendar mHelloTime = GregorianCalendar.getInstance();
    private Calendar mLastSleepDayReceived = GregorianCalendar.getInstance();
    private int mLastSlotReceived = -1;
    private int mLastSlotRequested = 0;
    private boolean mQuit = false;
    private boolean mSlotsInitialSync = true;
    private HPlusDataRecordRealtime prevRealTimeRecord = null;
    private final Object waitObject = new Object();

    public HPlusHandlerThread(GBDevice gbDevice, Context context, HPlusSupport hplusSupport) {
        super(gbDevice, context);
        LOG.info("Initializing HPlus Handler Thread");
        this.mQuit = false;
        this.mHPlusSupport = hplusSupport;
    }

    public void run() {
        this.mQuit = false;
        sync();
        long waitTime = 0;
        while (!this.mQuit) {
            if (waitTime > 0) {
                synchronized (this.waitObject) {
                    try {
                        this.waitObject.wait(waitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!this.mQuit) {
                if (this.gbDevice.getState() == GBDevice.State.NOT_CONNECTED) {
                    quit();
                }
                Calendar now = GregorianCalendar.getInstance();
                if (now.compareTo(this.mGetDaySlotsTime) > 0) {
                    requestNextDaySlots();
                }
                if (now.compareTo(this.mGetSleepTime) > 0) {
                    requestNextSleepData();
                }
                if (now.compareTo(this.mGetDaySummaryTime) > 0) {
                    requestDaySummaryData();
                }
                if (now.compareTo(this.mHelloTime) > 0) {
                    sendHello();
                }
                waitTime = Math.min(this.mGetDaySummaryTime.getTimeInMillis(), Math.min(this.mGetDaySlotsTime.getTimeInMillis(), Math.min(this.mHelloTime.getTimeInMillis(), this.mGetSleepTime.getTimeInMillis()))) - GregorianCalendar.getInstance().getTimeInMillis();
            } else {
                return;
            }
        }
    }

    public void quit() {
        LOG.info("HPlus: Quit Handler Thread");
        this.mQuit = true;
        synchronized (this.waitObject) {
            this.waitObject.notify();
        }
    }

    public void sync() {
        LOG.info("HPlus: Starting data synchronization");
        this.mGetSleepTime.setTimeInMillis(0);
        this.mGetDaySlotsTime.setTimeInMillis(0);
        this.mGetDaySummaryTime.setTimeInMillis(0);
        this.mLastSleepDayReceived.setTimeInMillis(0);
        this.mHelloTime = GregorianCalendar.getInstance();
        this.mHelloTime.add(13, this.HELLO_PERIOD);
        this.mSlotsInitialSync = true;
        this.mLastSlotReceived = -1;
        this.mLastSlotRequested = 0;
        this.mCurrentDaySlot = null;
        this.mDaySlotRecords.clear();
        try {
            if (!this.mHPlusSupport.isConnected()) {
                this.mHPlusSupport.connect();
            }
            TransactionBuilder builder = new TransactionBuilder("startSyncDayStats");
            builder.write(this.mHPlusSupport.ctrlCharacteristic, new byte[]{36});
            builder.write(this.mHPlusSupport.ctrlCharacteristic, new byte[]{23});
            builder.write(this.mHPlusSupport.ctrlCharacteristic, new byte[]{22});
            this.mHPlusSupport.performConnected(builder.getTransaction());
        } catch (Exception e) {
            Logger logger = LOG;
            logger.warn("HPlus: Synchronization exception: " + e);
        }
        synchronized (this.waitObject) {
            this.waitObject.notify();
        }
    }

    public void sendHello() {
        try {
            TransactionBuilder builder = new TransactionBuilder("hello");
            builder.write(this.mHPlusSupport.ctrlCharacteristic, HPlusConstants.CMD_ACTION_HELLO);
            this.mHPlusSupport.performConnected(builder.getTransaction());
        } catch (Exception e) {
        }
        this.mHelloTime = GregorianCalendar.getInstance();
        this.mHelloTime.add(13, this.HELLO_PERIOD);
        synchronized (this.waitObject) {
            this.waitObject.notify();
        }
    }

    public boolean processIncomingDaySlotData(byte[] data, int age) {
        Throwable th;
        try {
            try {
                HPlusDataRecordDaySlot record = new HPlusDataRecordDaySlot(data, age);
                Calendar now = GregorianCalendar.getInstance();
                byte b = 10;
                int nowSlot = (now.get(12) / 10) + (now.get(11) * 6);
                if (record.slot == nowSlot) {
                    HPlusDataRecordDaySlot hPlusDataRecordDaySlot = this.mCurrentDaySlot;
                    if (hPlusDataRecordDaySlot == null || hPlusDataRecordDaySlot == record) {
                        this.mCurrentDaySlot = record;
                        if (!this.mSlotsInitialSync) {
                            return true;
                        }
                    } else {
                        hPlusDataRecordDaySlot.accumulate(record);
                        this.mDaySlotRecords.add(this.mCurrentDaySlot);
                        this.mCurrentDaySlot = null;
                    }
                }
                if (this.mSlotsInitialSync) {
                    if (record.slot > nowSlot) {
                        record.timestamp -= 86400;
                    }
                    if (record.slot == this.mLastSlotReceived + 1) {
                        this.mLastSlotReceived = record.slot;
                    }
                    if (record.slot != nowSlot) {
                        this.mDaySlotRecords.add(record);
                    }
                    if (record.slot == this.mLastSlotRequested) {
                        this.mGetDaySlotsTime.clear();
                        synchronized (this.waitObject) {
                            this.waitObject.notify();
                        }
                    }
                    if (record.slot != 143) {
                        return true;
                    }
                } else {
                    this.mGetDaySlotsTime = GregorianCalendar.getInstance();
                    this.mGetDaySlotsTime.add(5, 1);
                }
                if (this.mDaySlotRecords.size() <= 0) {
                    return true;
                }
                Collections.sort(this.mDaySlotRecords, new Comparator<HPlusDataRecordDaySlot>() {
                    public int compare(HPlusDataRecordDaySlot one, HPlusDataRecordDaySlot other) {
                        return one.timestamp - other.timestamp;
                    }
                });
                List<Integer> notWornSlots = new ArrayList<>();
                try {
                    DBHandler dbHandler = GBApplication.acquireDB();
                    try {
                        HPlusHealthSampleProvider provider = new HPlusHealthSampleProvider(getDevice(), dbHandler.getDaoSession());
                        List<HPlusHealthActivitySample> samples = new ArrayList<>();
                        for (HPlusDataRecordDaySlot storedRecord : this.mDaySlotRecords) {
                            if (storedRecord.isValid()) {
                                HPlusHealthActivitySample sample = createSample(dbHandler, storedRecord.timestamp);
                                sample.setRawHPlusHealthData(storedRecord.getRawData());
                                sample.setSteps(storedRecord.steps);
                                sample.setRawIntensity(storedRecord.intensity);
                                sample.setHeartRate(storedRecord.heartRate);
                                sample.setRawKind(storedRecord.type);
                                sample.setProvider(provider);
                                samples.add(sample);
                                if (HPlusCoordinator.getAllDayHR(this.gbDevice.getAddress()) == b && storedRecord.heartRate == -1 && storedRecord.steps <= 0) {
                                    notWornSlots.add(Integer.valueOf(sample.getTimestamp()));
                                    notWornSlots.add(Integer.valueOf(sample.getTimestamp() + 600));
                                }
                                b = 10;
                            }
                        }
                        provider.getSampleDao().insertOrReplaceInTx(samples);
                        this.mDaySlotRecords.clear();
                        if (notWornSlots.size() > 0) {
                            DaoSession session = dbHandler.getDaoSession();
                            Long userId = DBHelper.getUser(session).getId();
                            Long deviceId = DBHelper.getDevice(getDevice(), session).getId();
                            HPlusHealthActivityOverlayDao overlayDao = session.getHPlusHealthActivityOverlayDao();
                            List<HPlusHealthActivityOverlay> overlayList = new ArrayList<>();
                            int firstSlotTimestamp = notWornSlots.get(0).intValue();
                            int lastSlotTimestamp = notWornSlots.get(0).intValue();
                            int i = firstSlotTimestamp;
                            HPlusHealthSampleProvider hPlusHealthSampleProvider = provider;
                            int lastSlotTimestamp2 = lastSlotTimestamp;
                            int firstSlotTimestamp2 = i;
                            for (Integer timestamp : notWornSlots) {
                                DaoSession session2 = session;
                                if (timestamp.intValue() - lastSlotTimestamp2 > 600) {
                                    overlayList.add(new HPlusHealthActivityOverlay(firstSlotTimestamp2, lastSlotTimestamp2, 8, deviceId.longValue(), userId.longValue(), (byte[]) null));
                                    firstSlotTimestamp2 = timestamp.intValue();
                                }
                                lastSlotTimestamp2 = timestamp.intValue();
                                session = session2;
                            }
                            if (firstSlotTimestamp2 != lastSlotTimestamp2) {
                                overlayList.add(new HPlusHealthActivityOverlay(firstSlotTimestamp2, lastSlotTimestamp2, 8, deviceId.longValue(), userId.longValue(), (byte[]) null));
                            }
                            overlayDao.insertOrReplaceInTx(overlayList);
                        }
                        if (dbHandler == null) {
                            return true;
                        }
                        dbHandler.close();
                        return true;
                    } catch (Throwable th2) {
                        Throwable th3 = th2;
                        if (dbHandler != null) {
                            dbHandler.close();
                        }
                        throw th3;
                    }
                } catch (GBException ex) {
                    LOG.info(ex.getMessage());
                    return true;
                } catch (Exception ex2) {
                    LOG.info(ex2.getMessage());
                    return true;
                } catch (Throwable th4) {
                    th.addSuppressed(th4);
                }
            } catch (IllegalArgumentException e) {
                e = e;
                LOG.info(e.getMessage());
                return false;
            }
        } catch (IllegalArgumentException e2) {
            e = e2;
            byte[] bArr = data;
            int i2 = age;
            LOG.info(e.getMessage());
            return false;
        }
    }

    public boolean processIncomingSleepData(byte[] data) {
        Throwable th;
        try {
            HPlusDataRecordSleep record = new HPlusDataRecordSleep(data);
            this.mLastSleepDayReceived.setTimeInMillis(((long) record.bedTimeStart) * 1000);
            try {
                DBHandler dbHandler = GBApplication.acquireDB();
                try {
                    DaoSession session = dbHandler.getDaoSession();
                    Long userId = DBHelper.getUser(session).getId();
                    Long deviceId = DBHelper.getDevice(getDevice(), session).getId();
                    HPlusHealthActivityOverlayDao overlayDao = session.getHPlusHealthActivityOverlayDao();
                    HPlusHealthSampleProvider provider = new HPlusHealthSampleProvider(getDevice(), dbHandler.getDaoSession());
                    List<HPlusHealthActivityOverlay> overlayList = new ArrayList<>();
                    for (HPlusDataRecord.RecordInterval interval : record.getIntervals()) {
                        int i = interval.timestampFrom;
                        int i2 = interval.timestampTo;
                        DaoSession session2 = session;
                        HPlusHealthActivityOverlay hPlusHealthActivityOverlay = r13;
                        HPlusHealthActivityOverlay hPlusHealthActivityOverlay2 = new HPlusHealthActivityOverlay(i, i2, interval.activityKind, deviceId.longValue(), userId.longValue(), (byte[]) null);
                        overlayList.add(hPlusHealthActivityOverlay);
                        byte[] bArr = data;
                        session = session2;
                    }
                    overlayDao.insertOrReplaceInTx(overlayList);
                    HPlusHealthActivitySample sample = createSample(dbHandler, record.timestamp);
                    sample.setRawHPlusHealthData(record.getRawData());
                    sample.setRawKind(record.activityKind);
                    sample.setProvider(provider);
                    provider.addGBActivitySample(sample);
                    if (dbHandler != null) {
                        dbHandler.close();
                    }
                    this.mGetSleepTime = GregorianCalendar.getInstance();
                    this.mGetSleepTime.add(13, this.SLEEP_SYNC_PERIOD);
                    return true;
                } catch (Throwable th2) {
                    Throwable th3 = th2;
                    if (dbHandler != null) {
                        dbHandler.close();
                    }
                    throw th3;
                }
            } catch (Exception ex) {
                LOG.info(ex.getMessage());
            } catch (Throwable th4) {
                th.addSuppressed(th4);
            }
        } catch (IllegalArgumentException e) {
            LOG.info(e.getMessage());
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x009f, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x00a0, code lost:
        if (r1 != null) goto L_0x00a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00aa, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processRealtimeStats(byte[] r10, int r11) {
        /*
            r9 = this;
            nodomain.freeyourgadget.gadgetbridge.service.devices.hplus.HPlusDataRecordRealtime r0 = new nodomain.freeyourgadget.gadgetbridge.service.devices.hplus.HPlusDataRecordRealtime     // Catch:{ IllegalArgumentException -> 0x00c2 }
            r0.<init>(r10, r11)     // Catch:{ IllegalArgumentException -> 0x00c2 }
            nodomain.freeyourgadget.gadgetbridge.service.devices.hplus.HPlusDataRecordRealtime r1 = r9.prevRealTimeRecord
            r2 = 1
            if (r1 == 0) goto L_0x0012
            boolean r1 = r0.same(r1)
            if (r1 == 0) goto L_0x0012
            return r2
        L_0x0012:
            r9.prevRealTimeRecord = r0
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r1 = r9.getDevice()
            byte r3 = r0.battery
            short r3 = (short) r3
            r1.setBatteryLevel(r3)
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r1 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ GBException -> 0x00b6, Exception -> 0x00ab }
            nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusHealthSampleProvider r3 = new nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusHealthSampleProvider     // Catch:{ all -> 0x009d }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r9.getDevice()     // Catch:{ all -> 0x009d }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r5 = r1.getDaoSession()     // Catch:{ all -> 0x009d }
            r3.<init>(r4, r5)     // Catch:{ all -> 0x009d }
            int r4 = r0.timestamp     // Catch:{ all -> 0x009d }
            nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivitySample r4 = r9.createSample(r1, r4)     // Catch:{ all -> 0x009d }
            int r5 = r0.type     // Catch:{ all -> 0x009d }
            r4.setRawKind(r5)     // Catch:{ all -> 0x009d }
            int r5 = r0.intensity     // Catch:{ all -> 0x009d }
            r4.setRawIntensity(r5)     // Catch:{ all -> 0x009d }
            int r5 = r0.heartRate     // Catch:{ all -> 0x009d }
            r4.setHeartRate(r5)     // Catch:{ all -> 0x009d }
            int r5 = r0.distance     // Catch:{ all -> 0x009d }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x009d }
            r4.setDistance(r5)     // Catch:{ all -> 0x009d }
            int r5 = r0.calories     // Catch:{ all -> 0x009d }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x009d }
            r4.setCalories(r5)     // Catch:{ all -> 0x009d }
            int r5 = r0.steps     // Catch:{ all -> 0x009d }
            r4.setSteps(r5)     // Catch:{ all -> 0x009d }
            byte[] r5 = r0.getRawData()     // Catch:{ all -> 0x009d }
            r4.setRawHPlusHealthData(r5)     // Catch:{ all -> 0x009d }
            r4.setProvider(r3)     // Catch:{ all -> 0x009d }
            r3.addGBActivitySample(r4)     // Catch:{ all -> 0x009d }
            int r5 = r4.getSteps()     // Catch:{ all -> 0x009d }
            nodomain.freeyourgadget.gadgetbridge.service.devices.hplus.HPlusDataRecordRealtime r6 = r9.prevRealTimeRecord     // Catch:{ all -> 0x009d }
            int r6 = r6.steps     // Catch:{ all -> 0x009d }
            int r5 = r5 - r6
            r4.setSteps(r5)     // Catch:{ all -> 0x009d }
            android.content.Intent r5 = new android.content.Intent     // Catch:{ all -> 0x009d }
            java.lang.String r6 = "nodomain.freeyourgadget.gadgetbridge.devices.action.realtime_samples"
            r5.<init>(r6)     // Catch:{ all -> 0x009d }
            java.lang.String r6 = "realtime_sample"
            android.content.Intent r5 = r5.putExtra(r6, r4)     // Catch:{ all -> 0x009d }
            java.lang.String r6 = "timestamp"
            long r7 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x009d }
            android.content.Intent r5 = r5.putExtra(r6, r7)     // Catch:{ all -> 0x009d }
            android.content.Context r6 = r9.getContext()     // Catch:{ all -> 0x009d }
            androidx.localbroadcastmanager.content.LocalBroadcastManager r6 = androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(r6)     // Catch:{ all -> 0x009d }
            r6.sendBroadcast(r5)     // Catch:{ all -> 0x009d }
            if (r1 == 0) goto L_0x00c0
            r1.close()     // Catch:{ GBException -> 0x00b6, Exception -> 0x00ab }
            goto L_0x00c0
        L_0x009d:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x009f }
        L_0x009f:
            r4 = move-exception
            if (r1 == 0) goto L_0x00aa
            r1.close()     // Catch:{ all -> 0x00a6 }
            goto L_0x00aa
        L_0x00a6:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ GBException -> 0x00b6, Exception -> 0x00ab }
        L_0x00aa:
            throw r4     // Catch:{ GBException -> 0x00b6, Exception -> 0x00ab }
        L_0x00ab:
            r1 = move-exception
            org.slf4j.Logger r3 = LOG
            java.lang.String r4 = r1.getMessage()
            r3.info(r4)
            goto L_0x00c1
        L_0x00b6:
            r1 = move-exception
            org.slf4j.Logger r3 = LOG
            java.lang.String r4 = r1.getMessage()
            r3.info(r4)
        L_0x00c0:
        L_0x00c1:
            return r2
        L_0x00c2:
            r0 = move-exception
            org.slf4j.Logger r1 = LOG
            java.lang.String r2 = r0.getMessage()
            r1.info(r2)
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.hplus.HPlusHandlerThread.processRealtimeStats(byte[], int):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0061, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0062, code lost:
        if (r1 != null) goto L_0x0064;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x006c, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processDaySummary(byte[] r7) {
        /*
            r6 = this;
            nodomain.freeyourgadget.gadgetbridge.service.devices.hplus.HPlusDataRecordDaySummary r0 = new nodomain.freeyourgadget.gadgetbridge.service.devices.hplus.HPlusDataRecordDaySummary     // Catch:{ IllegalArgumentException -> 0x0094 }
            r0.<init>(r7)     // Catch:{ IllegalArgumentException -> 0x0094 }
            nodomain.freeyourgadget.gadgetbridge.database.DBHandler r1 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ GBException -> 0x0078, Exception -> 0x006d }
            nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusHealthSampleProvider r2 = new nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusHealthSampleProvider     // Catch:{ all -> 0x005f }
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r3 = r6.getDevice()     // Catch:{ all -> 0x005f }
            nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r4 = r1.getDaoSession()     // Catch:{ all -> 0x005f }
            r2.<init>(r3, r4)     // Catch:{ all -> 0x005f }
            int r3 = r0.timestamp     // Catch:{ all -> 0x005f }
            nodomain.freeyourgadget.gadgetbridge.entities.HPlusHealthActivitySample r3 = r6.createSample(r1, r3)     // Catch:{ all -> 0x005f }
            int r4 = r0.type     // Catch:{ all -> 0x005f }
            r3.setRawKind(r4)     // Catch:{ all -> 0x005f }
            int r4 = r0.steps     // Catch:{ all -> 0x005f }
            r3.setSteps(r4)     // Catch:{ all -> 0x005f }
            int r4 = r0.distance     // Catch:{ all -> 0x005f }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x005f }
            r3.setDistance(r4)     // Catch:{ all -> 0x005f }
            int r4 = r0.calories     // Catch:{ all -> 0x005f }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x005f }
            r3.setCalories(r4)     // Catch:{ all -> 0x005f }
            int r4 = r0.distance     // Catch:{ all -> 0x005f }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x005f }
            r3.setDistance(r4)     // Catch:{ all -> 0x005f }
            int r4 = r0.maxHeartRate     // Catch:{ all -> 0x005f }
            int r5 = r0.minHeartRate     // Catch:{ all -> 0x005f }
            int r4 = r4 - r5
            int r4 = r4 / 2
            r3.setHeartRate(r4)     // Catch:{ all -> 0x005f }
            byte[] r4 = r0.getRawData()     // Catch:{ all -> 0x005f }
            r3.setRawHPlusHealthData(r4)     // Catch:{ all -> 0x005f }
            r3.setProvider(r2)     // Catch:{ all -> 0x005f }
            r2.addGBActivitySample(r3)     // Catch:{ all -> 0x005f }
            if (r1 == 0) goto L_0x0082
            r1.close()     // Catch:{ GBException -> 0x0078, Exception -> 0x006d }
            goto L_0x0082
        L_0x005f:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0061 }
        L_0x0061:
            r3 = move-exception
            if (r1 == 0) goto L_0x006c
            r1.close()     // Catch:{ all -> 0x0068 }
            goto L_0x006c
        L_0x0068:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ GBException -> 0x0078, Exception -> 0x006d }
        L_0x006c:
            throw r3     // Catch:{ GBException -> 0x0078, Exception -> 0x006d }
        L_0x006d:
            r1 = move-exception
            org.slf4j.Logger r2 = LOG
            java.lang.String r3 = r1.getMessage()
            r2.info(r3)
            goto L_0x0083
        L_0x0078:
            r1 = move-exception
            org.slf4j.Logger r2 = LOG
            java.lang.String r3 = r1.getMessage()
            r2.info(r3)
        L_0x0082:
        L_0x0083:
            java.util.Calendar r1 = java.util.GregorianCalendar.getInstance()
            r6.mGetDaySummaryTime = r1
            java.util.Calendar r1 = r6.mGetDaySummaryTime
            r2 = 13
            int r3 = r6.DAY_SUMMARY_SYNC_PERIOD
            r1.add(r2, r3)
            r1 = 1
            return r1
        L_0x0094:
            r0 = move-exception
            org.slf4j.Logger r1 = LOG
            java.lang.String r2 = r0.getMessage()
            r1.info(r2)
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.hplus.HPlusHandlerThread.processDaySummary(byte[]):boolean");
    }

    public boolean processVersion(byte[] data) {
        int minor;
        int major;
        if (data.length >= 11) {
            major = data[10] & 255;
            minor = data[9] & 255;
            GBDevice device = getDevice();
            device.setFirmwareVersion2((data[2] & 255) + "." + (data[1] & 255));
            this.mHPlusSupport.setUnicodeSupport(data[3] != 0);
        } else {
            major = data[2] & 255;
            minor = data[1] & 255;
        }
        GBDevice device2 = getDevice();
        device2.setFirmwareVersion(major + "." + minor);
        getDevice().sendDeviceUpdateIntent(getContext());
        return true;
    }

    private void requestNextSleepData() {
        try {
            TransactionBuilder builder = new TransactionBuilder("requestSleepStats");
            builder.write(this.mHPlusSupport.ctrlCharacteristic, new byte[]{25});
            this.mHPlusSupport.performConnected(builder.getTransaction());
        } catch (Exception e) {
        }
        this.mGetSleepTime = GregorianCalendar.getInstance();
        this.mGetSleepTime.add(13, this.SLEEP_SYNC_RETRY_PERIOD);
    }

    private void requestNextDaySlots() {
        Calendar now = GregorianCalendar.getInstance();
        int i = (now.get(11) * 6) + (now.get(12) / 10);
        this.mGetDaySlotsTime = now;
        if (!this.mSlotsInitialSync) {
            this.mGetDaySlotsTime.set(13, this.CURRENT_DAY_SYNC_PERIOD);
        } else if (this.mLastSlotReceived == 143) {
            this.mSlotsInitialSync = false;
            this.mGetDaySlotsTime.set(13, this.CURRENT_DAY_SYNC_PERIOD);
            this.mLastSlotReceived = -1;
            this.mLastSlotRequested = this.mLastSlotReceived + 1;
        } else {
            this.mGetDaySlotsTime.add(13, this.CURRENT_DAY_SYNC_RETRY_PERIOD);
            if (this.mLastSlotReceived == 143) {
                this.mLastSlotReceived = -1;
            }
            int i2 = this.mLastSlotReceived;
            byte hour = (byte) ((i2 + 1) / 6);
            byte nextHour = hour;
            this.mLastSlotRequested = (nextHour * 6) + (59 / 10);
            byte[] msg = {39, hour, (byte) (((i2 + 1) % 6) * 10), nextHour, 59};
            try {
                TransactionBuilder builder = new TransactionBuilder("getNextDaySlot");
                builder.write(this.mHPlusSupport.ctrlCharacteristic, msg);
                this.mHPlusSupport.performConnected(builder.getTransaction());
            } catch (Exception e) {
            }
        }
    }

    public void requestDaySummaryData() {
        try {
            TransactionBuilder builder = new TransactionBuilder("startSyncDaySummary");
            builder.write(this.mHPlusSupport.ctrlCharacteristic, new byte[]{21});
            this.mHPlusSupport.performConnected(builder.getTransaction());
        } catch (Exception e) {
        }
        this.mGetDaySummaryTime = GregorianCalendar.getInstance();
        this.mGetDaySummaryTime.add(13, this.DAY_SUMMARY_SYNC_RETRY_PERIOD);
    }

    private HPlusHealthActivitySample createSample(DBHandler dbHandler, int timestamp) {
        return new HPlusHealthActivitySample(timestamp, DBHelper.getDevice(getDevice(), dbHandler.getDaoSession()).getId().longValue(), DBHelper.getUser(dbHandler.getDaoSession()).getId().longValue(), (byte[]) null, 0, 0, -1, -1, -1, -1);
    }

    public void setHPlusSupport(HPlusSupport HPlusSupport) {
        LOG.info("Updating HPlusSupport object");
        this.mHPlusSupport = HPlusSupport;
    }
}
