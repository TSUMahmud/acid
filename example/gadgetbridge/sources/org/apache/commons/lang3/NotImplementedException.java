package org.apache.commons.lang3;

public class NotImplementedException extends UnsupportedOperationException {
    private static final long serialVersionUID = 20131021;
    private final String code;

    public NotImplementedException(String message) {
        this(message, (String) null);
    }

    public NotImplementedException(Throwable cause) {
        this(cause, (String) null);
    }

    public NotImplementedException(String message, Throwable cause) {
        this(message, cause, (String) null);
    }

    public NotImplementedException(String message, String code2) {
        super(message);
        this.code = code2;
    }

    public NotImplementedException(Throwable cause, String code2) {
        super(cause);
        this.code = code2;
    }

    public NotImplementedException(String message, Throwable cause, String code2) {
        super(message, cause);
        this.code = code2;
    }

    public String getCode() {
        return this.code;
    }
}
