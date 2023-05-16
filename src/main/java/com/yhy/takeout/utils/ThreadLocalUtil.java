package com.yhy.takeout.utils;

/**
 * 基于ThreadLocal的工具类，用户保存和获取当前登录用户id
 */
public class ThreadLocalUtil {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
