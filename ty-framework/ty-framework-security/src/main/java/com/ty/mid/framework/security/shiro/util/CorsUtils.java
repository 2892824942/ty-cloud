package com.ty.mid.framework.security.shiro.util;

import com.ty.mid.framework.core.config.CorsConfiguration;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;

@Slf4j
public abstract class CorsUtils {

    public static boolean setCorsHeaderIfEnabled(HttpServletResponse response, CorsConfiguration corsConfiguration) {
        if (corsConfiguration != null && corsConfiguration.isEnable()) {
            response.setHeader("Access-Control-Allow-Origin", corsConfiguration.getAllowOrigin());
            response.setHeader("Access-Control-Allow-Headers", corsConfiguration.getAllowHeaders());
            response.setHeader("Access-Control-Allow-Methods", corsConfiguration.getAllowMethods());
            response.setHeader("Access-Control-Allow-Credentials", corsConfiguration.getAllowCredentials());
            response.setHeader("Access-Control-Expose-Headers", corsConfiguration.getAllowExposeHeaders());

            return true;
        } else {
            log.info("currently cors is not enabled!");
            return false;
        }
    }


}
