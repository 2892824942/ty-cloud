package com.ty.mid.framework.sms.local;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.sms4j.api.universal.SupplierConfig;
import org.dromara.sms4j.provider.config.BaseConfig;

/**
 * 新增厂商的配置实现{@link SupplierConfig}
 * 这个类中还可以添加厂商携带的其他属性
 *
 * @author huangchengxing
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class LocalConfig extends BaseConfig {

    public static final String CONFIG_ID = "local";
    public static final String SUPPLIER = "local";
    //其他属性

    @Override
    public String getConfigId() {
        return CONFIG_ID;
    }

    @Override
    public String getSupplier() {
        return SUPPLIER;
    }
}



