package com.kodedu.service;

import com.kodedu.helper.IOHelper;
import com.kodedu.service.ui.TabService;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by usta on 19.08.2017.
 */
@Component
public class FileOpenListener {

    private final TabService tabService;

    @Autowired
    public FileOpenListener(TabService tabService) {
        this.tabService = tabService;
    }

    public void startupPerformed(String parameters) {
        Pattern pattern = Pattern.compile("\"([^\"]+)\"|.*");
        Matcher matcher = pattern.matcher(parameters);

        List<Path> pathList = new ArrayList<>();

        while (matcher.find()) {
            String group = matcher.group();
            group = group.replaceAll("\"", "");
            group = group.trim();
            if (!group.isEmpty()) {
                Path path = IOHelper.getPath(group).toAbsolutePath();
                if (!pathList.contains(path)) {
                    pathList.add(path);
                }
            }
        }

        Platform.runLater(() -> {
            for (Path path : pathList) {
                tabService.addTab(path);
            }
            tabService.closeFirstNewTab();
        });
    }

}
