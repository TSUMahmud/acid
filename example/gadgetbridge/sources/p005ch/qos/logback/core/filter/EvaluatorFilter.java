package p005ch.qos.logback.core.filter;

import p005ch.qos.logback.core.boolex.EvaluationException;
import p005ch.qos.logback.core.boolex.EventEvaluator;
import p005ch.qos.logback.core.spi.FilterReply;

/* renamed from: ch.qos.logback.core.filter.EvaluatorFilter */
public class EvaluatorFilter<E> extends AbstractMatcherFilter<E> {
    EventEvaluator<E> evaluator;

    public FilterReply decide(E e) {
        if (!isStarted() || !this.evaluator.isStarted()) {
            return FilterReply.NEUTRAL;
        }
        try {
            return this.evaluator.evaluate(e) ? this.onMatch : this.onMismatch;
        } catch (EvaluationException e2) {
            addError("Evaluator " + this.evaluator.getName() + " threw an exception", e2);
            return FilterReply.NEUTRAL;
        }
    }

    public EventEvaluator<E> getEvaluator() {
        return this.evaluator;
    }

    public void setEvaluator(EventEvaluator<E> eventEvaluator) {
        this.evaluator = eventEvaluator;
    }

    public void start() {
        if (this.evaluator != null) {
            super.start();
            return;
        }
        addError("No evaluator set for filter " + getName());
    }
}
