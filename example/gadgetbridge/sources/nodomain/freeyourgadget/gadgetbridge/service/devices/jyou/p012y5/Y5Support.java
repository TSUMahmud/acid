package nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.p012y5;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.nio.ByteBuffer;
import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventVersionInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.jyou.JYouConstants;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.JYouActivitySample;
import nodomain.freeyourgadget.gadgetbridge.entities.User;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.JYouSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.RealtimeSamplesSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.y5.Y5Support */
public class Y5Support extends JYouSupport {
    /* access modifiers changed from: private */
    public static final Logger LOG = LoggerFactory.getLogger((Class<?>) Y5Support.class);
    private RealtimeSamplesSupport realtimeSamplesSupport;

    public Y5Support() {
        super(LOG);
        addSupportedService(JYouConstants.UUID_SERVICE_JYOU);
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (super.onCharacteristicChanged(gatt, characteristic)) {
            return true;
        }
        UUID characteristicUUID = characteristic.getUuid();
        byte[] data = characteristic.getValue();
        if (data.length == 0) {
            return true;
        }
        byte b = data[0];
        if (b == -24) {
            byte heartRate = data[2];
            byte bloodPressureHigh = data[3];
            byte bloodPressureLow = data[4];
            byte b2 = data[5];
            byte b3 = data[6];
            Logger logger = LOG;
            logger.info("RECEIVE_BLOOD_PRESSURE: Heart rate: " + heartRate + " Pressure high: " + bloodPressureHigh + " pressure low: " + bloodPressureLow);
            return true;
        } else if (b == -20 || b == -13) {
            return true;
        } else {
            if (b == -7) {
                int steps = ByteBuffer.wrap(data, 5, 4).getInt();
                Logger logger2 = LOG;
                logger2.info("Number of walked steps: " + steps);
                handleRealtimeSteps(steps);
                return true;
            } else if (b == -4) {
                handleHeartrate(data[8]);
                return true;
            } else if (b == 50) {
                Logger logger3 = LOG;
                logger3.info("onCharacteristicChanged: " + data[0]);
                return true;
            } else if (b == -10) {
                byte b4 = data[7];
                int fwVerNum = data[4] & 255;
                GBDeviceEventVersionInfo gBDeviceEventVersionInfo = this.versionCmd;
                gBDeviceEventVersionInfo.fwVersion = (fwVerNum / 100) + "." + ((fwVerNum % 100) / 10) + "." + ((fwVerNum % 100) % 10);
                handleGBDeviceEvent(this.versionCmd);
                Logger logger4 = LOG;
                StringBuilder sb = new StringBuilder();
                sb.append("Firmware version is: ");
                sb.append(this.versionCmd.fwVersion);
                logger4.info(sb.toString());
                return true;
            } else if (b != -9) {
                Logger logger5 = LOG;
                logger5.info("Unhandled characteristic change: " + characteristicUUID + " code: " + String.format("0x%1x ...", new Object[]{Byte.valueOf(data[0])}));
                return true;
            } else {
                this.batteryCmd.level = (short) data[8];
                handleGBDeviceEvent(this.batteryCmd);
                Logger logger6 = LOG;
                logger6.info("Battery level is: " + this.batteryCmd.level);
                return true;
            }
        }
    }

    private void handleRealtimeSteps(int value) {
        if (LOG.isDebugEnabled()) {
            Logger logger = LOG;
            logger.debug("realtime steps: " + value);
        }
        getRealtimeSamplesSupport().setSteps(value);
    }

    private void handleHeartrate(int value) {
        if (LOG.isDebugEnabled()) {
            Logger logger = LOG;
            logger.debug("heart rate: " + value);
        }
        RealtimeSamplesSupport realtimeSamplesSupport2 = getRealtimeSamplesSupport();
        realtimeSamplesSupport2.setHeartrateBpm(value);
        if (!realtimeSamplesSupport2.isRunning()) {
            realtimeSamplesSupport2.triggerCurrentSample();
        }
    }

