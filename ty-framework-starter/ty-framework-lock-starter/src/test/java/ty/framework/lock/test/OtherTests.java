package ty.framework.lock.test;

import com.google.common.util.concurrent.CycleDetectingLockFactory;
import com.ty.mid.framework.lock.core.LockInfo;
import com.ty.mid.framework.lock.decorator.cycle.CycleDetectingLockDecorator;
import com.ty.mid.framework.lock.lock.ZkLock;
import com.ty.mid.framework.lock.strategy.CycleLockStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootTest(classes = LockTestApplication.class)
@Slf4j
public class OtherTests {

    /**
     * 代码方式
     *
     * @throws Exception
     */
    @Test
    public void testLock(@Autowired CuratorFramework curatorClient) throws Exception {
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

    @Test
    public void testDeadLock() throws Exception {
        CycleDetectingLockFactory cycleDetectingLockFactory = CycleDetectingLockFactory.newInstance(CycleDetectingLockFactory.Policies.THROW);
        ReentrantLock aaaa = cycleDetectingLockFactory.newReentrantLock("aaaa");

        ReentrantLock bbbb = cycleDetectingLockFactory.newReentrantLock("bbbb");
        try {
            aaaa.lock();
            bbbb.lock();
        } catch (Exception e) {
            throw e;
        } finally {
            aaaa.unlock();
            bbbb.unlock();
        }

        try {
            bbbb.lock();
            aaaa.lock();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bbbb.unlock();
            aaaa.unlock();
        }

    }

    @Test
    public void testThreadDeadLock() throws Exception {
        //CycleDetectingLockFactory cycleDetectingLockFactory = CycleDetectingLockFactory.newInstance(CycleDetectingLockFactory.Policies.THROW);
//        ReentrantLock aaaa = cycleDetectingLockFactory.newReentrantLock("aaaa");
//        ReentrantLock bbbb = cycleDetectingLockFactory.newReentrantLock("bbbb");
        LockInfo lockInfoA = new LockInfo();
        lockInfoA.setName("aaaa");
        lockInfoA.setCycleLockStrategy(CycleLockStrategy.THROWING);
        LockInfo lockInfoB = new LockInfo();
        lockInfoB.setCycleLockStrategy(CycleLockStrategy.THROWING);
        lockInfoB.setName("bbbb");

        Runnable runnable1 = () -> {
            Lock aaaa = new CycleDetectingLockDecorator(new ReentrantLock(), lockInfoA);
            Lock bbbb = new CycleDetectingLockDecorator(new ReentrantLock(), lockInfoB);
            try {
                aaaa.lock();
                bbbb.lock();
            } catch (Exception e) {
                throw e;
            } finally {
                aaaa.unlock();
                bbbb.unlock();
            }
            System.out.println("r1加解锁完毕");
        };
        runnable1.run();


        Runnable runnable2 = () -> {
            Lock aaaa = new CycleDetectingLockDecorator(new ReentrantLock(), lockInfoA);
            Lock bbbb = new CycleDetectingLockDecorator(new ReentrantLock(), lockInfoB);
            try {
                bbbb.lock();
                aaaa.lock();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {

                bbbb.unlock();
                aaaa.unlock();
            }
            System.out.println("r2加解锁完毕");
        };
        runnable2.run();

        Thread.sleep(1000);


    }

}
