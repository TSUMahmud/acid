package p005ch.qos.logback.classic.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.android.CommonPathUtil;
import p005ch.qos.logback.core.joran.spi.JoranException;
import p005ch.qos.logback.core.status.Status;
import p005ch.qos.logback.core.status.StatusManager;
import p005ch.qos.logback.core.status.WarnStatus;
import p005ch.qos.logback.core.util.Loader;

/* renamed from: ch.qos.logback.classic.android.AndroidManifestPropertiesUtil */
public class AndroidManifestPropertiesUtil {
    public static void setAndroidProperties(Context context) throws JoranException {
        String str;
        String str2;
        ASaxEventRecorder aSaxEventRecorder = new ASaxEventRecorder();
        aSaxEventRecorder.setFilter("-");
        aSaxEventRecorder.setAttributeWatch("manifest");
        StatusManager statusManager = context.getStatusManager();
        InputStream resourceAsStream = Loader.getClassLoaderOfObject(context).getResourceAsStream("AndroidManifest.xml");
        if (resourceAsStream == null) {
            statusManager.add((Status) new WarnStatus("Could not find AndroidManifest.xml", context));
            return;
        }
        try {
            aSaxEventRecorder.recordEvents(resourceAsStream);
            context.putProperty(CoreConstants.EXT_DIR_KEY, CommonPathUtil.getMountedExternalStorageDirectoryPath());
            Map<String, String> attributeWatchValues = aSaxEventRecorder.getAttributeWatchValues();
            for (String next : attributeWatchValues.keySet()) {
                if (next.equals("android:versionName")) {
                    str = attributeWatchValues.get(next);
                    str2 = CoreConstants.VERSION_NAME_KEY;
                } else if (next.equals("android:versionCode")) {
                    str = attributeWatchValues.get(next);
                    str2 = CoreConstants.VERSION_CODE_KEY;
                } else if (next.equals("package")) {
                    str = attributeWatchValues.get(next);
                    str2 = CoreConstants.PACKAGE_NAME_KEY;
                }
                context.putProperty(str2, str);
            }
            String str3 = attributeWatchValues.get("package");
            if (str3 == null || str3.length() <= 0) {
                statusManager.add((Status) new WarnStatus("Package name not found. Some properties cannot be set.", context));
            } else {
                context.putProperty(CoreConstants.DATA_DIR_KEY, CommonPathUtil.getFilesDirectoryPath(str3));
            }
        } finally {
            try {
                resourceAsStream.close();
            } catch (IOException e) {
            }
        }
    }
}
