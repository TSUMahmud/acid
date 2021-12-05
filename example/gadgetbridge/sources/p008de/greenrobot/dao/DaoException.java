package p008de.greenrobot.dao;

import android.database.SQLException;

/* renamed from: de.greenrobot.dao.DaoException */
public class DaoException extends SQLException {
    private static final long serialVersionUID = -5877937327907457779L;

    public DaoException() {
    }

    public DaoException(String error) {
        super(error);
    }

    public DaoException(String error, Throwable cause) {
        super(error);
        safeInitCause(cause);
    }

    public DaoException(Throwable th) {
        safeInitCause(th);
    }

    /* access modifiers changed from: protected */
    public void safeInitCause(Throwable cause) {
        try {
            initCause(cause);
        } catch (Throwable e) {
            DaoLog.m21e("Could not set initial cause", e);
            DaoLog.m21e("Initial cause is:", cause);
        }
    }
}
