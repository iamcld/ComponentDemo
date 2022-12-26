package com.example.jucdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * cas：比较并交换
 * 	cas原理：比较并交换 compareAndSet(5, 202);返回ture表示要写到的值之前为5，现在
 * 				某个线程从中拷贝变量值5到自己的线程内存空间，通过compareAndSet修改成202后，然后重新把写到时，会比较下现在的值是否为5，如果是的话，那就表示
 * 				其他线程没有修改过的值，可以直接把202写到中。否则就是其他线程改过了，compareAndSet返回false,写入202到失败，即原子操作。
 *
 * cas不需要加锁，底层已经自己为1个共享变量添加了volatile关键字，保证各个线程的可见性
 * 缺点：1、循环时间开销长开销很大
 *      2、只能保证1个共享变量的原子操作
 *      3、引出来ABA问题
 */
public class CASDemo {
    public static void main(String[] arg) {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        //如果线程的期望值（5）和待修改主内存的真实值（5）一样，表明待修改主内存的值没有被修改，
        // 则可以修改主内存的值为202
        System.out.println(atomicInteger.compareAndSet(5, 202)
                + " ;;; current data = " + atomicInteger.get());

        //如果线程的期望值5和待修改主内存的真实值（此时为202）不一样，表明待修改主内存的值已经被修改了。
        // 则修改主内存的的值为121失败，需要重新再去读取主内存中的值，并且比较值是否为期望值
        // (源码中有while循环判断值是否一样：这同时也是cas的缺点，如果长时间判断值不一样，会导致cpu开销大)
        System.out.println(atomicInteger.compareAndSet(5, 121)
                + " ;;; current data = " + atomicInteger.get());
    }


}

/**
 * true ;;; current data = 202
 * false ;;; current data = 202
 */

