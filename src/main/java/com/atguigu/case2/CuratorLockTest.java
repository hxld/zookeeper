package com.atguigu.case2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author hxld
 * @create 2022-08-14 16:42
 */
public class CuratorLockTest {
    public static void main(String[] args) {

        //创建分布式锁1

        InterProcessMutex lock1 = new InterProcessMutex(getCuratorFramework(), "/locks");

        //创建分布式锁2
        InterProcessMutex lock2 = new InterProcessMutex(getCuratorFramework(), "/locks");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.acquire();
                    System.out.println("线程1 获取到锁");

                    lock1.acquire();
                    System.out.println("线程1 再次获取到锁");

                    Thread.sleep(5*1000);

                    lock1.release();
                    System.out.println("线程1 释放锁");

                    lock1.release();
                    System.out.println("线程1 再次释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();




        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.acquire();
                    System.out.println("线程2 获取到锁");

                    lock1.acquire();
                    System.out.println("线程2 再次获取到锁");

                    Thread.sleep(5*1000);

                    lock1.release();
                    System.out.println("线程2 释放锁");

                    lock1.release();
                    System.out.println("线程2 再次释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private static CuratorFramework getCuratorFramework() {

        //失败之后重试的时间和次数
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(20000, 3);

        //创建客户端
        //retryPolicy 失败之后重试次数和时间
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("192.168.119.100:2181,192.168.119.110:2181,192.168.119.120:2181")
                .sessionTimeoutMs(200000)
                .connectionTimeoutMs(20000)
                .retryPolicy(retry)
                .build();

        //客户端启动
        client.start();
        System.out.println("zookeeper 客户端启动成功");
        //返回客户端
        return client;

    }
}
