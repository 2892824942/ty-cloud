package com.ty.mid.framework.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Profile;

/**
 * Created by suyouliang on 2022/03/26.
 */

@SpringBootApplication
@EnableCaching
@Profile(value = {"default", "caffeine", "redis"})
public class CacheTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheTestApplication.class, args);
    }

}
