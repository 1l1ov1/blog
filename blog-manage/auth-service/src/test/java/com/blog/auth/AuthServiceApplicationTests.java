package com.blog.auth;

import cn.hutool.core.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AuthServiceApplicationTests {
    private static final String NICKNAME_PREFIX = "ZW";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"); // 时间戳到毫秒级（3位）
    private static final String RANDOM_CHAR_POOL = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int RANDOM_LENGTH = 8; // 随机部分从6位增加到8位
    private static final AtomicInteger COUNTER = new AtomicInteger(0); // 原子计数器
    private static final int COUNTER_MAX = 9999; // 每秒最多支持9999次生成
    private final Object lock = new Object();

    @Test
    void contextLoads() throws InterruptedException {
        final int THREAD_COUNT = 100; // 测试线程数
        final int GENERATIONS_PER_THREAD = 500000; // 每个线程生成次数
        final Set<String> generatedSet = ConcurrentHashMap.newKeySet(); // 线程安全集合
        final AtomicInteger duplicates = new AtomicInteger(0); // 重复计数器

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < GENERATIONS_PER_THREAD; j++) {
                        String nickname = generateUniqueNickname();
                        if (!generatedSet.add(nickname)) {
                            duplicates.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("总生成数: " + (THREAD_COUNT * GENERATIONS_PER_THREAD));
        System.out.println("重复数: " + duplicates.get());
        assertEquals(0, duplicates.get(), "检测到重复昵称生成");


    }

    private String generateUniqueNickname() {
        StringBuilder sb = new StringBuilder(24);
        sb.append(NICKNAME_PREFIX);

        String timestamp;
        String randomStr;
        int counter;
        synchronized (lock) {
            timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            counter = COUNTER.getAndUpdate((prev) -> (prev >= COUNTER_MAX) ? 0 : prev + 1);
            randomStr = RandomUtil.randomString(RANDOM_CHAR_POOL, RANDOM_LENGTH);
        }

        // 格式: ZW + 17位时间戳（含毫秒） + 4位计数器 + 8位随机字符
        return sb.append(timestamp)
                .append(String.format("%04d", counter)) // 固定4位计数器，补零
                .append(randomStr)
                .toString();
    }

    @Test
    void testTimeLenth () {
        DateTimeFormatter TIMESTAMP_FORMATTER =
                DateTimeFormatter.ofPattern("yyyyMMddSSS");
        System.out.println(LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    }

}
