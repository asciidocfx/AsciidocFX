package com.kodcu.service;

import com.kodcu.other.ConverterResult;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class ThreadService {

    private final ExecutorService threadPollWorker;
    private final ScheduledExecutorService scheduledExecutorService;

    public ThreadService() {
        int nThreads = Runtime.getRuntime().availableProcessors() * 2;
        threadPollWorker = Executors.newFixedThreadPool((nThreads >= 4) ? nThreads : 4);
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    // Runs Task in background thread pool
    public <T> void runTaskLater(Runnable runnable) {

        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                runnable.run();
                return null;
            }
        };

        threadPollWorker.submit(task);
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

    public Executor executor() {
        return threadPollWorker;
    }

    public void scheduleWithFixedDelay(Runnable runnable, long ms) {
        scheduledExecutorService.scheduleWithFixedDelay(runnable, 0, ms, TimeUnit.MILLISECONDS);
    }

    public void schedule(Runnable runnable, long ms) {
        scheduledExecutorService.schedule(runnable, ms, TimeUnit.MILLISECONDS);
    }
}
