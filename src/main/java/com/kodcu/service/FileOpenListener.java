package com.kodcu.service;

import com.install4j.api.launcher.StartupNotification;
import com.kodcu.service.ui.TabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by usta on 19.08.2017.
 */
@Component
public class FileOpenListener implements StartupNotification.Listener {

    private final ThreadService threadService;
    private final TabService tabService;

    @Autowired
    public FileOpenListener(ThreadService threadService, TabService tabService) {
        this.threadService = threadService;
        this.tabService = tabService;
    }

    @Override
    public void startupPerformed(String parameters) {
        Pattern pattern = Pattern.compile("\"([^\"]+)\"|.*");
        Matcher matcher = pattern.matcher(parameters);

        List<Path> pathSet = matcher.results()
                .map(e -> e.group())
                .map(e -> e.replaceAll("\"", ""))
                .map(e -> e.trim())
                .filter(e -> !e.isEmpty())
                .map(Paths::get)
                .map(Path::toAbsolutePath)
                .distinct()
                .collect(Collectors.toList());

        threadService.runActionLater(() -> {

            for (Path path : pathSet) {
                tabService.addTab(path);
            }
        });
    }

}
