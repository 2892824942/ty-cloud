package com.ty.mid.framework.lock.parser;

import com.ty.mid.framework.lock.annotation.Lock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

@Slf4j
public abstract class AbstractLockParser<T> implements LockAnnotationParser {
    public abstract Lock convert(T lockAnnotation, JoinPoint joinPoint);
}
