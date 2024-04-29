package com.ty.mid.framework.lock.decorator.cycle;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.WeakConcurrentMap;
import com.ty.mid.framework.lock.exception.ExampleStackTrace;
import com.ty.mid.framework.lock.exception.PotentialDeadlockException;
import com.ty.mid.framework.lock.strategy.CycleLockStrategy;
import lombok.Getter;

import javax.annotation.CheckForNull;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@code LockGraphNode} associated with each lock instance keeps track of the directed edges in <p>
 * the lock acquisition graph. 
 */
public class LockGraphNode {

    /**
     * The map tracking the locks that are known to be acquired before this lock, each associated
     * with an example stack trace. Locks are weakly keyed to allow proper garbage collection when
     * they are no longer referenced.
     */
    final Map<LockGraphNode, ExampleStackTrace> allowedPriorLocks =
            new WeakConcurrentMap<>();

    /**
     * The map tracking lock nodes that can cause a lock acquisition cycle if acquired before this
     * node.
     */
    final Map<LockGraphNode, PotentialDeadlockException> disallowedPriorLocks =
            new WeakConcurrentMap<>();

    @Getter
    final String lockName;

    public LockGraphNode(String lockName) {
        this.lockName = Assert.notBlank(lockName);
    }

    void checkAcquiredLocks(CycleLockStrategy strategy, List<LockGraphNode> acquiredLocks) {
        for (LockGraphNode acquiredLock : acquiredLocks) {
            checkAcquiredLock(strategy, acquiredLock);
        }
    }

    /**
     * Checks the acquisition-ordering between {@code this}, which is about to be acquired, and the
     * specified {@code acquiredLock}.
     *
     *When this method returns, the {@code acquiredLock} should be in either the {@code
     * preAcquireLocks} map, for the case in which it is safe to acquire {@code this} after the
     * {@code acquiredLock}, or in the {@code disallowedPriorLocks} map, in which case it is not
     * safe.
     */
    void checkAcquiredLock(CycleLockStrategy strategy, LockGraphNode acquiredLock) {
        // checkAcquiredLock() should never be invoked by a lock that has already
        // been acquired. For unordered locks, aboutToAcquire() ensures this by
        // checking isAcquiredByCurrentThread(). For ordered locks, however, this
        // can happen because multiple locks may share the same LockGraphNode. In
        // this situation, throw an IllegalStateException as defined by contract
        // described in the documentation of WithExplicitOrdering.
//      Preconditions.checkState(
//          this != acquiredLock,
//          "Attempted to acquire multiple locks with the same rank %s",
//          acquiredLock.getLockName());

        if (allowedPriorLocks.containsKey(acquiredLock)) {
            // The acquisition ordering from "acquiredLock" to "this" has already
            // been verified as safe. In a properly written application, this is
            // the common case.
            return;
        }
        PotentialDeadlockException previousDeadlockException = disallowedPriorLocks.get(acquiredLock);
        if (previousDeadlockException != null) {
            // Previously determined to be an unsafe lock acquisition.
            // Create a new PotentialDeadlockException with the same causal chain
            // (the example cycle) as that of the cached exception.
            PotentialDeadlockException exception =
                    new PotentialDeadlockException(
                            acquiredLock, this, previousDeadlockException.getConflictingStackTrace());
            strategy.handlePotentialDeadlock(exception);
            return;
        }
        // Otherwise, it's the first time seeing this lock relationship. Look for
        // a path from the acquiredLock to this.
        Set<LockGraphNode> seen = new HashSet<>();
        ExampleStackTrace path = acquiredLock.findPathTo(this, seen);

        if (path == null) {
            // this can be safely acquired after the acquiredLock.
            //
            // Note that there is a race condition here which can result in missing
            // a cyclic edge: it's possible for two threads to simultaneous find
            // "safe" edges which together form a cycle. Preventing this race
            // condition efficiently without _introducing_ deadlock is probably
            // tricky. For now, just accept the race condition---missing a warning
            // now and then is still better than having no deadlock detection.
            allowedPriorLocks.put(acquiredLock, new ExampleStackTrace(acquiredLock, this));
        } else {
            // Unsafe acquisition order detected. Create and cache a
            // PotentialDeadlockException.
            PotentialDeadlockException exception =
                    new PotentialDeadlockException(acquiredLock, this, path);
            disallowedPriorLocks.put(acquiredLock, exception);
            strategy.handlePotentialDeadlock(exception);
        }
    }

    /**
     * Performs a depth-first traversal of the graph edges defined by each node's {@code
     * allowedPriorLocks} to find a path between {@code this} and the specified {@code lock}.
     *
     * @return If a path was found, a chained {@link ExampleStackTrace} illustrating the path to the
     * {@code lock}, or {@code null} if no path was found.
     */
    @CheckForNull
    public ExampleStackTrace findPathTo(LockGraphNode node, Set<LockGraphNode> seen) {
        if (!seen.add(this)) {
            return null; // Already traversed this node.
        }
        ExampleStackTrace found = allowedPriorLocks.get(node);
        if (found != null) {
            return found; // Found a path ending at the node!
        }
        // Recurse the edges.
        for (Map.Entry<LockGraphNode, ExampleStackTrace> entry : allowedPriorLocks.entrySet()) {
            LockGraphNode preAcquiredLock = entry.getKey();
            found = preAcquiredLock.findPathTo(node, seen);
            if (found != null) {
                // One of this node's allowedPriorLocks found a path. Prepend an
                // ExampleStackTrace(preAcquiredLock, this) to the returned chain of
                // ExampleStackTraces.
                ExampleStackTrace path = new ExampleStackTrace(preAcquiredLock, this);
                path.setStackTrace(entry.getValue().getStackTrace());
                path.initCause(found);
                return path;
            }
        }
        return null;
    }
}