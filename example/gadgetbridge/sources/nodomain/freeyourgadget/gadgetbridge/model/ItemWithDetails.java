package nodomain.freeyourgadget.gadgetbridge.model;

import android.os.Parcelable;

public interface ItemWithDetails extends Parcelable, Comparable<ItemWithDetails> {
    boolean equals(Object obj);

    String getDetails();

    int getIcon();

    String getName();
}
