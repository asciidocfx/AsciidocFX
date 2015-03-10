package com.kodcu.service;

import com.kodcu.other.OSHelper;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

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
