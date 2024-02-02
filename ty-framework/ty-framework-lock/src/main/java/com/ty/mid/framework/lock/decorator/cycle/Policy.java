package com.ty.mid.framework.lock.decorator.cycle;

/**
   * Encapsulates the action to be taken when a potential deadlock is encountered. Clients can use
   * one of the predefined {@link Policies} or specify a custom implementation. Implementations must
   * be thread-safe.
   *
   * @since 13.0
   */
  public interface Policy {

    /**
     * Called when a potential deadlock is encountered. Implementations can throw the given {@code
     * exception} and/or execute other desired logic.
     *
     * <p>Note that the method will be called even upon an invocation of {@code tryLock()}. Although
     * {@code tryLock()} technically recovers from deadlock by eventually timing out, this behavior
     * is chosen based on the assumption that it is the application's wish to prohibit any cyclical
     * lock acquisitions.
     */
    void handlePotentialDeadlock(PotentialDeadlockException exception);
  }
