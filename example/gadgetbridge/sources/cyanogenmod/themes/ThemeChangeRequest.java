package cyanogenmod.themes;

import android.content.pm.ThemeUtils;
import android.content.res.ThemeConfig;
import android.os.Parcel;
import android.os.Parcelable;
import cyanogenmod.p007os.Concierge;
import cyanogenmod.providers.ThemesContract;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ThemeChangeRequest implements Parcelable {
    public static final Parcelable.Creator<ThemeChangeRequest> CREATOR = new Parcelable.Creator<ThemeChangeRequest>() {
        public ThemeChangeRequest createFromParcel(Parcel source) {
            return new ThemeChangeRequest(source);
        }

        public ThemeChangeRequest[] newArray(int size) {
            return new ThemeChangeRequest[size];
        }
    };
    public static final int DEFAULT_WALLPAPER_ID = -1;
    private final Map<String, String> mPerAppOverlays;
    private RequestType mRequestType;
    private final Map<String, String> mThemeComponents;
    private long mWallpaperId;

    public enum RequestType {
        USER_REQUEST,
        USER_REQUEST_MIXNMATCH,
        THEME_UPDATED,
        THEME_REMOVED,
        THEME_RESET
    }

    public String getOverlayThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_OVERLAYS);
    }

    public String getStatusBarThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_STATUS_BAR);
    }

    public String getNavBarThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_NAVIGATION_BAR);
    }

    public String getFontThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_FONTS);
    }

    public String getIconsThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_ICONS);
    }

    public String getBootanimationThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_BOOT_ANIM);
    }

    public String getWallpaperThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_LAUNCHER);
    }

    public String getLockWallpaperThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_LOCKSCREEN);
    }

    public String getAlarmThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_ALARMS);
    }

    public String getNotificationThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_NOTIFICATIONS);
    }

    public String getRingtoneThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_RINGTONES);
    }

    public String getLiveLockScreenThemePackageName() {
        return getThemePackageNameForComponent(ThemesContract.ThemesColumns.MODIFIES_LIVE_LOCK_SCREEN);
    }

    public final Map<String, String> getThemeComponentsMap() {
        return Collections.unmodifiableMap(this.mThemeComponents);
    }

    public long getWallpaperId() {
        return this.mWallpaperId;
    }

    public final Map<String, String> getPerAppOverlays() {
        return Collections.unmodifiableMap(this.mPerAppOverlays);
    }

    public int getNumChangesRequested() {
        return this.mThemeComponents.size() + this.mPerAppOverlays.size();
    }

    public RequestType getReqeustType() {
        return this.mRequestType;
    }

    private String getThemePackageNameForComponent(String componentName) {
        return this.mThemeComponents.get(componentName);
    }

    private ThemeChangeRequest(Map<String, String> components, Map<String, String> perAppThemes, RequestType requestType, long wallpaperId) {
        this.mThemeComponents = new HashMap();
        this.mPerAppOverlays = new HashMap();
        this.mWallpaperId = -1;
        if (components != null) {
            this.mThemeComponents.putAll(components);
        }
        if (perAppThemes != null) {
            this.mPerAppOverlays.putAll(perAppThemes);
        }
        this.mRequestType = requestType;
        this.mWallpaperId = wallpaperId;
    }

    private ThemeChangeRequest(Parcel source) {
        this.mThemeComponents = new HashMap();
        this.mPerAppOverlays = new HashMap();
        this.mWallpaperId = -1;
        Concierge.ParcelInfo parcelInfo = Concierge.receiveParcel(source);
        int parcelVersion = parcelInfo.getParcelVersion();
        int numComponents = source.readInt();
        for (int i = 0; i < numComponents; i++) {
            this.mThemeComponents.put(source.readString(), source.readString());
        }
        int numComponents2 = source.readInt();
        for (int i2 = 0; i2 < numComponents2; i2++) {
            this.mPerAppOverlays.put(source.readString(), source.readString());
        }
        this.mRequestType = RequestType.values()[source.readInt()];
        this.mWallpaperId = source.readLong();
        parcelInfo.complete();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Concierge.ParcelInfo parcelInfo = Concierge.prepareParcel(dest);
        dest.writeInt(this.mThemeComponents.size());
        for (String component : this.mThemeComponents.keySet()) {
            dest.writeString(component);
            dest.writeString(this.mThemeComponents.get(component));
        }
        dest.writeInt(this.mPerAppOverlays.size());
        for (String appPkgName : this.mPerAppOverlays.keySet()) {
            dest.writeString(appPkgName);
            dest.writeString(this.mPerAppOverlays.get(appPkgName));
        }
        dest.writeInt(this.mRequestType.ordinal());
        dest.writeLong(this.mWallpaperId);
        parcelInfo.complete();
    }

    public static class Builder {
        Map<String, String> mPerAppOverlays = new HashMap();
        RequestType mRequestType = RequestType.USER_REQUEST;
        Map<String, String> mThemeComponents = new HashMap();
        long mWallpaperId;

        public Builder() {
        }

        public Builder(ThemeConfig themeConfig) {
            if (themeConfig != null) {
                buildChangeRequestFromThemeConfig(themeConfig);
            }
        }

        public Builder setOverlay(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_OVERLAYS, pkgName);
        }

        public Builder setStatusBar(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_STATUS_BAR, pkgName);
        }

        public Builder setNavBar(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_NAVIGATION_BAR, pkgName);
        }

        public Builder setFont(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_FONTS, pkgName);
        }

        public Builder setIcons(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_ICONS, pkgName);
        }

        public Builder setBootanimation(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_BOOT_ANIM, pkgName);
        }

        public Builder setWallpaper(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_LAUNCHER, pkgName);
        }

        public Builder setWallpaperId(long id) {
            this.mWallpaperId = id;
            return this;
        }

        public Builder setLockWallpaper(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_LOCKSCREEN, pkgName);
        }

        public Builder setAlarm(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_ALARMS, pkgName);
        }

        public Builder setNotification(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_NOTIFICATIONS, pkgName);
        }

        public Builder setRingtone(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_RINGTONES, pkgName);
        }

        public Builder setLiveLockScreen(String pkgName) {
            return setComponent(ThemesContract.ThemesColumns.MODIFIES_LIVE_LOCK_SCREEN, pkgName);
        }

        public Builder setComponent(String component, String pkgName) {
            if (pkgName != null) {
                this.mThemeComponents.put(component, pkgName);
            } else {
                this.mThemeComponents.remove(component);
            }
            return this;
        }

        public Builder setAppOverlay(String appPkgName, String themePkgName) {
            if (appPkgName != null) {
                if (themePkgName != null) {
                    this.mPerAppOverlays.put(appPkgName, themePkgName);
                } else {
                    this.mPerAppOverlays.remove(appPkgName);
                }
            }
            return this;
        }

        public Builder setRequestType(RequestType requestType) {
            this.mRequestType = requestType != null ? requestType : RequestType.USER_REQUEST;
            return this;
        }

        public ThemeChangeRequest build() {
            return new ThemeChangeRequest(this.mThemeComponents, this.mPerAppOverlays, this.mRequestType, this.mWallpaperId);
        }

        private void buildChangeRequestFromThemeConfig(ThemeConfig themeConfig) {
            if (themeConfig.getFontPkgName() != null) {
                setFont(themeConfig.getFontPkgName());
            }
            if (themeConfig.getIconPackPkgName() != null) {
                setIcons(themeConfig.getIconPackPkgName());
            }
            if (themeConfig.getOverlayPkgName() != null) {
                setOverlay(themeConfig.getOverlayPkgName());
            }
            if (themeConfig.getOverlayForStatusBar() != null) {
                setStatusBar(themeConfig.getOverlayForStatusBar());
            }
            if (themeConfig.getOverlayForNavBar() != null) {
                setNavBar(themeConfig.getOverlayForNavBar());
            }
            Map<String, ThemeConfig.AppTheme> themes = themeConfig.getAppThemes();
            for (String appPkgName : themes.keySet()) {
                if (ThemeUtils.isPerAppThemeComponent(appPkgName)) {
                    setAppOverlay(appPkgName, themes.get(appPkgName).getOverlayPkgName());
                }
            }
        }
    }
}
