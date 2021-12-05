package cyanogenmod.app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import cyanogenmod.p007os.Concierge;
import java.util.ArrayList;

public class CustomTile implements Parcelable {
    public static final Parcelable.Creator<CustomTile> CREATOR = new Parcelable.Creator<CustomTile>() {
        public CustomTile createFromParcel(Parcel in) {
            return new CustomTile(in);
        }

        public CustomTile[] newArray(int size) {
            return new CustomTile[size];
        }
    };
    public static final int PSEUDO_GRID_ITEM_MAX_COUNT = 9;
    public boolean collapsePanel;
    public String contentDescription;
    public PendingIntent deleteIntent;
    public ExpandedStyle expandedStyle;
    public int icon;
    public String label;
    public PendingIntent onClick;
    public Uri onClickUri;
    public PendingIntent onLongClick;
    public Intent onSettingsClick;
    public Bitmap remoteIcon;
    /* access modifiers changed from: private */
    public String resourcesPackageName;
    public boolean sensitiveData;

    public CustomTile(Parcel parcel) {
        this.resourcesPackageName = "";
        boolean z = true;
        this.collapsePanel = true;
        this.sensitiveData = false;
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(parcel);
        int parcelableVersion = parcelInfo.getParcelVersion();
        if (parcelableVersion >= 1) {
            if (parcel.readInt() != 0) {
                this.onClick = (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel);
            }
            if (parcel.readInt() != 0) {
                this.onSettingsClick = (Intent) Intent.CREATOR.createFromParcel(parcel);
            }
            if (parcel.readInt() != 0) {
                this.onClickUri = (Uri) Uri.CREATOR.createFromParcel(parcel);
            }
            if (parcel.readInt() != 0) {
                this.label = parcel.readString();
            }
            if (parcel.readInt() != 0) {
                this.contentDescription = parcel.readString();
            }
            if (parcel.readInt() != 0) {
                this.expandedStyle = ExpandedStyle.CREATOR.createFromParcel(parcel);
            }
            this.icon = parcel.readInt();
        }
        if (parcelableVersion >= 2) {
            this.resourcesPackageName = parcel.readString();
            this.collapsePanel = parcel.readInt() == 1;
            if (parcel.readInt() != 0) {
                this.remoteIcon = (Bitmap) Bitmap.CREATOR.createFromParcel(parcel);
            }
            if (parcel.readInt() != 0) {
                this.deleteIntent = (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel);
            }
            this.sensitiveData = parcel.readInt() != 1 ? false : z;
        }
        if (parcelableVersion >= 4 && parcel.readInt() != 0) {
            this.onLongClick = (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel);
        }
        parcelInfo.complete();
    }

    public CustomTile() {
        this.resourcesPackageName = "";
        this.collapsePanel = true;
        this.sensitiveData = false;
    }

    public String getResourcesPackageName() {
        return this.resourcesPackageName;
    }

    public CustomTile clone() {
        CustomTile that = new CustomTile();
        cloneInto(that);
        return that;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        if (this.onClickUri != null) {
            b.append("onClickUri=" + this.onClickUri.toString() + NEW_LINE);
        }
        if (this.onClick != null) {
            b.append("onClick=" + this.onClick.toString() + NEW_LINE);
        }
        if (this.onLongClick != null) {
            b.append("onLongClick=" + this.onLongClick.toString() + NEW_LINE);
        }
        if (this.onSettingsClick != null) {
            b.append("onSettingsClick=" + this.onSettingsClick.toString() + NEW_LINE);
        }
        if (!TextUtils.isEmpty(this.label)) {
            b.append("label=" + this.label + NEW_LINE);
        }
        if (!TextUtils.isEmpty(this.contentDescription)) {
            b.append("contentDescription=" + this.contentDescription + NEW_LINE);
        }
        if (this.expandedStyle != null) {
            b.append("expandedStyle=" + this.expandedStyle + NEW_LINE);
        }
        b.append("icon=" + this.icon + NEW_LINE);
        b.append("resourcesPackageName=" + this.resourcesPackageName + NEW_LINE);
        b.append("collapsePanel=" + this.collapsePanel + NEW_LINE);
        if (this.remoteIcon != null) {
            b.append("remoteIcon=" + this.remoteIcon.getGenerationId() + NEW_LINE);
        }
        if (this.deleteIntent != null) {
            b.append("deleteIntent=" + this.deleteIntent.toString() + NEW_LINE);
        }
        b.append("sensitiveData=" + this.sensitiveData + NEW_LINE);
        return b.toString();
    }

