package com.kodcu.service;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by usta on 30.11.2015.
 */
public class Buff {

    private final ThreadService threadService;
    private AtomicReference<Runnable> runnable;
    private ScheduledFuture<?> schedule;

    public Buff(ThreadService threadService) {
        this.threadService = threadService;
        runnable = new AtomicReference<>();
    }

    public void schedule(Runnable runnable, int delay, TimeUnit timeUnit) {
        this.runnable.set(runnable);
        if (Objects.isNull(this.schedule)) {
            this.schedule = threadService.schedule(() -> {
                this.schedule = null;
                Runnable currentRunnable = this.runnable.get();
                if (Objects.nonNull(currentRunnable)) {
                    currentRunnable.run();
                }
            }, delay, timeUnit);
        }

    }
}
