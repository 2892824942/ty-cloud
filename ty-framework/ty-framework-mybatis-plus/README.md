<!-- TOC -->
* [项目特点](#项目特点)
* [一:框架集成](#一框架集成)
  * [1.引入核心依赖](#1引入核心依赖)
  * [2.使用Mybatis-plus-join能力](#2使用mybatis-plus-join能力)
* [二:使用示例](#二使用示例)
* [三:typeHandler](#三typehandler)
  * [1.加密](#1加密)
  * [2.","连接格式](#2连接格式)
  * [3.json](#3json)
  * [4.默认值忽略](#4默认值忽略)
<!-- TOC -->
# 项目特点
开源地址:https://github.com/2892824942/ty-cloud/blob/main/ty-framework/ty-framework-service

1. 自动集成Mybatis-plus,提供统一的ORM层代码格式,同时支持Mybatis-plus-join(可选)
2. 提供父类Mapper以及Service,简化开发.支持连表查询,分页查询,简化非空判断等
3. 基于Mybatis TypeHandler,提供数据加密,json格式解析,连接符解析等能力

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

## 2.使用Mybatis-plus-join能力

```xml

<dependency>
  <groupId>com.github.yulichang</groupId>
  <artifactId>mybatis-plus-join-boot-starter</artifactId>
</dependency>
```
如不使用代码连表能力无需依赖

# 二:使用示例

```java
@Mapper
public interface UserMapper extends BaseMapperX<User, Long> {
  /**
   * 分页查询
   * @param userQuery
   * @return
   */
  default PageResult<User> selectPage(@Param("userPageQuery") UserQuery userQuery) {
    LambdaQueryWrapperX<User> wrapper = new LambdaQueryWrapperX<User>()
            .eqIfPresent(User::getId, userQuery.getId())
            .likeIfPresent(User::getName, userQuery.getName())
            .eqIfPresent(User::getAge, userQuery.getAge())
            .eqIfPresent(User::getAddrCode, userQuery.getAddrCode());

    return selectPage(userQuery, wrapper);
  }

  /**
   * 连表查询
   * @param userQuery
   * @return
   */
  default PageResult<UserFullBO> selectJoinPage(@Param("userPageQuery") UserQuery userQuery) {
    MPJLambdaWrapperX<User> wrapper = new MPJLambdaWrapperX<User>()
            .selectAll(User.class)
            .selectAs(Address::getName, UserFullBO::getAddrName)
            .leftJoin(Address.class, "addr", on -> on
                    .eq(Address::getCode, User::getAddrCode))
            .eqIfPresent(User::getCreator, userQuery.getId())
            .likeIfPresent(User::getName, userQuery.getName())
            .eqIfPresent(User::getAge, userQuery.getAge());

    return selectJoinPage(UserFullBO.class, userQuery, wrapper);
  }

}


/**
 * 实体DO
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "address", autoResultMap = true)
@Data
public class Address extends BaseDO {
  /**
   * 地址名称
   */
  private String name;
  /**
   * 地址标识
   */
  private String code;


}


```
1.Mapper需要继承BaseMapperX,同时需要指定泛型,泛型1为数据库表对应的实体类,泛型2为主键类型
2.查询时提供三个Wrapper:
(1)LambdaQueryWrapperWrapperX:支持lambda数据拼接
(2)QueryWrapperWrapperX:支持原生sql数据拼接
(3)MPJLambdaWrapperX:支持连表查询(基于mybatis-plus-join)
3.数据库实体提供统一的BaseDO继承,规范数据库审计相关字段,其中包括:
```java
    /**
    * 基础实体对象
    *
    * @author suyoulinag
    */
  @Setter
  public abstract class BaseDO implements Auditable<Long>, Serializable {

      /**
       * 用户id
       */

      @Schema(description = "主键ID")
      @TableId(type = IdType.AUTO)
      private Long id;

      /**
       * 创建者
       */
      @Schema(description = "创建者")
      @TableField(fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
      private Long creator;
      /**
       * 更新者
       */
      @Schema(description = "更新者")
      @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.VARCHAR)
      private Long updater;

      /**
       * 创建时间
       */
      @Schema(description = "创建时间")
      @TableField(fill = FieldFill.INSERT)
      private LocalDateTime createTime;
      /**
       * 最后更新时间
       */
      @Schema(description = "最后更新时间")
      @TableField(fill = FieldFill.INSERT_UPDATE)
      private LocalDateTime updateTime;

      /**
       * 是否删除 0:未删除 1:已删除
       * @see com.ty.mid.framework.common.constant.DeletedEnum
       */
      @Schema(description = "是否删除 0:未删除 1:已删除")
      @TableLogic
      private Boolean deleted;
  }
```
审计字段配合security模块自动填充.
特殊的"当上下文用户信息为空时,将使用默认的审计字段.
# 三:typeHandler
```java
public class User extends BaseDO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "姓名")
    @TableField("`name`")
    private String name;

    @Schema(description = "密码")
    @TableField(value = "`password`", typeHandler = EncryptTypeHandler.class)
    private String password;

    @Schema(description = "角色id列表,多个使用,号隔开")
    @TableField(value = "`role_ids`", typeHandler = LongListTypeHandler.class)
    @BMapping(values = {RoleDTO.class, RoleSimpleDTO.class})
    private List<Long> roleIds;

    @Schema(description = "年龄")
    @TableField(value = "`age`", typeHandler = DefaultTypeHandler.class)
    private Integer age;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户地址code")
    @TableField(value = "`addr_code`")
    @BMapping(values = AddrDTO.class)
    private String addrCode;
}
```

## 1.加密
可通过`@TableField(typeHandler = EncryptTypeHandler.class)`注解将数据库字段加密,目前仅支持AES加密
秘钥可通过配置`mybatis-plus.encryptor.password`设置

## 2.","连接格式
通过`@TableField(typeHandler = LongListTypeHandler.class)`注解将数据库字段映射为`List<Long>`,同时支持
IntegerListTypeHandler及StringListTypeHandler,分别将数据库字段映射为`List<Integer>`及`List<String>`

## 3.json
通过`@TableField(typeHandler = JsonLongSetTypeHandler.class)`注解将数据库字段映射为` Set<?>`

## 4.默认值忽略
数据库一般不建议使用null值, 因此一般数据库都会使用notNull的配置,在此前提下,部分非varchar的字段可能需要默认值,但是无论设置那种默认值,在数据查询的时候,
默认值总是让使用者困惑(比如age字段默认值为0).
如需要忽略默认值, 可通过`@TableField(typeHandler = DefaultTypeHandler.class)`将默认值转化为null.
系统全局默认值见:DefaultTypeConstants


更详细的使用案例,见:https://github.com/2892824942/framework-demo