    public void cloneInto(CustomTile that) {
        that.resourcesPackageName = this.resourcesPackageName;
        that.onClick = this.onClick;
        that.onLongClick = this.onLongClick;
        that.onSettingsClick = this.onSettingsClick;
        that.onClickUri = this.onClickUri;
        that.label = this.label;
        that.contentDescription = this.contentDescription;
        that.expandedStyle = this.expandedStyle;
        that.icon = this.icon;
        that.collapsePanel = this.collapsePanel;
        that.remoteIcon = this.remoteIcon;
        that.deleteIntent = this.deleteIntent;
        that.sensitiveData = this.sensitiveData;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(out);
        if (this.onClick != null) {
            out.writeInt(1);
            this.onClick.writeToParcel(out, 0);
        } else {
            out.writeInt(0);
        }
        if (this.onSettingsClick != null) {
            out.writeInt(1);
            this.onSettingsClick.writeToParcel(out, 0);
        } else {
            out.writeInt(0);
        }
        if (this.onClickUri != null) {
            out.writeInt(1);
            this.onClickUri.writeToParcel(out, 0);
        } else {
            out.writeInt(0);
        }
        if (this.label != null) {
            out.writeInt(1);
            out.writeString(this.label);
        } else {
            out.writeInt(0);
        }
        if (this.contentDescription != null) {
            out.writeInt(1);
            out.writeString(this.contentDescription);
        } else {
            out.writeInt(0);
        }
        if (this.expandedStyle != null) {
            out.writeInt(1);
            this.expandedStyle.writeToParcel(out, 0);
        } else {
            out.writeInt(0);
        }
        out.writeInt(this.icon);
        out.writeString(this.resourcesPackageName);
        out.writeInt(this.collapsePanel ? 1 : 0);
        if (this.remoteIcon != null) {
            out.writeInt(1);
            this.remoteIcon.writeToParcel(out, 0);
        } else {
            out.writeInt(0);
        }
        if (this.deleteIntent != null) {
            out.writeInt(1);
            this.deleteIntent.writeToParcel(out, 0);
        } else {
            out.writeInt(0);
        }
        out.writeInt(this.sensitiveData ? 1 : 0);
        if (this.onLongClick != null) {
            out.writeInt(1);
            this.onLongClick.writeToParcel(out, 0);
        } else {
            out.writeInt(0);
        }
        parcelInfo.complete();
    }

    public static class ExpandedStyle implements Parcelable {
        public static final Parcelable.Creator<ExpandedStyle> CREATOR = new Parcelable.Creator<ExpandedStyle>() {
            public ExpandedStyle createFromParcel(Parcel in) {
                return new ExpandedStyle(in);
            }

            public ExpandedStyle[] newArray(int size) {
                return new ExpandedStyle[size];
            }
        };
        public static final int GRID_STYLE = 0;
        public static final int LIST_STYLE = 1;
        public static final int NO_STYLE = -1;
        public static final int REMOTE_STYLE = 2;
        private RemoteViews contentViews;
        private ExpandedItem[] expandedItems;
        private int styleId;

        private ExpandedStyle() {
            this.styleId = -1;
        }

