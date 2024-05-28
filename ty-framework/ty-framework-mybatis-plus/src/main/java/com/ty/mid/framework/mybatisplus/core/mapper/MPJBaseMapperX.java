package com.ty.mid.framework.mybatisplus.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.interfaces.MPJBaseJoin;
import com.ty.mid.framework.common.entity.BaseIdDO;
import com.ty.mid.framework.common.pojo.PageParam;
import com.ty.mid.framework.common.pojo.PageResult;
import com.ty.mid.framework.mybatisplus.util.MyBatisUtils;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * 在 MyBatis Plus 的 BaseMapper 的基础上拓展，提供更多的能力 <p>
 * 1. {@link BaseMapper} 为 MyBatis Plus 的基础接口，提供基础的 CRUD 能力 <p>
 * 2. {@link MPJBaseMapper} 为 MyBatis Plus Join 的基础接口，提供连表 Join 能力 <p>
 * 3.为 MyBatis Plus 的List接口，快速转换Map能力
 */
public interface MPJBaseMapperX<T extends BaseIdDO<ID>, ID extends Serializable> extends BaseMapperX<T, ID>, MPJBaseMapper<T> {

    /**
     * 连表分页
     * 其他连表能力,直接调用父级方法
     *
     * @param pageParam
     * @param queryWrapper
     * @return
     */
    default <DTO> PageResult<DTO> selectJoinPage(Class<DTO> dtoClass, PageParam pageParam, @Param("ew") MPJBaseJoin<T> queryWrapper) {
        // 特殊：不分页，直接查询全部
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageNo())) {
            List<DTO> list = selectJoinList(dtoClass, queryWrapper);
            return PageResult.of(list, (long) list.size());
        }

        // MyBatis Plus 查询
        IPage<DTO> mpPage = MyBatisUtils.buildPage(pageParam);
        selectJoinPage(mpPage, dtoClass, queryWrapper);
        // 转换返回
        return PageResult.of(mpPage.getRecords(), mpPage.getTotal());
    }
}
