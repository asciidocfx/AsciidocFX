package com.kodcu.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class ThreadService {

    private ExecutorService threadPollWorker ;

    @PostConstruct
    public void init(){
        int nThreads = Runtime.getRuntime().availableProcessors() * 2;
        threadPollWorker = Executors.newFixedThreadPool((nThreads>=4)?nThreads:4);
    }

    // Runs Task in background thread pool
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

    // Runs task in JavaFX Thread
    public void runActionLater(Consumer<ActionEvent> consumer) {
        Platform.runLater(() -> {
            consumer.accept(null);
        });
    }
}
