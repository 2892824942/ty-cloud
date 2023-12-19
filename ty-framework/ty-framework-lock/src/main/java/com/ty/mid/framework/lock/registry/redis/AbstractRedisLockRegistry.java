//package com.ty.mid.framework.lock.registry.redis;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.integration.support.locks.LockRegistry;
//import org.springframework.util.Assert;
//
//
//@Slf4j
//public abstract class AbstractRedisLockRegistry implements LockRegistry {
//
//
//    private RedisConnectionFactory connectionFactory;
//
//    public AbstractRedisLockRegistry(RedisConnectionFactory connectionFactory) {
//        Assert.notNull(connectionFactory, "'connectionFactory' cannot be null");
//        this.connectionFactory = connectionFactory;
//    }
//
//}
