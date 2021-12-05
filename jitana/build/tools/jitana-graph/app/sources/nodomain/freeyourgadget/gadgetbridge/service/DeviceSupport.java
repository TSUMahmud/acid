package nodomain.freeyourgadget.gadgetbridge.service;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import nodomain.freeyourgadget.gadgetbridge.devices.EventHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public interface DeviceSupport extends EventHandler {
    boolean connect();

    boolean connectFirstTime();

    String customStringFilter(String str);

    void dispose();

    boolean getAutoReconnect();

    BluetoothAdapter getBluetoothAdapter();

    Context getContext();

    GBDevice getDevice();

    boolean isConnected();

    void setAutoReconnect(boolean z);

    void setContext(GBDevice gBDevice, BluetoothAdapter bluetoothAdapter, Context context);

    boolean useAutoConnect();
}
