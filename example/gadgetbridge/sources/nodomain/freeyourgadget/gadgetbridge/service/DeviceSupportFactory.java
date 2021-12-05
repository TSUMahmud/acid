package nodomain.freeyourgadget.gadgetbridge.service;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import java.util.EnumSet;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.service.ServiceDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.banglejs.BangleJSDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.casiogb6900.CasioGB6900DeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.hplus.HPlusSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip.AmazfitBipLiteSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitbip.AmazfitBipSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitcor.AmazfitCorSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitcor2.AmazfitCor2Support;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitgtr.AmazfitGTRSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfitgts.AmazfitGTSSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband3.MiBand3Support;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.miband4.MiBand4Support;
import nodomain.freeyourgadget.gadgetbridge.service.devices.id115.ID115Support;
import nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.BFH16DeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.TeclastH30.TeclastH30Support;
import nodomain.freeyourgadget.gadgetbridge.service.devices.jyou.p012y5.Y5Support;
import nodomain.freeyourgadget.gadgetbridge.service.devices.liveview.LiveviewSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.makibeshr3.MakibesHR3DeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband.MiBandSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.mijia_lywsd02.MijiaLywsd02Support;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miscale2.MiScale2DeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.no1f1.No1F1Support;
import nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.PebbleSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.QHybridSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.roidmi.RoidmiSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.vibratissimo.VibratissimoSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.watch9.Watch9DeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.xwatch.XWatchSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.zetime.ZeTimeDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public class DeviceSupportFactory {
    private final BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Context mContext;

    DeviceSupportFactory(Context context) {
        this.mContext = context;
    }

    public synchronized DeviceSupport createDeviceSupport(GBDevice device) throws GBException {
        DeviceSupport deviceSupport;
        String deviceAddress = device.getAddress();
        int indexFirstColon = deviceAddress.indexOf(":");
        if (indexFirstColon <= 0) {
            deviceSupport = createClassNameDeviceSupport(device);
        } else if (indexFirstColon == deviceAddress.lastIndexOf(":")) {
            deviceSupport = createTCPDeviceSupport(device);
        } else {
            deviceSupport = createBTDeviceSupport(device);
        }
        if (deviceSupport != null) {
            return deviceSupport;
        }
        checkBtAvailability();
        return null;
    }

    private DeviceSupport createClassNameDeviceSupport(GBDevice device) throws GBException {
        String className = device.getAddress();
        try {
            DeviceSupport support = (DeviceSupport) Class.forName(className).getConstructor(new Class[0]).newInstance(new Object[0]);
            support.setContext(device, (BluetoothAdapter) null, this.mContext);
            return support;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (Exception e2) {
            throw new GBException("Error creating DeviceSupport instance for " + className, e2);
        }
    }

    private void checkBtAvailability() {
        BluetoothAdapter bluetoothAdapter = this.mBtAdapter;
        if (bluetoothAdapter == null) {
            C1238GB.toast(this.mContext.getString(C0889R.string.bluetooth_is_not_supported_), 0, 2);
        } else if (!bluetoothAdapter.isEnabled()) {
            C1238GB.toast(this.mContext.getString(C0889R.string.bluetooth_is_disabled_), 0, 2);
        }
    }

    private DeviceSupport createBTDeviceSupport(GBDevice gbDevice) throws GBException {
        BluetoothAdapter bluetoothAdapter = this.mBtAdapter;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return null;
        }
        DeviceSupport deviceSupport = null;
        try {
            switch (gbDevice.getType()) {
                case PEBBLE:
                    deviceSupport = new ServiceDeviceSupport(new PebbleSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case MIBAND:
                    deviceSupport = new ServiceDeviceSupport(new MiBandSupport(), EnumSet.of(ServiceDeviceSupport.Flags.THROTTLING, ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case MIBAND2:
                    deviceSupport = new ServiceDeviceSupport(new HuamiSupport(), EnumSet.of(ServiceDeviceSupport.Flags.THROTTLING, ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case MIBAND3:
                    deviceSupport = new ServiceDeviceSupport(new MiBand3Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case MIBAND4:
                    deviceSupport = new ServiceDeviceSupport(new MiBand4Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case AMAZFITBIP:
                    deviceSupport = new ServiceDeviceSupport(new AmazfitBipSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case AMAZFITBIP_LITE:
                    deviceSupport = new ServiceDeviceSupport(new AmazfitBipLiteSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case AMAZFITGTR:
                    deviceSupport = new ServiceDeviceSupport(new AmazfitGTRSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case AMAZFITGTS:
                    deviceSupport = new ServiceDeviceSupport(new AmazfitGTSSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case AMAZFITCOR:
                    deviceSupport = new ServiceDeviceSupport(new AmazfitCorSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case AMAZFITCOR2:
                    deviceSupport = new ServiceDeviceSupport(new AmazfitCor2Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case VIBRATISSIMO:
                    deviceSupport = new ServiceDeviceSupport(new VibratissimoSupport(), EnumSet.of(ServiceDeviceSupport.Flags.THROTTLING, ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case LIVEVIEW:
                    deviceSupport = new ServiceDeviceSupport(new LiveviewSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case HPLUS:
                    deviceSupport = new ServiceDeviceSupport(new HPlusSupport(DeviceType.HPLUS), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case MAKIBESF68:
                    deviceSupport = new ServiceDeviceSupport(new HPlusSupport(DeviceType.MAKIBESF68), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case EXRIZUK8:
                    deviceSupport = new ServiceDeviceSupport(new HPlusSupport(DeviceType.EXRIZUK8), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case Q8:
                    deviceSupport = new ServiceDeviceSupport(new HPlusSupport(DeviceType.Q8), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case NO1F1:
                    deviceSupport = new ServiceDeviceSupport(new No1F1Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case TECLASTH30:
                    deviceSupport = new ServiceDeviceSupport(new TeclastH30Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case XWATCH:
                    deviceSupport = new ServiceDeviceSupport(new XWatchSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case FOSSILQHYBRID:
                    deviceSupport = new ServiceDeviceSupport(new QHybridSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case ZETIME:
                    deviceSupport = new ServiceDeviceSupport(new ZeTimeDeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case ID115:
                    deviceSupport = new ServiceDeviceSupport(new ID115Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case WATCH9:
                    deviceSupport = new ServiceDeviceSupport(new Watch9DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.THROTTLING, ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case ROIDMI:
                    deviceSupport = new ServiceDeviceSupport(new RoidmiSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case ROIDMI3:
                    deviceSupport = new ServiceDeviceSupport(new RoidmiSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case Y5:
                    deviceSupport = new ServiceDeviceSupport(new Y5Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case CASIOGB6900:
                    deviceSupport = new ServiceDeviceSupport(new CasioGB6900DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case MISCALE2:
                    deviceSupport = new ServiceDeviceSupport(new MiScale2DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case BFH16:
                    deviceSupport = new ServiceDeviceSupport(new BFH16DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case MIJIA_LYWSD02:
                    deviceSupport = new ServiceDeviceSupport(new MijiaLywsd02Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case MAKIBESHR3:
                    deviceSupport = new ServiceDeviceSupport(new MakibesHR3DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
                case BANGLEJS:
                    deviceSupport = new ServiceDeviceSupport(new BangleJSDeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                    break;
            }
            if (deviceSupport == null) {
                return null;
            }
            deviceSupport.setContext(gbDevice, this.mBtAdapter, this.mContext);
            return deviceSupport;
        } catch (Exception e) {
            throw new GBException(this.mContext.getString(C0889R.string.cannot_connect_bt_address_invalid_), e);
        }
    }

    private DeviceSupport createTCPDeviceSupport(GBDevice gbDevice) throws GBException {
        try {
            DeviceSupport deviceSupport = new ServiceDeviceSupport(new PebbleSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
            deviceSupport.setContext(gbDevice, this.mBtAdapter, this.mContext);
            return deviceSupport;
        } catch (Exception e) {
            throw new GBException("cannot connect to " + gbDevice, e);
        }
    }
}
