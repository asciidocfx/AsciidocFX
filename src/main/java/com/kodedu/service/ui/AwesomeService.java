package com.kodedu.service.ui;

import javafx.scene.Node;
import java.nio.file.Path;

/**
 * Created by usta on 16.12.2014.
 */
public interface AwesomeService {

    public final static String label = "core::service::ui::AwesomeService";

    public Node getIcon(final Path path);
}
