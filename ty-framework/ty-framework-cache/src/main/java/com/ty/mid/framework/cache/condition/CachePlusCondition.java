package com.ty.mid.framework.cache.condition;

import com.ty.mid.framework.cache.config.CachePlusConfig;
import com.ty.mid.framework.cache.configration.base.CacheConfigurations;
import com.ty.mid.framework.cache.constant.CachePlusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

import java.util.List;
import java.util.Objects;

/**
 * General cache condition used with all cache configuration classes.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @author Madhura Bhave
 */
@Slf4j
public class CachePlusCondition extends SpringBootCondition {
    private static final Bindable<List<CachePlusType>> MULTI_CACHE_OPEN_TYPE_BINDABLE = Bindable.listOf(CachePlusType.class);
    private static final Bindable<Boolean> MULTI_CACHE_SWITCH_BINDABLE = Bindable.of(Boolean.class);
    private static String JOINER = ".";
    private static final String MULTI_CACHE_OPEN_TYPE_NAME = CachePlusConfig.CACHE_PREFIX + JOINER + "type";
    private static final String MULTI_CACHE_SWITCH_NAME = CachePlusConfig.CACHE_PREFIX + JOINER + CachePlusConfig.CACHE_MULTI_ENABLE;

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String sourceClass = "";
        if (metadata instanceof ClassMetadata) {
            sourceClass = ((ClassMetadata) metadata).getClassName();
        }
        ConditionMessage.Builder message = ConditionMessage.forCondition("Cache", sourceClass);
        Environment environment = context.getEnvironment();
        try {
            BindResult<Boolean> multiSwitch = Binder.get(environment).bind(MULTI_CACHE_SWITCH_NAME, MULTI_CACHE_SWITCH_BINDABLE);
            if (!multiSwitch.isBound()) {
                log.debug("multi config unEnable");
                return ConditionOutcome.noMatch(message.because("multi config unEnable"));
            }
            BindResult<List<CachePlusType>> openType = Binder.get(environment).bind(MULTI_CACHE_OPEN_TYPE_NAME, MULTI_CACHE_OPEN_TYPE_BINDABLE);
            if (!openType.isBound()) {
                log.debug("no cache type open");
                return ConditionOutcome.noMatch(message.because("no cache type open"));
            }
            CachePlusType definitionType = CacheConfigurations.getType(((AnnotationMetadata) metadata).getClassName());
            log.debug("definitionType is:{}", definitionType);
            if (Objects.nonNull(definitionType) && openType.get().contains(definitionType)) {
                log.debug("multi cache used:{}", definitionType);
                return ConditionOutcome.match(message.because("multi cache used"));
            }
        } catch (BindException ex) {
            //do nothing
        }
        return ConditionOutcome.noMatch(message.because("unknown cache type"));
    }

}