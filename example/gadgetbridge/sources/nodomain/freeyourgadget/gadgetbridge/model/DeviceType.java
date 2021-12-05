package nodomain.freeyourgadget.gadgetbridge.model;

import androidx.recyclerview.widget.ItemTouchHelper;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import p005ch.qos.logback.core.net.SyslogConstants;

public enum DeviceType {
    UNKNOWN(-1, C0889R.C0890drawable.ic_device_default, C0889R.C0890drawable.ic_device_default_disabled, C0889R.string.devicetype_unknown),
    PEBBLE(1, C0889R.C0890drawable.ic_device_pebble, C0889R.C0890drawable.ic_device_pebble_disabled, C0889R.string.devicetype_pebble),
    MIBAND(10, C0889R.C0890drawable.ic_device_miband, C0889R.C0890drawable.ic_device_miband_disabled, C0889R.string.devicetype_miband),
    MIBAND2(11, C0889R.C0890drawable.ic_device_miband2, C0889R.C0890drawable.ic_device_miband2_disabled, C0889R.string.devicetype_miband2),
    AMAZFITBIP(12, C0889R.C0890drawable.ic_device_hplus, C0889R.C0890drawable.ic_device_hplus_disabled, C0889R.string.devicetype_amazfit_bip),
    AMAZFITCOR(13, C0889R.C0890drawable.ic_device_default, C0889R.C0890drawable.ic_device_default_disabled, C0889R.string.devicetype_amazfit_cor),
    MIBAND3(14, C0889R.C0890drawable.ic_device_miband2, C0889R.C0890drawable.ic_device_miband2_disabled, C0889R.string.devicetype_miband3),
    AMAZFITCOR2(15, C0889R.C0890drawable.ic_device_default, C0889R.C0890drawable.ic_device_default_disabled, C0889R.string.devicetype_amazfit_cor2),
    MIBAND4(16, C0889R.C0890drawable.ic_device_miband2, C0889R.C0890drawable.ic_device_miband2_disabled, C0889R.string.devicetype_miband4),
    AMAZFITBIP_LITE(17, C0889R.C0890drawable.ic_device_hplus, C0889R.C0890drawable.ic_device_hplus_disabled, C0889R.string.devicetype_amazfit_bip_lite),
    AMAZFITGTR(18, C0889R.C0890drawable.ic_device_hplus, C0889R.C0890drawable.ic_device_hplus_disabled, C0889R.string.devicetype_amazfit_gtr),
    AMAZFITGTS(19, C0889R.C0890drawable.ic_device_hplus, C0889R.C0890drawable.ic_device_hplus_disabled, C0889R.string.devicetype_amazfit_gts),
    LIVEVIEW(30, C0889R.C0890drawable.ic_device_default, C0889R.C0890drawable.ic_device_default_disabled, C0889R.string.devicetype_liveview),
    HPLUS(40, C0889R.C0890drawable.ic_device_hplus, C0889R.C0890drawable.ic_device_hplus_disabled, C0889R.string.devicetype_hplus),
    MAKIBESF68(41, C0889R.C0890drawable.ic_device_hplus, C0889R.C0890drawable.ic_device_hplus_disabled, C0889R.string.devicetype_makibes_f68),
    EXRIZUK8(42, C0889R.C0890drawable.ic_device_hplus, C0889R.C0890drawable.ic_device_hplus_disabled, C0889R.string.devicetype_exrizu_k8),
    Q8(43, C0889R.C0890drawable.ic_device_hplus, C0889R.C0890drawable.ic_device_hplus_disabled, C0889R.string.devicetype_q8),
    NO1F1(50, C0889R.C0890drawable.ic_device_hplus, C0889R.C0890drawable.ic_device_hplus_disabled, C0889R.string.devicetype_no1_f1),
    TECLASTH30(60, C0889R.C0890drawable.ic_device_h30_h10, C0889R.C0890drawable.ic_device_h30_h10_disabled, C0889R.string.devicetype_teclast_h30),
    Y5(61, C0889R.C0890drawable.ic_device_h30_h10, C0889R.C0890drawable.ic_device_roidmi_disabled, C0889R.string.devicetype_y5),
    XWATCH(70, C0889R.C0890drawable.ic_device_default, C0889R.C0890drawable.ic_device_default_disabled, C0889R.string.devicetype_xwatch),
    FOSSILQHYBRID(80, C0889R.C0890drawable.ic_device_zetime, C0889R.C0890drawable.ic_device_zetime_disabled, C0889R.string.devicetype_qhybrid),
    ZETIME(80, C0889R.C0890drawable.ic_device_zetime, C0889R.C0890drawable.ic_device_zetime_disabled, C0889R.string.devicetype_mykronoz_zetime),
    ID115(90, C0889R.C0890drawable.ic_device_h30_h10, C0889R.C0890drawable.ic_device_h30_h10_disabled, C0889R.string.devicetype_id115),
    WATCH9(100, C0889R.C0890drawable.ic_device_default, C0889R.C0890drawable.ic_device_default_disabled, C0889R.string.devicetype_watch9),
    ROIDMI(110, C0889R.C0890drawable.ic_device_roidmi, C0889R.C0890drawable.ic_device_roidmi_disabled, C0889R.string.devicetype_roidmi),
    ROIDMI3(112, C0889R.C0890drawable.ic_device_roidmi, C0889R.C0890drawable.ic_device_roidmi_disabled, C0889R.string.devicetype_roidmi3),
    CASIOGB6900(120, C0889R.C0890drawable.ic_device_default, C0889R.C0890drawable.ic_device_default_disabled, C0889R.string.devicetype_casiogb6900),
    MISCALE2(131, C0889R.C0890drawable.ic_device_default, C0889R.C0890drawable.ic_device_default_disabled, C0889R.string.devicetype_miscale2),
    BFH16(140, C0889R.C0890drawable.ic_device_default, C0889R.C0890drawable.ic_device_default_disabled, C0889R.string.devicetype_bfh16),
    MAKIBESHR3(150, C0889R.C0890drawable.ic_device_default, C0889R.C0890drawable.ic_device_hplus_disabled, C0889R.string.devicetype_makibes_hr3),
    BANGLEJS(SyslogConstants.LOG_LOCAL4, C0889R.C0890drawable.ic_device_zetime, C0889R.C0890drawable.ic_device_zetime_disabled, C0889R.string.devicetype_banglejs),
    MIJIA_LYWSD02(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, C0889R.C0890drawable.ic_device_pebble, C0889R.C0890drawable.ic_device_pebble_disabled, C0889R.string.devicetype_mijia_lywsd02),
    VIBRATISSIMO(300, C0889R.C0890drawable.ic_device_lovetoy, C0889R.C0890drawable.ic_device_lovetoy_disabled, C0889R.string.devicetype_vibratissimo),
    TEST(1000, C0889R.C0890drawable.ic_device_default, C0889R.C0890drawable.ic_device_default_disabled, C0889R.string.devicetype_test);
    
    private final int defaultIcon;
    private final int disabledIcon;
    private final int key;
    private final int name;

    private DeviceType(int key2, int defaultIcon2, int disabledIcon2, int name2) {
        this.key = key2;
        this.defaultIcon = defaultIcon2;
        this.disabledIcon = disabledIcon2;
        this.name = name2;
    }

    public int getKey() {
        return this.key;
    }

    public boolean isSupported() {
        return this != UNKNOWN;
    }

    public static DeviceType fromKey(int key2) {
        for (DeviceType type : values()) {
            if (type.key == key2) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public int getName() {
        return this.name;
    }

    public int getIcon() {
        return this.defaultIcon;
    }

    public int getDisabledIcon() {
        return this.disabledIcon;
    }
}
