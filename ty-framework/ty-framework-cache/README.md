开源地址:https://github.com/2892824942/ty-cloud/blob/main/ty-framework/ty-framework-cache

# 项目特点

## 1.保留原框架的集成，扩展，配置能力

- 集成
  通过依赖对应的cache实现，可自动装载对应配置
- 扩展
  依赖原spring-cache核心能力，保留其扩展能力
- 配置
  原spring配置同样生效，如不使用cache-plus框架能力，原生配置不受影响
  使用cache-plus相关功能，仅更换配置前缀即可

## 2.对于spring-cache增强

- 支持多cache同时使用
- 支持Redisson实现的CacheManager自动注入
- 支持具体缓存失效时间配置,全局配置及注解均支持
- 支持二级缓存
- 支持强一致事务缓存

## 1.引入核心依赖

暂时未发到中央仓库(准备中)...

```xml

<dependency>
    <groupId>com.ty</groupId>
    <artifactId>ty-framework-cache-starter</artifactId>
    <version>${最新版本}</version>
</dependency>

```

## 2.开启多cache配置

application.yml示例：

```yaml
application:
  cache:
    multi-enable: true
    type: redis,caffeine
    redis:
      cache-names: test1,test2
      enable-transactions: true
      store-type: key_value
      cache-null-values: true
      null-value-time-to-live: PT20s
      time-to-live: PT20m
      use-key-prefix: true
      key-prefix: cache
    caffeine:
      cache-names: caffeine-name
```

## 2.自选缓存pom依赖

- 如使用redis缓存,按照Spring Boot data Redis官方配置即可,以下为使用Redisson客户端pom:

```xml

<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
</dependency>

<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

正确配置redis示例

```yaml
  redis:
    host: localhost
    port: 6379
    timeout: 1200
```

- 如使用caffeine缓存,添加如下pom:

```xml

<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

# 二:使用示例

## 1.api示例

和常规spring cache一致

```java

@Component
public class test {
    @Autowired
    CacheManager cacheManager;

    public void cache() {
        Cache cache = cacheManager.getCache("test1");
        cache.put("aaa", 123);
        Integer value = cache.get("aaa", Integer.class);
    }
}
```

## 2.注解示例

和spring cache一致

```java

@Service
@Slf4j
@CacheConfig(cacheNames = CacheNameConstant.CAHCE_NAME)
public class CapitalAccountCacheService {
    @Autowired
    private MyCapitalCacheDAO myCapitalDAO;
    @Autowired
    @Lazy
    private CapitalAccountCacheService _self;

    public List<MyCapitalAccount> getAll() {
        return myCapitalDAO.getAll();
    }

    @Cacheable(key = "#id", sync = true)
    public MyCapitalAccount getById(Long id) {
        MyCapitalAccount byId = myCapitalDAO.getById(id);
        log.info("CapitalAccountCacheService getById from db,myCapitalAccount:{}", byId);
        return byId;
    }


    @CachePut(key = "#myCapitalAccount.id", condition = "#result.getId()!=null&&#result.getId()>0")
    public MyCapitalAccount insert(MyCapitalAccount myCapitalAccount) {
        log.info("CapitalAccountCacheService insert to db,myCapitalAccount:{}", myCapitalAccount);
        myCapitalDAO.insert(myCapitalAccount);
        return myCapitalAccount;
    }


    @CachePut(key = "#myCapitalAccount.id", condition = "#result!=null")
    public MyCapitalAccount update(MyCapitalAccount myCapitalAccount) {
        log.info("CapitalAccountCacheService update to db,myCapitalAccount:{}", myCapitalAccount);
        int update = myCapitalDAO.update(myCapitalAccount);
        if (update > 0) {
            return myCapitalAccount;
        }
        return null;
    }


    @CacheEvict(key = "#myCapitalAccount.id")
    public int delete(MyCapitalAccount myCapitalAccount) {
        log.info("CapitalAccountCacheService delete to db,myCapitalAccount:{}", myCapitalAccount);
        return myCapitalDAO.deleteById(myCapitalAccount.getId());
    }

    @CacheEvict(allEntries = true)
    public int deleteAll() {
        log.info("CapitalAccountCacheService delete All from db");
        return myCapitalDAO.deleteAll();

    }
}    
```

## 三:配置说明

除了spring-cache提供的缓存类型外,框架提供redis额外两个实现:
REDISSON_2PC,

```
 REDISSON_LOCAL_MAP 基于redisssonRLocalMap实现的二级缓存CacheManager
 REDISSON_2PC 基于redisson实现的支持强一致性事务的缓存CacheManager
```

## 1.REDISSON_LOCAL_MAP

Redisson官方宣传的,增加了本地的缓存,性能提升最高45倍,适用于对数据一致性要求不高,但访问量较大,对性能要求极高,甚至连网络请求都成为瓶颈的场景

## 2.REDISSON_2PC

基于redisson实现的支持强一致性事务的缓存CacheManager,整合mysql事务请求,在RR隔离级别下,通过事务线程绑定方式,在本地增加TreadLocal二级缓存,
保证缓存事务过程的可重复读,在mysql事务提交过程,通过redis事务提交或回滚redis相关操作,保证强一致性

更详细的使用案例,见:https://github.com/2892824942/framework-demo