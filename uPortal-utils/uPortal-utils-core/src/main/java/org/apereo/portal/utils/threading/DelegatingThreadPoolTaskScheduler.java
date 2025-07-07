package org.apereo.portal.utils.threading;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

/**
 * Delegates to a target {@link TaskScheduler} for all operations. Allows for a target scheduler to
 * be swapped out at runtime.
 */
public class DelegatingThreadPoolTaskScheduler
        implements TaskScheduler, SchedulingTaskExecutor, InitializingBean, DisposableBean {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TaskScheduler targetScheduler;
    private ExecutorService targetExecutor;
    private ScheduledExecutorService targetScheduledExecutor;

    private ErrorHandler errorHandler;

    public void setTargetScheduler(TaskScheduler targetScheduler) {
        this.targetScheduler = targetScheduler;

        if (targetScheduler instanceof ExecutorService) {
            this.targetExecutor = (ExecutorService) targetScheduler;
        } else {
            this.targetExecutor = null;
        }

        if (targetScheduler instanceof ScheduledExecutorService) {
            this.targetScheduledExecutor = (ScheduledExecutorService) targetScheduler;
        } else {
            this.targetScheduledExecutor = null;
        }
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.targetScheduler, "targetScheduler must not be null");
    }

    @Override
    public void destroy() throws Exception {
        if (this.targetScheduler instanceof DisposableBean) {
            ((DisposableBean) this.targetScheduler).destroy();
        }
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        try {
            ErrorHandlingRunnable r = new ErrorHandlingRunnable(task);
            return this.targetScheduler.schedule(r, trigger);
        } catch (TaskRejectedException ex) {
            throw ex;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + targetScheduler + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        try {
            ErrorHandlingRunnable r = new ErrorHandlingRunnable(task);
            return this.targetScheduler.schedule(r, startTime);
        } catch (TaskRejectedException ex) {
            throw ex;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + targetScheduler + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        try {
            ErrorHandlingRunnable r = new ErrorHandlingRunnable(task);
            return this.targetScheduler.scheduleAtFixedRate(r, startTime, period);
        } catch (TaskRejectedException ex) {
            throw ex;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + targetScheduler + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        try {
            ErrorHandlingRunnable r = new ErrorHandlingRunnable(task);
            return this.targetScheduler.scheduleAtFixedRate(r, period);
        } catch (TaskRejectedException ex) {
            throw ex;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + targetScheduler + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        try {
            ErrorHandlingRunnable r = new ErrorHandlingRunnable(task);
            return this.targetScheduler.scheduleWithFixedDelay(r, startTime, delay);
        } catch (TaskRejectedException ex) {
            throw ex;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + targetScheduler + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        try {
            ErrorHandlingRunnable r = new ErrorHandlingRunnable(task);
            return this.targetScheduler.scheduleWithFixedDelay(r, delay);
        } catch (TaskRejectedException ex) {
            throw ex;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + targetScheduler + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public void execute(Runnable task) {
        try {
            ErrorHandlingRunnable r = new ErrorHandlingRunnable(task);
            if (this.targetExecutor != null) {
                this.targetExecutor.execute(r);
            } else {
                final Date now = new Date();
                this.targetScheduler.schedule(
                        r,
                        new Trigger() {
                            @Override
                            public Instant nextExecution(TriggerContext triggerContext) {
                                return now.toInstant();
                            }
                        });
            }
        } catch (TaskRejectedException ex) {
            throw ex;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + targetScheduler + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public Future<?> submit(Runnable task) {
        try {
            ErrorHandlingRunnable r = new ErrorHandlingRunnable(task);
            if (this.targetExecutor != null) {
                return this.targetExecutor.submit(r);
            }

            FutureScheduledTask<Object> futureTask = new FutureScheduledTask<Object>(r, null);
            final Date now = new Date();
            final ScheduledFuture<?> scheduledFuture =
                    this.targetScheduler.schedule(
                            futureTask,
                            new Trigger() {
                                @Override
                                public Instant nextExecution(TriggerContext triggerContext) {
                                    return now.toInstant();
                                }
                            });
            futureTask.setScheduledFuture(scheduledFuture);
            return futureTask;
        } catch (TaskRejectedException ex) {
            throw ex;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + targetScheduler + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        try {
            if (this.targetExecutor != null) {
                return this.targetExecutor.submit(task);
            }

            FutureScheduledTask<T> futureTask = new FutureScheduledTask<T>(task);
            final Date now = new Date();
            final ScheduledFuture<?> scheduledFuture =
                    this.targetScheduler.schedule(
                            futureTask,
                            new Trigger() {
                                @Override
                                public Instant nextExecution(TriggerContext triggerContext) {
                                    return now.toInstant();
                                }
                            });
            futureTask.setScheduledFuture(scheduledFuture);
            return futureTask;
        } catch (TaskRejectedException ex) {
            throw ex;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + targetScheduler + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        execute(task);
    }

    @Override
    public boolean prefersShortLivedTasks() {
        if (this.targetExecutor instanceof SchedulingTaskExecutor) {
            return ((SchedulingTaskExecutor) this.targetExecutor).prefersShortLivedTasks();
        }
        return true;
    }

    // Spring 6 Duration-based methods
    @Override
    public ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
        return schedule(task, Date.from(startTime));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        return scheduleAtFixedRate(task, Date.from(startTime), period.toMillis());
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
        return scheduleAtFixedRate(task, period.toMillis());
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        return scheduleWithFixedDelay(task, Date.from(startTime), delay.toMillis());
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
        return scheduleWithFixedDelay(task, delay.toMillis());
    }

    private class ErrorHandlingRunnable implements Runnable {
        private final Runnable delegate;

        public ErrorHandlingRunnable(Runnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try {
                this.delegate.run();
            } catch (Throwable t) {
                if (errorHandler != null) {
                    errorHandler.handleError(t);
                } else {
                    TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER.handleError(t);
                }
            }
        }

        @Override
        public String toString() {
            return "ErrorHandlingRunnable [delegate=" + this.delegate + "]";
        }
    }

    private static class FutureScheduledTask<V> implements ScheduledFuture<V>, Runnable, Callable<V> {
        private final Object task;
        private final V result;
        private volatile ScheduledFuture<?> scheduledFuture;
        private volatile Exception executionException;
        private volatile boolean done = false;

        public FutureScheduledTask(Runnable task, V result) {
            this.task = task;
            this.result = result;
        }

        public FutureScheduledTask(Callable<V> task) {
            this.task = task;
            this.result = null;
        }

        public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        @Override
        public void run() {
            try {
                final Runnable runnable = (Runnable) this.task;
                runnable.run();
            } catch (Exception e) {
                this.executionException = e;
            } finally {
                this.done = true;
            }
        }

        @Override
        public V call() throws Exception {
            try {
                @SuppressWarnings("unchecked")
                final Callable<V> callable = (Callable<V>) this.task;
                return callable.call();
            } catch (Exception e) {
                this.executionException = e;
                throw e;
            } finally {
                this.done = true;
            }
        }

        @Override
        public long getDelay(TimeUnit unit) {
            if (this.scheduledFuture != null) {
                return this.scheduledFuture.getDelay(unit);
            }
            return 0;
        }

        @Override
        public int compareTo(Delayed o) {
            if (this.scheduledFuture != null) {
                return this.scheduledFuture.compareTo(o);
            }
            return 0;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (this.scheduledFuture != null) {
                return this.scheduledFuture.cancel(mayInterruptIfRunning);
            }
            return false;
        }

        @Override
        public boolean isCancelled() {
            if (this.scheduledFuture != null) {
                return this.scheduledFuture.isCancelled();
            }
            return false;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            if (this.executionException != null) {
                throw new ExecutionException(this.executionException);
            }
            if (this.done) {
                return this.result;
            }
            if (this.scheduledFuture != null) {
                this.scheduledFuture.get();
            }
            return this.result;
        }

        @Override
        public V get(long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            if (this.executionException != null) {
                throw new ExecutionException(this.executionException);
            }
            if (this.done) {
                return this.result;
            }
            if (this.scheduledFuture != null) {
                this.scheduledFuture.get(timeout, unit);
            }
            return this.result;
        }
    }
}