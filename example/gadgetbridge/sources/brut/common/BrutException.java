package brut.common;

public class BrutException extends Exception {
    public BrutException() {
    }

    public BrutException(String str) {
        super(str);
    }

    public BrutException(String str, Throwable th) {
        super(str, th);
    }

    public BrutException(Throwable th) {
        super(th);
    }
}
