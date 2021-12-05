package org.slf4j.helpers;

import java.io.PrintStream;

public final class Util {
    private static final ClassContextSecurityManager SECURITY_MANAGER = new ClassContextSecurityManager();

    private Util() {
    }

    private static final class ClassContextSecurityManager extends SecurityManager {
        private ClassContextSecurityManager() {
        }

        /* access modifiers changed from: protected */
        public Class<?>[] getClassContext() {
            return super.getClassContext();
        }
    }

    public static Class<?> getCallingClass() {
        Class<?>[] trace = SECURITY_MANAGER.getClassContext();
        String thisClassName = Util.class.getName();
        int i = 0;
        while (i < trace.length && !thisClassName.equals(trace[i].getName())) {
            i++;
        }
        if (i < trace.length && i + 2 < trace.length) {
            return trace[i + 2];
        }
        throw new IllegalStateException("Failed to find org.slf4j.helpers.Util or its caller in the stack; this should not happen");
    }

    public static final void report(String msg, Throwable t) {
        System.err.println(msg);
        System.err.println("Reported exception:");
        t.printStackTrace();
    }

    public static final void report(String msg) {
        PrintStream printStream = System.err;
        printStream.println("SLF4J: " + msg);
    }
}
