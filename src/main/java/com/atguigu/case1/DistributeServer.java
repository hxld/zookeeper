package com.atguigu.case1;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @author hxld
 * @create 2022-08-13 16:22
 */
public class DistributeServer {

    private String connectString = "192.168.119.100,192.168.119.110,192.168.119.120";
    private int sessionTimeout = 200000;
    private ZooKeeper zk;
    public static void main(String[] args) throws Exception {
        DistributeServer server = new DistributeServer();

        //1.获取zk连接
        server.getConnect();

        //2. 注册服务器到zk集群
        server.regist(args[0]);

        //3.启动业务逻辑（不让其一下子就执行完）
        server.business();
        
    }

    private void regist(String hostname) throws InterruptedException, KeeperException {
        //创建临时带序号节点并且传入主机名
        String create = zk.create("/servers/"+hostname, hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + " is online");
    }

    private void getConnect() throws IOException {

        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }
}
