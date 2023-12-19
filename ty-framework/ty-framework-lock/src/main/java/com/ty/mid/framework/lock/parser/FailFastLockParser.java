package com.ty.mid.framework.lock.parser;

import com.ty.mid.framework.lock.annotation.FailFastLock;
import com.ty.mid.framework.lock.annotation.Lock;
import org.aspectj.lang.JoinPoint;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationType;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FailFastLockParser extends AbstractLockParser<FailFastLock> {

    static FailFastLockParser failFastLockParser = new FailFastLockParser();

    private FailFastLockParser() {
    }

    public static FailFastLockParser getInstance() {
        return failFastLockParser;
    }


    @Override
    public Lock convert(FailFastLock lockAnnotation, JoinPoint joinPoint) {
        AnnotationType instance = AnnotationType.getInstance(Lock.class);
        Map<String, Object> stringObjectMap = instance.memberDefaults();
        // 完善注解必填属性
        stringObjectMap.put("name", lockAnnotation.name());
        stringObjectMap.put("keys", lockAnnotation.keys());
        stringObjectMap.put("waitTime", 0L);
        stringObjectMap.put("timeUnit", TimeUnit.SECONDS);
        return (Lock) AnnotationParser.annotationForMap(Lock.class, stringObjectMap);
    }
}

