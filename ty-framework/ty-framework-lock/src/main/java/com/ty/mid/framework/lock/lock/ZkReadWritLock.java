package com.ty.mid.framework.lock.lock;

import lombok.Data;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

@Data
public class ZkReadWritLock implements ReadWriteLock {
    private InterProcessReadWriteLock interProcessReadWriteLock;

    public ZkReadWritLock(InterProcessReadWriteLock interProcessReadWriteLock) {
        this.interProcessReadWriteLock = interProcessReadWriteLock;
    }

    @Override
    public Lock readLock() {
        return new ZkLock(interProcessReadWriteLock.readLock());
    }

    @Override
    public Lock writeLock() {
        return new ZkLock(interProcessReadWriteLock.writeLock());
    }
}