        private ExpandedStyle(Parcel parcel) {
            Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(parcel);
            int parcelableVersion = parcelInfo.getParcelVersion();
            if (parcelableVersion >= 1) {
                if (parcel.readInt() != 0) {
                    this.expandedItems = (ExpandedItem[]) parcel.createTypedArray(ExpandedItem.CREATOR);
                }
                this.styleId = parcel.readInt();
            }
            if (parcelableVersion >= 2 && parcel.readInt() != 0) {
                this.contentViews = (RemoteViews) RemoteViews.CREATOR.createFromParcel(parcel);
            }
            parcelInfo.complete();
        }

        public void setBuilder(Builder builder) {
            if (builder != null) {
                builder.setExpandedStyle(this);
            }
        }

        /* access modifiers changed from: protected */
        public void internalSetExpandedItems(ArrayList<? extends ExpandedItem> items) {
            if (this.styleId == 0 && items.size() > 9) {
                Log.w(CustomTile.class.getName(), "Attempted to publish greater than max grid item count");
            }
            this.expandedItems = new ExpandedItem[items.size()];
            items.toArray(this.expandedItems);
        }

        /* access modifiers changed from: protected */
        public void internalSetRemoteViews(RemoteViews remoteViews) {
            this.contentViews = remoteViews;
        }

        /* access modifiers changed from: protected */
        public void internalStyleId(int id) {
            this.styleId = id;
        }

        public ExpandedItem[] getExpandedItems() {
            return this.expandedItems;
        }

        public RemoteViews getContentViews() {
            return this.contentViews;
        }

        public int getStyle() {
            return this.styleId;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(parcel);
            if (this.expandedItems != null) {
                parcel.writeInt(1);
                parcel.writeTypedArray(this.expandedItems, 0);
            } else {
                parcel.writeInt(0);
            }
            parcel.writeInt(this.styleId);
            if (this.contentViews != null) {
                parcel.writeInt(1);
                this.contentViews.writeToParcel(parcel, 0);
            } else {
                parcel.writeInt(0);
            }
            parcelInfo.complete();
        }

        public String toString() {
            StringBuilder b = new StringBuilder();
            String NEW_LINE = System.getProperty("line.separator");
            if (this.expandedItems != null) {
                b.append("expandedItems= " + NEW_LINE);
                for (ExpandedItem item : this.expandedItems) {
                    b.append("     item=" + item.toString() + NEW_LINE);
                }
            }
            b.append("styleId=" + this.styleId + NEW_LINE);
            return b.toString();
        }
    }

    public static class GridExpandedStyle extends ExpandedStyle {
        public GridExpandedStyle() {
            super();
            internalStyleId(0);
        }

        public void setGridItems(ArrayList<ExpandedGridItem> expandedGridItems) {
            internalSetExpandedItems(expandedGridItems);
        }
    }

    public static class ListExpandedStyle extends ExpandedStyle {
        public ListExpandedStyle() {
            super();
            internalStyleId(1);
        }

        public void setListItems(ArrayList<ExpandedListItem> expandedListItems) {
            internalSetExpandedItems(expandedListItems);
        }
    }

    public static class RemoteExpandedStyle extends ExpandedStyle {
        public RemoteExpandedStyle() {
            super();
            internalStyleId(2);
        }

        public void setRemoteViews(RemoteViews remoteViews) {
            internalSetRemoteViews(remoteViews);
        }
    }

    public static class ExpandedItem implements Parcelable {
        public static final Parcelable.Creator<ExpandedItem> CREATOR = new Parcelable.Creator<ExpandedItem>() {
            public ExpandedItem createFromParcel(Parcel in) {
                return new ExpandedItem(in);
            }

            public ExpandedItem[] newArray(int size) {
                return new ExpandedItem[size];
            }
        };
        public Bitmap itemBitmapResource;
        public int itemDrawableResourceId;
        public String itemSummary;
        public String itemTitle;
        public PendingIntent onClickPendingIntent;

