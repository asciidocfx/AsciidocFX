package com.kodedu.service;

import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by usta on 14.06.2016.
 */
public class FxCompletableFeature<T> extends CompletableFuture {


    public <U> FxCompletableFeature<U> supplyAsyncFx(Supplier<U> supplier) {
        FxCompletableFeature<U> fxCompletableFeature = new FxCompletableFeature<>();

        super.supplyAsync(() -> {
            Platform.runLater(() -> {
                try {
                    U u = supplier.get();
                    fxCompletableFeature.complete(u);
                } catch (Exception ex) {
                    fxCompletableFeature.completeExceptionally(ex);
                }
            });
            return (U) fxCompletableFeature.join();
        });


        return fxCompletableFeature;
    }

    public FxCompletableFeature<Void> thenRunFx(Runnable action) {
        FxCompletableFeature<Void> fxCompletableFeature = new FxCompletableFeature<>();

        super.thenRun(() -> {
            Platform.runLater(() -> {
                try {
                    action.run();
                    fxCompletableFeature.complete(null);
                } catch (Exception ex) {
                    fxCompletableFeature.completeExceptionally(ex);
                }
            });
        });

        return fxCompletableFeature;
    }


    @Override
    public FxCompletableFeature<Void> thenAcceptAsync(Consumer action) {
        FxCompletableFeature<Void> fxCompletableFeature = new FxCompletableFeature<>();

        super.thenAcceptAsync(o -> {
            try {
                action.accept(o);
                fxCompletableFeature.complete(o);
            } catch (Exception ex) {
                fxCompletableFeature.completeExceptionally(ex);
            }
        });

        return fxCompletableFeature;
    }
}
