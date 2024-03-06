package com.ty.mid.framework.service.cache.mybatisplus;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.mybatisplus.core.dataobject.BaseDO;
import com.ty.mid.framework.mybatisplus.core.mapper.BaseMapperX;
import com.ty.mid.framework.service.cache.generic.CacheAutoWrapService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 抽象的带缓存service，基于mybatis-plus,实现自动缓存全量数据的功能
 * 注意:
 * 1.此类适合缓存如字典,全国省市区等基本不变且数量不多的场景
 * 2.如果不需要缓存全部,请使用BaseCacheService 定制缓存的内容
 * <p>
 *
 * @see CacheAutoWrapService
 */
@Slf4j
public abstract class AllCacheAutoWrapService<S extends BaseDO, T extends BaseIdDO<Long>, M extends BaseMapperX<S, Long>> extends CacheAutoWrapService<S, T, M> {
    protected static final Integer BATCH_SIZE = 3000;

    @Override
    public void init() {
        super.init();
        Long count = selectCount();
        if (count < BATCH_SIZE) {
            cacheReload();
            return;
        }
        List<S> dataList = new ArrayList<>(2000);
        //使用流式查询
        this.baseMapper.selectList(Wrappers.emptyWrapper(), resultContext -> {
            // 依次得到每条业务记录
            log.debug("当前处理第{}条记录.", resultContext.getResultCount());
            S source = resultContext.getResultObject();
            //做自己的业务处理,比如分发任务
            dataList.add(source);
            if (dataList.size() > 2000) {
                //存到缓存中
                Map<String, T> dbDataMap = getDbDataMap(dataList);
                getCache().putAll(dbDataMap);
                dataList.clear();
            }
        });
        //将剩余的全部放入缓存
        Map<String, T> dbDataMap = getDbDataMap(dataList);
        getCache().putAll(dbDataMap);
    }

    @Override
    public List<S> cacheLoadListFromDb() {
        return selectList();
    }

}