    public JYouActivitySample createActivitySample(Device device, User user, int timestampInSeconds, SampleProvider provider) {
        JYouActivitySample sample = new JYouActivitySample();
        sample.setDevice(device);
        sample.setUser(user);
        sample.setTimestamp(timestampInSeconds);
        sample.setProvider(provider);
        return sample;
    }

    private void enableRealtimeSamplesTimer(boolean enable) {
        if (enable) {
            getRealtimeSamplesSupport().start();
            return;
        }
        RealtimeSamplesSupport realtimeSamplesSupport2 = this.realtimeSamplesSupport;
        if (realtimeSamplesSupport2 != null) {
            realtimeSamplesSupport2.stop();
        }
    }

    private RealtimeSamplesSupport getRealtimeSamplesSupport() {
        if (this.realtimeSamplesSupport == null) {
            this.realtimeSamplesSupport = new RealtimeSamplesSupport(1000, 1000) {
                /* JADX WARNING: Code restructure failed: missing block: B:16:0x00ad, code lost:
                    r2 = move-exception;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:17:0x00ae, code lost:
                    if (r0 != null) goto L_0x00b0;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
                    r0.close();
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:23:0x00b8, code lost:
                    throw r2;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void doCurrentSample() {
                    /*
                        r8 = this;
                        nodomain.freeyourgadget.gadgetbridge.database.DBHandler r0 = nodomain.freeyourgadget.gadgetbridge.GBApplication.acquireDB()     // Catch:{ Exception -> 0x00b9 }
                        nodomain.freeyourgadget.gadgetbridge.entities.DaoSession r1 = r0.getDaoSession()     // Catch:{ all -> 0x00ab }
                        long r2 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x00ab }
                        r4 = 1000(0x3e8, double:4.94E-321)
                        long r2 = r2 / r4
                        int r3 = (int) r2     // Catch:{ all -> 0x00ab }
                        nodomain.freeyourgadget.gadgetbridge.devices.jyou.JYouSampleProvider r2 = new nodomain.freeyourgadget.gadgetbridge.devices.jyou.JYouSampleProvider     // Catch:{ all -> 0x00ab }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.y5.Y5Support r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.p012y5.Y5Support.this     // Catch:{ all -> 0x00ab }
                        nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r4.gbDevice     // Catch:{ all -> 0x00ab }
                        r2.<init>(r4, r1)     // Catch:{ all -> 0x00ab }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.y5.Y5Support r4 = nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.p012y5.Y5Support.this     // Catch:{ all -> 0x00ab }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.y5.Y5Support r5 = nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.p012y5.Y5Support.this     // Catch:{ all -> 0x00ab }
                        nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r5 = r5.getDevice()     // Catch:{ all -> 0x00ab }
                        nodomain.freeyourgadget.gadgetbridge.entities.Device r5 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getDevice(r5, r1)     // Catch:{ all -> 0x00ab }
                        nodomain.freeyourgadget.gadgetbridge.entities.User r6 = nodomain.freeyourgadget.gadgetbridge.database.DBHelper.getUser(r1)     // Catch:{ all -> 0x00ab }
                        nodomain.freeyourgadget.gadgetbridge.entities.JYouActivitySample r4 = r4.createActivitySample(r5, r6, r3, r2)     // Catch:{ all -> 0x00ab }
                        int r5 = r8.getHeartrateBpm()     // Catch:{ all -> 0x00ab }
                        r4.setHeartRate(r5)     // Catch:{ all -> 0x00ab }
                        r5 = -1
                        r4.setRawIntensity(r5)     // Catch:{ all -> 0x00ab }
                        r4.setRawKind(r5)     // Catch:{ all -> 0x00ab }
                        r2.addGBActivitySample(r4)     // Catch:{ all -> 0x00ab }
                        int r5 = r8.getSteps()     // Catch:{ all -> 0x00ab }
                        r4.setSteps(r5)     // Catch:{ all -> 0x00ab }
                        int r5 = r8.steps     // Catch:{ all -> 0x00ab }
                        r6 = 1
                        if (r5 <= r6) goto L_0x0068
                        org.slf4j.Logger r5 = nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.p012y5.Y5Support.LOG     // Catch:{ all -> 0x00ab }
                        java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
                        r6.<init>()     // Catch:{ all -> 0x00ab }
                        java.lang.String r7 = "Have steps: "
                        r6.append(r7)     // Catch:{ all -> 0x00ab }
                        int r7 = r8.getSteps()     // Catch:{ all -> 0x00ab }
                        r6.append(r7)     // Catch:{ all -> 0x00ab }
                        java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00ab }
                        r5.debug(r6)     // Catch:{ all -> 0x00ab }
                    L_0x0068:
                        org.slf4j.Logger r5 = nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.p012y5.Y5Support.LOG     // Catch:{ all -> 0x00ab }
                        boolean r5 = r5.isDebugEnabled()     // Catch:{ all -> 0x00ab }
                        if (r5 == 0) goto L_0x008a
                        org.slf4j.Logger r5 = nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.p012y5.Y5Support.LOG     // Catch:{ all -> 0x00ab }
                        java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
                        r6.<init>()     // Catch:{ all -> 0x00ab }
                        java.lang.String r7 = "realtime sample: "
                        r6.append(r7)     // Catch:{ all -> 0x00ab }
                        r6.append(r4)     // Catch:{ all -> 0x00ab }
                        java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00ab }
                        r5.debug(r6)     // Catch:{ all -> 0x00ab }
                    L_0x008a:
                        android.content.Intent r5 = new android.content.Intent     // Catch:{ all -> 0x00ab }
                        java.lang.String r6 = "nodomain.freeyourgadget.gadgetbridge.devices.action.realtime_samples"
                        r5.<init>(r6)     // Catch:{ all -> 0x00ab }
                        java.lang.String r6 = "realtime_sample"
                        android.content.Intent r5 = r5.putExtra(r6, r4)     // Catch:{ all -> 0x00ab }
                        nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.y5.Y5Support r6 = nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.p012y5.Y5Support.this     // Catch:{ all -> 0x00ab }
                        android.content.Context r6 = r6.getContext()     // Catch:{ all -> 0x00ab }
                        androidx.localbroadcastmanager.content.LocalBroadcastManager r6 = androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(r6)     // Catch:{ all -> 0x00ab }
                        r6.sendBroadcast(r5)     // Catch:{ all -> 0x00ab }
                        if (r0 == 0) goto L_0x00aa
                        r0.close()     // Catch:{ Exception -> 0x00b9 }
                    L_0x00aa:
                        goto L_0x00c3
                    L_0x00ab:
                        r1 = move-exception
                        throw r1     // Catch:{ all -> 0x00ad }
                    L_0x00ad:
                        r2 = move-exception
                        if (r0 == 0) goto L_0x00b8
                        r0.close()     // Catch:{ all -> 0x00b4 }
                        goto L_0x00b8
                    L_0x00b4:
                        r3 = move-exception
                        r1.addSuppressed(r3)     // Catch:{ Exception -> 0x00b9 }
                    L_0x00b8:
                        throw r2     // Catch:{ Exception -> 0x00b9 }
                    L_0x00b9:
                        r0 = move-exception
                        org.slf4j.Logger r1 = nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.p012y5.Y5Support.LOG
                        java.lang.String r2 = "Unable to acquire db for saving realtime samples"
                        r1.warn((java.lang.String) r2, (java.lang.Throwable) r0)
                    L_0x00c3:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.p012y5.Y5Support.C11901.doCurrentSample():void");
                }
            };
        }
        return this.realtimeSamplesSupport;
    }

    /* access modifiers changed from: protected */
    public void syncSettings(TransactionBuilder builder) {
        syncDateAndTime(builder);
    }

    public void dispose() {
        LOG.info("Dispose");
        super.dispose();
    }

    public void onHeartRateTest() {
        try {
            TransactionBuilder builder = performInitialized("HeartRateTest");
            builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 56, 0, 0));
            performConnected(builder.getTransaction());
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
    }

    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        try {
            TransactionBuilder builder = performInitialized("RealTimeHeartMeasurement");
            builder.write(this.ctrlCharacteristic, commandWithChecksum((byte) 13, 0, enable ? 1 : 0));
            performConnected(builder.getTransaction());
            enableRealtimeSamplesTimer(enable);
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
    }
}
