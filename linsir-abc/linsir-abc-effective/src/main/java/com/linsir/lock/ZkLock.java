package com.linsir.lock;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

/**
 * @ Author     ：linsir
 * @ Date       ：Created in 2018/9/2 23:25
 * @ Description：description
 * @ Modified By：
 * @ Version: 1.0.0
 */
public class ZkLock {



    public static void main(String[] args)
    {
           String ZOOKEEPER_IP_PORT="192.168.126.128:2181";
           ZkClient client=new ZkClient(ZOOKEEPER_IP_PORT,1000,1000,new SerializableSerializer());
           String path="/watcher";
           client.createPersistent(path);
//           client.writeData("/watcher","Linsir");
    }
}
