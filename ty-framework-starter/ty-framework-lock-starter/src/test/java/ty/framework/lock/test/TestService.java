package ty.framework.lock.test;

import com.ty.mid.framework.lock.annotation.FailFastLock;
import com.ty.mid.framework.lock.annotation.LocalLock;
import com.ty.mid.framework.lock.annotation.Lock;
import com.ty.mid.framework.lock.annotation.LockKey;
import com.ty.mid.framework.lock.strategy.FailOnLockStrategy;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Created by suyouliang on 2022/03/26.
 */
@Service
public class TestService {
    @Resource
    LockRegistry lockRegistry;
    @Lock(waitTime = 10, keys = {"#param"},  lockFailStrategy = FailOnLockStrategy.FAIL_FAST)
    public String getValue(String param) throws Exception {
        if ("sleep".equals(param)) {//线程休眠或者断点阻塞，达到一直占用锁的测试效果
            Thread.sleep(100 * 10);
        }
        return "success";
    }

    @Lock(keys = {"#userId"})
    public String getValue(String userId, @LockKey Integer id) throws Exception {
        Thread.sleep(60 * 10);
        return "success";
    }

    @Lock(keys = {"#user.name", "#user.id"})
    public String getValue(User user) throws Exception {
        Thread.sleep(2 * 1000);
        return "success";
    }

    /**
     * 注解方式
     * @param user
     * @return
     */
    @FailFastLock
    public String getValueWithFailFastLock(User user) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }

    @LocalLock
    public String getValueWithLocalLock(User user) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }

}
