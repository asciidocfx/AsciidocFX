package com.kodedu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.scene.control.Spinner;
import javafx.util.Callback;

/**
 * Created by usta on 17.07.2015.
 */
public class SpinnerFactory<T> implements Callback<Void, FXFormNode> {

    private final Spinner<T> spinner;

    public SpinnerFactory(Spinner<T> spinner) {
        this.spinner = spinner;
        this.spinner.setEditable(true);
    }

    @Override
    public FXFormNode call(Void param) {
        return new FXFormNodeWrapper(spinner, spinner.getValueFactory().valueProperty());
    }
}
