package com.ty.mid.framework.core.spring.boot;

import com.ty.mid.framework.common.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class ApplicationLauncher {

    private static Logger log = LoggerFactory.getLogger(ApplicationLauncher.class);

    public static void launchApplication(@NonNull Class<?> clazz, String... args) {
        beforeServiceLaunch(clazz, args);
        ConfigurableApplicationContext ctx = SpringApplication.run(clazz, args);
        afterServiceLaunch(ctx);
        afterServiceLaunchPostEvent(ctx);
    }

    public static void launchApplication(@NonNull Class[] sources, String... args) {
        beforeServiceLaunch(sources, args);
        ConfigurableApplicationContext ctx = SpringApplication.run(sources, args);
        afterServiceLaunch(ctx);
        afterServiceLaunchPostEvent(ctx);
    }

    private static void beforeServiceLaunch(Object source, String... args) {

    }

    private static void afterServiceLaunchPostEvent(ConfigurableApplicationContext ctx) {
        ctx.publishEvent(new PostSystemStartedEvent(ctx));
    }

    private static void afterServiceLaunch(ConfigurableApplicationContext ctx) {
        ConfigurableEnvironment env = ctx.getEnvironment();

        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }

        Locale locale = Locale.getDefault();
        TimeZone timeZone = TimeZone.getDefault();
        Date now = new Date();

        String nowStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);

        try {
            log.info("\n----------------------------------------------------------\n\t" +
                            "Application '{}' is running! Access URLs:\n\t" +
                            "Local: \t\t{}://localhost:{}\n\t" +
                            "External: \t{}://{}:{}\n\t" +
                            "Profile(s): \t{}\n\t" +
                            "Locale: \t{}\n\t" +
                            "TimeZone: \t{}\n\t" +
                            "now: \t{}\n----------------------------------------------------------",
                    env.getProperty("spring.application.name"),
                    protocol,
                    env.getProperty("local.server.port"),
                    protocol,
                    InetAddress.getLocalHost().getHostAddress(),
                    env.getProperty("local.server.port"),
                    env.getActiveProfiles(),
                    locale,
                    timeZone.getID(),
                    nowStr);
        } catch (UnknownHostException e) {
            log.warn("UnKnowHost", e);
        }
    }


}