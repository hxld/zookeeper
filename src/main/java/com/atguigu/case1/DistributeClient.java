package com.atguigu.case1;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hxld
 * @create 2022-08-13 16:35
 */
public class DistributeClient {
    private String connectString = "192.168.119.100,192.168.119.110,192.168.119.120";
    private int sessionTimeout = 200000;
    private ZooKeeper zk;
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        DistributeClient client = new DistributeClient();
        //1.获取zk连接
        client.getConnect();

        //2.监听/servers虾米子节点的增加和删除
        client.getServerList();

        //3.业务逻辑（睡觉）
        client.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void getServerList() throws InterruptedException, KeeperException {

        List<String> children = zk.getChildren("/servers", true);

        ArrayList<String> servers = new ArrayList<>();

        //遍历子节点，取出主机名称，封装到一个集合中进行打印（封装到集合中是为了方便打印）

        for (String child : children) {
            byte[] data = zk.getData("/servers/" + child, false, null);
            servers.add(new String(data));
        }

        //打印
        System.out.println(servers);
    }

    private void getConnect() throws IOException {
         zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

                //如果这里不加，那么程序就只监听一次，在初始化这里加了监听之后，时刻进行监听
                try {
                    getServerList();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
