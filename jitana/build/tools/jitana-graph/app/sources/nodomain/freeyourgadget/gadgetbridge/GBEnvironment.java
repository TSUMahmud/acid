package nodomain.freeyourgadget.gadgetbridge;

public class GBEnvironment {
    private static GBEnvironment environment;
    private boolean deviceTest;
    private boolean localTest;

    public static GBEnvironment createLocalTestEnvironment() {
        GBEnvironment env = new GBEnvironment();
        env.localTest = true;
        return env;
    }

    static GBEnvironment createDeviceEnvironment() {
        return new GBEnvironment();
    }

    public final boolean isTest() {
        return this.localTest || this.deviceTest;
    }

    public boolean isLocalTest() {
        return this.localTest;
    }

    public static synchronized GBEnvironment env() {
        GBEnvironment gBEnvironment;
        synchronized (GBEnvironment.class) {
            gBEnvironment = environment;
        }
        return gBEnvironment;
    }

    static synchronized boolean isEnvironmentSetup() {
        boolean z;
        synchronized (GBEnvironment.class) {
            z = environment != null;
        }
        return z;
    }

    public static synchronized void setupEnvironment(GBEnvironment env) {
        synchronized (GBEnvironment.class) {
            environment = env;
        }
    }
}
