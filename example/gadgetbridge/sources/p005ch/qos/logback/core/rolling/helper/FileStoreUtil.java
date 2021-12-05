package p005ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.lang.reflect.Method;
import p005ch.qos.logback.core.rolling.RolloverFailure;

/* renamed from: ch.qos.logback.core.rolling.helper.FileStoreUtil */
public class FileStoreUtil {
    static final String FILES_CLASS_STR = "java.nio.file.Files";
    static final String PATH_CLASS_STR = "java.nio.file.Path";

    public static boolean areOnSameFileStore(File file, File file2) throws RolloverFailure {
        if (!file.exists()) {
            throw new IllegalArgumentException("File [" + file + "] does not exist.");
        } else if (file2.exists()) {
            try {
                Class<?> cls = Class.forName(PATH_CLASS_STR);
                Class<?> cls2 = Class.forName(FILES_CLASS_STR);
                Method method = File.class.getMethod("toPath", new Class[0]);
                Method method2 = cls2.getMethod("getFileStore", new Class[]{cls});
                Object invoke = method.invoke(file, new Object[0]);
                Object invoke2 = method.invoke(file2, new Object[0]);
                return method2.invoke((Object) null, new Object[]{invoke}).equals(method2.invoke((Object) null, new Object[]{invoke2}));
            } catch (Exception e) {
                throw new RolloverFailure("Failed to check file store equality for [" + file + "] and [" + file2 + "]", e);
            }
        } else {
            throw new IllegalArgumentException("File [" + file2 + "] does not exist.");
        }
    }
}
