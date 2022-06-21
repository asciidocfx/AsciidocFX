package com.kodedu.service;

import java.nio.file.Path;

/**
 * Created by usta on 20.05.2015.
 */
public interface PathFinderService {
    public final static String label = "core::service::PathFinder";

    public Path findPath(String uri, Integer parent);
}
