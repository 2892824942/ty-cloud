package com.ty.mid.framework.api.switcher.loader;

import com.ty.mid.framework.api.switcher.model.ApiSwitcherConfig;


/**
 * api 配置加载类<p> <p>
 *
 * @author suyouliang<p> <p>
 * @createTime 2023-08-14 15:27<p>
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