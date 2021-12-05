package nodomain.freeyourgadget.gadgetbridge.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class WeatherSpec implements Parcelable {
    public static final Parcelable.Creator<WeatherSpec> CREATOR = new Parcelable.Creator<WeatherSpec>() {
        public WeatherSpec createFromParcel(Parcel in) {
            return new WeatherSpec(in);
        }

        public WeatherSpec[] newArray(int size) {
            return new WeatherSpec[size];
        }
    };
    public String currentCondition;
    public int currentConditionCode = 3200;
    public int currentHumidity;
    public int currentTemp;
    public ArrayList<Forecast> forecasts = new ArrayList<>();
    public String location;
    public int timestamp;
    public int todayMaxTemp;
    public int todayMinTemp;
    public int windDirection;
    public float windSpeed;

    public WeatherSpec() {
    }

    protected WeatherSpec(Parcel in) {
        this.timestamp = in.readInt();
        this.location = in.readString();
        this.currentTemp = in.readInt();
        this.currentConditionCode = in.readInt();
        this.currentCondition = in.readString();
        this.currentHumidity = in.readInt();
        this.todayMaxTemp = in.readInt();
        this.todayMinTemp = in.readInt();
        in.readList(this.forecasts, Forecast.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.timestamp);
        dest.writeString(this.location);
        dest.writeInt(this.currentTemp);
        dest.writeInt(this.currentConditionCode);
        dest.writeString(this.currentCondition);
        dest.writeInt(this.currentHumidity);
        dest.writeInt(this.todayMaxTemp);
        dest.writeInt(this.todayMinTemp);
        dest.writeList(this.forecasts);
    }

    public static class Forecast implements Parcelable {
        public static final Parcelable.Creator<Forecast> CREATOR = new Parcelable.Creator<Forecast>() {
            public Forecast createFromParcel(Parcel in) {
                return new Forecast(in);
            }

            public Forecast[] newArray(int size) {
                return new Forecast[size];
            }
        };
        public int conditionCode;
        public int humidity;
        public int maxTemp;
        public int minTemp;

        public Forecast() {
        }

        public Forecast(int minTemp2, int maxTemp2, int conditionCode2, int humidity2) {
            this.minTemp = minTemp2;
            this.maxTemp = maxTemp2;
            this.conditionCode = conditionCode2;
            this.humidity = humidity2;
        }

        Forecast(Parcel in) {
            this.minTemp = in.readInt();
            this.maxTemp = in.readInt();
            this.conditionCode = in.readInt();
            this.humidity = in.readInt();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.minTemp);
            dest.writeInt(this.maxTemp);
            dest.writeInt(this.conditionCode);
            dest.writeInt(this.humidity);
        }
    }
}
