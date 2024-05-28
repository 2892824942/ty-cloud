package com.ty.mid.framework.lock.exception;

import com.ty.mid.framework.common.util.collection.SetUtils;
import com.ty.mid.framework.lock.decorator.cycle.CycleDetectingLockDecorator;
import com.ty.mid.framework.lock.decorator.cycle.LockGraphNode;

import java.util.Arrays;
import java.util.Set;

/**
 * A Throwable used to record a stack trace that illustrates an example of a specific lock <p>
 * acquisition ordering. The top of the stack trace is truncated such that it starts with the <p>
 * acquisition of the lock in question, e.g. <p>
 * <pre> <p>
 * com...ExampleStackTrace: LockB -&gt; LockC <p>
 *   at com...CycleDetectingReentrantLock.lock(CycleDetectingLockFactory.java:443) <p>
 *   at ... <p>
 *   at ... <p>
 *   at com...MyClass.someMethodThatAcquiresLockB(MyClass.java:123) <p>
 * </pre>
 */
public class ExampleStackTrace extends IllegalStateException {

    static final Set<String> EXCLUDED_CLASS_NAMES =
            SetUtils.asSet(
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