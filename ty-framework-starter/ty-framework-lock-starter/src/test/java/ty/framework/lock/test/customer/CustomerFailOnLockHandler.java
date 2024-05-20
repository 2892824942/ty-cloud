package ty.framework.lock.test.customer;

import com.ty.mid.framework.lock.handler.lock.FailOnLockCustomerHandler;
import com.ty.mid.framework.lock.core.LockInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

import java.util.concurrent.locks.Lock;

@Slf4j
public class CustomerFailOnLockHandler extends FailOnLockCustomerHandler {
    @Override
    public boolean handle(LockInfo lockInfo, Lock lock, JoinPoint joinPoint) {
        log.error("自定义的CustomerFailOnLockHandler执行了！！");
        return false;
    }

    @Override
    public boolean handle(LockInfo lockInfo, Lock lock) {
        log.error("自定义的CustomerFailOnLockHandler执行了！！");
        return false;
    }
}
