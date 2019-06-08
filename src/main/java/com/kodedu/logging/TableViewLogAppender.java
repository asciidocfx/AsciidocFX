package com.kodedu.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.kodedu.service.ThreadService;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * Created by usta on 02.06.2015.
 */

public class TableViewLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static TableView<MyLog> logViewer;
    private static ObservableList<MyLog> logList;
    private static List<MyLog> buffer = Collections.synchronizedList(new LinkedList<MyLog>());
    private static Label logShortMessage;
    private static ThreadService threadService;
    PatternLayoutEncoder encoder;
    private static ToggleButton logShowHider;

    public static void setLogViewer(TableView<MyLog> logViewer) {
        TableViewLogAppender.logViewer = logViewer;
    }

    public static void setLogList(ObservableList<MyLog> logList) {
        TableViewLogAppender.logList = logList;
    }

    public static void setStatusMessage(Label logShortMessage) {
        TableViewLogAppender.logShortMessage = logShortMessage;
    }

    public static void setShowHideLogs(ToggleButton logShowHider) {
        TableViewLogAppender.logShowHider = logShowHider;
    }

    public static ToggleButton getLogShowHider() {
        return logShowHider;
    }

    @Override
    protected void append(ILoggingEvent event) {

        if (Objects.isNull(logViewer))
            return;

        String message = event.getFormattedMessage();
        String level = event.getLevel().toString();

        if (Objects.isNull(message)) {
            return;
        }

        if (event.getLevel() == Level.ERROR) {
            ObservableList<String> styleClass = logShowHider.getStyleClass();
            if (!styleClass.contains("red-label")) {
                styleClass.add("red-label");
            }
        }

        final String finalMessage = message;
        threadService.buff("logMessager").schedule(() -> {
            threadService.runActionLater(() -> {
                logShortMessage.setText(finalMessage);
            });
        }, 1, TimeUnit.SECONDS);


        IThrowableProxy tp = event.getThrowableProxy();
        if (Objects.nonNull(tp) && event.getLevel() == Level.ERROR) {
            String tpMessage = ThrowableProxyUtil.asString(tp);
            message += "\n" + tpMessage;
        }

        if (!message.isEmpty()) {
            MyLog myLog = new MyLog(level, message);
            buffer.add(myLog);
        }

        threadService.buff("logAppender").schedule(() -> {
            final List<MyLog> clone = new LinkedList<>(buffer);
            buffer.clear();
            threadService.runActionLater(() -> {
                logList.addAll(clone);
            });
        }, 2, TimeUnit.SECONDS);
    }

    public PatternLayoutEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(PatternLayoutEncoder encoder) {
        this.encoder = encoder;
    }

    public static void setThreadService(ThreadService threadService) {
        TableViewLogAppender.threadService = threadService;
    }
}
