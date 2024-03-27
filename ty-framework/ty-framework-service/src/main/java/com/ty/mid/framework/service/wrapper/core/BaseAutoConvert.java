package com.ty.mid.framework.service.wrapper.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.ty.mid.framework.common.dto.AbstractNameDTO;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.pojo.PageResult;
import com.ty.mid.framework.common.util.collection.CollectionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.*;
import java.util.function.Function;

/**
 * 用户入值 Covert
 *
 * @author suyouliang
 */
@Mapper
public interface BaseAutoConvert {
    String SKIP_FLAG = "skip";
    ThreadLocal<Map<String, Object>> METHOD_CONTEXT = new ThreadLocal<>();

    static void setMethodContext(String key, Object value) {
        Map<String, Object> contextMap = METHOD_CONTEXT.get();
        if (contextMap == null) {
            contextMap = new HashMap<>();
            METHOD_CONTEXT.set(contextMap);
        }
        contextMap.put(key, value);
    }

    static Object getMethodContext(String key) {
        Map<String, Object> contextMap = METHOD_CONTEXT.get();
        return (contextMap != null) ? contextMap.get(key) : null;
    }

    static Boolean exit(String key) {
        return Objects.nonNull(getMethodContext(key));
    }

    static void clearMethodContext() {
        METHOD_CONTEXT.remove();
    }

    @BeforeMapping
    default <S extends BaseIdDO<Long>> void tagList(List<S> sourceList) {
        setMethodContext(SKIP_FLAG, Boolean.TRUE);
    }

    @AfterMapping
    default <S extends BaseIdDO<Long>, T extends BaseIdDO<Long>> void handle(S source, @MappingTarget T target) {
        if (exit(SKIP_FLAG)) {
            return;
        }
        MappingProvider.autoWrapper(source, target);
    }

    @AfterMapping
    default <S extends BaseIdDO<Long>, T extends BaseIdDO<Long>> void handleList(List<S> sourceList, @MappingTarget List<T> targetList) {
        //必须在自动装载前清除,否则自动装载过程,影响其他自动装载项读取自己的上下文
        clearMethodContext();
        MappingProvider.autoWrapper(sourceList, targetList);

    }

    @AfterMapping
    default void handleAbstractNameDTO(@MappingTarget AbstractNameDTO abstractNameDTO) {
        if (exit(SKIP_FLAG)) {
            return;
        }
        handleAbstractNameDTOList(Collections.singletonList(abstractNameDTO));
    }

    @AfterMapping
    default <T extends AbstractNameDTO> void handleAbstractNameDTOList(@MappingTarget List<T> abstractNameDTOList) {
        if (CollUtil.isEmpty(abstractNameDTOList)) {
            return;
        }
        List<Long> creatorIdList = CollectionUtils.convertList(abstractNameDTOList, AbstractNameDTO::getCreator);
        List<Long> updaterIdList = CollectionUtils.convertList(abstractNameDTOList, AbstractNameDTO::getUpdater);
        Collection<Long> userIdList = CollUtil.addAll(creatorIdList, updaterIdList);
        if (CollUtil.isEmpty(userIdList)) {
            return;
        }
        Map<Long, String> userNameMap = getUserNameMap(userIdList);
        if (CollUtil.isEmpty(userNameMap)) {
            return;
        }
        abstractNameDTOList.forEach(abstractNameDTO -> {
            abstractNameDTO.setCreatorName(userNameMap.get(abstractNameDTO.getCreator()));
            abstractNameDTO.setUpdaterName(userNameMap.get(abstractNameDTO.getUpdater()));

        });

    }

    default Map<Long, String> getUserNameMap(Collection<Long> userIdList) {
        return null;
    }


    default <S, T> PageResult<T> covertPage(PageResult<S> dataPage, Function<List<S>, List<T>> function) {
        if (CollectionUtil.isEmpty(dataPage.getList())) {
            return PageResult.empty();
        }
        List<T> resultPage = function.apply(dataPage.getList());
        return PageResult.of(resultPage, dataPage.getTotal());
    }
}

