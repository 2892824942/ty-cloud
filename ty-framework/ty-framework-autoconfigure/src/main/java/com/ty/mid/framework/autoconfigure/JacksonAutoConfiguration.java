package com.ty.mid.framework.autoconfigure;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.ty.mid.framework.common.util.JsonUtils;
import com.ty.mid.framework.web.jackson.NumberSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class JacksonAutoConfiguration {
    /**
     * 注入的JsonUtils bean没啥意义,主要是针对spring中已经定义的ObjectMapper,对其定制增加逻辑
     *
     * 其中:Date和LocalDateTime都默认使用dataFormat配置
     * 另外:如果LocalDate以及DateTime格式不是想要的,参考此方法自行配置
     *
     * @param objectMappers
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public JsonUtils jsonUtils(List<ObjectMapper> objectMappers, Environment env) {
        String dataTimeFormat = env.getProperty("spring.jackson.date-format");
        DateTimeFormatter dateTimeFormatter = null;
        if (StrUtil.isNotEmpty(dataTimeFormat)) {
            dateTimeFormatter = DateTimeFormatter.ofPattern(dataTimeFormat);
        }
        // 1.1 创建 SimpleModule 对象
        SimpleModule simpleModule = new SimpleModule();
        simpleModule
                // 新增 Long 类型序列化规则，数值超过 2^53-1，在 JS 会出现精度丢失问题，因此 Long 自动序列化为字符串类型
                .addSerializer(Long.class, NumberSerializer.INSTANCE)
                .addSerializer(Long.TYPE, NumberSerializer.INSTANCE)
                .addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE)
                .addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE)
                .addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE)
                .addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE)
                // 新增 LocalDateTime 序列化、反序列化规则
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        // 1.2 注册到 objectMapper
        objectMappers.forEach(objectMapper -> objectMapper.registerModule(simpleModule));

        // 2. 设置 objectMapper 到 JsonUtils {
        JsonUtils.init(CollUtil.getFirst(objectMappers));
        log.info("[init][序列化配置初始化成功]");
        return new JsonUtils();
    }

}
