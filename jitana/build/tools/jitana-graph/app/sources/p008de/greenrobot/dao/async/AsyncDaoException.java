package p008de.greenrobot.dao.async;

import p008de.greenrobot.dao.DaoException;

/* renamed from: de.greenrobot.dao.async.AsyncDaoException */
public class AsyncDaoException extends DaoException {
    private static final long serialVersionUID = 5872157552005102382L;
    private final AsyncOperation failedOperation;

    public AsyncDaoException(AsyncOperation failedOperation2, Throwable cause) {
        super(cause);
        this.failedOperation = failedOperation2;
    }

    public AsyncOperation getFailedOperation() {
        return this.failedOperation;
    }
}
