package com.kodedu.service.impl;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;

import org.springframework.stereotype.Component;

import com.kodedu.service.Buff;
import com.kodedu.service.ThreadService;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by usta on 25.12.2014.
 */
@Component(ThreadService.label)
public class ThreadServiceImpl implements ThreadService {
    private final ExecutorService threadPollWorker;
    private final ScheduledExecutorService scheduledWorker;
    private final ConcurrentHashMap<String, Buff> buffMap;
    private final Semaphore uiSemaphore;
    private final ExecutorService singleExecutor;

    public ThreadServiceImpl() {
        ThreadFactory threadFactory = Thread.ofVirtual().factory();
        scheduledWorker = Executors.newSingleThreadScheduledExecutor(threadFactory);
        threadPollWorker = Executors.newVirtualThreadPerTaskExecutor();
        singleExecutor = Executors.newSingleThreadExecutor(threadFactory);
        uiSemaphore = new Semaphore(1);
        buffMap = new ConcurrentHashMap<>();
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduledWorker.schedule(runnable, delay, timeUnit);
    }

    public ScheduledFuture<?> scheduleWithDelay(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return scheduledWorker.scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit);
    }

    // Runs Task in background thread pool
    @Override
    public <T> Future<?> runTaskLater(Runnable runnable) {

        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                runnable.run();
                return null;
            }
        };

        task.exceptionProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                newValue.printStackTrace();
            }
        });

        return threadPollWorker.submit(task);
    }

    // Runs task in JavaFX Thread
    @Override
    public void runActionLater(Consumer<ActionEvent> consumer) {
        runActionLater(() -> {
            consumer.accept(null);
        });
    }


    // Runs task in JavaFX Thread
    @Override
    public void runActionLater(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            try {
                uiSemaphore.acquire();
                Platform.runLater(() -> {
                    try {
                        runnable.run();
                        releaseUiSemaphore();
                    } catch (Exception e) {
                        releaseUiSemaphore();
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                releaseUiSemaphore();
                throw new RuntimeException(e);
            }
        }
    }

    private void releaseUiSemaphore() {
        singleExecutor.submit(() -> {
            uiSemaphore.release();
        });
    }

    @Override
    public void runActionLater(Runnable runnable, boolean force) {
        if (force) {
            Platform.runLater(runnable);
        } else {
            runActionLater(runnable);
        }
    }

    @Override
    public void start(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public Executor executor() {
        return threadPollWorker;
    }

    @Override
    public Buff buff(String id) {
        buffMap.putIfAbsent(id, new Buff(this));
        return buffMap.get(id);
    }

    @Override
    public <T> T supply(Supplier<T> supplier) {

        if (Platform.isFxApplicationThread()) {
            return supplier.get();
        }

        final CompletableFuture<T> completableFuture = new CompletableFuture<T>();
        completableFuture.runAsync(() -> {
            runActionLater(() -> {
                try {
                    T t = supplier.get();
                    completableFuture.complete(t);
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            });
        }, threadPollWorker);

        return completableFuture.join();
    }

    @Override
    public <T> void runActionLater(Consumer<T> consumer, T t) {
        runActionLater(() -> {
            consumer.accept(t);
        });
    }

    @Override
    public ScheduledFuture<?> scheduleWithDelay(Runnable runnable, int timeBetweenFramesMS, TimeUnit milliseconds) {
        return scheduleWithDelay(runnable, 0, timeBetweenFramesMS, milliseconds);
    }
}
