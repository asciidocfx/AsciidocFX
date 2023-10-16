package com.kodedu.service.convert;

import com.kodedu.other.RenderResult;

import java.io.File;
import java.util.function.Consumer;

/**
 * Created by usta on 09.04.2015.
 */
public interface DocumentConverter<T> {

    public void convert(boolean askPath, Consumer<T>... nextStep);

    default void onSuccessfulConversation(Consumer<RenderResult>[] nextSteps, File destFile) {
        for (Consumer<RenderResult> consumer : nextSteps) {
            RenderResult renderResult = new RenderResult();
            renderResult.setSuccessful(true);
            renderResult.setDestination(destFile.toPath());
            consumer.accept(renderResult);
        }
    }

    default void onSuccessfulConversation(Consumer<RenderResult>[] nextSteps, String content) {
        for (Consumer<RenderResult> consumer : nextSteps) {
            RenderResult renderResult = new RenderResult();
            renderResult.setSuccessful(true);
            renderResult.setContent(content);
            consumer.accept(renderResult);
        }
    }

    default void onFailedConversation(Consumer<RenderResult>[] nextSteps, Exception e) {
        for (Consumer<RenderResult> consumer : nextSteps) {
            RenderResult renderResult = new RenderResult();
            renderResult.setSuccessful(false);
            renderResult.setException(e);
            consumer.accept(renderResult);
        }
    }

}
