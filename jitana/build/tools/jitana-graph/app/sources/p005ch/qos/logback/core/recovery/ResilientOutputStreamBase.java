package p005ch.qos.logback.core.recovery;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.status.ErrorStatus;
import p005ch.qos.logback.core.status.InfoStatus;
import p005ch.qos.logback.core.status.Status;
import p005ch.qos.logback.core.status.StatusManager;

/* renamed from: ch.qos.logback.core.recovery.ResilientOutputStreamBase */
public abstract class ResilientOutputStreamBase extends OutputStream {
    static final int STATUS_COUNT_LIMIT = 8;
    private Context context;
    private int noContextWarning = 0;

    /* renamed from: os */
    protected OutputStream f56os;
    protected boolean presumedClean = true;
    private RecoveryCoordinator recoveryCoordinator;
    private int statusCount = 0;

    private boolean isPresumedInError() {
        return this.recoveryCoordinator != null && !this.presumedClean;
    }

    private void postSuccessfulWrite() {
        if (this.recoveryCoordinator != null) {
            this.recoveryCoordinator = null;
            this.statusCount = 0;
            addStatus(new InfoStatus("Recovered from IO failure on " + getDescription(), this));
        }
    }

    public void addStatus(Status status) {
        Context context2 = this.context;
        if (context2 == null) {
            int i = this.noContextWarning;
            this.noContextWarning = i + 1;
            if (i == 0) {
                PrintStream printStream = System.out;
                printStream.println("LOGBACK: No context given for " + this);
                return;
            }
            return;
        }
        StatusManager statusManager = context2.getStatusManager();
        if (statusManager != null) {
            statusManager.add(status);
        }
    }

    /* access modifiers changed from: package-private */
    public void addStatusIfCountNotOverLimit(Status status) {
        this.statusCount++;
        if (this.statusCount < 8) {
            addStatus(status);
        }
        if (this.statusCount == 8) {
            addStatus(status);
            addStatus(new InfoStatus("Will supress future messages regarding " + getDescription(), this));
        }
    }

    /* access modifiers changed from: package-private */
    public void attemptRecovery() {
        try {
            close();
        } catch (IOException e) {
        }
        addStatusIfCountNotOverLimit(new InfoStatus("Attempting to recover from IO failure on " + getDescription(), this));
        try {
            this.f56os = openNewOutputStream();
            this.presumedClean = true;
        } catch (IOException e2) {
            addStatusIfCountNotOverLimit(new ErrorStatus("Failed to open " + getDescription(), this, e2));
        }
    }

    public void close() throws IOException {
        OutputStream outputStream = this.f56os;
        if (outputStream != null) {
            outputStream.close();
        }
    }

    public void flush() {
        OutputStream outputStream = this.f56os;
        if (outputStream != null) {
            try {
                outputStream.flush();
                postSuccessfulWrite();
            } catch (IOException e) {
                postIOFailure(e);
            }
        }
    }

    public Context getContext() {
        return this.context;
    }

    /* access modifiers changed from: package-private */
    public abstract String getDescription();

    /* access modifiers changed from: package-private */
    public abstract OutputStream openNewOutputStream() throws IOException;

    /* access modifiers changed from: package-private */
    public void postIOFailure(IOException iOException) {
        addStatusIfCountNotOverLimit(new ErrorStatus("IO failure while writing to " + getDescription(), this, iOException));
        this.presumedClean = false;
        if (this.recoveryCoordinator == null) {
            this.recoveryCoordinator = new RecoveryCoordinator();
        }
    }

    public void setContext(Context context2) {
        this.context = context2;
    }

    public void write(int i) {
        if (!isPresumedInError()) {
            try {
                this.f56os.write(i);
                postSuccessfulWrite();
            } catch (IOException e) {
                postIOFailure(e);
            }
        } else if (!this.recoveryCoordinator.isTooSoon()) {
            attemptRecovery();
        }
    }

    public void write(byte[] bArr, int i, int i2) {
        if (!isPresumedInError()) {
            try {
                this.f56os.write(bArr, i, i2);
                postSuccessfulWrite();
            } catch (IOException e) {
                postIOFailure(e);
            }
        } else if (!this.recoveryCoordinator.isTooSoon()) {
            attemptRecovery();
        }
    }
}
