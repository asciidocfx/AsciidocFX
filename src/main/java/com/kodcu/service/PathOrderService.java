package com.kodcu.service;

import com.kodcu.other.OSHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * Created by usta on 01.01.2015.
 */
@Component
public class PathOrderService {

    public int comparePaths(Path first, Path second) {

        if (OSHelper.isMac() || OSHelper.isWindows())
            return first.getFileName().toString().compareToIgnoreCase(second.getFileName().toString());

        return first.compareTo(second);
    }

}
