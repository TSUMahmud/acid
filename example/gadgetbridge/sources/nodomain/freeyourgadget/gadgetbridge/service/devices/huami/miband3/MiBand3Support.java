package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband3;

import android.content.Context;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import java.io.IOException;
import java.util.Set;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3.MiBand3FWHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3.MiBand3Service;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.devices.qhybrid.PackageConfigHelper;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip.AmazfitBipSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiBand3Support extends AmazfitBipSupport {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) MiBand3Support.class);

    /* access modifiers changed from: protected */
    public byte getAuthFlags() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public MiBand3Support setDisplayItems(TransactionBuilder builder) {
        Set<String> pages = HuamiCoordinator.getDisplayItems(this.gbDevice.getAddress());
        Logger logger = LOG;
        StringBuilder sb = new StringBuilder();
        sb.append("Setting display items to ");
        sb.append(pages == null ? "none" : pages);
        logger.info(sb.toString());
        byte[] command = (byte[]) MiBand3Service.COMMAND_CHANGE_SCREENS.clone();
        byte pos = 1;
        if (pages != null) {
            if (pages.contains(PackageConfigHelper.DB_TABLE)) {
                command[1] = (byte) (command[1] | 2);
                command[4] = 1;
                pos = (byte) (1 + 1);
            }
            if (pages.contains(DeviceService.EXTRA_WEATHER)) {
                command[1] = (byte) (command[1] | 4);
                command[5] = pos;
                pos = (byte) (pos + 1);
            }
            if (pages.contains("activity")) {
                command[1] = (byte) (command[1] | 8);
                command[6] = pos;
                pos = (byte) (pos + 1);
            }
            if (pages.contains("more")) {
                command[1] = (byte) (command[1] | 16);
                command[7] = pos;
                pos = (byte) (pos + 1);
            }
            if (pages.contains(NotificationCompat.CATEGORY_STATUS)) {
                command[1] = (byte) (command[1] | 32);
                command[8] = pos;
                pos = (byte) (pos + 1);
            }
            if (pages.contains(MiBandConst.PREF_MI2_DISPLAY_ITEM_HEART_RATE)) {
                command[1] = (byte) (command[1] | 64);
                command[9] = pos;
                pos = (byte) (pos + 1);
            }
            if (pages.contains("timer")) {
                command[1] = (byte) (command[1] | 128);
                command[10] = pos;
                pos = (byte) (pos + 1);
            }
            if (pages.contains("nfc")) {
                command[2] = (byte) (command[2] | 1);
                command[11] = pos;
                pos = (byte) (pos + 1);
            }
        }
        for (int i = 4; i <= 11; i++) {
            if (command[i] == 0) {
                command[i] = pos;
                pos = (byte) (pos + 1);
            }
        }
        builder.write(getCharacteristic(HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION), command);
        return this;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x004b A[ADDED_TO_REGION, Catch:{ IOException -> 0x005f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSendConfiguration(java.lang.String r7) {
        /*
            r6 = this;
            r0 = 1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x005f }
            r1.<init>()     // Catch:{ IOException -> 0x005f }
            java.lang.String r2 = "Sending configuration for option: "
            r1.append(r2)     // Catch:{ IOException -> 0x005f }
            r1.append(r7)     // Catch:{ IOException -> 0x005f }
            java.lang.String r1 = r1.toString()     // Catch:{ IOException -> 0x005f }
            nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder r1 = r6.performInitialized(r1)     // Catch:{ IOException -> 0x005f }
            r2 = -1
            int r3 = r7.hashCode()     // Catch:{ IOException -> 0x005f }
            r4 = -1293726387(0xffffffffb2e34d4d, float:-2.646143E-8)
            r5 = 2
            if (r3 == r4) goto L_0x0040
            r4 = -601793174(0xffffffffdc215d6a, float:-1.81680724E17)
            if (r3 == r4) goto L_0x0036
            r4 = 137187462(0x82d5086, float:5.215495E-34)
            if (r3 == r4) goto L_0x002c
        L_0x002b:
            goto L_0x0049
        L_0x002c:
            java.lang.String r3 = "night_mode_end"
            boolean r3 = r7.equals(r3)     // Catch:{ IOException -> 0x005f }
            if (r3 == 0) goto L_0x002b
            r2 = 2
            goto L_0x0049
        L_0x0036:
            java.lang.String r3 = "night_mode"
            boolean r3 = r7.equals(r3)     // Catch:{ IOException -> 0x005f }
            if (r3 == 0) goto L_0x002b
            r2 = 0
            goto L_0x0049
        L_0x0040:
            java.lang.String r3 = "night_mode_start"
            boolean r3 = r7.equals(r3)     // Catch:{ IOException -> 0x005f }
            if (r3 == 0) goto L_0x002b
            r2 = 1
        L_0x0049:
            if (r2 == 0) goto L_0x0053
            if (r2 == r0) goto L_0x0053
            if (r2 == r5) goto L_0x0053
            super.onSendConfiguration(r7)     // Catch:{ IOException -> 0x005f }
            return
        L_0x0053:
            r6.setNightMode(r1)     // Catch:{ IOException -> 0x005f }
            nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEQueue r2 = r6.getQueue()     // Catch:{ IOException -> 0x005f }
            r1.queue(r2)     // Catch:{ IOException -> 0x005f }
            goto L_0x0066
        L_0x005f:
            r1 = move-exception
            r2 = 3
            java.lang.String r3 = "Error setting configuration"
            nodomain.freeyourgadget.gadgetbridge.util.C1238GB.toast((java.lang.String) r3, (int) r0, (int) r2, (java.lang.Throwable) r1)
        L_0x0066:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband3.MiBand3Support.onSendConfiguration(java.lang.String):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00d1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband3.MiBand3Support setNightMode(nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder r9) {
        /*
            r8 = this;
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r0 = r8.gbDevice
            java.lang.String r0 = r0.getAddress()
            java.lang.String r0 = nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3.MiBand3Coordinator.getNightMode(r0)
            org.slf4j.Logger r1 = LOG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Setting night mode to "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            r1.info(r2)
            int r1 = r0.hashCode()
            r2 = -891172202(0xffffffffcae1ca96, float:-7398731.0)
            r3 = 1
            r4 = 2
            if (r1 == r2) goto L_0x004a
            r2 = -160710483(0xfffffffff66bc0ad, float:-1.1954079E33)
            if (r1 == r2) goto L_0x0040
            r2 = 109935(0x1ad6f, float:1.54052E-40)
            if (r1 == r2) goto L_0x0036
        L_0x0035:
            goto L_0x0054
        L_0x0036:
            java.lang.String r1 = "off"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0035
            r1 = 1
            goto L_0x0055
        L_0x0040:
            java.lang.String r1 = "scheduled"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0035
            r1 = 2
            goto L_0x0055
        L_0x004a:
            java.lang.String r1 = "sunset"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0035
            r1 = 0
            goto L_0x0055
        L_0x0054:
            r1 = -1
        L_0x0055:
            if (r1 == 0) goto L_0x00d1
            if (r1 == r3) goto L_0x00c5
            if (r1 == r4) goto L_0x0072
            org.slf4j.Logger r1 = LOG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Invalid night mode: "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            r1.error(r2)
            goto L_0x00dd
        L_0x0072:
            byte[] r1 = nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3.MiBand3Service.COMMAND_NIGHT_MODE_SCHEDULED
            java.lang.Object r1 = r1.clone()
            byte[] r1 = (byte[]) r1
            java.util.Calendar r2 = java.util.GregorianCalendar.getInstance()
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r3 = r8.gbDevice
            java.lang.String r3 = r3.getAddress()
            java.util.Date r3 = nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3.MiBand3Coordinator.getNightModeStart(r3)
            r2.setTime(r3)
            r5 = 11
            int r6 = r2.get(r5)
            byte r6 = (byte) r6
            r1[r4] = r6
            r4 = 3
            r6 = 12
            int r7 = r2.get(r6)
            byte r7 = (byte) r7
            r1[r4] = r7
            nodomain.freeyourgadget.gadgetbridge.impl.GBDevice r4 = r8.gbDevice
            java.lang.String r4 = r4.getAddress()
            java.util.Date r4 = nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3.MiBand3Coordinator.getNightModeEnd(r4)
            r2.setTime(r4)
            r7 = 4
            int r5 = r2.get(r5)
            byte r5 = (byte) r5
            r1[r7] = r5
            r5 = 5
            int r6 = r2.get(r6)
            byte r6 = (byte) r6
            r1[r5] = r6
            java.util.UUID r5 = nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION
            android.bluetooth.BluetoothGattCharacteristic r5 = r8.getCharacteristic(r5)
            r9.write(r5, r1)
            goto L_0x00dd
        L_0x00c5:
            java.util.UUID r1 = nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION
            android.bluetooth.BluetoothGattCharacteristic r1 = r8.getCharacteristic(r1)
            byte[] r2 = nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3.MiBand3Service.COMMAND_NIGHT_MODE_OFF
            r9.write(r1, r2)
            goto L_0x00dd
        L_0x00d1:
            java.util.UUID r1 = nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiService.UUID_CHARACTERISTIC_3_CONFIGURATION
            android.bluetooth.BluetoothGattCharacteristic r1 = r8.getCharacteristic(r1)
            byte[] r2 = nodomain.freeyourgadget.gadgetbridge.devices.huami.miband3.MiBand3Service.COMMAND_NIGHT_MODE_SUNSET
            r9.write(r1, r2)
        L_0x00dd:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband3.MiBand3Support.setNightMode(nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder):nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband3.MiBand3Support");
    }

    public void phase2Initialize(TransactionBuilder builder) {
        super.phase2Initialize(builder);
        LOG.info("phase2Initialize...");
        setLanguage(builder);
        setBandScreenUnlock(builder);
        setNightMode(builder);
        setDateFormat(builder);
    }

    public HuamiFWHelper createFWHelper(Uri uri, Context context) throws IOException {
        return new MiBand3FWHelper(uri, context);
    }
}
