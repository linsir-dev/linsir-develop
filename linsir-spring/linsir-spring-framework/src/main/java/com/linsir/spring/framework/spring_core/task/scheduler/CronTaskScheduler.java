package com.linsir.spring.framework.spring_core.task.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cron表达式任务调度器
 *
 * 支持Unix Cron表达式格式的任务调度。
 * Cron表达式格式：秒 分 时 日 月 周
 *
 * @author linsir
 * @since 1.0.0
 */
public class CronTaskScheduler implements TaskScheduler {

    private final ConcurrentTaskScheduler delegate;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * 默认构造函数
     */
    public CronTaskScheduler() {
        this.delegate = new ConcurrentTaskScheduler();
    }

    /**
     * 使用指定的线程池大小创建调度器
     *
     * @param poolSize 线程池大小
     */
    public CronTaskScheduler(int poolSize) {
        this.delegate = new ConcurrentTaskScheduler(poolSize);
    }

    /**
     * 使用Cron表达式调度任务
     *
     * @param task 要执行的任务
     * @param cronExpression Cron表达式
     * @return 任务ID，可用于取消任务
     */
    public String scheduleWithCron(Runnable task, String cronExpression) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (cronExpression == null || cronExpression.trim().isEmpty()) {
            throw new IllegalArgumentException("Cron expression must not be empty");
        }

        String taskId = UUID.randomUUID().toString();
        CronTrigger trigger = new CronTrigger(cronExpression);

        ScheduledFuture<?> future = scheduleNextExecution(task, trigger, taskId);
        scheduledTasks.put(taskId, future);

        return taskId;
    }

    /**
     * 调度下一次执行
     */
    private ScheduledFuture<?> scheduleNextExecution(Runnable task, CronTrigger trigger, String taskId) {
        Date nextExecutionTime = trigger.nextExecutionTime();
        if (nextExecutionTime == null) {
            return null;
        }

        return delegate.schedule(() -> {
            try {
                task.run();
            } finally {
                // 调度下一次执行
                ScheduledFuture<?> nextFuture = scheduleNextExecution(task, trigger, taskId);
                if (nextFuture != null) {
                    scheduledTasks.put(taskId, nextFuture);
                } else {
                    scheduledTasks.remove(taskId);
                }
            }
        }, nextExecutionTime);
    }

    /**
     * 取消指定ID的任务
     *
     * @param taskId 任务ID
     * @return true 如果成功取消
     */
    public boolean cancelTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(false);
            return true;
        }
        return false;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        return delegate.schedule(task, startTime);
    }

    @Override
    public ScheduledFuture<?> scheduleWithDelay(Runnable task, long delayMillis) {
        return delegate.scheduleWithDelay(task, delayMillis);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long periodMillis) {
        return delegate.scheduleAtFixedRate(task, periodMillis);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long periodMillis) {
        return delegate.scheduleAtFixedRate(task, startTime, periodMillis);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delayMillis) {
        return delegate.scheduleWithFixedDelay(task, delayMillis);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delayMillis) {
        return delegate.scheduleWithFixedDelay(task, startTime, delayMillis);
    }

    @Override
    public ScheduledFuture<?> executeImmediately(Runnable task) {
        return delegate.executeImmediately(task);
    }

    @Override
    public void shutdown() {
        for (ScheduledFuture<?> future : scheduledTasks.values()) {
            future.cancel(false);
        }
        scheduledTasks.clear();
        delegate.shutdown();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    /**
     * Cron触发器
     */
    private static class CronTrigger {
        private final String expression;
        private final CronExpression cronExpression;

        public CronTrigger(String expression) {
            this.expression = expression;
            this.cronExpression = new CronExpression(expression);
        }

        public Date nextExecutionTime() {
            return cronExpression.nextExecutionTime(new Date());
        }
    }

    /**
     * Cron表达式解析器
     */
    private static class CronExpression {
        private final String seconds;
        private final String minutes;
        private final String hours;
        private final String dayOfMonth;
        private final String month;
        private final String dayOfWeek;

        public CronExpression(String expression) {
            String[] parts = expression.trim().split("\\s+");
            if (parts.length != 6) {
                throw new IllegalArgumentException("Cron expression must have 6 fields: seconds minutes hours day month weekday");
            }
            this.seconds = parts[0];
            this.minutes = parts[1];
            this.hours = parts[2];
            this.dayOfMonth = parts[3];
            this.month = parts[4];
            this.dayOfWeek = parts[5];
        }

        public Date nextExecutionTime(Date afterTime) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(afterTime);
            calendar.add(Calendar.SECOND, 1);

            // 简化实现：只支持简单的数字或 * 格式
            for (int i = 0; i < 366 * 24 * 60 * 60; i++) { // 最多查找一年
                if (matches(calendar)) {
                    return calendar.getTime();
                }
                calendar.add(Calendar.SECOND, 1);
            }
            return null;
        }

        private boolean matches(Calendar calendar) {
            return matchesField(seconds, calendar.get(Calendar.SECOND), 0, 59)
                    && matchesField(minutes, calendar.get(Calendar.MINUTE), 0, 59)
                    && matchesField(hours, calendar.get(Calendar.HOUR_OF_DAY), 0, 23)
                    && matchesField(dayOfMonth, calendar.get(Calendar.DAY_OF_MONTH), 1, 31)
                    && matchesField(month, calendar.get(Calendar.MONTH) + 1, 1, 12)
                    && matchesDayOfWeek(calendar);
        }

        private boolean matchesField(String field, int value, int min, int max) {
            if ("*".equals(field)) {
                return true;
            }
            try {
                int fieldValue = Integer.parseInt(field);
                return fieldValue == value;
            } catch (NumberFormatException e) {
                // 简化处理，不支持复杂表达式
                return true;
            }
        }

        private boolean matchesDayOfWeek(Calendar calendar) {
            if ("*".equals(dayOfWeek)) {
                return true;
            }
            try {
                int fieldValue = Integer.parseInt(dayOfWeek);
                // 转换：Cron的0=周日，Calendar的1=周日
                int cronDay = fieldValue == 0 ? 1 : fieldValue + 1;
                return cronDay == calendar.get(Calendar.DAY_OF_WEEK);
            } catch (NumberFormatException e) {
                return true;
            }
        }
    }
}
