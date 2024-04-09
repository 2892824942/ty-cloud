<!-- TOC -->

* [项目特点](#项目特点)
* [一:框架集成](#一框架集成)
    * [1.引入核心依赖](#1引入核心依赖)
* [二:使用示例](#二使用示例)
* [三:功能详情](#三功能详情)
    * [1.DO->DTO自动装载](#1do-dto自动装载)
    * [2.数据缓存](#2数据缓存)
        * [(1)能力介绍](#1能力介绍)
        * [(2)使用示例](#2使用示例)
    * [3.问题答疑](#3问题答疑)

<!-- TOC -->

开源地址:https://github.com/2892824942/ty-cloud/blob/main/ty-framework/ty-framework-service

# 项目特点

1.
自动依赖mybatis-plus模块,拥有cloud下mybatis-plus模块所有能力.具体见:https://github.com/2892824942/ty-cloud/blob/main/ty-framework/ty-framework-mybatis-plus
2. 提供实体对象缓存能力,简化简单缓存业务代码开发
3. DO<-->DTO 通过Mapstruct转换,增强DO-->DTO转换,支持简单关联字段自动映射,支持审计字段自动映射

# 一:框架集成

## 1.引入核心依赖

暂时未发到中央仓库(准备中)...

```xml

<dependency>
    <groupId>com.ty</groupId>
    <artifactId>ty-framework-service-starter</artifactId>
    <version>${最新版本}</version>
</dependency>

```

# 二:使用示例

```java

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author suyouliang
 * @since 2023-11-27
 */
@Service
public class UserServiceImpl extends AutoWrapService<User, UserFullDTO, UserMapper> implements IUserService, UserNameTranslation {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserFullDTO getById(Long id) {
        if (isNegative(id)) {
            return null;
        }
        return selectOneDTO(User::getId, id);
    }

    @Override
    public PageResult<UserFullBO> getPage(UserQuery userQuery) {
        return userMapper.selectJoinPage(userQuery);
    }

    @Override
    public List<UserFullDTO> getFullList(UserQuery userQuery) {
        userQuery.openSelectAll();
        PageResult<User> userPageResult = userMapper.selectPage(userQuery);
        return convert(userPageResult.getList(), UserFullDTO.class);
    }

    @Override
    public List<UserInfoDTO> getInfoList(UserQuery userQuery) {
        userQuery.setPageNo(PageParam.PAGE_SIZE_NONE);
        PageResult<User> userPageResult = userMapper.selectPage(userQuery);
        return convert(userPageResult.getList(), UserInfoDTO.class);
    }

    @Override
    public Boolean save(UserSaveQuery query) {
        User user = convert(query);
        return userMapper.insert(user) > 0;
    }

    @Override
    public void saveBatch(List<UserSaveQuery> queryList) {
        if (CollUtil.isEmpty(queryList)) {
            return;
        }
        List<User> userList = convert(queryList, User.class);

        userMapper.insertBatch(userList);
    }

    @Override
    public Boolean deleteById(Long id) {
        return userMapper.deleteById(id) > 0;
    }
}

```

1.Service需要继承父Service以获得对应能力,同时需要指定泛型,泛型1为数据库表对应的实体类,泛型2为主DTO类,泛型3为对应Mapper类(
如存在)
2.主要提供以下父Service

- GenericService:支持简化开发api
- AutoWrapService:支持简化开发api,支持DO->DTO自动装载
- CacheAutoWrapService:支持简化开发api,支持DO->DTO自动装载,支持自定义数据缓存
- AllCacheAutoWrapService:支持简化开发api,支持DO->DTO自动装载,支持全量数据缓存

# 三:功能详情

## 1.DO->DTO自动装载

### 1.1 基础字段自动装载
项目集成了mapstruct-plus,通过一下方式实现自动装载,具体参见[https://www.mapstruct.plus/](官方文档)

```java
@Schema(description = "用户全量对象")
@Getter
@Setter
@AutoMapper(target = User.class)
public class UserFullDTO extends AbstractNameDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "姓名")
    @ChineseNameDesensitize
    private String name;

    @Schema(description = "密码")
    @PasswordDesensitize
    private String password;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "邮箱")
    @EmailDesensitize
    private String email;

    @Schema(description = "角色信息")
    private List<RoleDTO> roleInfos;

    @Schema(description = "用户地址code")
    private AddrDTO addrInfo;

}

```
本框架已在AutoWrapperService中提供covert方法,直接转换,也可通过xxxDTO()的方法直接获取转换后的DTO.
### 1.2 数据库字段自动装载

一张表中有几个字段是映射的其他表,比如用户拥有areaId,roleCode属性,分别对应区域表及角色表.这种一个字段映射其他表是非常常见的,
在开发过程中,我们通常会定义一个DTO,将类似以上的主表内容以及其他表内容通过DTO进行封装,这就需要针对这种其他表映射字段进行封装,
当字段较多时,开发难度虽不大,但是比较繁琐.而在实际开发过程,这种情况非常常见

DO->DTO自动装载主要是针对以上场景,简化开发过程
整个自动装载流程,以User表的roleCode为例:
RoleId->查询RoleDO->转换为RoleDTO->写入到UserDTO对应属性roleDTO中 通用的步骤即:

- [字段->DO]
- [DO->DTO]
- [DTO写入目标属性]

使用方法:
(1)定义自动装载映射
当继承Service支持自动装载时,例如AutoWrapService,则[字段->查询DO]默认定义为id查询,[DO->DTO]则通过mapstruct-plus自动转换
(2)在需要自动装载的字段上,添加注解@AutoWrap,并定义需要自动装载的DTO类

```java
public class User extends BaseDO {
    
    @AutoWrap(values = {RoleDTO.class, RoleSimpleDTO.class})
    private List<Long> roleIds;
    
}
```

(3)需使用AutoWrapperService中提供covert方法,直接转换,或通过xxxDTO()的方法直接获取转换后的DTO.

- 支持字段一对一,一对多,多对多
- 支持自动装载嵌套.例如A中的b字段映射B表,B中的c字段映射C表,当b->B,c->C自动装载配置后,则A->B->C自动装载

## 2.数据缓存

### (1)能力介绍

当继承CacheAutoWrapService或AllCacheAutoWrapService支持数据缓存

- 数据会按照key,value的形式存储在缓存中
- 支持key自定义
- 缓存基于Jcache标准,可自动替换其他实现
- 缓存使用ReadThrough模式
- 支持列表查询,如List<Long> ids.
- 自动装载过程支持缓存,如果缓存的key定义和自动装载的字段相同,则自动装载过程将走缓存

### (2)使用示例

```java
@Service
public class AddressServiceImpl extends AllCacheAutoWrapService<Address, AddrDTO, AddressMapper> implements IAddressService {
    @Override
    public List<AddrDTO> getByCodesFromCache(List<String> codes) {
        Map<String, AddrDTO> all = cacheGetAll(codes);
        return new ArrayList<>(all.values());
    }

    @Override
    public AddrDTO getByCodeFromCache(String code) {
        return cacheGetByKey(code);
    }

    @Override
    public List<AddrDTO> getList(AddrQuery addrQuery) {
        addrQuery.setPageNo(PageParam.PAGE_SIZE_NONE);
        PageResult<Address> pageResult = baseMapper.getPage(addrQuery);
        if (pageResult.isEmpty()) {
            return Collections.emptyList();
        }
        return AddrDTOConvert.INSTANCE.convert(pageResult.getList());
    }

    /**
     *↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓增删改部分demo↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     */

    /**
     * 默认开启了ReadThrough,读取不到会自动查询数据库
     *
     * @param query
     * @return
     */

    @Override
    public Boolean save(AddrSaveQuery query) {
        Address address = AddrConvert.INSTANCE.convert(query);
        return save(address);
    }

    /**
     * 1.如更新不会更新key相关字段,直接删除缓存即可
     * 2.如更新会更新key相关字段,则需要先查询数据库原始数据,update后需要删除前后两个值已达到缓存重新加载的目的
     *
     * @param addrUpdateQuery
     * @return
     */
    @Override
    @Transactional
    public Boolean update(AddrUpdateQuery addrUpdateQuery) {
        Address address = AddrConvert.INSTANCE.convert(addrUpdateQuery);
        Address dbAddress = getById(address.getId());
        boolean result = updateById(address);
        if (result) {
            cacheClear(Lists.newArrayList(address, dbAddress));
        }
        return null;
    }
    

    /**
     * 删除需要手动操作缓存,更新类似
     * @param id
     * @return
     */
    @Override
    @Transactional
    public Boolean deleteById(Long id) {
        Address address = selectOne(Address::getId, id);
        if (address == null) {
            return Boolean.TRUE;
        }
        boolean result = removeById(id);
        cacheClear(address);
        return result;
    }

    /**
     * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓框架父类方法重写↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     * @return
     */

//    /**
//     * 缓存Service:
//     * 重写缓存定义key,这里缓存两个key没有实际意义,只是为了测试多个key作为缓存的场景
//     * @return
//     */
//    @Override
//    public List<SFunction<Address, ?>> cacheDefineDOMapKeys() {
//        return Lists.newArrayList(Address::getCode, Address::getName);
//    }

    /**
     * 缓存Service:
     * 缓存key和自动装载字段一致,自动装载将走缓存
     *
     * @return
     */
    @Override
    public SFunction<Address, ?> cacheDefineDOMapKey() {
        return Address::getCode;
    }

    /**
     * 自动装载Service:
     * 定义code->AddrDTO自动装载
     *
     * @return
     */
    @Override
    public Map<?, AddrDTO> autoWrap(Collection<?> collection) {
        Function<List<Address>, List<AddrDTO>> function = AddrDTOConvert.INSTANCE::convert;
        return convert(GenericsUtil.check2Collection(collection), Address::getCode, AddrDTO::getCode, function);
    }

}
```

## 3.问题答疑

问题1:如果定义的字段不是id或者[DO->DTO]过程部分字段名称不一致无法自动转换怎么办

答:顶层接口定义了autoWrap的方法,支持重写[字段->DO]和[DO->DTO]过程

```java
@Service
public class AddressServiceImpl extends AllCacheAutoWrapService<Address, AddrDTO, AddressMapper> implements IAddressService {
    /**
     * 自动装载Service:
     * 定义code->AddrDTO自动装载
     *
     * @return
     */
    @Override
    public Map<?, AddrDTO> autoWrap(Collection<?> collection) {
        //通过Mapstruct重新重新定义[DO->DTO]过程
        Function<List<Address>, List<AddrDTO>> function = AddrDTOConvert.INSTANCE::convert;
        //指定[字段->查询DO]使用code查询
        return convert(GenericsUtil.check2Collection(collection), Address::getCode, AddrDTO::getCode, function);
    }
}
```

问题2:一个表中既有id又有code多字段自动映射,或者一个字段可能不同的场景返回的DTO不同怎么办?

答:自定义AutoWrapper,可以通过再次注册AutoWrapper来为当前表定义其他字段的自动装载

```java
@Service
public class RoleServiceImpl extends GenericService<Role, RoleDTO, RoleMapper> implements IRoleService {
   /**
   * 为角色表定义code->RoleSimpleDTO自动装载
   * @return
   */
    @Bean
    public AutoWrapper<Role> roleSimpleDTOAutoWrapper() {
        //注意:不可以省略后面的泛型否则报错,默认使用maperstruct-plus能力自动转换,也可重写对应方法
        return new AutoWrapService<Role, RoleSimpleDTO, RoleMapper>() {
        };
    }
}
```

如上,此时Role支持[code->RoleSimpleDTO]和[code->RoleDTO]两种自动装载

问题3:自动装配和JPA好像啊?

是的,自动装配也参考了JPA的思路,但是JPA的级联join查询不被大多数互联网公司接受.但是自动将目标数据装载到当前DTO确实是个非常方便的方式,尤其在常见的id,code等单字段映射场景.
自动装配采用的是sql in的方式查询,不使用join.

注意:目前AutoWrapper是按照DTO类型做自动识别,所以不支持多个字段转换为同一个DTO.

更详细的使用案例,见:https://github.com/2892824942/framework-demo