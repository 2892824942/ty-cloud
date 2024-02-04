package ty.framework.lock.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by suyouliang on 2022/03/26
 */
@Service
@Slf4j
public class RegistryService {
    @Autowired
    LockRegistry lockRegistry;
//    LockRegistry lockRegistry = LocalLockRegistry.getInstance();


    public String getValue(String param, long waitTime, long leaseTime, TimeUnit timeUnit, long sleepTime) throws Exception {
        Lock lock = lockRegistry.obtain("param");
        log.info("线程:[" + Thread.currentThread().getName() + "]准备开始:{}", Thread.currentThread().getId());
        boolean lockResult = lock.tryLock(waitTime, timeUnit);
        try {
            if (lockResult) {
                log.info("线程:[" + Thread.currentThread().getName() + "]拿到锁" + new Date().toLocaleString());
                Thread.sleep(sleepTime);
            } else {
                log.info("线程:[" + Thread.currentThread().getName() + "]没有拿到锁，放弃" + new Date().toLocaleString());
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("线程:[" + Thread.currentThread().getName() + "]拿锁异常，e:" + e);
            return null;
        } finally {
            if (lockResult) {
                lock.unlock();
            }
        }
        return "success";
    }


}
