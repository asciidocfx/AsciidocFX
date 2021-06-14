package com.kodedu.service.ui;

import com.kodedu.component.EditorPane;
import com.kodedu.component.MyTab;
import javafx.scene.Node;


/**
 * Created by usta on 25.12.2014.
 */
public interface EditorService {
    public Node createEditorVBox(EditorPane editorPane, MyTab myTab);
}
