package com.kodcu.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class ThreadService {

    private ExecutorService singleWorker = Executors.newSingleThreadExecutor();
    private ExecutorService threadPollWorker = Executors.newFixedThreadPool(4);

    public <T> void runTaskLater(Consumer<Task<T>> consumer) {

        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                consumer.accept(this);
                return null;
            }
        };

        threadPollWorker.submit(task);
    }

    public <T> void runSingleTaskLater(Consumer<Task<T>> consumer) {

        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                consumer.accept(this);
                return null;
            }
        };

        singleWorker.submit(task);
    }


    public void runActionLater(Consumer<ActionEvent> consumer) {
        Platform.runLater(() -> {
            consumer.accept(null);
        });
    }
}
