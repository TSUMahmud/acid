package brut.androlib;

import brut.common.BrutException;

public class AndrolibException extends BrutException {
    public AndrolibException() {
    }

    public AndrolibException(String str) {
        super(str);
    }

    public AndrolibException(String str, Throwable th) {
        super(str, th);
    }

    public AndrolibException(Throwable th) {
        super(th);
    }
}
