package org.apache.commons.lang3.concurrent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.Validate;

public class MultiBackgroundInitializer extends BackgroundInitializer<MultiBackgroundInitializerResults> {
    private final Map<String, BackgroundInitializer<?>> childInitializers = new HashMap();

    public MultiBackgroundInitializer() {
    }

    public MultiBackgroundInitializer(ExecutorService exec) {
        super(exec);
    }

    public void addInitializer(String name, BackgroundInitializer<?> init) {
        boolean z = true;
        Validate.isTrue(name != null, "Name of child initializer must not be null!", new Object[0]);
        if (init == null) {
            z = false;
        }
        Validate.isTrue(z, "Child initializer must not be null!", new Object[0]);
        synchronized (this) {
            if (!isStarted()) {
                this.childInitializers.put(name, init);
            } else {
                throw new IllegalStateException("addInitializer() must not be called after start()!");
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getTaskCount() {
        int result = 1;
        for (BackgroundInitializer<?> bi : this.childInitializers.values()) {
            result += bi.getTaskCount();
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public MultiBackgroundInitializerResults initialize() throws Exception {
        Map<String, BackgroundInitializer<?>> inits;
        synchronized (this) {
            inits = new HashMap<>(this.childInitializers);
        }
        ExecutorService exec = getActiveExecutor();
        for (BackgroundInitializer<?> bi : inits.values()) {
            if (bi.getExternalExecutor() == null) {
                bi.setExternalExecutor(exec);
            }
            bi.start();
        }
        Map<String, Object> results = new HashMap<>();
        Map<String, ConcurrentException> excepts = new HashMap<>();
        for (Map.Entry<String, BackgroundInitializer<?>> e : inits.entrySet()) {
            try {
                results.put(e.getKey(), e.getValue().get());
            } catch (ConcurrentException cex) {
                excepts.put(e.getKey(), cex);
            }
        }
        return new MultiBackgroundInitializerResults(inits, results, excepts);
    }

    public static class MultiBackgroundInitializerResults {
        private final Map<String, ConcurrentException> exceptions;
        private final Map<String, BackgroundInitializer<?>> initializers;
        private final Map<String, Object> resultObjects;

        private MultiBackgroundInitializerResults(Map<String, BackgroundInitializer<?>> inits, Map<String, Object> results, Map<String, ConcurrentException> excepts) {
            this.initializers = inits;
            this.resultObjects = results;
            this.exceptions = excepts;
        }

        public BackgroundInitializer<?> getInitializer(String name) {
            return checkName(name);
        }

        public Object getResultObject(String name) {
            checkName(name);
            return this.resultObjects.get(name);
        }

        public boolean isException(String name) {
            checkName(name);
            return this.exceptions.containsKey(name);
        }

        public ConcurrentException getException(String name) {
            checkName(name);
            return this.exceptions.get(name);
        }

        public Set<String> initializerNames() {
            return Collections.unmodifiableSet(this.initializers.keySet());
        }

        public boolean isSuccessful() {
            return this.exceptions.isEmpty();
        }

        private BackgroundInitializer<?> checkName(String name) {
            BackgroundInitializer<?> init = this.initializers.get(name);
            if (init != null) {
                return init;
            }
            throw new NoSuchElementException("No child initializer with name " + name);
        }
    }
}