        private ExpandedItem() {
            this.itemSummary = null;
        }

        /* access modifiers changed from: protected */
        public void internalSetItemDrawable(int resourceId) {
            this.itemDrawableResourceId = resourceId;
        }

        /* access modifiers changed from: protected */
        public void internalSetItemBitmap(Bitmap bitmap) {
            this.itemBitmapResource = bitmap;
        }

        /* access modifiers changed from: protected */
        public void internalSetItemSummary(String resourceId) {
            this.itemSummary = resourceId;
        }

        /* access modifiers changed from: protected */
        public void internalSetItemTitle(String title) {
            this.itemTitle = title;
        }

        /* access modifiers changed from: protected */
        public void internalSetOnClickPendingIntent(PendingIntent pendingIntent) {
            this.onClickPendingIntent = pendingIntent;
        }

        protected ExpandedItem(Parcel parcel) {
            this.itemSummary = null;
            Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(parcel);
            int parcelableVersion = parcelInfo.getParcelVersion();
            if (parcelableVersion >= 1) {
                if (parcel.readInt() != 0) {
                    this.onClickPendingIntent = (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel);
                }
                if (parcel.readInt() != 0) {
                    this.itemTitle = parcel.readString();
                }
                if (parcel.readInt() != 0) {
                    this.itemSummary = parcel.readString();
                }
                this.itemDrawableResourceId = parcel.readInt();
            }
            if (parcelableVersion >= 2 && parcel.readInt() != 0) {
                this.itemBitmapResource = (Bitmap) Bitmap.CREATOR.createFromParcel(parcel);
            }
            parcelInfo.complete();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(out);
            if (this.onClickPendingIntent != null) {
                out.writeInt(1);
                this.onClickPendingIntent.writeToParcel(out, 0);
            } else {
                out.writeInt(0);
            }
            if (!TextUtils.isEmpty(this.itemTitle)) {
                out.writeInt(1);
                out.writeString(this.itemTitle);
            } else {
                out.writeInt(0);
            }
            if (!TextUtils.isEmpty(this.itemSummary)) {
                out.writeInt(1);
                out.writeString(this.itemSummary);
            } else {
                out.writeInt(0);
            }
            out.writeInt(this.itemDrawableResourceId);
            if (this.itemBitmapResource != null) {
                out.writeInt(1);
                this.itemBitmapResource.writeToParcel(out, 0);
            } else {
                out.writeInt(0);
            }
            parcelInfo.complete();
        }

        public String toString() {
            StringBuilder b = new StringBuilder();
            String NEW_LINE = System.getProperty("line.separator");
            if (this.onClickPendingIntent != null) {
                b.append("onClickPendingIntent= " + this.onClickPendingIntent.toString() + NEW_LINE);
            }
            if (this.itemTitle != null) {
                b.append("itemTitle= " + this.itemTitle.toString() + NEW_LINE);
            }
            if (this.itemSummary != null) {
                b.append("itemSummary= " + this.itemSummary.toString() + NEW_LINE);
            }
            b.append("itemDrawableResourceId=" + this.itemDrawableResourceId + NEW_LINE);
            if (this.itemBitmapResource != null) {
                b.append("itemBitmapResource=" + this.itemBitmapResource.getGenerationId() + NEW_LINE);
            }
            return b.toString();
        }
    }

    public static class ExpandedGridItem extends ExpandedItem {
        public ExpandedGridItem() {
            super();
        }

        public void setExpandedGridItemTitle(String title) {
            internalSetItemTitle(title);
        }

        public void setExpandedGridItemOnClickIntent(PendingIntent intent) {
            internalSetOnClickPendingIntent(intent);
        }

        public void setExpandedGridItemDrawable(int resourceId) {
            internalSetItemDrawable(resourceId);
        }

        public void setExpandedGridItemBitmap(Bitmap bitmap) {
            internalSetItemBitmap(bitmap);
        }
    }

