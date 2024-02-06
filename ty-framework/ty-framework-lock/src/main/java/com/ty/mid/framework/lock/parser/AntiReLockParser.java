package com.ty.mid.framework.lock.parser;

import com.ty.mid.framework.lock.annotation.AntiReLock;
import com.ty.mid.framework.lock.annotation.Lock;
import org.aspectj.lang.JoinPoint;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationType;

import java.util.Map;

public class AntiReLockParser extends AbstractLockParser<AntiReLock> {
    static AntiReLockParser antiReLockParser = new AntiReLockParser();

    private AntiReLockParser() {
    }

    public static AntiReLockParser getInstance() {
        return antiReLockParser;
    }

    @Override
    public Lock convert(AntiReLock antiReLock, JoinPoint joinPoint) {
        AnnotationType instance = AnnotationType.getInstance(Lock.class);
        Map<String, Object> stringObjectMap = instance.memberDefaults();
        // 完善注解必填属性
        stringObjectMap.put("name", antiReLock.name());
        stringObjectMap.put("keys", antiReLock.keys());
        stringObjectMap.put("waitTime", antiReLock.waitTime());
        stringObjectMap.put("timeUnit", antiReLock.timeUnit());
        stringObjectMap.put("exceptionMsg", antiReLock.exceptionMsg());
        stringObjectMap.put("annotationClass", AntiReLock.class);
        return (Lock) AnnotationParser.annotationForMap(Lock.class, stringObjectMap);
    }
}
