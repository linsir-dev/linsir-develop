package com.linsir.abc.core.base.util.concurrent.lock;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 条件变量实现
 * 演示Condition的核心原理：等待队列、通知机制、与Lock配合使用
 *
 * <p>核心特性：</p>
 * <ul>
 *   <li>等待/通知：await()等待条件，signal()/signalAll()通知</li>
 *   <li>与Lock绑定：Condition必须与Lock配合使用</li>
 *   <li>中断响应：支持中断响应和超时</li>
 *   <li>防止虚假唤醒：await()应在循环中调用</li>
 * </ul>
 *
 * <p>与Object.wait/notify对比：</p>
 * <ul>
 *   <li>多个条件：一个Lock可以有多个Condition</li>
 *   <li>精确通知：可以精确通知等待特定条件的线程</li>
 *   <li>可中断：支持可中断等待</li>
 *   <li>超时：支持带超时的等待</li>
 * </ul>
 *
 * <p>使用模式：</p>
 * <pre>
 * lock.lock();
 * try {
 *     while (!condition) {
 *         condition.await();
 *     }
 *     // 执行业务逻辑
 * } finally {
 *     lock.unlock();
 * }
 * </pre>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConditionVariable implements Condition {

    /**
     * 关联的锁
     */
    private final Lock lock;

    /**
     * 等待队列头节点
     */
    private transient Node firstWaiter;

    /**
     * 等待队列尾节点
     */
    private transient Node lastWaiter;

    /**
     * 构造器
     *
     * @param lock 关联的锁
     */
    public ConditionVariable(Lock lock) {
        if (lock == null) {
            throw new NullPointerException("lock cannot be null");
        }
        this.lock = lock;
    }

    @Override
    public void await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        // 添加到条件队列
        Node node = addConditionWaiter();

        // 完全释放锁
        int savedState = fullyRelease();

        int interruptMode = 0;

        // 等待直到被转移到同步队列
        while (!isOnSyncQueue(node)) {
            LockSupport.park(this);
            if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) {
                break;
            }
        }

        // 重新获取锁
        if (acquireQueued(node, savedState) && interruptMode != 0) {
            reportInterruptAfterWait(interruptMode);
        }
    }

    @Override
    public void awaitUninterruptibly() {
        // 添加到条件队列
        Node node = addConditionWaiter();

        // 完全释放锁
        int savedState = fullyRelease();

        boolean interrupted = false;

        // 等待直到被转移到同步队列
        while (!isOnSyncQueue(node)) {
            LockSupport.park(this);
            if (Thread.interrupted()) {
                interrupted = true;
            }
        }

        // 重新获取锁
        if (acquireQueued(node, savedState) || interrupted) {
            selfInterrupt();
        }
    }

    @Override
    public long awaitNanos(long nanosTimeout) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        Node node = addConditionWaiter();
        int savedState = fullyRelease();
        final long deadline = System.nanoTime() + nanosTimeout;
        int interruptMode = 0;

        while (!isOnSyncQueue(node)) {
            if (nanosTimeout <= 0L) {
                transferAfterCancelledWait(node);
                break;
            }
            if (nanosTimeout > 1000L) { // 自旋阈值
                LockSupport.parkNanos(this, nanosTimeout);
            }
            if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) {
                break;
            }
            nanosTimeout = deadline - System.nanoTime();
        }

        if (acquireQueued(node, savedState) && interruptMode != 0) {
            reportInterruptAfterWait(interruptMode);
        }

        return deadline - System.nanoTime();
    }

    @Override
    public boolean await(long time, TimeUnit unit) throws InterruptedException {
        return awaitNanos(unit.toNanos(time)) > 0;
    }

    @Override
    public boolean awaitUntil(Date deadline) throws InterruptedException {
        long abstime = deadline.getTime();
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        Node node = addConditionWaiter();
        int savedState = fullyRelease();
        boolean timedout = false;
        int interruptMode = 0;

        while (!isOnSyncQueue(node)) {
            if (System.currentTimeMillis() > abstime) {
                timedout = transferAfterCancelledWait(node);
                break;
            }
            LockSupport.parkUntil(this, abstime);
            if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) {
                break;
            }
        }

        if (acquireQueued(node, savedState) && interruptMode != 0) {
            reportInterruptAfterWait(interruptMode);
        }

        return !timedout;
    }

    @Override
    public void signal() {
        // 检查是否持有锁
        if (!isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException("lock is not held by current thread");
        }

        Node first = firstWaiter;
        if (first != null) {
            doSignal(first);
        }
    }

    @Override
    public void signalAll() {
        // 检查是否持有锁
        if (!isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException("lock is not held by current thread");
        }

        Node first = firstWaiter;
        if (first != null) {
            doSignalAll(first);
        }
    }

    /**
     * 通知单个等待线程
     */
    private void doSignal(Node first) {
        do {
            if ((firstWaiter = first.nextWaiter) == null) {
                lastWaiter = null;
            }
            first.nextWaiter = null;
        } while (!transferForSignal(first) &&
                (first = firstWaiter) != null);
    }

    /**
     * 通知所有等待线程
     */
    private void doSignalAll(Node first) {
        lastWaiter = firstWaiter = null;
        do {
            Node next = first.nextWaiter;
            first.nextWaiter = null;
            transferForSignal(first);
            first = next;
        } while (first != null);
    }

    /**
     * 将节点从条件队列转移到同步队列
     */
    private boolean transferForSignal(Node node) {
        // 尝试将节点状态改为0
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            return false;
        }

        // 添加到同步队列
        Node p = enq(node);
        int ws = p.waitStatus;
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL)) {
            LockSupport.unpark(node.thread);
        }
        return true;
    }

    /**
     * 取消等待后转移
     */
    private boolean transferAfterCancelledWait(Node node) {
        if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            enq(node);
            return true;
        }
        // 已经在同步队列中
        while (!isOnSyncQueue(node)) {
            Thread.yield();
        }
        return false;
    }

    /**
     * 添加到条件等待队列
     */
    private Node addConditionWaiter() {
        Node t = lastWaiter;

        // 清理已取消的节点
        if (t != null && t.waitStatus != Node.CONDITION) {
            unlinkCancelledWaiters();
            t = lastWaiter;
        }

        Node node = new Node(Thread.currentThread(), Node.CONDITION);
        if (t == null) {
            firstWaiter = node;
        } else {
            t.nextWaiter = node;
        }
        lastWaiter = node;
        return node;
    }

    /**
     * 清理已取消的等待节点
     */
    private void unlinkCancelledWaiters() {
        Node t = firstWaiter;
        Node trail = null;
        while (t != null) {
            Node next = t.nextWaiter;
            if (t.waitStatus != Node.CONDITION) {
                t.nextWaiter = null;
                if (trail == null) {
                    firstWaiter = next;
                } else {
                    trail.nextWaiter = next;
                }
                if (next == null) {
                    lastWaiter = trail;
                }
            } else {
                trail = t;
            }
            t = next;
        }
    }

    /**
     * 完全释放锁
     */
    private int fullyRelease() {
        boolean failed = true;
        try {
            int savedState = getState();
            if (release(savedState)) {
                failed = false;
                return savedState;
            } else {
                throw new IllegalMonitorStateException("release failed");
            }
        } finally {
            if (failed) {
                // 标记节点为取消状态
            }
        }
    }

    /**
     * 检查是否在同步队列中
     */
    private boolean isOnSyncQueue(Node node) {
        if (node.waitStatus == Node.CONDITION || node.prev == null) {
            return false;
        }
        if (node.next != null) {
            return true;
        }
        return findNodeFromTail(node);
    }

    /**
     * 从尾部查找节点
     */
    private boolean findNodeFromTail(Node node) {
        Node t = tail;
        for (;;) {
            if (t == node) {
                return true;
            }
            if (t == null) {
                return false;
            }
            t = t.prev;
        }
    }

    /**
     * 检查中断状态
     */
    private int checkInterruptWhileWaiting(Node node) {
        return Thread.interrupted() ?
                (transferAfterCancelledWait(node) ? 1 : 2) :
                0;
    }

    /**
     * 报告中断
     */
    private void reportInterruptAfterWait(int interruptMode) throws InterruptedException {
        if (interruptMode == 1) {
            throw new InterruptedException();
        } else {
            selfInterrupt();
        }
    }

    // 队列相关方法（简化实现）
    private transient volatile Node head;
    private transient volatile Node tail;
    private volatile int state;

    private Node enq(Node node) {
        for (;;) {
            Node t = tail;
            if (t == null) {
                if (compareAndSetHead(new Node())) {
                    tail = head;
                }
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }

    private boolean acquireQueued(Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null;
                    failed = false;
                    return interrupted;
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                        parkAndCheckInterrupt()) {
                    interrupted = true;
                }
            }
        } finally {
            if (failed) {
                cancelAcquire(node);
            }
        }
    }

    private void setHead(Node node) {
        head = node;
        node.thread = null;
        node.prev = null;
    }

    private boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL) {
            return true;
        }
        if (ws > 0) {
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }

    private boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }

    private void cancelAcquire(Node node) {
        if (node == null) {
            return;
        }
        node.thread = null;
        Node pred = node.prev;
        while (pred.waitStatus > 0) {
            node.prev = pred = pred.prev;
        }
        Node predNext = pred.next;
        node.waitStatus = Node.CANCELLED;
        if (node == tail && compareAndSetTail(node, pred)) {
            compareAndSetNext(pred, predNext, null);
        } else {
            if (pred != head &&
                    (pred.waitStatus == Node.SIGNAL ||
                            compareAndSetWaitStatus(pred, 0, Node.SIGNAL)) &&
                    pred.thread != null) {
                Node next = node.next;
                if (next != null && next.waitStatus <= 0) {
                    compareAndSetNext(pred, predNext, next);
                }
            } else {
                unparkSuccessor(node);
            }
        }
    }

    private void unparkSuccessor(Node node) {
        int ws = node.waitStatus;
        if (ws < 0) {
            compareAndSetWaitStatus(node, ws, 0);
        }
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev) {
                if (t.waitStatus <= 0) {
                    s = t;
                }
            }
        }
        if (s != null) {
            LockSupport.unpark(s.thread);
        }
    }

    // 需要子类实现的方法
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    protected int getState() {
        return state;
    }

    protected void setState(int newState) {
        state = newState;
    }

    protected boolean isHeldByCurrentThread() {
        // 简化实现，实际应该检查锁的持有状态
        return true;
    }

    protected boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0) {
                unparkSuccessor(h);
            }
            return true;
        }
        return false;
    }

    // CAS操作
    private static final sun.misc.Unsafe unsafe;
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static {
        try {
            unsafe = sun.misc.Unsafe.getUnsafe();
            stateOffset = unsafe.objectFieldOffset(
                    ConditionVariable.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset(
                    ConditionVariable.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(
                    ConditionVariable.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset(
                    Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset(
                    Node.class.getDeclaredField("next"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    private static final boolean compareAndSetWaitStatus(Node node, int expect, int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset, expect, update);
    }

    private static final boolean compareAndSetNext(Node node, Node expect, Node update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }

    private static void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    /**
     * 队列节点
     */
    static final class Node {
        static final int CANCELLED =  1;
        static final int SIGNAL    = -1;
        static final int CONDITION = -2;
        static final int PROPAGATE = -3;

        volatile int waitStatus;
        volatile Node prev;
        volatile Node next;
        volatile Thread thread;
        Node nextWaiter;

        Node() {}

        Node(Thread thread, int waitStatus) {
            this.waitStatus = waitStatus;
            this.thread = thread;
        }

        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null) {
                throw new NullPointerException();
            }
            return p;
        }
    }

    // LockSupport工具方法
    private static class LockSupport {
        static void unpark(Thread thread) {
            if (thread != null) {
                sun.misc.Unsafe.getUnsafe().unpark(thread);
            }
        }

        static void park(Object blocker) {
            sun.misc.Unsafe.getUnsafe().park(false, 0L);
        }

        static void parkNanos(Object blocker, long nanos) {
            if (nanos > 0) {
                sun.misc.Unsafe.getUnsafe().park(false, nanos);
            }
        }

        static void parkUntil(Object blocker, long deadline) {
            sun.misc.Unsafe.getUnsafe().park(true, deadline);
        }
    }
}
