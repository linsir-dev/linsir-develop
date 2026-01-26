# 基于Zookeeper如何实现分布式锁？

## Zookeeper简介

Zookeeper是一个分布式的、开放源码的分布式应用程序协调服务，是Google Chubby的开源实现。Zookeeper提供了高性能、高可用、具有严格顺序访问控制能力的分布式协调服务。

### Zookeeper的核心特性

1. **顺序一致性**：从同一个客户端发起的事务请求，最终将会严格按照其发起顺序被应用到Zookeeper上
2. **原子性**：所有事务请求的处理结果在整个集群中所有机器上的应用情况是一致的
3. **单一视图**：无论客户端连接到哪个Zookeeper服务器，它看到的服务端数据模型都是一致的
4. **可靠性**：一旦服务端成功地应用了一个事务，并完成对客户端的响应，那么该事务所引起的服务端状态变更将会一直保留，直到有另一个事务又对其进行了改变
5. **实时性**：Zookeeper保证在一定的时间段内，客户端最终一定能够从服务端上读取到最新的数据状态

## Zookeeper的数据模型

Zookeeper的数据模型是一个树形结构，类似于Unix文件系统。每个节点称为一个Znode，每个Znode都可以存储数据。

### Znode的类型

1. **持久节点**：节点创建后会一直存在，直到被显式删除
2. **临时节点**：节点的生命周期依赖于创建该节点的客户端会话，一旦客户端会话结束，节点会被自动删除
3. **持久顺序节点**：节点创建后会一直存在，Zookeeper会自动为节点名称添加一个数字后缀
4. **临时顺序节点**：节点的生命周期依赖于创建该节点的客户端会话，Zookeeper会自动为节点名称添加一个数字后缀

## 基于Zookeeper实现分布式锁的方案

### 1. 使用临时节点实现

#### 1.1 基本原理

利用Zookeeper的临时节点特性，当客户端创建临时节点成功时，表示获取锁成功；当客户端会话结束时，临时节点被自动删除，表示释放锁。

#### 1.2 实现代码

```java
public class ZkDistributedLock {
    private ZooKeeper zk;
    private String lockPath;
    private String lockValue;

    public ZkDistributedLock(String connectString, String lockPath, String lockValue) throws IOException {
        this.zk = new ZooKeeper(connectString, 30000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
            }
        });
        this.lockPath = lockPath;
        this.lockValue = lockValue;
    }

    public boolean tryLock() {
        try {
            zk.create(lockPath, lockValue.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        } catch (KeeperException.NodeExistsException e) {
            return false;
        } catch (KeeperException | InterruptedException e) {
            return false;
        }
    }

    public void unlock() {
        try {
            zk.delete(lockPath, -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
```

#### 1.3 存在的问题

- **惊群效应**：当锁释放时，所有等待的客户端都会被唤醒，但只有一个客户端能获取锁，其他客户端又进入等待状态，造成资源浪费
- **不可重入**：同一个客户端无法多次获取同一个锁
- **不公平**：客户端获取锁的顺序是随机的，不公平

### 2. 使用临时顺序节点实现

#### 2.1 基本原理

1. 客户端创建临时顺序节点
2. 获取所有子节点，判断自己是否是最小的节点
3. 如果是最小的节点，表示获取锁成功
4. 如果不是最小的节点，监听前一个节点的删除事件
5. 当前一个节点被删除时，表示前一个客户端释放了锁，当前客户端可以获取锁

#### 2.2 实现代码

```java
public class ZkDistributedLockV2 {
    private ZooKeeper zk;
    private String lockPath;
    private String lockValue;
    private String currentPath;
    private CountDownLatch latch;

    public ZkDistributedLockV2(String connectString, String lockPath, String lockValue) throws IOException {
        this.zk = new ZooKeeper(connectString, 30000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeDeleted && latch != null) {
                    latch.countDown();
                }
            }
        });
        this.lockPath = lockPath;
        this.lockValue = lockValue;
    }

    public boolean tryLock() {
        try {
            currentPath = zk.create(lockPath + "/", lockValue.getBytes(), 
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            
            while (true) {
                List<String> children = zk.getChildren(lockPath, false);
                Collections.sort(children);
                
                String currentNode = currentPath.substring(lockPath.length() + 1);
                int currentIndex = children.indexOf(currentNode);
                
                if (currentIndex == 0) {
                    return true;
                }
                
                String previousNode = children.get(currentIndex - 1);
                latch = new CountDownLatch(1);
                zk.exists(lockPath + "/" + previousNode, true);
                latch.await();
            }
        } catch (KeeperException | InterruptedException e) {
            return false;
        }
    }

    public void unlock() {
        try {
            zk.delete(currentPath, -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
```

#### 2.3 优点

