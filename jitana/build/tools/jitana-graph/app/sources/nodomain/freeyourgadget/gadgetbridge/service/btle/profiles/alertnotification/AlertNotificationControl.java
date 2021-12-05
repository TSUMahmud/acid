package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification;

import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;

public class AlertNotificationControl {
    private AlertCategory category;
    private Command command;

    public void setCategory(AlertCategory category2) {
        this.category = category2;
    }

    public void setCommand(Command command2) {
        this.command = command2;
    }

    public AlertCategory getCategory() {
        return this.category;
    }

    public Command getCommand() {
        return this.command;
    }

    public byte[] getControlMessage() {
        return new byte[]{BLETypeConversions.fromUint8(this.command.getId()), BLETypeConversions.fromUint8(this.category.getId())};
    }
}
