package ty.framework.lock.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@SpringBootTest(classes = LockTestApplication.class)
@Slf4j
public class RegistryTests {

    @Autowired
    RegistryService registryService;


    /**
     * 同一进程内多线程获取锁测试
     *
     * @throws Exception
     */
    @Test
    public void multithreadingTest() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.range(0, 100).forEach(i -> executorService.submit(() -> {
            try {
                String result = registryService.getValue("test-1", 2, 30, TimeUnit.SECONDS, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        System.out.println("所有线程启动完毕！！");
        executorService.awaitTermination(200, TimeUnit.SECONDS);
    }


    /**
     * 同一进程内多线程获取锁测试,性能测试
     *
     * @throws Exception
     */
    @Test
    public void multithreadingRateTest() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        AtomicInteger successExcuseTime = new AtomicInteger();
        AtomicLong excuseTimeTotal = new AtomicLong();
        CountDownLatch countDownLatch = new CountDownLatch(100);
        IntStream.range(0, 100).forEach(i -> executorService.submit(() -> {
            try {
                long startTime = System.currentTimeMillis();
                String result = registryService.getValue("test-1", 1, 30, TimeUnit.SECONDS, 1000);
                if ("success".equals(result)) {
                    successExcuseTime.incrementAndGet();
                }
                long endTime = System.currentTimeMillis();
                excuseTimeTotal.addAndGet(endTime - startTime);
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        System.out.println("所有线程启动完毕！！");
        countDownLatch.await();
        log.info("共计耗时：{}(ms),成功获取次数：{}", excuseTimeTotal.intValue(), successExcuseTime.intValue());
    }

    /**
     * 同一进程内多线程获取锁测试,性能测试，20线程
     *
     * @throws Exception
     */
    @Test
    public void multithreadingRate1Test() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        AtomicInteger successExcuseTime = new AtomicInteger();
        AtomicLong excuseTimeTotal = new AtomicLong();
        CountDownLatch countDownLatch = new CountDownLatch(100);
        IntStream.range(0, 100).forEach(i -> executorService.submit(() -> {
            try {
                long startTime = System.currentTimeMillis();
                String result = registryService.getValue("test-1", 1, 30, TimeUnit.SECONDS, 1000);
                if ("success".equals(result)) {
                    successExcuseTime.incrementAndGet();
                }
                long endTime = System.currentTimeMillis();
                excuseTimeTotal.addAndGet(endTime - startTime);
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        System.out.println("所有线程启动完毕！！");
        countDownLatch.await();
        log.info("共计耗时：{}(ms),成功获取次数：{}", excuseTimeTotal.intValue(), successExcuseTime.intValue());
    }


    /**
     * 同一进程内多线程获取锁测试,性能测试,不做任何阻塞，最大化并发冲突
     *
     * @throws Exception
     */
    @Test
    public void multithreadingNoWaitRateTest() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        AtomicInteger successExcuseTime = new AtomicInteger();
        AtomicLong excuseTimeTotal = new AtomicLong();
        CountDownLatch countDownLatch = new CountDownLatch(100);
        IntStream.range(0, 100).forEach(i -> executorService.submit(() -> {
            try {
                long startTime = System.currentTimeMillis();
                String result = registryService.getValue("test-1", 0, 30, TimeUnit.SECONDS, 0);
                if ("success".equals(result)) {
                    successExcuseTime.incrementAndGet();
                }
                long endTime = System.currentTimeMillis();
                excuseTimeTotal.addAndGet(endTime - startTime);
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        System.out.println("所有线程启动完毕！！");

        countDownLatch.await();
        log.info("共计耗时：{}(ms),成功获取次数：{}", excuseTimeTotal.intValue(), successExcuseTime.intValue());
    }


    /**
     * 同一进程内多线程获取锁测试,性能测试,不做任何阻塞，100线程，最大化并发冲突
     *
     * @throws Exception
     */
    @Test
    public void multithreadingNoWait100RateTest() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        AtomicInteger successExcuseTime = new AtomicInteger();
        AtomicLong excuseTimeTotal = new AtomicLong();
        CountDownLatch countDownLatch = new CountDownLatch(100);
        IntStream.range(0, 100).forEach(i -> executorService.submit(() -> {
            try {
                long startTime = System.currentTimeMillis();
                String result = registryService.getValue("test-1", 0, 30, TimeUnit.SECONDS, 0);
                if ("success".equals(result)) {
                    successExcuseTime.incrementAndGet();
                }
                long endTime = System.currentTimeMillis();
                excuseTimeTotal.addAndGet(endTime - startTime);
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        System.out.println("所有线程启动完毕！！");

        countDownLatch.await();
        log.info("共计耗时：{}(ms),成功获取次数：{}", excuseTimeTotal.intValue(), successExcuseTime.intValue());
    }


    /**
     * 同一进程内但线程循环获取锁测试
     *
     * @throws Exception
     */
    @Test
    public void singleThreadMultiReadTest() throws Exception {

        IntStream.range(0, 100).forEach(i -> {
            try {
                String result = registryService.getValue("test-1", 2, 30, TimeUnit.SECONDS, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 同一进程内但线程循环获取锁测试，不阻塞。测试性能
     *
     * @throws Exception
     */
    @Test
    public void singleThreadMultiRead1Test() throws Exception {
        long startTime = System.currentTimeMillis();
        AtomicInteger atomicInteger = new AtomicInteger();
        IntStream.range(0, 100).forEach(i -> {
            try {
                String result = registryService.getValue("test-1", 0, 30, TimeUnit.SECONDS, 0);
                if ("success".equals(result)) {
                    atomicInteger.incrementAndGet();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        long endTime = System.currentTimeMillis();
        log.info("共计耗时：{}(ms),成功获取次数：{}", endTime - startTime, atomicInteger.intValue());
    }


}
