package com.ty.mid.framework.mybatisplus.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 在使用存在父类的泛型的Lambda表达式时会报错： <p>
 * {@code MybatisPlusException: can not find lambda cache for this entity [com.copm.ifm.base.basic.pojo.BaseTreePO]} <p>
 * 原因是在执行{@link com.baomidou.mybatisplus.core.toolkit.LambdaUtils#getColumnMap(Class)}时 <p>
 * {@code COLUMN_CACHE_MAP}中没有{@link BaseDO}的信息 <p>
 * 根据源码 <p>
 * {@link com.baomidou.mybatisplus.core.MybatisMapperRegistry#addMapper(Class)} <p>
 * {@link com.baomidou.mybatisplus.core.MybatisMapperAnnotationBuilder#parse()} <p>
 * {@link com.baomidou.mybatisplus.core.MybatisMapperAnnotationBuilder parsePendingMethods()} <p>
 * {@link com.baomidou.mybatisplus.core.injector.AbstractSqlInjector inspectInject(MapperBuilderAssistant, Class)} <p>
 * {@link com.baomidou.mybatisplus.core.metadata.TableInfoHelper initTableInfo(Configuration, String, Class)} <p>
 * {@link com.baomidou.mybatisplus.core.toolkit.LambdaUtils installCache(TableInfo)} <p>
 * 方法的加载逻辑 <p>
 * 他会将所有扫描到的mapper中的泛型({@link BaseMapper<Class>}中的Class，即实体类)的字段信息缓存到 <p>
 * {@link com.baomidou.mybatisplus.core.toolkit.LambdaUtils}中的{@code COLUMN_CACHE_MAP}中。 <p>
 * 但是没有单独缓存父类的信息，所以{@code COLUMN_CACHE_MAP}中没有相关缓存，就报错了。 <p>
 * 因此我们单独为{@link BaseDO}添加一个的Mapper类，这样他就会缓存该类的信息了。 <p>
 * 另外一个解决方案是给相关Wrapper指定泛型类型，告诉mp让他加载子类的字段信息，也可以解决该问题： <p>
 * 使用{@link com.baomidou.mybatisplus.core.conditions.AbstractWrapper#setEntityClass(Class)}
 */
@Mapper
public interface BaseDOMapper extends BaseMapper<BaseDO> {
}