    public static class ExpandedListItem extends ExpandedItem {
        public ExpandedListItem() {
            super();
        }

        public void setExpandedListItemTitle(String title) {
            internalSetItemTitle(title);
        }

        public void setExpandedListItemSummary(String summary) {
            internalSetItemSummary(summary);
        }

        public void setExpandedListItemOnClickIntent(PendingIntent intent) {
            internalSetOnClickPendingIntent(intent);
        }

        public void setExpandedListItemDrawable(int resourceId) {
            internalSetItemDrawable(resourceId);
        }

        public void setExpandedListItemBitmap(Bitmap bitmap) {
            internalSetItemBitmap(bitmap);
        }
    }

    public static class Builder {
        private boolean mCollapsePanel = true;
        private String mContentDescription;
        private Context mContext;
        private PendingIntent mDeleteIntent;
        private ExpandedStyle mExpandedStyle;
        private int mIcon;
        private String mLabel;
        private PendingIntent mOnClick;
        private Uri mOnClickUri;
        private PendingIntent mOnLongClick;
        private Intent mOnSettingsClick;
        private Bitmap mRemoteIcon;
        private boolean mSensitiveData = false;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setLabel(String label) {
            this.mLabel = label;
            return this;
        }

        public Builder setLabel(int id) {
            this.mLabel = this.mContext.getString(id);
            return this;
        }

        public Builder setContentDescription(String contentDescription) {
            this.mContentDescription = contentDescription;
            return this;
        }

        public Builder setContentDescription(int id) {
            this.mContentDescription = this.mContext.getString(id);
            return this;
        }

        public Builder setOnClickIntent(PendingIntent intent) {
            this.mOnClick = intent;
            return this;
        }

        public Builder setOnLongClickIntent(PendingIntent intent) {
            this.mOnLongClick = intent;
            return this;
        }

        public Builder setOnSettingsClickIntent(Intent intent) {
            this.mOnSettingsClick = intent;
            return this;
        }

        public Builder setOnClickUri(Uri uri) {
            this.mOnClickUri = uri;
            return this;
        }

        public Builder setIcon(int drawableId) {
            this.mIcon = drawableId;
            return this;
        }

        public Builder setIcon(Bitmap remoteIcon) {
            this.mIcon = 0;
            this.mRemoteIcon = remoteIcon;
            return this;
        }

        public Builder setExpandedStyle(ExpandedStyle expandedStyle) {
            if (this.mExpandedStyle != expandedStyle) {
                this.mExpandedStyle = expandedStyle;
                if (this.mExpandedStyle != null) {
                    expandedStyle.setBuilder(this);
                }
            }
            return this;
        }

        public Builder shouldCollapsePanel(boolean bool) {
            this.mCollapsePanel = bool;
            return this;
        }

        public Builder setDeleteIntent(PendingIntent intent) {
            this.mDeleteIntent = intent;
            return this;
        }

        public Builder hasSensitiveData(boolean bool) {
            this.mSensitiveData = bool;
            return this;
        }

        public CustomTile build() {
            CustomTile tile = new CustomTile();
            String unused = tile.resourcesPackageName = this.mContext.getPackageName();
            tile.onClick = this.mOnClick;
            tile.onLongClick = this.mOnLongClick;
            tile.onSettingsClick = this.mOnSettingsClick;
            tile.onClickUri = this.mOnClickUri;
            tile.label = this.mLabel;
            tile.contentDescription = this.mContentDescription;
            tile.expandedStyle = this.mExpandedStyle;
            tile.icon = this.mIcon;
            tile.collapsePanel = this.mCollapsePanel;
            tile.remoteIcon = this.mRemoteIcon;
            tile.deleteIntent = this.mDeleteIntent;
            tile.sensitiveData = this.mSensitiveData;
            return tile;
        }
    }
}
