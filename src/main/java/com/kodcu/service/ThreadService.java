package com.kodcu.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class ThreadService {

    private final ExecutorService threadPollWorker;
    private final Logger logger = LoggerFactory.getLogger(ThreadService.class);

    public ThreadService() {
        int nThreads = Runtime.getRuntime().availableProcessors() * 2;
        threadPollWorker = Executors.newFixedThreadPool((nThreads >= 4) ? nThreads : 4);
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
}
