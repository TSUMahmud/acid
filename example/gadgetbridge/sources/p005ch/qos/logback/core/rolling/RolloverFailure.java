package p005ch.qos.logback.core.rolling;

import p005ch.qos.logback.core.LogbackException;

/* renamed from: ch.qos.logback.core.rolling.RolloverFailure */
public class RolloverFailure extends LogbackException {
    private static final long serialVersionUID = -4407533730831239458L;

    public RolloverFailure(String str) {
        super(str);
    }

    public RolloverFailure(String str, Throwable th) {
        super(str, th);
    }
}
