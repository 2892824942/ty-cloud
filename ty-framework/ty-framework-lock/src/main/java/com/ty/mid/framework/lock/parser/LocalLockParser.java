package com.ty.mid.framework.lock.parser;

import com.ty.mid.framework.lock.annotation.LocalLock;
import com.ty.mid.framework.lock.annotation.Lock;
import com.ty.mid.framework.lock.config.LockConfig;
import org.aspectj.lang.JoinPoint;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationType;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LocalLockParser extends AbstractLockParser<LocalLock> {
    static LocalLockParser localLockParser = new LocalLockParser();

    private LocalLockParser() {
    }

    public static LocalLockParser getInstance() {
        return localLockParser;
    }

    @Override
    public Lock convert(LocalLock lockAnnotation, JoinPoint joinPoint) {
        AnnotationType instance = AnnotationType.getInstance(Lock.class);
        Map<String, Object> stringObjectMap = instance.memberDefaults();
        // 完善注解必填属性
        stringObjectMap.put("name", lockAnnotation.name());
        stringObjectMap.put("keys", lockAnnotation.keys());
        stringObjectMap.put("waitTime", 0L);
        stringObjectMap.put("timeUnit", TimeUnit.SECONDS);
        stringObjectMap.put("withLocalCache", Boolean.FALSE);
        stringObjectMap.put("implementer", LockConfig.LockImplementer.JVM);
        return (Lock) AnnotationParser.annotationForMap(Lock.class, stringObjectMap);
    }
}
