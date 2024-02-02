package com.ty.mid.framework.lock.decorator.cycle;

import lombok.extern.slf4j.Slf4j;

/**
   * Pre-defined {@link Policy} implementations.
   *
   * @since 13.0
   */
@Slf4j
  public enum Policies implements Policy {
    /**
     * When potential deadlock is detected, this policy results in the throwing of the {@code
     * PotentialDeadlockException} indicating the potential deadlock, which includes stack traces
     * illustrating the cycle in lock acquisition order.
     */
    THROW {
      @Override
      public void handlePotentialDeadlock(PotentialDeadlockException e) {
        throw e;
      }
    },

    /**
     * When potential deadlock is detected, this policy results in the logging of a {@link
     * Level#SEVERE} message indicating the potential deadlock, which includes stack traces
     * illustrating the cycle in lock acquisition order.
     */
    WARN {
      @Override
      public void handlePotentialDeadlock(PotentialDeadlockException e) {
        log.warn("Detected potential deadlock", e);
      }
    },

    /**
     * Disables cycle detection. This option causes the factory to return unmodified lock
     * implementations provided by the JDK, and is provided to allow applications to easily
     * parameterize when cycle detection is enabled.
     *
     * <p>Note that locks created by a factory with this policy will <em>not</em> participate the
     * cycle detection performed by locks created by other factories.
     */
    DISABLED {
      @Override
      public void handlePotentialDeadlock(PotentialDeadlockException e) {}
    };
  }