- **避免惊群效应**：客户端只监听前一个节点的删除事件，不会造成大量客户端同时被唤醒
- **公平锁**：客户端按照节点创建顺序获取锁，保证公平性
- **可重入**：可以通过记录客户端信息实现可重入

### 3. 使用Curator实现

#### 3.1 Curator简介

Curator是Netflix开源的一套Zookeeper客户端框架，封装了Zookeeper的底层API，提供了更高级的抽象和更简单的API。

#### 3.2 基本使用

```java
public class CuratorDistributedLock {
    private CuratorFramework client;
    private InterProcessMutex lock;

    public CuratorDistributedLock(String connectString, String lockPath) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        this.client.start();
        this.lock = new InterProcessMutex(client, lockPath);
    }

    public void doWithLock(Runnable runnable) {
        try {
            lock.acquire();
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean tryLock(long time, TimeUnit unit) {
        try {
            return lock.acquire(time, unit);
        } catch (Exception e) {
            return false;
        }
    }

    public void unlock() {
        try {
            lock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

#### 3.3 InterProcessMutex的特性

- **可重入锁**：同一个线程可以多次获取同一个锁
- **公平锁**：按照请求顺序获取锁
- **锁超时**：支持设置获取锁的超时时间
- **锁续期**：支持锁的续期机制

### 4. 实现可重入锁

#### 4.1 基本实现

```java
public class ZkReentrantLock {
    private ZooKeeper zk;
    private String lockPath;
    private String lockValue;
    private String currentPath;
    private ThreadLocal<Integer> lockCount = new ThreadLocal<>();

