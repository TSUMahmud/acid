package nodomain.freeyourgadget.gadgetbridge.service.btle;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public abstract class AbstractGattCallback implements GattCallback {
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
    }

    public void onServicesDiscovered(BluetoothGatt gatt) {
    }

    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        return false;
    }

    public boolean onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        return false;
    }

    public boolean onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        return false;
    }

    public boolean onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        return false;
    }

    public boolean onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        return false;
    }

    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
    }

    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
    }
}
