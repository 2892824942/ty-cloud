package com.ty.mid.framework.application.runner.core;

import com.ty.mid.framework.core.spring.SpringContextHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

/**
 * 项目启动成功后，提供文档相关的地址
 *
 * @author suyoulinag
 */
@Slf4j
public class ApplicationRunnerLauncher implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        ConfigurableEnvironment env = SpringContextHelper.getEnvironment();

        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }

        Locale locale = Locale.getDefault();
        TimeZone timeZone = TimeZone.getDefault();
        Date now = new Date();

        String nowStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
        //TODO syl 1.修改为可扩展配置类,可以让使用者自定义  2.支持系统级根据具体条件决定是否展示
        try {
            log.info("\n----------------------------------------------------------\n\t" +
                            "Application '{}' is running! Access URLs:\n\t" +
                            "Local: \t\t{}://localhost:{}\n\t" +
                            "External: \t{}://{}:{}\n\t" +
                            "Actuator: \t{}://{}:{}{}\n\t" +
                            "Profile(s):\t{}\n\t" +
                            "Locale:   \t{}\n\t" +
                            "TimeZone: \t{}\n\t" +
                            "Now:     \t{}\n----------------------------------------------------------",
                    env.getProperty("spring.application.name"),
                    protocol,
                    env.getProperty("local.server.port"),
                    protocol,
                    InetAddress.getLocalHost().getHostAddress(),
                    env.getProperty("local.server.port"),
                    protocol,
                    InetAddress.getLocalHost().getHostAddress(),
                    env.getProperty("management.server.port"),
                    Optional.ofNullable(env.getProperty("management.endpoints.web.base-path")).orElse("/actuator"),
                    env.getActiveProfiles(),
                    locale,
                    timeZone.getID(),
                    nowStr);
        } catch (UnknownHostException e) {
            log.warn("UnKnowHost", e);
        }
    }

}
