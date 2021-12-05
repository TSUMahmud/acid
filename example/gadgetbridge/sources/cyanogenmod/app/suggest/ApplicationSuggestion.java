package cyanogenmod.app.suggest;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import cyanogenmod.p007os.Concierge;

public class ApplicationSuggestion implements Parcelable {
    public static final Parcelable.Creator<ApplicationSuggestion> CREATOR = new Parcelable.Creator<ApplicationSuggestion>() {
        public ApplicationSuggestion createFromParcel(Parcel in) {
            return new ApplicationSuggestion(in);
        }

        public ApplicationSuggestion[] newArray(int size) {
            return new ApplicationSuggestion[size];
        }
    };
    private Uri mDownloadUri;
    private String mName;
    private String mPackage;
    private Uri mThumbnailUri;

    public ApplicationSuggestion(String name, String pkg, Uri downloadUri, Uri thumbnailUri) {
        this.mName = name;
        this.mPackage = pkg;
        this.mDownloadUri = downloadUri;
        this.mThumbnailUri = thumbnailUri;
    }

    private ApplicationSuggestion(Parcel in) {
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(in);
        if (parcelInfo.getParcelVersion() >= 1) {
            this.mName = in.readString();
            this.mPackage = in.readString();
            this.mDownloadUri = (Uri) in.readParcelable(Uri.class.getClassLoader());
            this.mThumbnailUri = (Uri) in.readParcelable(Uri.class.getClassLoader());
        }
        parcelInfo.complete();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(out);
        out.writeString(this.mName);
        out.writeString(this.mPackage);
        out.writeParcelable(this.mDownloadUri, flags);
        out.writeParcelable(this.mThumbnailUri, flags);
        parcelInfo.complete();
    }

    public String getName() {
        return this.mName;
    }

    public String getPackageName() {
        return this.mPackage;
    }

    public Uri getDownloadUri() {
        return this.mDownloadUri;
    }

    public Uri getThumbailUri() {
        return this.mThumbnailUri;
    }
}
