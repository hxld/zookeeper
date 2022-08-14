package com.atguigu.case2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author hxld
 * @create 2022-08-13 17:40
 */
public class DistributedLock {

    private final String connectString = "192.168.119.100:2081,192.168.119.110:2081,192.168.119.120:2081";
    private final int sessionTimeout = 200000;
    private final ZooKeeper zk ;

    private  CountDownLatch countDownLatch = new CountDownLatch(1);

    //等待前一步骤完成之后，下一步骤才进行执行
    private  CountDownLatch waitLatch = new CountDownLatch(1);

    //前一个节点的路径
    private String waitPath;
    private String currentMode;


    public DistributedLock() throws IOException, InterruptedException, KeeperException {

        //获取连接
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

                //监听器中判断释放的时机


            //countDownLatch 如果连接上zk 可以释放

                //判断监听的事件的状态是否是连接
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    //如果是，释放掉
                    countDownLatch.countDown();
                }

                //waitLatch 需要释放

                //如果是节点的删除而且节点路径还是前一个节点路径，证明前一个节点已经下线
                if(watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)){
                    waitLatch.countDown();
                }
            }
        });

        //countDownLatch作用：等待zk正常连接后，程序才往下执行，代码健壮性更强
        countDownLatch.await();

        //判断根节点/locks是否存在
        Stat stat = zk.exists("/locks", false);


        //对状态进行判断
        if(stat == null){

            //如果不存在，创建根节点
            zk.create("/locks","locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        }
    }


    //对zk加锁  ---其实就是在/locks目录下创建对应的临时带序号的节点
    public void zklock(){
        //创建对应的临时带序号节点
        try {

            currentMode = zk.create("/locks/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            //wait-小会，让结果更清晰一些
            Thread.sleep(10);

            //判断创建的节点是否是最小的序号节点，如果是获取到锁；如果不是，监听他序号前一个节点
            List<String> children = zk.getChildren("/locks", false);

            //如果children只有一个值，那就直接获取锁；如果有多个节点，需要判断，谁最小；
            if(children.size() == 1){

                //直接返回，获取锁
                return;
            }else{
                //有多个节点，需要取出来进行比较


                //排序
                Collections.sort(children);

                //获取节点名称 seq-00000000
                String thisNode = currentMode.substring("/locks/".length());

                //通过seq-00000000获取该节点在children集合的位置
                int index = children.indexOf(thisNode);

                //判断
                if(index == -1){
                    System.out.println("数据异常");
                }else if (index == 0){
                    //就一个节点，直接返回，获取到锁
                    return ;
                }else {
                    //如果不是只有一个节点，就需要进行监听了


                    //需要监听 他前一个节点变化
                    //waitpath:前一个节点的路径
                    waitPath = "/locks/"+children.get(index -1);

                    //监听
                    zk.getData(waitPath,true,new Stat());

                    //等待监听
                    waitLatch.await();

                    return;
                }
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //解锁  --- 其实就是删除/locks目录下的临时节点
    public void unZklock(){

        //删除节点
        try {
            zk.delete(this.currentMode,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
