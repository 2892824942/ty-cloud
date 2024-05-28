package ty.framework.sms.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by suyouliang on 2022/03/26
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class SmsTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsTestApplication.class, args);
    }

}
