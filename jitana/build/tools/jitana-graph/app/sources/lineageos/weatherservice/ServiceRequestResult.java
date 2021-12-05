package lineageos.weatherservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lineageos.p010os.Concierge;
import lineageos.weather.WeatherInfo;
import lineageos.weather.WeatherLocation;

public final class ServiceRequestResult implements Parcelable {
    public static final Parcelable.Creator<ServiceRequestResult> CREATOR = new Parcelable.Creator<ServiceRequestResult>() {
        public ServiceRequestResult createFromParcel(Parcel in) {
            return new ServiceRequestResult(in);
        }

        public ServiceRequestResult[] newArray(int size) {
            return new ServiceRequestResult[size];
        }
    };
    /* access modifiers changed from: private */
    public String mKey;
    /* access modifiers changed from: private */
    public List<WeatherLocation> mLocationLookupList;
    /* access modifiers changed from: private */
    public WeatherInfo mWeatherInfo;

    private ServiceRequestResult() {
    }

    private ServiceRequestResult(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        if (parcelInfo.getParcelVersion() >= 5) {
            this.mKey = in.readString();
            if (in.readInt() == 1) {
                this.mWeatherInfo = WeatherInfo.CREATOR.createFromParcel(in);
            }
            if (in.readInt() == 1) {
                this.mLocationLookupList = new ArrayList();
                for (int listSize = in.readInt(); listSize > 0; listSize--) {
                    this.mLocationLookupList.add(WeatherLocation.CREATOR.createFromParcel(in));
                }
            }
        }
        parcelInfo.complete();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeString(this.mKey);
        if (this.mWeatherInfo != null) {
            dest.writeInt(1);
            this.mWeatherInfo.writeToParcel(dest, 0);
        } else {
            dest.writeInt(0);
        }
        if (this.mLocationLookupList != null) {
            dest.writeInt(1);
            dest.writeInt(this.mLocationLookupList.size());
            for (WeatherLocation lookup : this.mLocationLookupList) {
                lookup.writeToParcel(dest, 0);
            }
        } else {
            dest.writeInt(0);
        }
        parcelInfo.complete();
    }

    public static class Builder {
        private List<WeatherLocation> mLocationLookupList;
        private WeatherInfo mWeatherInfo;

        public Builder() {
            this.mWeatherInfo = null;
            this.mLocationLookupList = null;
        }

        public Builder(WeatherInfo weatherInfo) {
            if (weatherInfo != null) {
                this.mWeatherInfo = weatherInfo;
                return;
            }
            throw new IllegalArgumentException("WeatherInfo can't be null");
        }

        public Builder(List<WeatherLocation> locations) {
            if (locations != null) {
                this.mLocationLookupList = locations;
                return;
            }
            throw new IllegalArgumentException("Weather location list can't be null");
        }

        public ServiceRequestResult build() {
            ServiceRequestResult result = new ServiceRequestResult();
            WeatherInfo unused = result.mWeatherInfo = this.mWeatherInfo;
            List unused2 = result.mLocationLookupList = this.mLocationLookupList;
            String unused3 = result.mKey = UUID.randomUUID().toString();
            return result;
        }
    }

    public WeatherInfo getWeatherInfo() {
        return this.mWeatherInfo;
    }

    public List<WeatherLocation> getLocationLookupList() {
        return new ArrayList(this.mLocationLookupList);
    }

    public int hashCode() {
        int i = 1 * 31;
        String str = this.mKey;
        return i + (str != null ? str.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            return TextUtils.equals(this.mKey, ((ServiceRequestResult) obj).mKey);
        }
        return false;
    }
}
