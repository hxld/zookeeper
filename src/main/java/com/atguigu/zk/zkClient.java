package com.atguigu.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author hxld
 * @create 2022-08-13 14:41
 */
public class zkClient {
    private String connectString = "192.168.119.100,192.168.119.110,192.168.119.120";
    private int sessionTimeout = 200000;
    private ZooKeeper zkClient;

//    @Test
    @Before
    public void  init() throws IOException {
       zkClient =  new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

                //监听代码
//                System.out.println("----------------------------------");
//                //监听实时变化
//                //监听根目录下
//                List<String> children = null;
//                try {
//                    children = zkClient.getChildren("/", true);
//
//                    for (String child: children) {
//                        System.out.println(child);
//                    }
//                    System.out.println("----------------------------------");
//
//                } catch (KeeperException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

            }
        });
    }


    @Test
    public void create() throws InterruptedException, KeeperException {
        String nodeCreated = zkClient.create("/atguigu", "ss.avi".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }


    //监听实时变化
    @Test
    public void getChildren() throws InterruptedException, KeeperException {
        //true  开启监听
        List<String> children = zkClient.getChildren("/", true);

        for (String child: children) {
            System.out.println(child);
        }

        //设置延时(否则马上就执行完，再创建一个，控制台监听不到)
        Thread.sleep(Long.MAX_VALUE);
    }


    @Test
    public void exist() throws InterruptedException, KeeperException {
        Stat stat = zkClient.exists("/atguigu", false);
        System.out.println(stat==null? "not exist" : "exist");

    }
}
