package ty.framework.sms.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by suyouliang on 2022/03/26 
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class LockTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LockTestApplication.class, args);
    }

}
