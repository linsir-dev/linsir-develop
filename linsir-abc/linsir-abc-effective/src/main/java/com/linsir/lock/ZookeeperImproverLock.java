package com.linsir.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @ Author     ：linsir
 * @ Date       ：Created in 2018/9/3 1:36
 * @ Description：description
 * @ Modified By：
 * @ Version: 1.0.0
 */
public class ZookeeperImproverLock implements Lock {

    private static final String LOCK_PATH="/LOCK";
    private static final String ZOOKEEPER_IP_PORT="192.168.126.128:2181";

    private ZkClient client =new ZkClient(ZOOKEEPER_IP_PORT,1000,1000,new SerializableSerializer());

    private static Logger logger=LoggerFactory.getLogger(ZookeeperImproverLock.class);

    private CountDownLatch cdl;

    private String beforePath; //当前请求的前一个节点
    private String currentPath; //当前请求的节点


    public ZookeeperImproverLock(){
        if (!this.client.exists(LOCK_PATH))
        {
            this.client.createPersistent(LOCK_PATH);
        }
    }


        /**
         *
         * 阻塞式加锁
         * create by: linsir
         * create time: 9:56 2018/9/3
         * params:
         * @return
         */
    @Override
    public void lock() {
        if(tryLock())
        {
            return;
        }
        waitForLock();
        lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    private void waitForLock(){
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                logger.info(Thread.currentThread().getName()+" 捕捉到Data删除事件");
                if (cdl!=null)
                {
                    cdl.countDown();
                }

            }
        };

        this.client.subscribeDataChanges(beforePath,listener);

        if(this.client.exists(beforePath))
        {
            cdl=new CountDownLatch(1);
            try {
                cdl.await();
            }catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        this.client.unsubscribeDataChanges(beforePath,listener);
    }


    @Override
    public boolean tryLock() {

        //  如果currentPath 为空则为第一次尝试加锁，第一次加锁并赋值 如果currentPath
        if (currentPath==null || currentPath.length()<=0)
        {
            currentPath=this.client.createEphemeralSequential(LOCK_PATH+"/","lock");
            logger.info("-------------->"+currentPath);
        }

        List<String> childrens=this.client.getChildren(LOCK_PATH);
        Collections.sort(childrens);

//       获取临时节点，临时节点的名称为自增长字符串000004000
        if (currentPath.equals((LOCK_PATH+"/"+childrens.get(0))))
        {
            return true;
        }else
        {  //  如果当前节点所在节点不是排名第一，则获取前面的节点名称 并赋值
            int wz=Collections.binarySearch(childrens,currentPath.substring(6));
            beforePath=LOCK_PATH+"/"+childrens.get(wz-1);
        }

        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        client.delete(currentPath);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
