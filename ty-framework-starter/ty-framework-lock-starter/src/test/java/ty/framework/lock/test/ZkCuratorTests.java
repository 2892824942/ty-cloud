package ty.framework.lock.test;

import com.ty.mid.framework.lock.lock.ZkLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

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
