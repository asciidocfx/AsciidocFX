package com.kodcu.service;

import com.install4j.api.launcher.StartupNotification;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ui.TabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
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

        List<Path> pathList = new ArrayList<>();

        while (matcher.find()) {
            String group = matcher.group();
            group = group.replaceAll("\"", "");
            group = group.trim();
            if(!group.isEmpty()){
                Path path = IOHelper.getPath(group).toAbsolutePath();
                if (!pathList.contains(path)) {
                    pathList.add(path);
                }
            }

        }
//
//        List<Path> pathSet = matcher.results()
//                .map(e -> e.group())
//                .map(e -> e.replaceAll("\"", ""))
//                .map(e -> e.trim())
//                .filter(e -> !e.isEmpty())
//                .map(Paths::get)
//                .map(Path::toAbsolutePath)
//                .distinct()
//                .collect(Collectors.toList());

        threadService.runActionLater(() -> {

            for (Path path : pathList) {
                tabService.addTab(path);
            }
        });
    }

}
