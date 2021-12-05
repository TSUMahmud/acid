package p005ch.qos.logback.core.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import p005ch.qos.logback.core.CoreConstants;

/* renamed from: ch.qos.logback.core.util.ExecutorServiceUtil */
public class ExecutorServiceUtil {
    public static ExecutorService newExecutorService() {
        return new ThreadPoolExecutor(CoreConstants.CORE_POOL_SIZE, 32, 0, TimeUnit.MILLISECONDS, new SynchronousQueue());
    }

    public static void shutdown(ExecutorService executorService) {
        executorService.shutdownNow();
    }
}
