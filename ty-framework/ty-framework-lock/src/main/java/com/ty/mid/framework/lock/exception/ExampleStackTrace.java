package com.ty.mid.framework.lock.exception;

import com.google.common.collect.ImmutableSet;
import com.ty.mid.framework.lock.decorator.cycle.CycleDetectingLockDecorator;
import com.ty.mid.framework.lock.decorator.cycle.LockGraphNode;

import java.util.Arrays;

/**
 * A Throwable used to record a stack trace that illustrates an example of a specific lock
 * acquisition ordering. The top of the stack trace is truncated such that it starts with the
 * acquisition of the lock in question, e.g.
 *
 * <pre>
 * com...ExampleStackTrace: LockB -&gt; LockC
 *   at com...CycleDetectingReentrantLock.lock(CycleDetectingLockFactory.java:443)
 *   at ...
 *   at ...
 *   at com...MyClass.someMethodThatAcquiresLockB(MyClass.java:123)
 * </pre>
 */
public class ExampleStackTrace extends IllegalStateException {

    static final ImmutableSet<String> EXCLUDED_CLASS_NAMES =
            ImmutableSet.of(
                    CycleDetectingLockDecorator.class.getName(),
                    ExampleStackTrace.class.getName(),
                    LockGraphNode.class.getName());

    public ExampleStackTrace(LockGraphNode node1, LockGraphNode node2) {
        super(node1.getLockName() + " -> " + node2.getLockName());
        StackTraceElement[] origStackTrace = getStackTrace();
        for (int i = 0, n = origStackTrace.length; i < n; i++) {
            if (!EXCLUDED_CLASS_NAMES.contains(origStackTrace[i].getClassName())) {
                setStackTrace(Arrays.copyOfRange(origStackTrace, i, n));
                break;
            }
        }
    }
}