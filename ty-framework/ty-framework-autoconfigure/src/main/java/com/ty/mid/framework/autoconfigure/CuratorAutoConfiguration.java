package com.ty.mid.framework.autoconfigure;

import com.ty.mid.framework.lock.config.LockConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @date 2022/03/26
 * Content :lock自动装配
 * 只要加入自动配置,直接会加载,除非enable设置为false
 */
@ConditionalOnClass(CuratorFramework.class)
@EnableConfigurationProperties({LockConfig.class, CuratorConfig.class})
public class CuratorAutoConfiguration {
    /**
     * 初始化curator客户端
     * 这个CuratorFramework就是客户端
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CuratorConfig.PREFIX, name = "address")
    public CuratorFramework curatorFramework(CuratorConfig curatorConfig) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(curatorConfig.getSleepTimeOut(), curatorConfig.getMaxRetries());
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(curatorConfig.getAddress())
                .connectionTimeoutMs(curatorConfig.getConnectionTimeout())
                .sessionTimeoutMs(curatorConfig.getSessionTimeout())
                .namespace(curatorConfig.getNamespace())
                .retryPolicy(retryPolicy).build();
        client.start();
        return client;
    }

}
