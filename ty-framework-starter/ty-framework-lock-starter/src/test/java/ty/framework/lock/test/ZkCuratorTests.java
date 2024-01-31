package ty.framework.lock.test;

import com.ty.mid.framework.autoconfigure.CuratorConfig;
import com.ty.mid.framework.lock.lock.ZkLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.utils.ZookeeperFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.locks.LockRegistry;
import ty.framework.lock.test.LockTestApplication;
import ty.framework.lock.test.TestService;
import ty.framework.lock.test.TimeoutService;
import ty.framework.lock.test.User;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = LockTestApplication.class)
@Slf4j
public class ZkCuratorTests {

    @Autowired
    CuratorFramework curatorClient;

    /**
     * 代码方式
     *
     * @throws Exception
     */
    @Test
    public void testLock() throws Exception {
        InterProcessLock interProcessLock = new InterProcessMutex(curatorClient, "/test");
        Lock lock = new ZkLock(interProcessLock);
        try {
            boolean b = lock.tryLock(2, TimeUnit.SECONDS);
            if (b) {
                //do your business code
            }
        } finally {
            lock.unlock();
        }
    }

}