    public ZkReentrantLock(String connectString, String lockPath, String lockValue) throws IOException {
        this.zk = new ZooKeeper(connectString, 30000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
            }
        });
        this.lockPath = lockPath;
        this.lockValue = lockValue;
    }

    public boolean tryLock() {
        Integer count = lockCount.get();
        if (count != null && count > 0) {
            lockCount.set(count + 1);
            return true;
        }
        
        try {
            currentPath = zk.create(lockPath + "/", lockValue.getBytes(), 
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            
            while (true) {
                List<String> children = zk.getChildren(lockPath, false);
                Collections.sort(children);
                
                String currentNode = currentPath.substring(lockPath.length() + 1);
                int currentIndex = children.indexOf(currentNode);
                
                if (currentIndex == 0) {
                    lockCount.set(1);
                    return true;
                }
                
                String previousNode = children.get(currentIndex - 1);
                CountDownLatch latch = new CountDownLatch(1);
                zk.exists(lockPath + "/" + previousNode, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (event.getType() == Event.EventType.NodeDeleted) {
                            latch.countDown();
                        }
                    }
                });
                latch.await();
            }
        } catch (KeeperException | InterruptedException e) {
            return false;
        }
    }

    public void unlock() {
        Integer count = lockCount.get();
        if (count == null || count <= 0) {
            return;
        }
        
        if (count > 1) {
            lockCount.set(count - 1);
            return;
        }
        
        try {
            zk.delete(currentPath, -1);
            lockCount.remove();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
```

### 5. 实现读写锁

#### 5.1 基本实现

```java
public class ZkReadWriteLock {
    private ZooKeeper zk;
    private String lockPath;
    private String lockValue;

    public ZkReadWriteLock(String connectString, String lockPath, String lockValue) throws IOException {
        this.zk = new ZooKeeper(connectString, 30000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
            }
        });
        this.lockPath = lockPath;
        this.lockValue = lockValue;
    }

    public boolean tryReadLock() {
        try {
            String currentPath = zk.create(lockPath + "/read-", lockValue.getBytes(), 
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            
            while (true) {
                List<String> children = zk.getChildren(lockPath, false);
                Collections.sort(children);
                
                String currentNode = currentPath.substring(lockPath.length() + 1);
                int currentIndex = children.indexOf(currentNode);
                
                boolean hasWriteLock = false;
                for (int i = 0; i < currentIndex; i++) {
                    if (children.get(i).startsWith("write-")) {
                        hasWriteLock = true;
                        break;
                    }
                }
                
                if (!hasWriteLock) {
                    return true;
                }
                
                String previousNode = children.get(currentIndex - 1);
                CountDownLatch latch = new CountDownLatch(1);
                zk.exists(lockPath + "/" + previousNode, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (event.getType() == Event.EventType.NodeDeleted) {
                            latch.countDown();
                        }
                    }
                });
                latch.await();
            }
        } catch (KeeperException | InterruptedException e) {
            return false;
        }
    }

    public boolean tryWriteLock() {
        try {
            String currentPath = zk.create(lockPath + "/write-", lockValue.getBytes(), 
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            
            while (true) {
                List<String> children = zk.getChildren(lockPath, false);
                Collections.sort(children);
                
                String currentNode = currentPath.substring(lockPath.length() + 1);
                int currentIndex = children.indexOf(currentNode);
                
                if (currentIndex == 0) {
                    return true;
                }
                
                String previousNode = children.get(currentIndex - 1);
                CountDownLatch latch = new CountDownLatch(1);
                zk.exists(lockPath + "/" + previousNode, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (event.getType() == Event.EventType.NodeDeleted) {
                            latch.countDown();
                        }
                    }
                });
                latch.await();
            }
        } catch (KeeperException | InterruptedException e) {
            return false;
        }
    }

    public void unlock(String currentPath) {
        try {
            zk.delete(currentPath, -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
```

#### 5.2 使用Curator实现读写锁

```java
public class CuratorReadWriteLock {
    private CuratorFramework client;
    private InterProcessReadWriteLock lock;

    public CuratorReadWriteLock(String connectString, String lockPath) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        this.client.start();
        this.lock = new InterProcessReadWriteLock(client, lockPath);
    }

    public void doWithReadLock(Runnable runnable) {
        try {
            lock.readLock().acquire();
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.readLock().release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void doWithWriteLock(Runnable runnable) {
        try {
            lock.writeLock().acquire();
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.writeLock().release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```

## Zookeeper分布式锁的优缺点

### 优点

1. **可靠性高**：Zookeeper保证了数据的强一致性，避免了Redis主从切换导致的锁丢失问题
2. **支持锁续期**：通过监听机制，可以实现锁的续期
3. **支持公平锁**：通过临时顺序节点，可以实现公平锁
4. **支持可重入锁**：可以通过记录客户端信息实现可重入
5. **支持读写锁**：可以区分读锁和写锁，提高并发性能
6. **避免惊群效应**：客户端只监听前一个节点的删除事件，不会造成大量客户端同时被唤醒

### 缺点

1. **性能相对较低**：Zookeeper的读写性能不如Redis，获取锁和释放锁的响应时间较长
2. **实现复杂**：相比Redis，Zookeeper的实现更复杂，需要处理更多的异常情况
3. **依赖Zookeeper集群**：需要部署和维护Zookeeper集群，增加了运维成本
4. **网络开销大**：每次获取锁和释放锁都需要与Zookeeper集群交互，网络开销大
5. **Zookeeper单点问题**：虽然Zookeeper支持集群，但如果大部分节点宕机，锁服务不可用

## Zookeeper分布式锁的最佳实践

### 1. 使用Curator框架

Curator提供了成熟的分布式锁实现，包括：

- **InterProcessMutex**：可重入锁
- **InterProcessSemaphoreMutex**：不可重入锁
- **InterProcessReadWriteLock**：读写锁
- **InterProcessMultiLock**：联锁

```java
CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
client.start();

InterProcessMutex lock = new InterProcessMutex(client, "/locks/myLock");
try {
    lock.acquire();
    doSomething();
} finally {
    lock.release();
}
```

### 2. 设置合理的超时时间

```java
boolean acquired = lock.acquire(10, TimeUnit.SECONDS);
if (acquired) {
    try {
        doSomething();
    } finally {
        lock.release();
    }
}
```

### 3. 实现锁续期机制

```java
public class LockRenewalTask implements Runnable {
    private InterProcessMutex lock;
    private volatile boolean running = true;

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(10000);
                if (lock.isAcquiredInThisProcess()) {
                    lock.makeRevocable(new RevocationListener<InterProcessMutex>() {
                        @Override
                        public void revocationRequested(InterProcessMutex lock) {
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
    }
}
```

### 4. 监控锁的使用情况

```java
public class LockMonitor {
    private CuratorFramework client;
    private String lockPath;

    public void monitorLock() {
        try {
            List<String> children = client.getChildren().forPath(lockPath);
            System.out.println("Current lock holders: " + children);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 5. 处理异常情况

```java
public class SafeLock {
    private InterProcessMutex lock;

    public void doWithLock(Runnable runnable) {
        try {
            lock.acquire();
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (lock.isAcquiredInThisProcess()) {
                    lock.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```

## 总结

基于Zookeeper实现分布式锁是一种可靠性高的方案，适用于对可靠性要求高的场景。Zookeeper通过临时顺序节点和监听机制，实现了公平锁、可重入锁、读写锁等功能。

在实际应用中，建议使用Curator框架，它提供了成熟的分布式锁实现，简化了开发难度。同时，需要注意设置合理的超时时间、实现锁续期机制、监控锁的使用情况等最佳实践。

如果对性能要求高，可以选择基于Redis的分布式锁；如果对可靠性要求高，可以选择基于Zookeeper的分布式锁。
