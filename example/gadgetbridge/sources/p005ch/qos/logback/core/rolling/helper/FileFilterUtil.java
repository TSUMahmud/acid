package p005ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import p005ch.qos.logback.core.CoreConstants;

/* renamed from: ch.qos.logback.core.rolling.helper.FileFilterUtil */
public class FileFilterUtil {
    public static String afterLastSlash(String str) {
        int lastIndexOf = str.lastIndexOf(47);
        return lastIndexOf == -1 ? str : str.substring(lastIndexOf + 1);
    }

    public static int extractCounter(File file, String str) {
        Pattern compile = Pattern.compile(str);
        String name = file.getName();
        Matcher matcher = compile.matcher(name);
        if (matcher.matches()) {
            return new Integer(matcher.group(1)).intValue();
        }
        throw new IllegalStateException("The regex [" + str + "] should match [" + name + "]");
    }

    public static File[] filesInFolderMatchingStemRegex(File file, final String str) {
        return file == null ? new File[0] : (!file.exists() || !file.isDirectory()) ? new File[0] : file.listFiles(new FilenameFilter() {
            public boolean accept(File file, String str) {
                return str.matches(str);
            }
        });
    }

    public static int findHighestCounter(File[] fileArr, String str) {
        int i = Integer.MIN_VALUE;
        for (File extractCounter : fileArr) {
            int extractCounter2 = extractCounter(extractCounter, str);
            if (i < extractCounter2) {
                i = extractCounter2;
            }
        }
        return i;
    }

    public static boolean isEmptyDirectory(File file) {
        if (file.isDirectory()) {
            String[] list = file.list();
            return list == null || list.length == 0;
        }
        throw new IllegalArgumentException("[" + file + "] must be a directory");
    }

    public static void removeEmptyParentDirectories(File file, int i) {
        if (i < 3) {
            File parentFile = file.getParentFile();
            if (parentFile.isDirectory() && isEmptyDirectory(parentFile)) {
                parentFile.delete();
                removeEmptyParentDirectories(parentFile, i + 1);
            }
        }
    }

    public static void reverseSortFileArrayByName(File[] fileArr) {
        Arrays.sort(fileArr, new Comparator<File>() {
            public int compare(File file, File file2) {
                return file2.getName().compareTo(file.getName());
            }
        });
    }

    public static String slashify(String str) {
        return str.replace(CoreConstants.ESCAPE_CHAR, '/');
    }

    public static void sortFileArrayByName(File[] fileArr) {
        Arrays.sort(fileArr, new Comparator<File>() {
            public int compare(File file, File file2) {
                return file.getName().compareTo(file2.getName());
            }
        });
    }
}
