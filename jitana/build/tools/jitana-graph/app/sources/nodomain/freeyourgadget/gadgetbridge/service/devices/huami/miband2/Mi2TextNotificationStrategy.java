package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband2;

import android.bluetooth.BluetoothGattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.VibrationProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.AlertCategory;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.AlertNotificationProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.NewAlert;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.OverflowStrategy;
import nodomain.freeyourgadget.gadgetbridge.service.devices.common.SimpleNotification;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiIcon;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.util.StringUtils;

public class Mi2TextNotificationStrategy extends Mi2NotificationStrategy {
    private final BluetoothGattCharacteristic newAlertCharacteristic;

    public Mi2TextNotificationStrategy(HuamiSupport support) {
        super(support);
        this.newAlertCharacteristic = support.getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_NEW_ALERT);
    }

    /* access modifiers changed from: protected */
    public void sendCustomNotification(VibrationProfile vibrationProfile, SimpleNotification simpleNotification, BtLEAction extraAction, TransactionBuilder builder) {
        if (simpleNotification == null || simpleNotification.getAlertCategory() != AlertCategory.IncomingCall) {
            super.sendCustomNotification(vibrationProfile, simpleNotification, extraAction, builder);
            if (simpleNotification != null && !StringUtils.isEmpty(simpleNotification.getMessage())) {
                sendAlert(simpleNotification, builder);
                return;
            }
            return;
        }
        sendAlert(simpleNotification, builder);
    }

    /* access modifiers changed from: protected */
    public void startNotify(TransactionBuilder builder, int alertLevel, SimpleNotification simpleNotification) {
        builder.write(this.newAlertCharacteristic, getNotifyMessage(simpleNotification));
    }

    /* access modifiers changed from: protected */
    public byte[] getNotifyMessage(SimpleNotification simpleNotification) {
        if (simpleNotification == null || simpleNotification.getNotificationType() == null || simpleNotification.getAlertCategory() == AlertCategory.SMS) {
            return new byte[]{BLETypeConversions.fromUint8(AlertCategory.SMS.getId()), BLETypeConversions.fromUint8(1)};
        }
        byte customIconId = HuamiIcon.mapToIconId(simpleNotification.getNotificationType());
        if (customIconId == 34) {
            return new byte[]{BLETypeConversions.fromUint8(AlertCategory.Email.getId()), BLETypeConversions.fromUint8(1)};
        }
        return new byte[]{BLETypeConversions.fromUint8(AlertCategory.CustomHuami.getId()), BLETypeConversions.fromUint8(1), customIconId};
    }

    /* access modifiers changed from: protected */
    public void sendAlert(SimpleNotification simpleNotification, TransactionBuilder builder) {
        AlertNotificationProfile<?> profile = new AlertNotificationProfile<>(getSupport());
        AlertCategory category = AlertCategory.SMS;
        if (simpleNotification.getAlertCategory() == AlertCategory.IncomingCall) {
            category = simpleNotification.getAlertCategory();
        }
        profile.newAlert(builder, new NewAlert(category, 1, simpleNotification.getMessage()), OverflowStrategy.MAKE_MULTIPLE);
    }

    public void stopCurrentNotification(TransactionBuilder builder) {
        builder.write(((HuamiSupport) getSupport()).getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_NEW_ALERT), new byte[]{(byte) AlertCategory.IncomingCall.getId(), 0});
    }
}
