package com.kodcu.logging;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by usta on 02.06.2015.
 */
@Plugin(name = "TableViewLogAppender", category = "Core", elementType = "appender", printObject = true)
public class TableViewLogAppender extends AbstractAppender {

    private static TableView<MyLog> logViewer;
    private static ObservableList<MyLog> logList;
    private static final ExecutorService logExecutor = Executors.newSingleThreadExecutor();
    private List<MyLog> buffer = Collections.synchronizedList(new LinkedList<MyLog>());
    private final AtomicBoolean scheduled = new AtomicBoolean(false);
    private static Label logShortMessage;

    protected TableViewLogAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
    }

    @PluginFactory
    public static TableViewLogAppender createAppender(@PluginAttribute("name") String name,
                                                      @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                                      @PluginElement("Layout") Layout layout,
                                                      @PluginElement("Filters") Filter filter) {

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new TableViewLogAppender(name, filter, layout);
    }

    public static void setLogViewer(TableView<MyLog> logViewer) {
        TableViewLogAppender.logViewer = logViewer;
    }

    public static void setLogList(ObservableList<MyLog> logList) {
        TableViewLogAppender.logList = logList;
    }

    public static void setLogShortMessage(Label logShortMessage) {
        TableViewLogAppender.logShortMessage = logShortMessage;
    }

    @Override
    public void append(LogEvent event) {

        if (Objects.isNull(logViewer))
            return;

        String message = event.getMessage().getFormattedMessage();
        String level = event.getLevel().name();
        MyLog myLog = new MyLog(level, message);
        buffer.add(myLog);

        Platform.runLater(() -> {
            logShortMessage.setText(message);
        });

        if (!scheduled.get()) {
            scheduled.set(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        List<MyLog> clone = new LinkedList<>(buffer);
                        buffer.clear();
                        logList.addAll(clone);
                        scheduled.set(false);
                    });
                }
            }, 3000);
        }
    }
}
