package com.kodedu.service;

import javafx.event.ActionEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by usta on 25.12.2014.
 */
public interface ThreadService {
    public final static String label = "core::service::ThreadService";

    public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit);

    public ScheduledFuture<?> scheduleWithDelay(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit);

    /**
     * Runs Task in background thread pool
     * @param <T>
     * @param runnable
     * @return
     */
    public <T> Future<?> runTaskLater(Runnable runnable);

    /**
     *  Runs task in JavaFX Thread
     *  @param consumer
     */
    public void runActionLater(Consumer<ActionEvent> consumer);

    /**
     *  Runs task in JavaFX Thread
     *  @param runnable
     */
    public void runActionLater(final Runnable runnable);

    public void runActionLater(Runnable runnable, boolean force);

    public void start(Runnable runnable);

    public Executor executor();

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
//            logger.error("Error in Thread#sleep", e);
        }
    }

    public Buff buff(String id);

    public <T> T supply(Supplier<T> supplier);

    public <T> void runActionLater(Consumer<T> consumer, T t);

    public ScheduledFuture<?> scheduleWithDelay(Runnable runnable, int timeBetweenFramesMS, TimeUnit milliseconds);
}
