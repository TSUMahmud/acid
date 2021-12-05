package p005ch.qos.logback.core.pattern;

import java.util.List;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.spi.ContextAware;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.spi.LifeCycle;
import p005ch.qos.logback.core.status.Status;

/* renamed from: ch.qos.logback.core.pattern.DynamicConverter */
public abstract class DynamicConverter<E> extends FormattingConverter<E> implements LifeCycle, ContextAware {
    ContextAwareBase cab = new ContextAwareBase(this);
    private List<String> optionList;
    protected boolean started = false;

    public void addError(String str) {
        this.cab.addError(str);
    }

    public void addError(String str, Throwable th) {
        this.cab.addError(str, th);
    }

    public void addInfo(String str) {
        this.cab.addInfo(str);
    }

    public void addInfo(String str, Throwable th) {
        this.cab.addInfo(str, th);
    }

    public void addStatus(Status status) {
        this.cab.addStatus(status);
    }

    public void addWarn(String str) {
        this.cab.addWarn(str);
    }

    public void addWarn(String str, Throwable th) {
        this.cab.addWarn(str, th);
    }

    public Context getContext() {
        return this.cab.getContext();
    }

    public String getFirstOption() {
        List<String> list = this.optionList;
        if (list == null || list.size() == 0) {
            return null;
        }
        return this.optionList.get(0);
    }

    /* access modifiers changed from: protected */
    public List<String> getOptionList() {
        return this.optionList;
    }

    public boolean isStarted() {
        return this.started;
    }

    public void setContext(Context context) {
        this.cab.setContext(context);
    }

    public void setOptionList(List<String> list) {
        this.optionList = list;
    }

    public void start() {
        this.started = true;
    }

    public void stop() {
        this.started = false;
    }
}
