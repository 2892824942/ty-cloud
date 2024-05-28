package com.ty.mid.framework.sms.local;

import org.dromara.sms4j.provider.factory.BaseProviderFactory;

/**
 * 新增厂商的工厂实现{@link LocalSmsImpl}
 * 这个类就是获取LocalSmsImpl的工厂
 *
 * @author huangchengxing
 */
public class LocalFactory implements BaseProviderFactory<LocalSmsImpl, LocalConfig> {

    private static final LocalFactory INSTANCE = new LocalFactory();

    /**
     * 获取建造者实例
     *
     * @return 建造者实例
     */
    public static LocalFactory instance() {
        return INSTANCE;
    }

    @Override
    public LocalSmsImpl createSms(LocalConfig localConfig) {
        return new LocalSmsImpl();
    }

    @Override
    public Class<LocalConfig> getConfigClass() {
        return LocalConfig.class;
    }

    @Override
    public String getSupplier() {
        return LocalConfig.CONFIG_ID;
    }
}
