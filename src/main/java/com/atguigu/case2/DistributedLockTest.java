package com.atguigu.case2;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * @author hxld
 * @create 2022-08-14 16:22
 */
public class DistributedLockTest {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        DistributedLock lock1 = new DistributedLock();

        DistributedLock lock2 = new DistributedLock();


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //创建锁
                    lock1.zklock();
                    System.out.println("线程1 启动，获取到锁");
                    //让线程延迟一会
                    Thread.sleep(5 * 1000);

                    lock1.unZklock();
                    System.out.println("线程1 释放锁");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //创建锁
                    lock2.zklock();
                    System.out.println("线程2 启动，获取到锁");
                    //让线程延迟一会
                    Thread.sleep(5 * 1000);

                    lock2.unZklock();
                    System.out.println("线程2 释放锁");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
