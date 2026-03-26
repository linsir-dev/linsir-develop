package com.linsir.abc.core.base.util.concurrent.executor;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程池执行器实现
 * 演示ThreadPoolExecutor的核心原理：任务队列、工作线程、拒绝策略、线程生命周期管理
 *
 * <p>核心组件：</p>
 * <ul>
 *   <li>任务队列：存储等待执行的任务</li>
 *   <li>工作线程：执行任务的线程</li>
 *   <li>线程工厂：创建新线程</li>
 *   <li>拒绝策略：处理无法执行的任务</li>
 * </ul>
 *
 * <p>线程池状态：</p>
 * <ul>
 *   <li>RUNNING：接受新任务，处理队列任务</li>
 *   <li>SHUTDOWN：不接受新任务，处理队列任务</li>
 *   <li>STOP：不接受新任务，不处理队列任务，中断正在执行的任务</li>
 *   <li>TIDYING：所有任务已终止，工作线程数为0</li>
 *   <li>TERMINATED：terminated()方法执行完成</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ThreadPoolExecutorImplementation {

    // 线程池状态常量
    private static final int RUNNING    = -1 << 29;
    private static final int SHUTDOWN   =  0 << 29;
    private static final int STOP       =  1 << 29;
    private static final int TIDYING    =  2 << 29;
    private static final int TERMINATED =  3 << 29;

    // 状态和线程数组合（高3位状态，低29位线程数）
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));

    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // 运行状态判断
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }

    // 配置参数
    private final int corePoolSize;      // 核心线程数
    private final int maximumPoolSize;   // 最大线程数
    private final long keepAliveTime;    // 空闲线程存活时间
    private final TimeUnit unit;         // 时间单位
    private final BlockingQueue<Runnable> workQueue;  // 任务队列
    private final ThreadFactory threadFactory;        // 线程工厂
    private final RejectedExecutionHandler handler;   // 拒绝策略

    // 工作线程集合
    private final Set<Worker> workers = new HashSet<>();

    // 锁
    private final ReentrantLock mainLock = new ReentrantLock();
    private final Condition termination = mainLock.newCondition();

    // 统计
    private long completedTaskCount;  // 已完成任务数
    private int largestPoolSize;      // 最大线程数

    /**
     * 构造器
     */
    public ThreadPoolExecutorImplementation(int corePoolSize,
                                              int maximumPoolSize,
                                              long keepAliveTime,
                                              TimeUnit unit,
                                              BlockingQueue<Runnable> workQueue,
                                              ThreadFactory threadFactory,
                                              RejectedExecutionHandler handler) {
        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0) {
            throw new IllegalArgumentException("参数错误");
        }
        if (workQueue == null || threadFactory == null || handler == null) {
            throw new NullPointerException("参数不能为null");
        }
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.unit = unit;
        this.workQueue = workQueue;
        this.threadFactory = threadFactory;
        this.handler = handler;
    }

    /**
     * 简化构造器
     */
    public ThreadPoolExecutorImplementation(int corePoolSize,
                                              int maximumPoolSize,
                                              long keepAliveTime,
                                              TimeUnit unit,
                                              BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory(), new AbortPolicy());
    }

    /**
     * 提交任务
     */
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }

        int c = ctl.get();
        // 1. 如果线程数小于核心线程数，创建新线程
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true)) {
                return;
            }
            c = ctl.get();
        }

        // 2. 如果线程池在运行状态，将任务加入队列
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            // 双重检查
            if (!isRunning(recheck) && remove(command)) {
                reject(command);
            } else if (workerCountOf(recheck) == 0) {
                addWorker(null, false);
            }
        }
        // 3. 队列已满，尝试创建非核心线程
        else if (!addWorker(command, false)) {
            reject(command);
        }
    }

    /**
     * 判断线程池是否在运行状态
     */
    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    /**
     * 添加工作线程
     */
    private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // 检查状态
            if (rs >= SHUTDOWN && !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty())) {
                return false;
            }

            for (;;) {
                int wc = workerCountOf(c);
                if (wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize)) {
                    return false;
                }
                if (ctl.compareAndSet(c, ctlOf(rs, wc + 1))) {
                    break retry;
                }
                c = ctl.get();
                if (runStateOf(c) != rs) {
                    continue retry;
                }
            }
        }

        Worker w = new Worker(firstTask);
        final Thread t = w.thread;

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int rs = runStateOf(ctl.get());
            if (t != null && (rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null))) {
                if (t.isAlive()) {
                    throw new IllegalThreadStateException();
                }
                workers.add(w);
                int s = workers.size();
                if (s > largestPoolSize) {
                    largestPoolSize = s;
                }
            }
        } finally {
            mainLock.unlock();
        }

        t.start();
        return true;
    }

    /**
     * 工作线程运行逻辑
     */
    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock();
        boolean completedAbruptly = true;

        try {
            while (task != null || (task = getTask()) != null) {
                w.lock();
                // 检查线程池状态
                if ((runStateAtLeast(ctl.get(), STOP) ||
                        (Thread.interrupted() && runStateAtLeast(ctl.get(), STOP))) &&
                        !wt.isInterrupted()) {
                    wt.interrupt();
                }

                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    } catch (RuntimeException | Error x) {
                        thrown = x;
                        throw x;
                    } catch (Throwable x) {
                        thrown = x;
                        throw new Error(x);
                    } finally {
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly);
        }
    }

    /**
     * 获取任务
     */
    private Runnable getTask() {
        boolean timedOut = false;

        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // 检查状态
            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();
                return null;
            }

            int wc = workerCountOf(c);
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

            if ((wc > maximumPoolSize || (timed && timedOut))
                    && (wc > 1 || workQueue.isEmpty())) {
                if (ctl.compareAndSet(c, ctlOf(rs, wc - 1))) {
                    return null;
                }
                continue;
            }

            try {
                Runnable r = timed ?
                        workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                        workQueue.take();
                if (r != null) {
                    return r;
                }
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }

    /**
     * 处理工作线程退出
     */
    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        if (completedAbruptly) {
            decrementWorkerCount();
        }

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completedTaskCount += w.completedTasks;
            workers.remove(w);
        } finally {
            mainLock.unlock();
        }

        tryTerminate();

        int c = ctl.get();
        if (runStateLessThan(c, STOP)) {
            if (!completedAbruptly) {
                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
                if (min == 0 && !workQueue.isEmpty()) {
                    min = 1;
                }
                if (workerCountOf(c) >= min) {
                    return;
                }
            }
            addWorker(null, false);
        }
    }

    /**
     * 尝试终止线程池
     */
    final void tryTerminate() {
        for (;;) {
            int c = ctl.get();
            if (isRunning(c) ||
                    runStateAtLeast(c, TIDYING) ||
                    (runStateOf(c) == SHUTDOWN && !workQueue.isEmpty())) {
                return;
            }
            if (workerCountOf(c) != 0) {
                interruptIdleWorkers();
                return;
            }

            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
                    try {
                        terminated();
                    } finally {
                        ctl.set(ctlOf(TERMINATED, 0));
                        termination.signalAll();
                    }
                    return;
                }
            } finally {
                mainLock.unlock();
            }
        }
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            advanceRunState(SHUTDOWN);
            interruptIdleWorkers();
            onShutdown();
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
    }

    /**
     * 立即关闭线程池
     */
    public List<Runnable> shutdownNow() {
        List<Runnable> tasks;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            advanceRunState(STOP);
            interruptWorkers();
            tasks = drainQueue();
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
        return tasks;
    }

    /**
     * 判断是否已关闭
     */
    public boolean isShutdown() {
        return !isRunning(ctl.get());
    }

    /**
     * 判断是否已终止
     */
    public boolean isTerminated() {
        return runStateAtLeast(ctl.get(), TERMINATED);
    }

    /**
     * 等待终止
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (;;) {
                if (isTerminated()) {
                    return true;
                }
                if (nanos <= 0) {
                    return false;
                }
                nanos = termination.awaitNanos(nanos);
            }
        } finally {
            mainLock.unlock();
        }
    }

    // 辅助方法
    private void advanceRunState(int targetState) {
        for (;;) {
            int c = ctl.get();
            if (runStateAtLeast(c, targetState) ||
                    ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c)))) {
                break;
            }
        }
    }

    private void interruptIdleWorkers() {
        interruptIdleWorkers(false);
    }

    private void interruptIdleWorkers(boolean onlyOne) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    } catch (SecurityException ignore) {
                    } finally {
                        w.unlock();
                    }
                }
                if (onlyOne) {
                    break;
                }
            }
        } finally {
            mainLock.unlock();
        }
    }

    private void interruptWorkers() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                w.thread.interrupt();
            }
        } finally {
            mainLock.unlock();
        }
    }

    private List<Runnable> drainQueue() {
        List<Runnable> taskList = new ArrayList<>();
        workQueue.drainTo(taskList);
        if (!workQueue.isEmpty()) {
            for (Runnable r : workQueue.toArray(new Runnable[0])) {
                if (workQueue.remove(r)) {
                    taskList.add(r);
                }
            }
        }
        return taskList;
    }

    private void decrementWorkerCount() {
        ctl.addAndGet(-1);
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private boolean remove(Runnable task) {
        return workQueue.remove(task);
    }

    private void reject(Runnable command) {
        handler.rejectedExecution(command, this);
    }

    // 钩子方法
    protected void beforeExecute(Thread t, Runnable r) { }
    protected void afterExecute(Runnable r, Throwable t) { }
    protected void terminated() { }
    protected void onShutdown() { }

    // 配置方法
    private volatile boolean allowCoreThreadTimeOut;

    public void allowCoreThreadTimeOut(boolean value) {
        this.allowCoreThreadTimeOut = value;
    }

    public int getPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return workers.size();
        } finally {
            mainLock.unlock();
        }
    }

    public int getActiveCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            for (Worker w : workers) {
                if (w.isLocked()) {
                    ++n;
                }
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    public long getTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (Worker w : workers) {
                n += w.completedTasks;
            }
            return n + workQueue.size();
        } finally {
            mainLock.unlock();
        }
        }

    public long getCompletedTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (Worker w : workers) {
                n += w.completedTasks;
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    public int getLargestPoolSize() {
        return largestPoolSize;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    /**
     * 工作线程内部类
     */
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {

        private static final long serialVersionUID = 6138294804551838833L;

        final Thread thread;
        Runnable firstTask;
        volatile long completedTasks;

        Worker(Runnable firstTask) {
            setState(-1);
            this.firstTask = firstTask;
            this.thread = threadFactory.newThread(this);
        }

        @Override
        public void run() {
            runWorker(this);
        }

        @Override
        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        @Override
        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        public void lock() {
            acquire(1);
        }

        public boolean tryLock() {
            return tryAcquire(1);
        }

        public void unlock() {
            release(1);
        }

        public boolean isLocked() {
            return isHeldExclusively();
        }
    }

    /**
     * 拒绝策略接口
     */
    public interface RejectedExecutionHandler {
        void rejectedExecution(Runnable r, ThreadPoolExecutorImplementation executor);
    }

    /**
     * 中止策略（默认）
     */
    public static class AbortPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutorImplementation e) {
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + e.toString());
        }
    }

    /**
     * 调用者运行策略
     */
    public static class CallerRunsPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutorImplementation e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }

    /**
     * 丢弃策略
     */
    public static class DiscardPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutorImplementation e) {
            // 什么都不做，直接丢弃
        }
    }

    /**
     * 丢弃最旧策略
     */
    public static class DiscardOldestPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutorImplementation e) {
            if (!e.isShutdown()) {
                e.workQueue.poll();
                e.execute(r);
            }
        }
    }
}
