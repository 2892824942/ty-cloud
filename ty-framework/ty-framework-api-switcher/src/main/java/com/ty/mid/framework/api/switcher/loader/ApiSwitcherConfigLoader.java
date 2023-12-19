package com.ty.mid.framework.api.switcher.loader;

import com.ty.mid.framework.api.switcher.model.ApiSwitcherConfig;

/**
 * api 配置加载类
 *
 * @author suyouliang
 * @createTime 2019-08-14 15:27
 */
public interface ApiSwitcherConfigLoader {

    /**
     * 加载配置
     *
     * @param apiName
     * @return
     */
    ApiSwitcherConfig loadApiSwitcherConfig(String apiName);

}