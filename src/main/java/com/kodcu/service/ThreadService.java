package com.kodcu.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class ThreadService {

    private final ScheduledExecutorService threadPollWorker;
    private ConcurrentHashMap<String, Buff> buffMap = new ConcurrentHashMap<>();

    public ThreadService() {
        int nThreads = Runtime.getRuntime().availableProcessors() * 2;
        threadPollWorker = Executors.newScheduledThreadPool((nThreads >= 4) ? nThreads : 4);
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        return threadPollWorker.schedule(runnable, delay, timeUnit);
    }

    // Runs Task in background thread pool
    public <T> Future<?> runTaskLater(Runnable runnable) {

        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                runnable.run();
                return null;
            }
        };

        return threadPollWorker.submit(task);
    }

    // Runs task in JavaFX Thread
    public void runActionLater(Consumer<ActionEvent> consumer) {
        if (Platform.isFxApplicationThread()) {
            consumer.accept(null);
        } else {
            Platform.runLater(() -> consumer.accept(null));
        }
    }

    // Runs task in JavaFX Thread
    public void runActionLater(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    public void runActionLater(Runnable runnable, boolean force) {
        if (force) {
            Platform.runLater(runnable);
        } else {
            runActionLater(runnable);
        }
    }

    public void start(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public Executor executor() {
        return threadPollWorker;
    }

    public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
//            logger.error("Error in Thread#sleep", e);
        }
    }


    public Buff buff(String id) {
        buffMap.putIfAbsent(id, new Buff(this));
        return buffMap.get(id);
    }

    public void runActionFairlyLater(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
            sleep(50);
        }
    }
}
