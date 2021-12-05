package p005ch.qos.logback.classic.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import p005ch.qos.logback.classic.spi.CallerData;
import p005ch.qos.logback.classic.spi.ILoggingEvent;
import p005ch.qos.logback.core.Context;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.boolex.EvaluationException;
import p005ch.qos.logback.core.boolex.EventEvaluator;
import p005ch.qos.logback.core.status.ErrorStatus;

/* renamed from: ch.qos.logback.classic.pattern.CallerDataConverter */
public class CallerDataConverter extends ClassicConverter {
    public static final String DEFAULT_CALLER_LINE_PREFIX = "Caller+";
    final int MAX_ERROR_COUNT = 4;
    int depth = 5;
    int errorCount = 0;
    List<EventEvaluator<ILoggingEvent>> evaluatorList = null;

    private void addEvaluator(EventEvaluator<ILoggingEvent> eventEvaluator) {
        if (this.evaluatorList == null) {
            this.evaluatorList = new ArrayList();
        }
        this.evaluatorList.add(eventEvaluator);
    }

    public String convert(ILoggingEvent iLoggingEvent) {
        boolean z;
        StringBuilder sb = new StringBuilder();
        if (this.evaluatorList != null) {
            int i = 0;
            while (true) {
                z = true;
                if (i >= this.evaluatorList.size()) {
                    z = false;
                    break;
                }
                EventEvaluator eventEvaluator = this.evaluatorList.get(i);
                try {
                    if (eventEvaluator.evaluate(iLoggingEvent)) {
                        break;
                    }
                    i++;
                } catch (EvaluationException e) {
                    this.errorCount++;
                    int i2 = this.errorCount;
                    if (i2 < 4) {
                        addError("Exception thrown for evaluator named [" + eventEvaluator.getName() + "]", e);
                    } else if (i2 == 4) {
                        ErrorStatus errorStatus = new ErrorStatus("Exception thrown for evaluator named [" + eventEvaluator.getName() + "].", this, e);
                        errorStatus.add(new ErrorStatus("This was the last warning about this evaluator's errors.We don't want the StatusManager to get flooded.", this));
                        addStatus(errorStatus);
                    }
                }
            }
            if (!z) {
                return "";
            }
        }
        StackTraceElement[] callerData = iLoggingEvent.getCallerData();
        if (callerData == null || callerData.length <= 0) {
            return CallerData.CALLER_DATA_NA;
        }
        int i3 = this.depth;
        if (i3 >= callerData.length) {
            i3 = callerData.length;
        }
        for (int i4 = 0; i4 < i3; i4++) {
            sb.append(getCallerLinePrefix());
            sb.append(i4);
            sb.append("\t at ");
            sb.append(callerData[i4]);
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public String getCallerLinePrefix() {
        return DEFAULT_CALLER_LINE_PREFIX;
    }

    public void start() {
        EventEvaluator eventEvaluator;
        String firstOption = getFirstOption();
        if (firstOption != null) {
            try {
                this.depth = Integer.parseInt(firstOption);
            } catch (NumberFormatException e) {
                addError("Failed to parse depth option [" + firstOption + "]", e);
            }
            List<String> optionList = getOptionList();
            if (optionList != null) {
                if (optionList.size() > 1) {
                    int size = optionList.size();
                    for (int i = 1; i < size; i++) {
                        String str = optionList.get(i);
                        Context context = getContext();
                        if (!(context == null || (eventEvaluator = (EventEvaluator) ((Map) context.getObject(CoreConstants.EVALUATOR_MAP)).get(str)) == null)) {
                            addEvaluator(eventEvaluator);
                        }
                    }
                }
            }
        }
    }
}
