package techwolf.blue.usl.framework.springboot.lock.test;

import com.ty.mid.framework.lock.annotation.Lock;
import com.ty.mid.framework.lock.model.FailOnLockStrategy;
import com.ty.mid.framework.lock.model.ReleaseTimeoutStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author 苏友良
 * @since 2019/4/16
 **/
@Service
public class TimeoutService {

    private static final Logger log = LoggerFactory.getLogger(TimeoutService.class);

    @Lock(name = "foo-service", leaseTime = 3000, releaseTimeoutStrategy = ReleaseTimeoutStrategy.FAIL_FAST)
    public void foo1() {
        try {
            log.info("foo1 acquire lock");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Lock(name = "foo-service", waitTime = 1, lockFailStrategy = FailOnLockStrategy.FAIL_FAST)
    public void foo2() {
        try {
            log.info("acquire lock");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Lock(name = "foo-service", waitTime = 2, lockFailStrategy = FailOnLockStrategy.KEEP_ACQUIRE)
    public void foo3() {
        try {
            TimeUnit.SECONDS.sleep(2);
            log.info("acquire lock");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Lock(name = "foo-service",
            waitTime = 2,
            customLockFailStrategy = "customLockTimeout")
    public String foo4(String foo, String bar) {
        try {
            TimeUnit.SECONDS.sleep(2);
            log.info("acquire lock");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "foo4";
    }

    private String customLockTimeout(String foo, String bar) {

        log.info("customLockTimeout foo:" + foo + " bar:" + bar);
        return "custom foo:" + foo + " bar:" + bar;
    }


    @Lock(name = "foo-service", waitTime = 10)
    public void foo5(String foo, String bar) {
        try {
            TimeUnit.SECONDS.sleep(2);
            log.info("acquire lock");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Lock(name = "foo-service", leaseTime = 1, waitTime = 10000)
    public void foo6(String foo, String bar) {
        try {
            TimeUnit.SECONDS.sleep(2);
            log.info("acquire lock");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Lock(name = "foo-service", leaseTime = 1, waitTime = 10000, releaseTimeoutStrategy = ReleaseTimeoutStrategy.FAIL_FAST)
    public void foo7(String foo, String bar) {
        try {
            TimeUnit.SECONDS.sleep(2);
            log.info("acquire lock");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Lock(name = "foo-service", leaseTime = 1, waitTime = 10000, customReleaseTimeoutStrategy = "customReleaseTimeout")
    public String foo8(String foo, String bar) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "foo8";
    }


    @Lock(name = "foo-service", waitTime = 1, lockFailStrategy = FailOnLockStrategy.CUSTOMER)
    public void foo9() {
        try {
            log.info("acquire lock");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Lock(name = "foo-service", waitTime = 1, exceptionClass = "techwolf.blue.usl.framework.springboot.lock.test.customer.exception.CustomerException", exceptionMsg = "我是注解自定义异常信息")
    public void foo10() {
        try {
            log.info("acquire lock");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String customReleaseTimeout(String foo, String bar) {

        throw new IllegalStateException("customReleaseTimeout");
    }
}
