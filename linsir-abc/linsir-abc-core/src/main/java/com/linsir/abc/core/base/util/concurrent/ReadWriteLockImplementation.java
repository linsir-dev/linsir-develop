package com.linsir.abc.core.base.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * 读写锁实现
 * 演示ReentrantReadWriteLock的核心原理：读共享、写独占、锁降级
 *
 * <p>核心特性：</p>
 * <ul>
 *   <li>读共享：多个读线程可以同时持有读锁</li>
 *   <li>写独占：写锁独占，其他读写线程都阻塞</li>
 *   <li>锁降级：写锁可以降级为读锁，读锁不能升级为写锁</li>
 *   <li>重入性：读写锁都支持重入</li>
 * </ul>
 *
 * <p>适用场景：</p>
 * <ul>
 *   <li>读多写少：读操作远多于写操作</li>
 *   <li>数据一致性：读操作需要看到最新数据</li>
 *   <li>并发性能：提高读操作的并发度</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ReadWriteLockImplementation implements ReadWriteLock {

    /**
     * 读锁
     */
    private final ReadLock readerLock;

    /**
     * 写锁
     */
    private final WriteLock writerLock;

    /**
     * 同步器
     */
    final Sync sync;

    /**
     * 默认构造器（非公平）
     */
    public ReadWriteLockImplementation() {
        this(false);
    }

    /**
     * 带公平性参数的构造器
     *
     * @param fair true表示公平锁
     */
    public ReadWriteLockImplementation(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
        readerLock = new ReadLock(this);
        writerLock = new WriteLock(this);
    }

    @Override
    public Lock readLock() {
        return readerLock;
    }

    @Override
    public Lock writeLock() {
        return writerLock;
    }

    /**
     * 获取读锁
     */
    public ReadLock readLockImpl() {
        return readerLock;
    }

    /**
     * 获取写锁
     */
    public WriteLock writeLockImpl() {
        return writerLock;
    }

    /**
     * 抽象同步器
     */
    abstract static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 6317671515068378041L;

        // 位运算常量
        static final int SHARED_SHIFT   = 16;
        static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
        static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
        static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

        // 获取读锁数量
        static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }

        // 获取写锁数量
        static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }

        // 读锁持有计数（ThreadLocal）
        private transient ThreadLocalHoldCounter readHolds;
        private transient HoldCounter cachedHoldCounter;
        private transient Thread firstReader;
        private transient int firstReaderHoldCount;

        Sync() {
            readHolds = new ThreadLocalHoldCounter();
            setState(getState());
        }

        // 抽象方法
        abstract boolean readerShouldBlock();
        abstract boolean writerShouldBlock();

        // 尝试释放写锁
        @Override
        protected final boolean tryRelease(int releases) {
            if (!isHeldExclusively()) {
                throw new IllegalMonitorStateException();
            }
            int nextc = getState() - releases;
            boolean free = exclusiveCount(nextc) == 0;
            if (free) {
                setExclusiveOwnerThread(null);
            }
            setState(nextc);
            return free;
        }

        // 尝试获取写锁
        @Override
        protected final boolean tryAcquire(int acquires) {
            Thread current = Thread.currentThread();
            int c = getState();
            int w = exclusiveCount(c);
            if (c != 0) {
                // 有读锁或写锁被其他线程持有
                if (w == 0 || current != getExclusiveOwnerThread()) {
                    return false;
                }
                // 重入
                if (w + exclusiveCount(acquires) > MAX_COUNT) {
                    throw new Error("Maximum lock count exceeded");
                }
                setState(c + acquires);
                return true;
            }
            if (writerShouldBlock() ||
                    !compareAndSetState(c, c + acquires)) {
                return false;
            }
            setExclusiveOwnerThread(current);
            return true;
        }

        // 尝试释放读锁
        @Override
        protected final boolean tryReleaseShared(int unused) {
            Thread current = Thread.currentThread();
            if (firstReader == current) {
                if (firstReaderHoldCount == 1) {
                    firstReader = null;
                } else {
                    firstReaderHoldCount--;
                }
            } else {
                HoldCounter rh = cachedHoldCounter;
                if (rh == null || rh.tid != getThreadId(current)) {
                    rh = readHolds.get();
                }
                int count = rh.count;
                if (count <= 1) {
                    readHolds.remove();
                    if (count <= 0) {
                        throw unmatchedUnlockException();
                    }
                }
                --rh.count;
            }
            for (;;) {
                int c = getState();
                int nextc = c - SHARED_UNIT;
                if (compareAndSetState(c, nextc)) {
                    return nextc == 0;
                }
            }
        }

        private IllegalMonitorStateException unmatchedUnlockException() {
            return new IllegalMonitorStateException(
                    "attempt to unlock read lock, not locked by current thread");
        }

        // 尝试获取读锁
        @Override
        protected final int tryAcquireShared(int unused) {
            Thread current = Thread.currentThread();
            int c = getState();
            // 有写锁且不是当前线程持有，不能获取读锁
            if (exclusiveCount(c) != 0 &&
                    getExclusiveOwnerThread() != current) {
                return -1;
            }
            int r = sharedCount(c);
            if (!readerShouldBlock() &&
                    r < MAX_COUNT &&
                    compareAndSetState(c, c + SHARED_UNIT)) {
                if (r == 0) {
                    firstReader = current;
                    firstReaderHoldCount = 1;
                } else if (firstReader == current) {
                    firstReaderHoldCount++;
                } else {
                    HoldCounter rh = cachedHoldCounter;
                    if (rh == null || rh.tid != getThreadId(current)) {
                        cachedHoldCounter = rh = readHolds.get();
                    } else if (rh.count == 0) {
                        readHolds.set(rh);
                    }
                    rh.count++;
                }
                return 1;
            }
            return fullTryAcquireShared(current);
        }

        final int fullTryAcquireShared(Thread current) {
            HoldCounter rh = null;
            for (;;) {
                int c = getState();
                if (exclusiveCount(c) != 0) {
                    if (getExclusiveOwnerThread() != current) {
                        return -1;
                    }
                } else if (readerShouldBlock()) {
                    if (firstReader == current) {
                        // 重入
                    } else {
                        if (rh == null) {
                            rh = cachedHoldCounter;
                            if (rh == null || rh.tid != getThreadId(current)) {
                                rh = readHolds.get();
                                if (rh.count == 0) {
                                    readHolds.remove();
                                }
                            }
                        }
                        if (rh.count == 0) {
                            return -1;
                        }
                    }
                }
                if (sharedCount(c) == MAX_COUNT) {
                    throw new Error("Maximum lock count exceeded");
                }
                if (compareAndSetState(c, c + SHARED_UNIT)) {
                    if (sharedCount(c) == 0) {
                        firstReader = current;
                        firstReaderHoldCount = 1;
                    } else if (firstReader == current) {
                        firstReaderHoldCount++;
                    } else {
                        if (rh == null) {
                            rh = cachedHoldCounter;
                        }
                        if (rh == null || rh.tid != getThreadId(current)) {
                            rh = readHolds.get();
                        } else if (rh.count == 0) {
                            readHolds.set(rh);
                        }
                        rh.count++;
                        cachedHoldCounter = rh;
                    }
                    return 1;
                }
            }
        }

        // 判断是否为写锁持有者
        @Override
        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        // 条件对象
        final ConditionObject newCondition() {
            return new ConditionObject();
        }

        // 获取持有线程
        final Thread getOwner() {
            return ((exclusiveCount(getState()) == 0) ?
                    null :
                    getExclusiveOwnerThread());
        }

        // 获取读锁持有数
        final int getReadLockCount() {
            return sharedCount(getState());
        }

        // 判断写锁是否被持有
        final boolean isWriteLocked() {
            return exclusiveCount(getState()) != 0;
        }

        // 获取写锁持有数
        final int getWriteHoldCount() {
            return isHeldExclusively() ? exclusiveCount(getState()) : 0;
        }

        // 获取读锁持有数
        final int getReadHoldCount() {
            if (getReadLockCount() == 0) {
                return 0;
            }
            Thread current = Thread.currentThread();
            if (firstReader == current) {
                return firstReaderHoldCount;
            }
            HoldCounter rh = cachedHoldCounter;
            if (rh != null && rh.tid == getThreadId(current)) {
                return rh.count;
            }
            int count = readHolds.get().count;
            if (count == 0) {
                readHolds.remove();
            }
            return count;
        }

        /**
         * 判断队列中的第一个等待线程是否正在等待独占锁（写锁）
         * 用于非公平读锁的获取，防止写线程饥饿
         */
        final boolean apparentlyFirstQueuedIsExclusive() {
            Node h, s;
            return (h = getHead()) != null &&
                    (s = h.next) != null &&
                    !s.isShared() &&
                    s.thread != null;
        }
    }

    /**
     * 非公平同步器
     */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = -8159625535654395037L;

        @Override
        final boolean writerShouldBlock() {
            return false; // 非公平，写锁不阻塞
        }

        @Override
        final boolean readerShouldBlock() {
            // 如果队列头节点是写线程，则读线程阻塞（防止写线程饥饿）
            return apparentlyFirstQueuedIsExclusive();
        }
    }

    /**
     * 公平同步器
     */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -2274990926593161451L;

        @Override
        final boolean writerShouldBlock() {
            return hasQueuedPredecessors();
        }

        @Override
        final boolean readerShouldBlock() {
            return hasQueuedPredecessors();
        }
    }

    /**
     * 读锁
     */
    public static class ReadLock implements Lock {
        private final Sync sync;

        protected ReadLock(ReadWriteLockImplementation lock) {
            sync = lock.sync;
        }

        @Override
        public void lock() {
            sync.acquireShared(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            sync.acquireSharedInterruptibly(1);
        }

        @Override
        public boolean tryLock() {
            return sync.tryAcquireShared(1) >= 0;
        }

        @Override
        public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
            return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
        }

        @Override
        public void unlock() {
            sync.releaseShared(1);
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 写锁
     */
    public static class WriteLock implements Lock {
        private final Sync sync;

        protected WriteLock(ReadWriteLockImplementation lock) {
            sync = lock.sync;
        }

        @Override
        public void lock() {
            sync.acquire(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            sync.acquireInterruptibly(1);
        }

        @Override
        public boolean tryLock() {
            return sync.tryAcquire(1);
        }

        @Override
        public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
            return sync.tryAcquireNanos(1, unit.toNanos(timeout));
        }

        @Override
        public void unlock() {
            sync.release(1);
        }

        @Override
        public Condition newCondition() {
            return sync.newCondition();
        }

        /**
         * 判断写锁是否被当前线程持有
         */
        public boolean isHeldByCurrentThread() {
            return sync.isHeldExclusively();
        }

        /**
         * 获取写锁持有数
         */
        public int getHoldCount() {
            return sync.getWriteHoldCount();
        }
    }

    /**
     * 持有计数器
     */
    static final class HoldCounter {
        int count = 0;
        final long tid = getThreadId(Thread.currentThread());
    }

    /**
     * ThreadLocal持有计数器
     */
    static final class ThreadLocalHoldCounter extends ThreadLocal<HoldCounter> {
        @Override
        public HoldCounter initialValue() {
            return new HoldCounter();
        }
    }

    // 获取线程ID
    static long getThreadId(Thread thread) {
        return thread.getId();
    }

    // 公开方法
    public final boolean isFair() {
        return sync instanceof FairSync;
    }

    protected Thread getOwner() {
        return sync.getOwner();
    }

    public int getReadLockCount() {
        return sync.getReadLockCount();
    }

    public boolean isWriteLocked() {
        return sync.isWriteLocked();
    }

    public boolean isWriteLockedByCurrentThread() {
        return sync.isHeldExclusively();
    }

    public int getWriteHoldCount() {
        return sync.getWriteHoldCount();
    }

    public int getReadHoldCount() {
        return sync.getReadHoldCount();
    }

    protected boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    public final boolean hasQueuedThread(Thread thread) {
        return sync.isQueued(thread);
    }

    public final int getQueueLength() {
        return sync.getQueueLength();
    }

    public String toString() {
        int c = sync.getState();
        int w = Sync.exclusiveCount(c);
        int r = Sync.sharedCount(c);
        return super.toString() +
                "[Write locks = " + w + ", Read locks = " + r + "]";
    }
}
