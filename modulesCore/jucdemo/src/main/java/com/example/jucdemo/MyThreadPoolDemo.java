package com.example.jucdemo;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPoolDemo {

    /**
     *
     如果当前线程池中的数量小于corePoolSize，创建并添加的任务。
     如果当前线程池中的数量等于corePoolSize，缓冲队列 workQueue未满，那么任务被放入缓冲队列、等待任务调度执行。
     如果当前线程池中的数量大于corePoolSize，缓冲队列workQueue已满，并且线程池中的数量小于maximumPoolSize，新提交任务会创建新线程执行任务。
     如果当前线程池中的数量大于corePoolSize，缓冲队列workQueue已满，并且线程池中的数量等于maximumPoolSize，新提交任务由Handler处理。
     当线程池中的线程大于corePoolSize时，多余线程空闲时间超过keepAliveTime时，会关闭这部分线程。
     * @param arg
     */
    public static void main(String[] arg) {
        // cpu核心个数
        System.out.println(Runtime.getRuntime().availableProcessors());

        // 最大线程数参数考公式：cpu核数/（1-阻塞系数） 阻塞系数在0.8~0.9之间
        // 比如8核cpu:8/(1-0.9) = 80个线程
        ExecutorService executorService = new ThreadPoolExecutor(
                2, // 核心线程数
                6,// 线程池能够容纳同时执行的最大线程数
                1L,//多余的空闲线程的存活时间，当前线程池数量超过corePoolSize时，当空闲线程时间达到keepAliveTime时，
                // 多余空闲线程会被销毁直到只剩下corePoolSize个线程为止
                TimeUnit.SECONDS,// keepAliveTime时间单位
                new LinkedBlockingQueue<Runnable>(3),//任务队列，被提交但尚未被执行的任务
                Executors.defaultThreadFactory(),// 表示生成线程池中工作线程的线程工厂，用于创建线程，一般默认即可                new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略，表示当队列满了，并且工作线程大于等于线程池中的最大线程数时，如何来进行拒绝
                new ThreadPoolExecutor.AbortPolicy()); // 拒绝策略，超出最大处理线程抛出异常（最大处理线程数=最大线程数+阻塞队列大小）
//                new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略，从哪个线程创建就由那个线程执行（如main线程创建，就在main线程执行）
//                new ThreadPoolExecutor.DiscardOldestPolicy()); // 拒绝策略，队列满了不会抛出异常
//                new ThreadPoolExecutor.DiscardPolicy()); // 拒绝策略，尝试去和第一个竞争，也不会抛出异常

        try {
            // 模拟10个用户来办理业务，每个用户就是一个来自外部的请求线程
            for (int i = 0; i <= 6; i++) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(Thread.currentThread().getName() + "办理业务");
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

    }

}
