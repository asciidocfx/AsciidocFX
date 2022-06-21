package com.kodedu.service;

import java.nio.file.Path;

/**
 * Created by usta on 01.01.2015.
 */
public interface PathOrderService {
    public final static String label = "core::service::PathOrder";

    public int comparePaths(Path first, Path second);

}
