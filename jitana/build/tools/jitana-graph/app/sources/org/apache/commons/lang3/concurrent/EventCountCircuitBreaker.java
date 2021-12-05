package org.apache.commons.lang3.concurrent;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.concurrent.AbstractCircuitBreaker;

public class EventCountCircuitBreaker extends AbstractCircuitBreaker<Integer> {
    private static final Map<AbstractCircuitBreaker.State, StateStrategy> STRATEGY_MAP = createStrategyMap();
    private final AtomicReference<CheckIntervalData> checkIntervalData;
    private final long closingInterval;
    private final int closingThreshold;
    private final long openingInterval;
    private final int openingThreshold;

    public EventCountCircuitBreaker(int openingThreshold2, long openingInterval2, TimeUnit openingUnit, int closingThreshold2, long closingInterval2, TimeUnit closingUnit) {
        this.checkIntervalData = new AtomicReference<>(new CheckIntervalData(0, 0));
        this.openingThreshold = openingThreshold2;
        this.openingInterval = openingUnit.toNanos(openingInterval2);
        this.closingThreshold = closingThreshold2;
        this.closingInterval = closingUnit.toNanos(closingInterval2);
    }

    public EventCountCircuitBreaker(int openingThreshold2, long checkInterval, TimeUnit checkUnit, int closingThreshold2) {
        this(openingThreshold2, checkInterval, checkUnit, closingThreshold2, checkInterval, checkUnit);
    }

    public EventCountCircuitBreaker(int threshold, long checkInterval, TimeUnit checkUnit) {
        this(threshold, checkInterval, checkUnit, threshold);
    }

    public int getOpeningThreshold() {
        return this.openingThreshold;
    }

    public long getOpeningInterval() {
        return this.openingInterval;
    }

    public int getClosingThreshold() {
        return this.closingThreshold;
    }

    public long getClosingInterval() {
        return this.closingInterval;
    }

    public boolean checkState() {
        return performStateCheck(0);
    }

    public boolean incrementAndCheckState(Integer increment) throws CircuitBreakingException {
        return performStateCheck(1);
    }

    public boolean incrementAndCheckState() {
        return incrementAndCheckState((Integer) 1);
    }

    public void open() {
        super.open();
        this.checkIntervalData.set(new CheckIntervalData(0, now()));
    }

    public void close() {
        super.close();
        this.checkIntervalData.set(new CheckIntervalData(0, now()));
    }

    private boolean performStateCheck(int increment) {
        AbstractCircuitBreaker.State currentState;
        CheckIntervalData currentData;
        CheckIntervalData nextData;
        do {
            long time = now();
            currentState = (AbstractCircuitBreaker.State) this.state.get();
            currentData = this.checkIntervalData.get();
            nextData = nextCheckIntervalData(increment, currentData, currentState, time);
        } while (!updateCheckIntervalData(currentData, nextData));
        if (stateStrategy(currentState).isStateTransition(this, currentData, nextData)) {
            currentState = currentState.oppositeState();
            changeStateAndStartNewCheckInterval(currentState);
        }
        return !isOpen(currentState);
    }

    private boolean updateCheckIntervalData(CheckIntervalData currentData, CheckIntervalData nextData) {
        return currentData == nextData || this.checkIntervalData.compareAndSet(currentData, nextData);
    }

    private void changeStateAndStartNewCheckInterval(AbstractCircuitBreaker.State newState) {
        changeState(newState);
        this.checkIntervalData.set(new CheckIntervalData(0, now()));
    }

    private CheckIntervalData nextCheckIntervalData(int increment, CheckIntervalData currentData, AbstractCircuitBreaker.State currentState, long time) {
        if (stateStrategy(currentState).isCheckIntervalFinished(this, currentData, time)) {
            return new CheckIntervalData(increment, time);
        }
        return currentData.increment(increment);
    }

    /* access modifiers changed from: package-private */
    public long now() {
        return System.nanoTime();
    }

    private static StateStrategy stateStrategy(AbstractCircuitBreaker.State state) {
        return STRATEGY_MAP.get(state);
    }

    private static Map<AbstractCircuitBreaker.State, StateStrategy> createStrategyMap() {
        Map<AbstractCircuitBreaker.State, StateStrategy> map = new EnumMap<>(AbstractCircuitBreaker.State.class);
        map.put(AbstractCircuitBreaker.State.CLOSED, new StateStrategyClosed());
        map.put(AbstractCircuitBreaker.State.OPEN, new StateStrategyOpen());
        return map;
    }

    private static class CheckIntervalData {
        private final long checkIntervalStart;
        private final int eventCount;

        CheckIntervalData(int count, long intervalStart) {
            this.eventCount = count;
            this.checkIntervalStart = intervalStart;
        }

        public int getEventCount() {
            return this.eventCount;
        }

        public long getCheckIntervalStart() {
            return this.checkIntervalStart;
        }

        public CheckIntervalData increment(int delta) {
            return delta != 0 ? new CheckIntervalData(getEventCount() + delta, getCheckIntervalStart()) : this;
        }
    }

    private static abstract class StateStrategy {
        /* access modifiers changed from: protected */
        public abstract long fetchCheckInterval(EventCountCircuitBreaker eventCountCircuitBreaker);

        public abstract boolean isStateTransition(EventCountCircuitBreaker eventCountCircuitBreaker, CheckIntervalData checkIntervalData, CheckIntervalData checkIntervalData2);

        private StateStrategy() {
        }

        public boolean isCheckIntervalFinished(EventCountCircuitBreaker breaker, CheckIntervalData currentData, long now) {
            return now - currentData.getCheckIntervalStart() > fetchCheckInterval(breaker);
        }
    }

    private static class StateStrategyClosed extends StateStrategy {
        private StateStrategyClosed() {
            super();
        }

        public boolean isStateTransition(EventCountCircuitBreaker breaker, CheckIntervalData currentData, CheckIntervalData nextData) {
            return nextData.getEventCount() > breaker.getOpeningThreshold();
        }

        /* access modifiers changed from: protected */
        public long fetchCheckInterval(EventCountCircuitBreaker breaker) {
            return breaker.getOpeningInterval();
        }
    }

    private static class StateStrategyOpen extends StateStrategy {
        private StateStrategyOpen() {
            super();
        }

        public boolean isStateTransition(EventCountCircuitBreaker breaker, CheckIntervalData currentData, CheckIntervalData nextData) {
            return nextData.getCheckIntervalStart() != currentData.getCheckIntervalStart() && currentData.getEventCount() < breaker.getClosingThreshold();
        }

        /* access modifiers changed from: protected */
        public long fetchCheckInterval(EventCountCircuitBreaker breaker) {
            return breaker.getClosingInterval();
        }
    }
}
