package com.ty.mid.framework.lock.parser;

import com.ty.mid.framework.lock.annotation.Lock;
import org.aspectj.lang.JoinPoint;

public class LockParser extends AbstractLockParser<Lock> {
    static LockParser lockParser = new LockParser();

    private LockParser() {
    }

    public static LockParser getInstance() {
        return lockParser;
    }

    @Override
    public Lock convert(Lock lockAnnotation, JoinPoint joinPoint) {
        return lockAnnotation;
    }
}
