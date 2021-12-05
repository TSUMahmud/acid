package lineageos.weather;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.UUID;
import lineageos.p010os.Concierge;

public final class WeatherLocation implements Parcelable {
    public static final Parcelable.Creator<WeatherLocation> CREATOR = new Parcelable.Creator<WeatherLocation>() {
        public WeatherLocation createFromParcel(Parcel in) {
            return new WeatherLocation(in);
        }

        public WeatherLocation[] newArray(int size) {
            return new WeatherLocation[size];
        }
    };
    /* access modifiers changed from: private */
    public String mCity;
    /* access modifiers changed from: private */
    public String mCityId;
    /* access modifiers changed from: private */
    public String mCountry;
    /* access modifiers changed from: private */
    public String mCountryId;
    /* access modifiers changed from: private */
    public String mKey;
    /* access modifiers changed from: private */
    public String mPostal;
    /* access modifiers changed from: private */
    public String mState;

    private WeatherLocation() {
    }

    public static class Builder {
        String mCity = "";
        String mCityId = "";
        String mCountry = "";
        String mCountryId = "";
        String mPostal = "";
        String mState = "";

        public Builder(String cityId, String cityName) {
            if (cityId == null || cityName == null) {
                throw new IllegalArgumentException("Illegal to set city id AND city to null");
            }
            this.mCityId = cityId;
            this.mCity = cityName;
        }

        public Builder(String cityName) {
            if (cityName != null) {
                this.mCity = cityName;
                return;
            }
            throw new IllegalArgumentException("City name can't be null");
        }

        public Builder setCountryId(String countryId) {
            if (countryId != null) {
                this.mCountryId = countryId;
                return this;
            }
            throw new IllegalArgumentException("Country ID can't be null");
        }

        public Builder setCountry(String country) {
            if (country != null) {
                this.mCountry = country;
                return this;
            }
            throw new IllegalArgumentException("Country can't be null");
        }

        public Builder setPostalCode(String postalCode) {
            if (postalCode != null) {
                this.mPostal = postalCode;
                return this;
            }
            throw new IllegalArgumentException("Postal code/ZIP can't be null");
        }

        public Builder setState(String state) {
            if (state != null) {
                this.mState = state;
                return this;
            }
            throw new IllegalArgumentException("State can't be null");
        }

        public WeatherLocation build() {
            WeatherLocation weatherLocation = new WeatherLocation();
            String unused = weatherLocation.mCityId = this.mCityId;
            String unused2 = weatherLocation.mCity = this.mCity;
            String unused3 = weatherLocation.mState = this.mState;
            String unused4 = weatherLocation.mPostal = this.mPostal;
            String unused5 = weatherLocation.mCountryId = this.mCountryId;
            String unused6 = weatherLocation.mCountry = this.mCountry;
            String unused7 = weatherLocation.mKey = UUID.randomUUID().toString();
            return weatherLocation;
        }
    }

    public String getCityId() {
        return this.mCityId;
    }

    public String getCity() {
        return this.mCity;
    }

    public String getState() {
        return this.mState;
    }

    public String getPostalCode() {
        return this.mPostal;
    }

    public String getCountryId() {
        return this.mCountryId;
    }

    public String getCountry() {
        return this.mCountry;
    }

    private WeatherLocation(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        if (parcelInfo.getParcelVersion() >= 5) {
            this.mKey = in.readString();
            this.mCityId = in.readString();
            this.mCity = in.readString();
            this.mState = in.readString();
            this.mPostal = in.readString();
            this.mCountryId = in.readString();
            this.mCountry = in.readString();
        }
        parcelInfo.complete();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeString(this.mKey);
        dest.writeString(this.mCityId);
        dest.writeString(this.mCity);
        dest.writeString(this.mState);
        dest.writeString(this.mPostal);
        dest.writeString(this.mCountryId);
        dest.writeString(this.mCountry);
        parcelInfo.complete();
    }

    public String toString() {
        return "{ City ID: " + this.mCityId + " City: " + this.mCity + " State: " + this.mState + " Postal/ZIP Code: " + this.mPostal + " Country Id: " + this.mCountryId + " Country: " + this.mCountry + "}";
    }

    public int hashCode() {
        int i = 1 * 31;
        String str = this.mKey;
        return i + (str != null ? str.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            return TextUtils.equals(this.mKey, ((WeatherLocation) obj).mKey);
        }
        return false;
    }
}
