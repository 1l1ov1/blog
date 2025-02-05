package com.blog.common.context;

/**
 * 用户上下文
 * @author wan
 */
public class UserContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户ID
     *
     * 使用ThreadLocal来存储用户ID，可以确保每个线程都有自己的用户ID副本，
     * 这样可以避免多线程环境下的数据共享问题，提高系统的安全性和一致性
     *
     * @param userId 用户ID，用于标识和追踪用户操作
     */
    public static void setUserId(Long userId) {
        threadLocal.set(userId);
    }

    /**
     * 获取当前线程的用户ID
     *
     * 使用ThreadLocal来存储用户ID，确保在多线程环境下每个线程都有独立的用户ID实例
     * 这样可以避免用户ID在不同线程间共享导致的安全问题
     *
     * @return 当前线程的用户ID，如果未设置则返回null
     */
    public static Long getUserId() {
        return threadLocal.get();
    }

    /**
     * 清除当前线程的ThreadLocal变量
     *
     * 使用ThreadLocal.remove()方法来移除当前线程中与该ThreadLocal实例关联的值
     * 这通常用于防止内存泄漏，特别是在使用ThreadLocal的地方结束时，或者在线程池环境中
     * 以确保没有线程因为重用而保留了旧的值
     */
    public static void clear() {
        threadLocal.remove();
    }
}
