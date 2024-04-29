package com.ty.mid.framework.api.switcher.aspect;

import com.ty.mid.framework.api.switcher.annotation.ApiSwitcher;
import com.ty.mid.framework.api.switcher.exception.ApiDisabledException;
import com.ty.mid.framework.api.switcher.loader.ApiSwitcherConfigLoader;
import com.ty.mid.framework.api.switcher.model.ApiSwitcherConfig;
import com.ty.mid.framework.common.lang.NonNull;
import com.ty.mid.framework.core.aspect.AbstractAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Date;

/** <p>
 * @author suyouliang <p>
 * @createTime 2023-08-14 15:23 
 */
@Aspect
public class ApiSwitcherAspect extends AbstractAspect {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ApiSwitcherConfigLoader configLoader;

    public ApiSwitcherAspect(ApiSwitcherConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @Pointcut("@within(com.ty.mid.framework.api.switcher.annotation.ApiSwitcher) OR @annotation(cn.techwolf.ugroup.framework.api.switcher.annotation.ApiSwitcher)")
    public void apiSwitcherPointcut() {
    }

    @Before("apiSwitcherPointcut()")
    public void checkApiSwitcher(JoinPoint jp) {
        Method method = this.resolveMethod(jp);
        log.info("check api switcher from class: {}, method: {}", method.getDeclaringClass().getName(), method.getName());

        // check method
        this.checkMethodApiSwitcher(method);
        // check class
        this.checkClassApiSwitcher(method);
    }

    /**
     * 如果方法上存在注解，则进行开关检查
     *
     * @param method
     */
    protected void checkMethodApiSwitcher(@NonNull Method method) {
        log.info("checking api switcher from class: {}", method.getClass());
        this.doCheck(method);
    }

    /**
     * 如果类上存在注解，则对类进行检查
     *
     * @param method
     */
    protected void checkClassApiSwitcher(@NonNull Method method) {
        Class clazz = method.getDeclaringClass();
        log.info("checking api switcher from method: {}", method.getName());

        this.doCheck(clazz);
    }

    /**
     * 检查开关逻辑
     *
     * @param annotatedElement
     */
    protected void doCheck(AnnotatedElement annotatedElement) {
        ApiSwitcher annotation = this.findAnnotation(annotatedElement, ApiSwitcher.class);
        if (annotation == null) {
            log.info("there is no ApiSwitcher annotation on {}, so skip api switcher check", annotatedElement.toString());
            return;
        }

        // 获取 api 名称
        String apiName = annotation.name();

        // 加载配置
        ApiSwitcherConfig config = this.configLoader.loadApiSwitcherConfig(apiName);
        if (config == null) {
            log.info("there is no api switcher config for {}", apiName);
            return;
        }

        // date check
        boolean disabled = config.isDateDisabled(new Date());

        if (!disabled) {
            log.info("api {} current time is not disabled", apiName);
            return;
        }

        String tipMessage = StringUtils.isEmpty(config.getTipMessage()) ? annotation.tip() : config.getTipMessage();
        throw new ApiDisabledException(config.getFromDate(), config.getThruDate(), tipMessage);
    }
}
