package p005ch.qos.logback.core.util;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.helpers.ThrowableToStringArray;
import p005ch.qos.logback.core.status.Status;
import p005ch.qos.logback.core.status.StatusManager;
import p005ch.qos.logback.core.status.StatusUtil;

/* renamed from: ch.qos.logback.core.util.StatusPrinter */
public class StatusPrinter {
    static CachingDateFormatter cachingDateFormat = new CachingDateFormatter("HH:mm:ss,SSS");

    /* renamed from: ps */
    private static PrintStream f62ps = System.out;

    private static void appendThrowable(StringBuilder sb, Throwable th) {
        for (String str : ThrowableToStringArray.convert(th)) {
            if (!str.startsWith(CoreConstants.CAUSED_BY)) {
                sb.append(Character.isDigit(str.charAt(0)) ? "\t... " : "\tat ");
            }
            sb.append(str);
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
    }

    public static void buildStr(StringBuilder sb, String str, Status status) {
        String str2;
        StringBuilder sb2;
        if (status.hasChildren()) {
            sb2 = new StringBuilder();
            sb2.append(str);
            str2 = "+ ";
        } else {
            sb2 = new StringBuilder();
            sb2.append(str);
            str2 = "|-";
        }
        sb2.append(str2);
        String sb3 = sb2.toString();
        CachingDateFormatter cachingDateFormatter = cachingDateFormat;
        if (cachingDateFormatter != null) {
            sb.append(cachingDateFormatter.format(status.getDate().longValue()));
            sb.append(StringUtils.SPACE);
        }
        sb.append(sb3);
        sb.append(status);
        sb.append(CoreConstants.LINE_SEPARATOR);
        if (status.getThrowable() != null) {
            appendThrowable(sb, status.getThrowable());
        }
        if (status.hasChildren()) {
            Iterator<Status> it = status.iterator();
            while (it.hasNext()) {
                buildStr(sb, str + "  ", it.next());
            }
        }
    }

    private static void buildStrFromStatusList(StringBuilder sb, List<Status> list) {
        if (list != null) {
            for (Status buildStr : list) {
                buildStr(sb, "", buildStr);
            }
        }
    }

    public static void print(Context context) {
        print(context, 0);
    }

    public static void print(Context context, long j) {
        if (context != null) {
            StatusManager statusManager = context.getStatusManager();
            if (statusManager == null) {
                PrintStream printStream = f62ps;
                printStream.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
                return;
            }
            print(statusManager, j);
            return;
        }
        throw new IllegalArgumentException("Context argument cannot be null");
    }

    public static void print(StatusManager statusManager) {
        print(statusManager, 0);
    }

    public static void print(StatusManager statusManager, long j) {
        StringBuilder sb = new StringBuilder();
        buildStrFromStatusList(sb, StatusUtil.filterStatusListByTimeThreshold(statusManager.getCopyOfStatusList(), j));
        f62ps.println(sb.toString());
    }

    public static void print(List<Status> list) {
        StringBuilder sb = new StringBuilder();
        buildStrFromStatusList(sb, list);
        f62ps.println(sb.toString());
    }

    public static void printIfErrorsOccured(Context context) {
        if (context != null) {
            StatusManager statusManager = context.getStatusManager();
            if (statusManager == null) {
                PrintStream printStream = f62ps;
                printStream.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
            } else if (new StatusUtil(context).getHighestLevel(0) == 2) {
                print(statusManager);
            }
        } else {
            throw new IllegalArgumentException("Context argument cannot be null");
        }
    }

    public static void printInCaseOfErrorsOrWarnings(Context context) {
        printInCaseOfErrorsOrWarnings(context, 0);
    }

    public static void printInCaseOfErrorsOrWarnings(Context context, long j) {
        if (context != null) {
            StatusManager statusManager = context.getStatusManager();
            if (statusManager == null) {
                PrintStream printStream = f62ps;
                printStream.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
            } else if (new StatusUtil(context).getHighestLevel(j) >= 1) {
                print(statusManager, j);
            }
        } else {
            throw new IllegalArgumentException("Context argument cannot be null");
        }
    }

    public static void setPrintStream(PrintStream printStream) {
        f62ps = printStream;
    }
}
