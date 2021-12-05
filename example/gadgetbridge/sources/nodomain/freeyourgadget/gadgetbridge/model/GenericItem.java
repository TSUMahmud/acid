package nodomain.freeyourgadget.gadgetbridge.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.Collator;

public class GenericItem implements ItemWithDetails {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<GenericItem>() {
        public GenericItem createFromParcel(Parcel source) {
            GenericItem item = new GenericItem();
            item.setName(source.readString());
            item.setDetails(source.readString());
            item.setIcon(source.readInt());
            return item;
        }

        public GenericItem[] newArray(int size) {
            return new GenericItem[size];
        }
    };
    private String details;
    private int icon;
    private String name;

    public GenericItem(String name2, String details2) {
        this.name = name2;
        this.details = details2;
    }

    public GenericItem(String name2) {
        this.name = name2;
    }

    public GenericItem() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeString(getDetails());
        dest.writeInt(getIcon());
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public void setDetails(String details2) {
        this.details = details2;
    }

    public void setIcon(int icon2) {
        this.icon = icon2;
    }

    public String getName() {
        return this.name;
    }

    public String getDetails() {
        return this.details;
    }

    public int getIcon() {
        return this.icon;
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GenericItem that = (GenericItem) o;
        if (getName() != null) {
            if (!getName().equals(that.getName())) {
                return false;
            }
            return true;
        } else if (that.getName() == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (getName() != null) {
            return getName().hashCode();
        }
        return 0;
    }

    public int compareTo(ItemWithDetails another) {
        if (getName() == another.getName()) {
            return 0;
        }
        if (getName() == null) {
            return 1;
        }
        if (another.getName() == null) {
            return -1;
        }
        return Collator.getInstance().compare(getName(), another.getName());
    }
}
