package com.ty.mid.framework.mybatisplus.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ty.mid.framework.common.entity.Auditable;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.core.util.ThreadUtils;
import com.ty.mid.framework.mybatisplus.core.audit.AuditorInfoResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.io.Serializable;
import java.util.Date;

@Slf4j
public class AuditorMetaObjectHandler<T extends Serializable> implements MetaObjectHandler {


    protected AuditorInfoResolver<T> auditorInfoResolver;

    public  AuditorMetaObjectHandler(AuditorInfoResolver<T> auditorInfoResolver) {
        this.auditorInfoResolver = auditorInfoResolver;
    }


    @Override
    public void insertFill(MetaObject metaObject) {
        Auditable info = this.getAuditorInfo();

        Date now = this.getCurrentDate();

        this.setFieldValByName("creator", info.getId(), metaObject);
        this.setFieldValByName("updater", info.getId(), metaObject);

        this.setFieldValByName("createTime", now, metaObject);
        this.setFieldValByName("updateTime", now, metaObject);

    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateFill(MetaObject metaObject) {
        Auditable<T> info = (Auditable<T>) this.getAuditorInfo();

        this.setFieldValByName("updateBy", info.getId(), metaObject);
        this.setFieldValByName("updateTime", this.getCurrentDate(), metaObject);
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    protected Date getCurrentDate() {
        return new Date();
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    protected Auditable<?> getAuditorInfo() {
        Auditable<?> info = this.auditorInfoResolver.resolveCurrentAuditor();

        if (null == info) {
            log.info("无法解析当前用户信息，使用默认用户信息 {} 代替，当前线程名称：{}, 所属线程组: {}",
                    "DEFAULT_AUDITOR",
                    ThreadUtils.getCurrentThreadNameSafety(), ThreadUtils.getCurrentThreadGroupNameSafety());
            info = auditorInfoResolver.resolveCurrentAuditor();
            if (null ==info){
                throw new FrameworkException("默认的用户上下文信息不能为空");
            }
        }
        return info;
    }

}
