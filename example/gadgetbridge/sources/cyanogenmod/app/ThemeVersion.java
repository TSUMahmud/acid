package cyanogenmod.app;

import android.content.ThemeVersion;
import android.os.Build;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThemeVersion {
    private static final int CM11 = 1;
    private static final int CM12_PRE_VERSIONING = 2;
    private static final String MIN_SUPPORTED_THEME_VERSION_FIELD_NAME = "MIN_SUPPORTED_THEME_VERSION";
    private static final String THEME_VERSION_CLASS_NAME = "android.content.ThemeVersion";
    private static final String THEME_VERSION_FIELD_NAME = "THEME_VERSION";

    public static int getVersion() {
        try {
            return ((Integer) Class.forName(THEME_VERSION_CLASS_NAME).getField(THEME_VERSION_FIELD_NAME).get((Object) null)).intValue();
        } catch (Exception e) {
            return Build.VERSION.SDK_INT < 21 ? 1 : 2;
        }
    }

    public static int getMinSupportedVersion() {
        try {
            return ((Integer) Class.forName(THEME_VERSION_CLASS_NAME).getField(MIN_SUPPORTED_THEME_VERSION_FIELD_NAME).get((Object) null)).intValue();
        } catch (Exception e) {
            return Build.VERSION.SDK_INT < 21 ? 1 : 2;
        }
    }

    public static ComponentVersion getComponentVersion(ThemeComponent component) {
        int version = getVersion();
        if (version == 1) {
            throw new UnsupportedOperationException();
        } else if (version == 2) {
            return ThemeVersionImpl2.getDeviceComponentVersion(component);
        } else {
            return ThemeVersionImpl3.getDeviceComponentVersion(component);
        }
    }

    public static List<ComponentVersion> getComponentVersions() {
        int version = getVersion();
        if (version == 1) {
            throw new UnsupportedOperationException();
        } else if (version == 2) {
            return ThemeVersionImpl2.getDeviceComponentVersions();
        } else {
            return ThemeVersionImpl3.getDeviceComponentVersions();
        }
    }

    public static class ComponentVersion {
        protected ThemeComponent component;
        protected int currentVersion;

        /* renamed from: id */
        protected int f104id;
        protected int minVersion;
        protected String name;

        protected ComponentVersion(int id, ThemeComponent component2, int targetVersion) {
            this(id, component2, component2.name(), targetVersion, targetVersion);
        }

        protected ComponentVersion(int id, ThemeComponent component2, String name2, int minVersion2, int targetVersion) {
            this.f104id = id;
            this.component = component2;
            this.name = name2;
            this.minVersion = minVersion2;
            this.currentVersion = targetVersion;
        }

        public ComponentVersion(ComponentVersion copy) {
            this(copy.f104id, copy.component, copy.name, copy.minVersion, copy.currentVersion);
        }

        public int getId() {
            return this.f104id;
        }

        public String getName() {
            return this.name;
        }

        public ThemeComponent getComponent() {
            return this.component;
        }

        public int getMinVersion() {
            return this.minVersion;
        }

        public int getCurrentVersion() {
            return this.currentVersion;
        }
    }

    private static class ThemeVersionImpl2 {
        private static ArrayList<ComponentVersion> cVersions = new ArrayList<ComponentVersion>() {
            {
                add(new ComponentVersion(0, ThemeComponent.OVERLAY, 2));
                add(new ComponentVersion(1, ThemeComponent.BOOT_ANIM, 1));
                add(new ComponentVersion(2, ThemeComponent.WALLPAPER, 1));
                add(new ComponentVersion(3, ThemeComponent.LOCKSCREEN, 1));
                add(new ComponentVersion(4, ThemeComponent.ICON, 1));
                add(new ComponentVersion(5, ThemeComponent.FONT, 1));
                add(new ComponentVersion(6, ThemeComponent.SOUND, 1));
            }
        };

        private ThemeVersionImpl2() {
        }

        public static ComponentVersion getDeviceComponentVersion(ThemeComponent component) {
            Iterator i$ = cVersions.iterator();
            while (i$.hasNext()) {
                ComponentVersion compVersion = i$.next();
                if (compVersion.component.equals(component)) {
                    return new ComponentVersion(compVersion);
                }
            }
            return null;
        }

        public static List<ComponentVersion> getDeviceComponentVersions() {
            ArrayList<ComponentVersion> versions = new ArrayList<>();
            versions.addAll(cVersions);
            return versions;
        }
    }

    private static class ThemeVersionImpl3 {
        private ThemeVersionImpl3() {
        }

        public static ComponentVersion getDeviceComponentVersion(ThemeComponent component) {
            for (ThemeVersion.ComponentVersion version : ThemeVersion.ComponentVersion.values()) {
                ComponentVersion sdkVersionInfo = fwCompVersionToSdkVersion(version);
                if (sdkVersionInfo.component.equals(component)) {
                    return sdkVersionInfo;
                }
            }
            return null;
        }

        public static List<ComponentVersion> getDeviceComponentVersions() {
            List<ComponentVersion> versions = new ArrayList<>();
            for (ThemeVersion.ComponentVersion version : ThemeVersion.ComponentVersion.values()) {
                versions.add(fwCompVersionToSdkVersion(version));
            }
            return versions;
        }

        public static ComponentVersion fwCompVersionToSdkVersion(ThemeVersion.ComponentVersion version) {
            ThemeComponent component = ThemeComponent.UNKNOWN;
            for (ThemeComponent aComponent : ThemeComponent.values()) {
                if (aComponent.f103id == version.id) {
                    component = aComponent;
                }
            }
            return new ComponentVersion(version.id, component, version.name(), version.minSupportedVersion, version.currentVersion);
        }
    }
}
