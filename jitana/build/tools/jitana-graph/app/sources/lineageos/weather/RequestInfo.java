package lineageos.weather;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.UUID;
import lineageos.p010os.Concierge;
import lineageos.weather.IRequestInfoListener;

public final class RequestInfo implements Parcelable {
    public static final Parcelable.Creator<RequestInfo> CREATOR = new Parcelable.Creator<RequestInfo>() {
        public RequestInfo createFromParcel(Parcel in) {
            return new RequestInfo(in);
        }

        public RequestInfo[] newArray(int size) {
            return new RequestInfo[size];
        }
    };
    public static final int TYPE_LOOKUP_CITY_NAME_REQ = 3;
    public static final int TYPE_WEATHER_BY_GEO_LOCATION_REQ = 1;
    public static final int TYPE_WEATHER_BY_WEATHER_LOCATION_REQ = 2;
    /* access modifiers changed from: private */
    public String mCityName;
    /* access modifiers changed from: private */
    public boolean mIsQueryOnly;
    /* access modifiers changed from: private */
    public String mKey;
    /* access modifiers changed from: private */
    public IRequestInfoListener mListener;
    /* access modifiers changed from: private */
    public Location mLocation;
    /* access modifiers changed from: private */
    public int mRequestType;
    /* access modifiers changed from: private */
    public int mTempUnit;
    /* access modifiers changed from: private */
    public WeatherLocation mWeatherLocation;

    private RequestInfo() {
    }

    static class Builder {
        private String mCityName;
        private boolean mIsQueryOnly = false;
        private IRequestInfoListener mListener;
        private Location mLocation;
        private int mRequestType;
        private int mTempUnit = 2;
        private WeatherLocation mWeatherLocation;

        public Builder(IRequestInfoListener listener) {
            this.mListener = listener;
        }

        public Builder setCityName(String cityName) {
            if (cityName != null) {
                this.mCityName = cityName;
                this.mRequestType = 3;
                this.mLocation = null;
                this.mWeatherLocation = null;
                return this;
            }
            throw new IllegalArgumentException("City name can't be null");
        }

        public Builder setLocation(Location location) {
            if (location != null) {
                this.mLocation = new Location(location);
                this.mCityName = null;
                this.mWeatherLocation = null;
                this.mRequestType = 1;
                return this;
            }
            throw new IllegalArgumentException("Location can't be null");
        }

        public Builder setWeatherLocation(WeatherLocation weatherLocation) {
            if (weatherLocation != null) {
                this.mWeatherLocation = weatherLocation;
                this.mLocation = null;
                this.mCityName = null;
                this.mRequestType = 2;
                return this;
            }
            throw new IllegalArgumentException("WeatherLocation can't be null");
        }

        public Builder setTemperatureUnit(int unit) {
            if (isValidTempUnit(unit)) {
                this.mTempUnit = unit;
                return this;
            }
            throw new IllegalArgumentException("Invalid temperature unit");
        }

        public Builder queryOnly() {
            int i = this.mRequestType;
            if (i == 1 || i == 2) {
                this.mIsQueryOnly = true;
            } else {
                this.mIsQueryOnly = false;
            }
            return this;
        }

        public RequestInfo build() {
            RequestInfo info = new RequestInfo();
            IRequestInfoListener unused = info.mListener = this.mListener;
            int unused2 = info.mRequestType = this.mRequestType;
            String unused3 = info.mCityName = this.mCityName;
            WeatherLocation unused4 = info.mWeatherLocation = this.mWeatherLocation;
            Location unused5 = info.mLocation = this.mLocation;
            int unused6 = info.mTempUnit = this.mTempUnit;
            boolean unused7 = info.mIsQueryOnly = this.mIsQueryOnly;
            String unused8 = info.mKey = UUID.randomUUID().toString();
            return info;
        }

        private boolean isValidTempUnit(int unit) {
            if (unit == 1 || unit == 2) {
                return true;
            }
            return false;
        }
    }

    private RequestInfo(Parcel parcel) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(parcel);
        if (parcelInfo.getParcelVersion() >= 5) {
            this.mKey = parcel.readString();
            this.mRequestType = parcel.readInt();
            int i = this.mRequestType;
            boolean z = true;
            if (i == 1) {
                this.mLocation = (Location) Location.CREATOR.createFromParcel(parcel);
                this.mTempUnit = parcel.readInt();
            } else if (i == 2) {
                this.mWeatherLocation = WeatherLocation.CREATOR.createFromParcel(parcel);
                this.mTempUnit = parcel.readInt();
            } else if (i == 3) {
                this.mCityName = parcel.readString();
            }
            this.mIsQueryOnly = parcel.readInt() != 1 ? false : z;
            this.mListener = IRequestInfoListener.Stub.asInterface(parcel.readStrongBinder());
        }
        parcelInfo.complete();
    }

    public int getRequestType() {
        return this.mRequestType;
    }

    public Location getLocation() {
        return new Location(this.mLocation);
    }

    public WeatherLocation getWeatherLocation() {
        return this.mWeatherLocation;
    }

    public IRequestInfoListener getRequestListener() {
        return this.mListener;
    }

    public String getCityName() {
        return this.mCityName;
    }

    public int getTemperatureUnit() {
        int i = this.mRequestType;
        if (i == 1 || i == 2) {
            return this.mTempUnit;
        }
        return -1;
    }

    public boolean isQueryOnlyWeatherRequest() {
        int i = this.mRequestType;
        if (i == 1 || i == 2) {
            return this.mIsQueryOnly;
        }
        return false;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeString(this.mKey);
        dest.writeInt(this.mRequestType);
        int i = this.mRequestType;
        int i2 = 0;
        if (i == 1) {
            this.mLocation.writeToParcel(dest, 0);
            dest.writeInt(this.mTempUnit);
        } else if (i == 2) {
            this.mWeatherLocation.writeToParcel(dest, 0);
            dest.writeInt(this.mTempUnit);
        } else if (i == 3) {
            dest.writeString(this.mCityName);
        }
        if (this.mIsQueryOnly) {
            i2 = 1;
        }
        dest.writeInt(i2);
        dest.writeStrongBinder(this.mListener.asBinder());
        parcelInfo.complete();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ Request for ");
        int i = this.mRequestType;
        if (i == 1) {
            builder.append("Location: ");
            builder.append(this.mLocation);
            builder.append(" Temp Unit: ");
            if (this.mTempUnit == 2) {
                builder.append("Fahrenheit");
            } else {
                builder.append(" Celsius");
            }
        } else if (i == 2) {
            builder.append("WeatherLocation: ");
            builder.append(this.mWeatherLocation);
            builder.append(" Temp Unit: ");
            if (this.mTempUnit == 2) {
                builder.append("Fahrenheit");
            } else {
                builder.append(" Celsius");
            }
        } else if (i == 3) {
            builder.append("Lookup City: ");
            builder.append(this.mCityName);
        }
        builder.append(" }");
        return builder.toString();
    }

    public int hashCode() {
        int i = 1 * 31;
        String str = this.mKey;
        return i + (str != null ? str.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            return TextUtils.equals(this.mKey, ((RequestInfo) obj).mKey);
        }
        return false;
    }
}
