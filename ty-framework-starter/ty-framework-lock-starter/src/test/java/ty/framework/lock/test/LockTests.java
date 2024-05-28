package ty.framework.lock.test;

import com.ty.mid.framework.lock.config.LockConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.locks.LockRegistry;

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
public class LockTests {

    @Autowired
    TestService testService;
    @Autowired
    TimeoutService timeoutService;
    @Autowired
    LockRegistry lockRegistry;
    @Autowired
    LockConfig lockConfig;

    /**
     * 代码方式
     *
     * @throws Exception
     */
    @Test
    public void testApi() throws Exception {
        Lock lock = lockRegistry.obtain("aaa");
        try {
            boolean b = lock.tryLock(2, TimeUnit.SECONDS);
            if (b) {
                //do your business code
                log.info("当前lockRegistry:{}, lockConfig:{}", lockRegistry.getClass().getSimpleName(), lockConfig);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * obtain
     * 同一进程内多线程获取锁测试
     *
     * @throws Exception
     */
    @Test
    public void multithreadingTest() throws Exception {
        log.info("now use lockRegistry:{}", lockRegistry);
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.range(0, 10).forEach(i -> executorService.submit(() -> {
            try {
                String result = testService.getValue("sleep");
                System.err.println("线程:[" + Thread.currentThread().getName() + "]拿到结果=》" + result + new Date().toLocaleString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        executorService.awaitTermination(20, TimeUnit.SECONDS);
    }


    /**
     * 线程休眠50秒
     *
     * @throws Exception
     */
    @Test
    public void jvm1() throws Exception {
        String result = testService.getValue("sleep");
        assertEquals(result, "success");
    }

    /**
     * 不休眠
     *
     * @throws Exception
     */
    @Test
    public void jvm2() throws Exception {
        String result = testService.getValue("aaa");
        assertEquals(result, "success");
    }

    /**
     * 不休眠
     *
     * @throws Exception
     */
    @Test
    public void jvm3() throws Exception {
        String result = testService.getValue("noSleep");
        assertEquals(result, "success");
    }

    /**
     * 测试业务key
     */
    @Test
    public void businessKeyJvm1() throws Exception {
        String result = testService.getValue("user1", null);
        assertEquals(result, "success");
    }

    /**
     * 测试业务key
     */
    @Test
    public void businessKeyJvm2() throws Exception {
        String result = testService.getValue("user1", 1);
        assertEquals(result, "success");
    }

    /**
     * 测试业务key
     */
    @Test
    public void businessKeyJvm3() throws Exception {
        String result = testService.getValue("user1", 2);
        assertEquals(result, "success");
    }

    /**
     * 测试业务key
     */
    @Test
    public void businessKeyJvm4() throws Exception {
        String result = testService.getValue(new User(3, null));
        assertEquals(result, "success");
    }

    /**
     * 测试watchdog无限延长加锁时间
     */
    @Test
    public void infiniteLeaseTime() {
        timeoutService.foo1();
    }

    /**
     * 测试加锁超时快速失败
     */
    @Test
    public void lockTimeoutFailFast() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                countDownLatch.countDown();
                try {
                    timeoutService.foo2();
                } catch (Exception e) {
                    log.error("执行异常：", e);
                }

            });
        }
        countDownLatch.await();

        TimeUnit.MILLISECONDS.sleep(10000);

        timeoutService.foo2();

    }


    /**
     * 测试加锁超时阻塞等待
     * 会打印10次acquire lock
     */
    @Test
    public void lockTimeoutKeepAcquire() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    timeoutService.foo3();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        long start = System.currentTimeMillis();
        startLatch.countDown();
        endLatch.await();
        long end = System.currentTimeMillis();
    }

    /**
     * 测试自定义加锁超时处理策略
     * 会执行1次自定义加锁超时处理策略
     */
    @Test
    public void lockTimeoutCustom() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(2);
        executorService.submit(() -> {
            try {
                timeoutService.foo2();
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        executorService.submit(() -> {
            try {
                timeoutService.foo4("foo", "bar");
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });


        latch.await();
        Thread.sleep(6000);
    }

    /**
     * 测试加锁超时不做处理
     */
    @Test
    public void lockTimeoutNoOperation() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    timeoutService.foo5("foo", "bar");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });

        }

        long start = System.currentTimeMillis();
        startLatch.countDown();
        endLatch.await();
        long end = System.currentTimeMillis();
    }

    /**
     * 测试释放锁时已超时，不做处理
     */
    @Test
    public void releaseTimeoutNoOperation() {

        timeoutService.foo6("foo", "bar");
    }

    /**
     * 测试释放锁时已超时，快速失败
     */
    @Test
    public void releaseTimeoutFailFast() {

        timeoutService.foo7("foo", "bar");
    }

    /**
     * 测试释放锁时已超时，自定义策略
     */
    @Test
    public void releaseTimeoutCustom() {
        timeoutService.foo8("foo", "bar");
    }


    /**
     * 测试自定义加锁失败策略
     */
    @Test
    public void lockTimeoutFailFastCustomer() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                countDownLatch.countDown();
                try {
                    timeoutService.foo9();
                } catch (Exception e) {
                    log.error("执行异常：", e);
                }

            });
        }
        countDownLatch.await();

        TimeUnit.MILLISECONDS.sleep(10000);

        timeoutService.foo9();

    }

    /**
     * 测试自定义异常
     */
    @Test
    public void lockTimeoutFailFastCustomerException() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                countDownLatch.countDown();
                try {
                    timeoutService.foo10();
                } catch (Exception e) {
                    log.error("执行异常：", e);
                }

            });
        }
        countDownLatch.await();

        TimeUnit.MILLISECONDS.sleep(10000);

        timeoutService.foo9();

    }

    /**
     * 测试AntiRelock自定义异常
     */
    @Test
    public void lockTimeoutAntiReLockException() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                countDownLatch.countDown();
                try {
                    timeoutService.foo11();
                } catch (Exception e) {
                    log.error("执行异常：", e);
                }

            });
        }
        countDownLatch.await();

        TimeUnit.MILLISECONDS.sleep(10000);

        timeoutService.foo9();

    }


    /**
     * 测试注解子类 FailFastLock
     */
    @Test
    public void subClassFailFastLockAnno() {
        String result = testService.getValueWithFailFastLock(new User());
        assertEquals(result, "success");

    }

    /**
     * 测试注解子类 AntiReLock
     */
    @Test
    public void subClassAntiReLockAnno() {
        String result = testService.getValueWithAntiReLock(new User());
        assertEquals(result, "success");

    }

    /**
     * 测试注解子类 localLock
     */
    @Test
    public void subClassLocalLockAnno() {
        String result = testService.getValueWithLocalLock(new User());
        assertEquals(result, "success");

    }

    @Test
    public void testNormalApiThreadDeadLock() throws Exception {
        Runnable runnable1 = () -> {
            Lock aaaa = lockRegistry.obtain("aaaa");
            Lock bbbb = lockRegistry.obtain("bbbb");
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
            Lock aaaa = lockRegistry.obtain("aaaa");
            Lock bbbb = lockRegistry.obtain("bbbb");
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
