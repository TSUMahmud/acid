package cyanogenmod.profiles;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wimax.WimaxHelper;
import android.nfc.NfcAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import cyanogenmod.p007os.Build;
import cyanogenmod.p007os.Concierge;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class ConnectionSettings implements Parcelable {
    private static final String ACTION_MODIFY_NETWORK_MODE = "com.android.internal.telephony.MODIFY_NETWORK_MODE";
    private static final int CM_MODE_2G = 0;
    private static final int CM_MODE_2G3G = 3;
    private static final int CM_MODE_3G = 1;
    private static final int CM_MODE_4G = 2;
    private static final int CM_MODE_ALL = 4;
    public static final Parcelable.Creator<ConnectionSettings> CREATOR = new Parcelable.Creator<ConnectionSettings>() {
        public ConnectionSettings createFromParcel(Parcel in) {
            return new ConnectionSettings(in);
        }

        public ConnectionSettings[] newArray(int size) {
            return new ConnectionSettings[size];
        }
    };
    private static final String EXTRA_NETWORK_MODE = "networkMode";
    private static final String EXTRA_SUB_ID = "subId";
    public static final int PROFILE_CONNECTION_2G3G4G = 9;
    public static final int PROFILE_CONNECTION_BLUETOOTH = 7;
    public static final int PROFILE_CONNECTION_GPS = 4;
    public static final int PROFILE_CONNECTION_MOBILEDATA = 0;
    public static final int PROFILE_CONNECTION_NFC = 8;
    public static final int PROFILE_CONNECTION_SYNC = 5;
    public static final int PROFILE_CONNECTION_WIFI = 1;
    public static final int PROFILE_CONNECTION_WIFIAP = 2;
    public static final int PROFILE_CONNECTION_WIMAX = 3;
    private int mConnectionId;
    private boolean mDirty;
    private boolean mOverride;
    private int mSubId;
    private int mValue;

    public static class BooleanState {
        public static final int STATE_DISALED = 0;
        public static final int STATE_ENABLED = 1;
    }

    public ConnectionSettings(Parcel parcel) {
        this.mSubId = -1;
        readFromParcel(parcel);
    }

    public ConnectionSettings(int connectionId) {
        this(connectionId, 0, false);
    }

    public ConnectionSettings(int connectionId, int value, boolean override) {
        this.mSubId = -1;
        this.mConnectionId = connectionId;
        this.mValue = value;
        this.mOverride = override;
        this.mDirty = false;
    }

    public int getConnectionId() {
        return this.mConnectionId;
    }

    public int getValue() {
        return this.mValue;
    }

    public void setValue(int value) {
        this.mValue = value;
        this.mDirty = true;
    }

    public void setOverride(boolean override) {
        this.mOverride = override;
        this.mDirty = true;
    }

    public void setSubId(int subId) {
        this.mSubId = subId;
        this.mDirty = true;
    }

    public boolean isOverride() {
        return this.mOverride;
    }

    public int getSubId() {
        return this.mSubId;
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    public void processOverride(Context context) {
        Context context2 = context;
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        LocationManager lm = (LocationManager) context2.getSystemService("location");
        WifiManager wm = (WifiManager) context2.getSystemService("wifi");
        TelephonyManager tm = (TelephonyManager) context2.getSystemService("phone");
        NfcAdapter nfcAdapter = null;
        try {
            nfcAdapter = NfcAdapter.getNfcAdapter(context);
        } catch (UnsupportedOperationException e) {
        }
        boolean forcedState = getValue() == 1;
        switch (getConnectionId()) {
            case 0:
                if (forcedState != tm.getDataEnabled()) {
                    int phoneCount = tm.getPhoneCount();
                    for (int i = 0; i < phoneCount; i++) {
                        ContentResolver contentResolver = context.getContentResolver();
                        Settings.Global.putInt(contentResolver, "mobile_data" + i, forcedState ? 1 : 0);
                        tm.setDataEnabled(SubscriptionManager.getSubId(i)[0], forcedState);
                    }
                    return;
                }
                return;
            case 1:
                int wifiApState = wm.getWifiApState();
                if (wm.isWifiEnabled() != forcedState) {
                    if ((forcedState && wifiApState == 12) || wifiApState == 13) {
                        wm.setWifiApEnabled((WifiConfiguration) null, false);
                    }
                    wm.setWifiEnabled(forcedState);
                    return;
                }
                return;
            case 2:
                int wifiState = wm.getWifiState();
                if (wm.isWifiApEnabled() != forcedState) {
                    if ((forcedState && wifiState == 2) || wifiState == 3) {
                        wm.setWifiEnabled(false);
                    }
                    wm.setWifiApEnabled((WifiConfiguration) null, forcedState);
                    return;
                }
                return;
            case 3:
                if (WimaxHelper.isWimaxSupported(context) && WimaxHelper.isWimaxEnabled(context) != forcedState) {
                    WimaxHelper.setWimaxEnabled(context2, forcedState);
                    return;
                }
                return;
            case 4:
                if (lm.isProviderEnabled("gps") != forcedState) {
                    Settings.Secure.setLocationProviderEnabled(context.getContentResolver(), "gps", forcedState);
                    return;
                }
                return;
            case 5:
                if (forcedState != ContentResolver.getMasterSyncAutomatically()) {
                    ContentResolver.setMasterSyncAutomatically(forcedState);
                    return;
                }
                return;
            case 7:
                int btstate = bta.getState();
                if (forcedState && (btstate == 10 || btstate == 13)) {
                    bta.enable();
                    return;
                } else if (forcedState) {
                    return;
                } else {
                    if (btstate == 12 || btstate == 11) {
                        bta.disable();
                        return;
                    }
                    return;
                }
            case 8:
                if (nfcAdapter != null) {
                    int adapterState = nfcAdapter.getAdapterState();
                    if ((adapterState == 3 || adapterState == 2) == forcedState) {
                        return;
                    }
                    if (forcedState) {
                        nfcAdapter.enable();
                        return;
                    } else if (!forcedState && adapterState != 4) {
                        nfcAdapter.disable();
                        return;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            case 9:
                if (Build.CM_VERSION.SDK_INT >= 5) {
                    Intent intent = new Intent(ACTION_MODIFY_NETWORK_MODE);
                    intent.putExtra(EXTRA_NETWORK_MODE, getValue());
                    intent.putExtra(EXTRA_SUB_ID, getSubId());
                    context2.sendBroadcast(intent, "com.android.phone.CHANGE_NETWORK_MODE");
                    return;
                }
                Intent intent2 = new Intent(ACTION_MODIFY_NETWORK_MODE);
                int value = getValue();
                if (value == 0) {
                    intent2.putExtra(EXTRA_NETWORK_MODE, 1);
                } else if (value == 1) {
                    intent2.putExtra(EXTRA_NETWORK_MODE, 2);
                } else if (value == 2) {
                    intent2.putExtra(EXTRA_NETWORK_MODE, 11);
                } else if (value == 3) {
                    intent2.putExtra(EXTRA_NETWORK_MODE, 0);
                } else if (value == 4) {
                    intent2.putExtra(EXTRA_NETWORK_MODE, 9);
                } else {
                    return;
                }
                context2.sendBroadcast(intent2);
                return;
            default:
                return;
        }
    }

    public static ConnectionSettings fromXml(XmlPullParser xpp, Context context) throws XmlPullParserException, IOException {
        int event = xpp.next();
        ConnectionSettings connectionDescriptor = new ConnectionSettings(0);
        while (true) {
            if (event == 3 && xpp.getName().equals("connectionDescriptor")) {
                return connectionDescriptor;
            }
            if (event == 2) {
                String name = xpp.getName();
                if (name.equals("connectionId")) {
                    connectionDescriptor.mConnectionId = Integer.parseInt(xpp.nextText());
                } else if (name.equals("value")) {
                    connectionDescriptor.mValue = Integer.parseInt(xpp.nextText());
                } else if (name.equals("override")) {
                    connectionDescriptor.mOverride = Boolean.parseBoolean(xpp.nextText());
                } else if (name.equals(EXTRA_SUB_ID)) {
                    connectionDescriptor.mSubId = Integer.parseInt(xpp.nextText());
                }
            } else if (event == 1) {
                throw new IOException("Premature end of file while parsing connection settings");
            }
            event = xpp.next();
        }
    }

    public void getXmlString(StringBuilder builder, Context context) {
        builder.append("<connectionDescriptor>\n<connectionId>");
        builder.append(this.mConnectionId);
        builder.append("</connectionId>\n<value>");
        builder.append(this.mValue);
        builder.append("</value>\n<override>");
        builder.append(this.mOverride);
        builder.append("</override>\n");
        if (Build.CM_VERSION.SDK_INT >= 5 && this.mConnectionId == 9 && this.mSubId != -1) {
            builder.append("<subId>");
            builder.append(this.mSubId);
            builder.append("</subId>\n");
        }
        builder.append("</connectionDescriptor>\n");
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeInt(this.mConnectionId);
        dest.writeInt(this.mOverride ? 1 : 0);
        dest.writeInt(this.mValue);
        dest.writeInt(this.mDirty ? 1 : 0);
        if (this.mConnectionId == 9) {
            dest.writeInt(this.mSubId);
        }
        parcelInfo.complete();
    }

    public void readFromParcel(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        int parcelableVersion = parcelInfo.getParcelVersion();
        if (parcelableVersion >= 2) {
            this.mConnectionId = in.readInt();
            boolean z = true;
            this.mOverride = in.readInt() != 0;
            this.mValue = in.readInt();
            if (in.readInt() == 0) {
                z = false;
            }
            this.mDirty = z;
        }
        if (parcelableVersion >= 5 && this.mConnectionId == 9) {
            this.mSubId = in.readInt();
        }
        parcelInfo.complete();
    }
}
