package cyanogenmod.weather;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import cyanogenmod.p007os.Concierge;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class WeatherInfo implements Parcelable {
    public static final Parcelable.Creator<WeatherInfo> CREATOR = new Parcelable.Creator<WeatherInfo>() {
        public WeatherInfo createFromParcel(Parcel source) {
            return new WeatherInfo(source);
        }

        public WeatherInfo[] newArray(int size) {
            return new WeatherInfo[size];
        }
    };
    /* access modifiers changed from: private */
    public String mCity;
    /* access modifiers changed from: private */
    public int mConditionCode;
    /* access modifiers changed from: private */
    public List<DayForecast> mForecastList;
    /* access modifiers changed from: private */
    public double mHumidity;
    /* access modifiers changed from: private */
    public String mKey;
    /* access modifiers changed from: private */
    public int mTempUnit;
    /* access modifiers changed from: private */
    public double mTemperature;
    /* access modifiers changed from: private */
    public long mTimestamp;
    /* access modifiers changed from: private */
    public double mTodaysHighTemp;
    /* access modifiers changed from: private */
    public double mTodaysLowTemp;
    /* access modifiers changed from: private */
    public double mWindDirection;
    /* access modifiers changed from: private */
    public double mWindSpeed;
    /* access modifiers changed from: private */
    public int mWindSpeedUnit;

    private WeatherInfo() {
    }

    public static class Builder {
        private String mCity;
        private int mConditionCode = 3200;
        private List<DayForecast> mForecastList = new ArrayList(0);
        private double mHumidity = Double.NaN;
        private int mTempUnit;
        private double mTemperature;
        private long mTimestamp = -1;
        private double mTodaysHighTemp = Double.NaN;
        private double mTodaysLowTemp = Double.NaN;
        private double mWindDirection = Double.NaN;
        private double mWindSpeed = Double.NaN;
        private int mWindSpeedUnit = 2;

        public Builder(String cityName, double temperature, int tempUnit) {
            if (cityName == null) {
                throw new IllegalArgumentException("City name can't be null");
            } else if (Double.isNaN(temperature)) {
                throw new IllegalArgumentException("Invalid temperature");
            } else if (isValidTempUnit(tempUnit)) {
                this.mCity = cityName;
                this.mTemperature = temperature;
                this.mTempUnit = tempUnit;
            } else {
                throw new IllegalArgumentException("Invalid temperature unit");
            }
        }

        public Builder setTimestamp(long timeStamp) {
            this.mTimestamp = timeStamp;
            return this;
        }

        public Builder setHumidity(double humidity) {
            if (!Double.isNaN(humidity)) {
                this.mHumidity = humidity;
                return this;
            }
            throw new IllegalArgumentException("Invalid humidity value");
        }

        public Builder setWind(double windSpeed, double windDirection, int windSpeedUnit) {
            if (Double.isNaN(windSpeed)) {
                throw new IllegalArgumentException("Invalid wind speed value");
            } else if (Double.isNaN(windDirection)) {
                throw new IllegalArgumentException("Invalid wind direction value");
            } else if (isValidWindSpeedUnit(windSpeedUnit)) {
                this.mWindSpeed = windSpeed;
                this.mWindSpeedUnit = windSpeedUnit;
                this.mWindDirection = windDirection;
                return this;
            } else {
                throw new IllegalArgumentException("Invalid speed unit");
            }
        }

        public Builder setWeatherCondition(int conditionCode) {
            if (WeatherInfo.isValidWeatherCode(conditionCode)) {
                this.mConditionCode = conditionCode;
                return this;
            }
            throw new IllegalArgumentException("Invalid weather condition code");
        }

        public Builder setForecast(List<DayForecast> forecasts) {
            if (forecasts != null) {
                this.mForecastList = forecasts;
                return this;
            }
            throw new IllegalArgumentException("Forecast list can't be null");
        }

        public Builder setTodaysHigh(double todaysHigh) {
            if (!Double.isNaN(todaysHigh)) {
                this.mTodaysHighTemp = todaysHigh;
                return this;
            }
            throw new IllegalArgumentException("Invalid temperature value");
        }

        public Builder setTodaysLow(double todaysLow) {
            if (!Double.isNaN(todaysLow)) {
                this.mTodaysLowTemp = todaysLow;
                return this;
            }
            throw new IllegalArgumentException("Invalid temperature value");
        }

        public WeatherInfo build() {
            WeatherInfo info = new WeatherInfo();
            String unused = info.mCity = this.mCity;
            int unused2 = info.mConditionCode = this.mConditionCode;
            double unused3 = info.mTemperature = this.mTemperature;
            int unused4 = info.mTempUnit = this.mTempUnit;
            double unused5 = info.mHumidity = this.mHumidity;
            double unused6 = info.mWindSpeed = this.mWindSpeed;
            double unused7 = info.mWindDirection = this.mWindDirection;
            int unused8 = info.mWindSpeedUnit = this.mWindSpeedUnit;
            long j = this.mTimestamp;
            if (j == -1) {
                j = System.currentTimeMillis();
            }
            long unused9 = info.mTimestamp = j;
            List unused10 = info.mForecastList = this.mForecastList;
            double unused11 = info.mTodaysHighTemp = this.mTodaysHighTemp;
            double unused12 = info.mTodaysLowTemp = this.mTodaysLowTemp;
            String unused13 = info.mKey = UUID.randomUUID().toString();
            return info;
        }

        private boolean isValidTempUnit(int unit) {
            if (unit == 1 || unit == 2) {
                return true;
            }
            return false;
        }

        private boolean isValidWindSpeedUnit(int unit) {
            if (unit == 1 || unit == 2) {
                return true;
            }
            return false;
        }
    }

    /* access modifiers changed from: private */
    public static boolean isValidWeatherCode(int code) {
        if ((code < 0 || code > 44) && code != 3200) {
            return false;
        }
        return true;
    }

    public String getCity() {
        return this.mCity;
    }

    public int getConditionCode() {
        return this.mConditionCode;
    }

    public double getHumidity() {
        return this.mHumidity;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public double getWindDirection() {
        return this.mWindDirection;
    }

    public double getWindSpeed() {
        return this.mWindSpeed;
    }

    public int getWindSpeedUnit() {
        return this.mWindSpeedUnit;
    }

    public double getTemperature() {
        return this.mTemperature;
    }

    public int getTemperatureUnit() {
        return this.mTempUnit;
    }

    public double getTodaysHigh() {
        return this.mTodaysHighTemp;
    }

    public double getTodaysLow() {
        return this.mTodaysLowTemp;
    }

    public List<DayForecast> getForecasts() {
        return new ArrayList(this.mForecastList);
    }

    private WeatherInfo(Parcel parcel) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(parcel);
        if (parcelInfo.getParcelVersion() >= 5) {
            this.mKey = parcel.readString();
            this.mCity = parcel.readString();
            this.mConditionCode = parcel.readInt();
            this.mTemperature = parcel.readDouble();
            this.mTempUnit = parcel.readInt();
            this.mHumidity = parcel.readDouble();
            this.mWindSpeed = parcel.readDouble();
            this.mWindDirection = parcel.readDouble();
            this.mWindSpeedUnit = parcel.readInt();
            this.mTodaysHighTemp = parcel.readDouble();
            this.mTodaysLowTemp = parcel.readDouble();
            this.mTimestamp = parcel.readLong();
            this.mForecastList = new ArrayList();
            for (int forecastListSize = parcel.readInt(); forecastListSize > 0; forecastListSize--) {
                this.mForecastList.add(DayForecast.CREATOR.createFromParcel(parcel));
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
        dest.writeString(this.mCity);
        dest.writeInt(this.mConditionCode);
        dest.writeDouble(this.mTemperature);
        dest.writeInt(this.mTempUnit);
        dest.writeDouble(this.mHumidity);
        dest.writeDouble(this.mWindSpeed);
        dest.writeDouble(this.mWindDirection);
        dest.writeInt(this.mWindSpeedUnit);
        dest.writeDouble(this.mTodaysHighTemp);
        dest.writeDouble(this.mTodaysLowTemp);
        dest.writeLong(this.mTimestamp);
        dest.writeInt(this.mForecastList.size());
        for (DayForecast dayForecast : this.mForecastList) {
            dayForecast.writeToParcel(dest, 0);
        }
        parcelInfo.complete();
    }

    public static class DayForecast implements Parcelable {
        public static final Parcelable.Creator<DayForecast> CREATOR = new Parcelable.Creator<DayForecast>() {
            public DayForecast createFromParcel(Parcel source) {
                return new DayForecast(source);
            }

            public DayForecast[] newArray(int size) {
                return new DayForecast[size];
            }
        };
        int mConditionCode;
        double mHigh;
        String mKey;
        double mLow;

        private DayForecast() {
        }

        public static class Builder {
            int mConditionCode;
            double mHigh = Double.NaN;
            double mLow = Double.NaN;

            public Builder(int conditionCode) {
                if (WeatherInfo.isValidWeatherCode(conditionCode)) {
                    this.mConditionCode = conditionCode;
                    return;
                }
                throw new IllegalArgumentException("Invalid weather condition code");
            }

            public Builder setHigh(double high) {
                if (!Double.isNaN(high)) {
                    this.mHigh = high;
                    return this;
                }
                throw new IllegalArgumentException("Invalid high forecast temperature");
            }

            public Builder setLow(double low) {
                if (!Double.isNaN(low)) {
                    this.mLow = low;
                    return this;
                }
                throw new IllegalArgumentException("Invalid low forecast temperature");
            }

            public DayForecast build() {
                DayForecast forecast = new DayForecast();
                forecast.mLow = this.mLow;
                forecast.mHigh = this.mHigh;
                forecast.mConditionCode = this.mConditionCode;
                forecast.mKey = UUID.randomUUID().toString();
                return forecast;
            }
        }

        public double getLow() {
            return this.mLow;
        }

        public double getHigh() {
            return this.mHigh;
        }

        public int getConditionCode() {
            return this.mConditionCode;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
            dest.writeString(this.mKey);
            dest.writeDouble(this.mLow);
            dest.writeDouble(this.mHigh);
            dest.writeInt(this.mConditionCode);
            parcelInfo.complete();
        }

        private DayForecast(Parcel parcel) {
            Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(parcel);
            if (parcelInfo.getParcelVersion() >= 5) {
                this.mKey = parcel.readString();
                this.mLow = parcel.readDouble();
                this.mHigh = parcel.readDouble();
                this.mConditionCode = parcel.readInt();
            }
            parcelInfo.complete();
        }

        public String toString() {
            return "{Low temp: " + this.mLow + " High temp: " + this.mHigh + " Condition code: " + this.mConditionCode + "}";
        }

        public int hashCode() {
            int i = 1 * 31;
            String str = this.mKey;
            return i + (str != null ? str.hashCode() : 0);
        }

        public boolean equals(Object obj) {
            if (obj != null && getClass() == obj.getClass()) {
                return TextUtils.equals(this.mKey, ((DayForecast) obj).mKey);
            }
            return false;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" City Name: ");
        sb.append(this.mCity);
        sb.append(" Condition Code: ");
        sb.append(this.mConditionCode);
        sb.append(" Temperature: ");
        sb.append(this.mTemperature);
        sb.append(" Temperature Unit: ");
        sb.append(this.mTempUnit);
        sb.append(" Humidity: ");
        sb.append(this.mHumidity);
        sb.append(" Wind speed: ");
        sb.append(this.mWindSpeed);
        sb.append(" Wind direction: ");
        sb.append(this.mWindDirection);
        sb.append(" Wind Speed Unit: ");
        sb.append(this.mWindSpeedUnit);
        sb.append(" Today's high temp: ");
        sb.append(this.mTodaysHighTemp);
        sb.append(" Today's low temp: ");
        sb.append(this.mTodaysLowTemp);
        sb.append(" Timestamp: ");
        sb.append(this.mTimestamp);
        StringBuilder builder = sb.append(" Forecasts: [");
        for (DayForecast dayForecast : this.mForecastList) {
            builder.append(dayForecast.toString());
        }
        builder.append("]}");
        return builder.toString();
    }

    public int hashCode() {
        int i = 1 * 31;
        String str = this.mKey;
        return i + (str != null ? str.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            return TextUtils.equals(this.mKey, ((WeatherInfo) obj).mKey);
        }
        return false;
    }
}
