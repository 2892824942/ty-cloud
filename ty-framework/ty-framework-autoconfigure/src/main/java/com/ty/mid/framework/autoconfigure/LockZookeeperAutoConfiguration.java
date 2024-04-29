package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.lock.config.LockConfig;
import com.ty.mid.framework.lock.factory.LockFactory;
import com.ty.mid.framework.lock.manager.AbstractTypeLockManager;
import com.ty.mid.framework.lock.manager.LockManagerKeeper;
import com.ty.mid.framework.lock.manager.support.JvmLockManager;
import com.ty.mid.framework.lock.manager.support.ZkLockManager;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @date 2022/03/26 <p>
 * Content :lock自动装配 <p>
 * 满足条件,redis分布式锁就会自动加载到Spring上下文已供使用 
 */
@ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "enable", matchIfMissing = true)
@AutoConfigureAfter(CuratorAutoConfiguration.class)
@ConditionalOnBean(CuratorFramework.class)
public class LockZookeeperAutoConfiguration {


    @Bean
    public ZkLockManager zkLockManager(LockManagerKeeper LockManagerKeeper, CuratorFramework curatorFramework) {
        List<AbstractTypeLockManager> typeLockManagers = LockManagerKeeper.getTypeLockManagers();
        if (typeLockManagers == null) {
            typeLockManagers = new ArrayList<>();
            typeLockManagers.add(new JvmLockManager());
        }
        ZkLockManager zkLockManager = new ZkLockManager(curatorFramework);
        typeLockManagers.add(zkLockManager);
        return zkLockManager;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "implementer", havingValue = "zookeeper")
    public LockRegistry lockRegistry(ZkLockManager zkLockManager) {
        return zkLockManager.getLockRegistry();
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = LockConfig.PREFIX, name = "implementer", havingValue = "zookeeper")
    public LockFactory lockFactory(ZkLockManager zkLockManager) {
        return zkLockManager.getLockFactory();
    }


}
