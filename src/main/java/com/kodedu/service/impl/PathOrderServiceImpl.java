package com.kodedu.service.impl;

import com.kodedu.helper.OSHelper;
import com.kodedu.service.PathOrderService;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Created by usta on 01.01.2015.
 */
@Component(PathOrderService.label)
public class PathOrderServiceImpl implements PathOrderService {
    @Override
    public int comparePaths(Path first, Path second) {

        if (OSHelper.isMac() || OSHelper.isWindows())
            return first.getFileName().toString().compareToIgnoreCase(second.getFileName().toString());

        return first.compareTo(second);
    }

}
