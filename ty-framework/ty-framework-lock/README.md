# spring-boot-Lock-starter

分布式锁spring-boot starter组件，

# 快速开始

> spring boot项目接入


1.项目特点

- 项目集成分布式锁能力变得异常简单
- 抽象分布式锁的集成，方便快速切换分布式锁方案
- 锁的调用基于java顶层接口，实现切换无感知（特色实现需面向具体调用）
- 提供注解式分布式锁实现

目前支持本地lock，spring官方实现的redislock，redisson实现的redis lock
支持带有本地缓存的分布式锁（以上分布式lock）

2.application.properties配置redis链接：framework.lock.address=127.0.0.1:6379

3.在需要加分布式锁的方法上，添加注解@Lock，如：

```java
@Service
public class TestService {

    @Lock(waitTime = Long.MAX_VALUE)
    public String getValue(String param) throws Exception {
        if ("sleep".equals(param)) {//线程休眠或者断点阻塞，达到一直占用锁的测试效果
            Thread.sleep(1000 * 50);
        }
        return "success";
    }
}

```

4. 支持锁指定的业务key，如同一个方法ID入参相同的加锁，其他的放行。业务key的获取支持Spel

# 使用登记

如果这个项目解决了你的实际问题，可在[https://gitee.com/kekingcn/spring-boot-Lock-starter/issues/IH4NE](http://https://gitee.com/kekingcn/spring-boot-Lock-starter/issues/IH4NE)
登记下，如果节省了你的研发时间，也愿意支持下的话，可点击下方【捐助】请作者喝杯咖啡，也是非常感谢

#关于常见的Redisson实现和java Lock的定义不同问题
项目主要是基于Java Lock的定义调用，而比较常见的分布式锁实现是redis的Redisson实现，对于前者，是没有定义带失效时间的tryLock方法的
tryLock（long waitTime,TimeUnit unit） ----java lock
tryLock（long waitTime,long leaseTime,TimeUnit unit） ----Redisson RLock
所以在面向java lock调用时 将没有带leaseTime的tryLock api，项目的注解方式在实现的时候加入了方言支持,对于redisson的实现类,对应注解支持失效时间
