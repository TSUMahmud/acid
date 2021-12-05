package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip;

import nodomain.freeyourgadget.gadgetbridge.devices.miband.VibrationProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BtLEAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.AlertCategory;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.AlertNotificationProfile;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.NewAlert;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.OverflowStrategy;
import nodomain.freeyourgadget.gadgetbridge.service.devices.common.SimpleNotification;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband2.Mi2TextNotificationStrategy;

class AmazfitBipTextNotificationStrategy extends Mi2TextNotificationStrategy {
    AmazfitBipTextNotificationStrategy(HuamiSupport support) {
        super(support);
    }

    /* access modifiers changed from: protected */
    public void sendCustomNotification(VibrationProfile vibrationProfile, SimpleNotification simpleNotification, BtLEAction extraAction, TransactionBuilder builder) {
        if (simpleNotification != null) {
            sendAlert(simpleNotification, builder);
        }
    }

    /* access modifiers changed from: protected */
    public void sendAlert(SimpleNotification simpleNotification, TransactionBuilder builder) {
        AlertNotificationProfile<?> profile = new AlertNotificationProfile<>(getSupport());
        profile.setMaxLength(255);
        AlertCategory category = simpleNotification.getAlertCategory();
        int i = C11851.f183x46c12258[simpleNotification.getAlertCategory().ordinal()];
        if (!(i == 1 || i == 2 || i == 3)) {
            category = AlertCategory.SMS;
        }
        profile.newAlert(builder, new NewAlert(category, 1, simpleNotification.getMessage()), OverflowStrategy.TRUNCATE);
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip.AmazfitBipTextNotificationStrategy$1 */
    static /* synthetic */ class C11851 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$service$btle$profiles$alertnotification$AlertCategory */
        static final /* synthetic */ int[] f183x46c12258 = new int[AlertCategory.values().length];

        static {
            try {
                f183x46c12258[AlertCategory.Email.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f183x46c12258[AlertCategory.IncomingCall.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f183x46c12258[AlertCategory.SMS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }
}
