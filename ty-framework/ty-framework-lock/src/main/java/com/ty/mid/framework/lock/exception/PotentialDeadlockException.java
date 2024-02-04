package com.ty.mid.framework.lock.exception;

import com.ty.mid.framework.lock.decorator.cycle.LockGraphNode;

import static java.util.Objects.requireNonNull;

/**
 * Represents a detected cycle in lock acquisition ordering. The exception includes a causal chain
 * of {@code ExampleStackTrace} instances to illustrate the cycle, e.g.
 *
 * <pre>
 * com....PotentialDeadlockException: Potential Deadlock from LockC -&gt; ReadWriteA
 *   at ...
 *   at ...
 * Caused by: com...ExampleStackTrace: LockB -&gt; LockC
 *   at ...
 *   at ...
 * Caused by: com...ExampleStackTrace: ReadWriteA -&gt; LockB
 *   at ...
 *   at ...
 * </pre>
 *
 * <p>Instances are logged for the {@code Policies.WARN}, and thrown for {@code Policies.THROW}.
 *
 * @since 13.0
 */
public final class PotentialDeadlockException extends ExampleStackTrace {

    private final ExampleStackTrace conflictingStackTrace;

    public PotentialDeadlockException(
            LockGraphNode node1, LockGraphNode node2, ExampleStackTrace conflictingStackTrace) {
        super(node1, node2);
        this.conflictingStackTrace = conflictingStackTrace;
        initCause(conflictingStackTrace);
    }

    public ExampleStackTrace getConflictingStackTrace() {
        return conflictingStackTrace;
    }

    /**
     * Appends the chain of messages from the {@code conflictingStackTrace} to the original {@code
     * message}.
     */
    @Override
    public String getMessage() {
        // requireNonNull is safe because ExampleStackTrace sets a non-null message.
        StringBuilder message = new StringBuilder(requireNonNull(super.getMessage()));
        for (Throwable t = conflictingStackTrace; t != null; t = t.getCause()) {
            message.append(", ").append(t.getMessage());
        }
        return message.toString();
    }
}