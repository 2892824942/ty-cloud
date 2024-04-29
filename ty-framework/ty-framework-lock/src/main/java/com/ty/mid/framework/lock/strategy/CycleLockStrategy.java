package com.ty.mid.framework.lock.strategy;

import com.ty.mid.framework.lock.exception.PotentialDeadlockException;
import lombok.extern.slf4j.Slf4j;

/**
 * 当存在死锁时的策略 <p>
 * 由于实现方式的原因,增强了死锁检测的能力,但是特殊情况性能方面不佳,不建议生产启用
 */
@Slf4j
public enum CycleLockStrategy {
    /**
     * 生效，打warming日志（仅开启supportTransaction生效）
     */
    WARMING {
        @Override
        public void handlePotentialDeadlock(PotentialDeadlockException e) {
            log.warn("Detected potential deadlock", e);
        }
    },
    /**
     * 禁止，会抛异常阻断业务逻辑（仅开启supportTransaction生效）
     */
    THROWING {
        @Override
        public void handlePotentialDeadlock(PotentialDeadlockException e) {
            throw e;
        }
    },
    /**
     * 关闭探测
     */
    DISABLED {
        @Override
        public void handlePotentialDeadlock(PotentialDeadlockException e) {
            //do nothing
        }
    },

    ;

    public abstract void handlePotentialDeadlock(PotentialDeadlockException e);
}
