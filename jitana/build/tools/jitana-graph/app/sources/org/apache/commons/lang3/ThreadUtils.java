package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ThreadUtils {
    public static final AlwaysTruePredicate ALWAYS_TRUE_PREDICATE = new AlwaysTruePredicate();

    public interface ThreadGroupPredicate {
        boolean test(ThreadGroup threadGroup);
    }

    public interface ThreadPredicate {
        boolean test(Thread thread);
    }

    public static Thread findThreadById(long threadId, ThreadGroup threadGroup) {
        Validate.isTrue(threadGroup != null, "The thread group must not be null", new Object[0]);
        Thread thread = findThreadById(threadId);
        if (thread == null || !threadGroup.equals(thread.getThreadGroup())) {
            return null;
        }
        return thread;
    }

    public static Thread findThreadById(long threadId, String threadGroupName) {
        Validate.isTrue(threadGroupName != null, "The thread group name must not be null", new Object[0]);
        Thread thread = findThreadById(threadId);
        if (thread == null || thread.getThreadGroup() == null || !thread.getThreadGroup().getName().equals(threadGroupName)) {
            return null;
        }
        return thread;
    }

    public static Collection<Thread> findThreadsByName(String threadName, ThreadGroup threadGroup) {
        return findThreads(threadGroup, false, new NamePredicate(threadName));
    }

    public static Collection<Thread> findThreadsByName(String threadName, String threadGroupName) {
        boolean z = true;
        Validate.isTrue(threadName != null, "The thread name must not be null", new Object[0]);
        if (threadGroupName == null) {
            z = false;
        }
        Validate.isTrue(z, "The thread group name must not be null", new Object[0]);
        Collection<ThreadGroup> threadGroups = findThreadGroups(new NamePredicate(threadGroupName));
        if (threadGroups.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<Thread> result = new ArrayList<>();
        NamePredicate threadNamePredicate = new NamePredicate(threadName);
        for (ThreadGroup group : threadGroups) {
            result.addAll(findThreads(group, false, threadNamePredicate));
        }
        return Collections.unmodifiableCollection(result);
    }

    public static Collection<ThreadGroup> findThreadGroupsByName(String threadGroupName) {
        return findThreadGroups(new NamePredicate(threadGroupName));
    }

    public static Collection<ThreadGroup> getAllThreadGroups() {
        return findThreadGroups(ALWAYS_TRUE_PREDICATE);
    }

    public static ThreadGroup getSystemThreadGroup() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while (threadGroup.getParent() != null) {
            threadGroup = threadGroup.getParent();
        }
        return threadGroup;
    }

    public static Collection<Thread> getAllThreads() {
        return findThreads(ALWAYS_TRUE_PREDICATE);
    }

    public static Collection<Thread> findThreadsByName(String threadName) {
        return findThreads(new NamePredicate(threadName));
    }

    public static Thread findThreadById(long threadId) {
        Collection<Thread> result = findThreads(new ThreadIdPredicate(threadId));
        if (result.isEmpty()) {
            return null;
        }
        return result.iterator().next();
    }

    private static final class AlwaysTruePredicate implements ThreadPredicate, ThreadGroupPredicate {
        private AlwaysTruePredicate() {
        }

        public boolean test(ThreadGroup threadGroup) {
            return true;
        }

        public boolean test(Thread thread) {
            return true;
        }
    }

    public static class NamePredicate implements ThreadPredicate, ThreadGroupPredicate {
        private final String name;

        public NamePredicate(String name2) {
            Validate.isTrue(name2 != null, "The name must not be null", new Object[0]);
            this.name = name2;
        }

        public boolean test(ThreadGroup threadGroup) {
            return threadGroup != null && threadGroup.getName().equals(this.name);
        }

        public boolean test(Thread thread) {
            return thread != null && thread.getName().equals(this.name);
        }
    }

    public static class ThreadIdPredicate implements ThreadPredicate {
        private final long threadId;

        public ThreadIdPredicate(long threadId2) {
            if (threadId2 > 0) {
                this.threadId = threadId2;
                return;
            }
            throw new IllegalArgumentException("The thread id must be greater than zero");
        }

        public boolean test(Thread thread) {
            return thread != null && thread.getId() == this.threadId;
        }
    }

    public static Collection<Thread> findThreads(ThreadPredicate predicate) {
        return findThreads(getSystemThreadGroup(), true, predicate);
    }

    public static Collection<ThreadGroup> findThreadGroups(ThreadGroupPredicate predicate) {
        return findThreadGroups(getSystemThreadGroup(), true, predicate);
    }

    public static Collection<Thread> findThreads(ThreadGroup group, boolean recurse, ThreadPredicate predicate) {
        Thread[] threads;
        Validate.isTrue(group != null, "The group must not be null", new Object[0]);
        Validate.isTrue(predicate != null, "The predicate must not be null", new Object[0]);
        int count = group.activeCount();
        do {
            threads = new Thread[((count / 2) + count + 1)];
            count = group.enumerate(threads, recurse);
        } while (count >= threads.length);
        List<Thread> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            if (predicate.test(threads[i])) {
                result.add(threads[i]);
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    public static Collection<ThreadGroup> findThreadGroups(ThreadGroup group, boolean recurse, ThreadGroupPredicate predicate) {
        ThreadGroup[] threadGroups;
        Validate.isTrue(group != null, "The group must not be null", new Object[0]);
        Validate.isTrue(predicate != null, "The predicate must not be null", new Object[0]);
        int count = group.activeGroupCount();
        do {
            threadGroups = new ThreadGroup[((count / 2) + count + 1)];
            count = group.enumerate(threadGroups, recurse);
        } while (count >= threadGroups.length);
        List<ThreadGroup> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            if (predicate.test(threadGroups[i])) {
                result.add(threadGroups[i]);
            }
        }
        return Collections.unmodifiableCollection(result);
    }
}
