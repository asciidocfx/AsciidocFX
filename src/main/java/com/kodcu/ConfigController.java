package com.kodcu;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ResourceBundle;


@Component
public class ConfigController implements Initializable {

    public Slider fontSizeSlider;
    public Slider mouseSpeedSlider;
    public ComboBox<String> themeSelector;
    public Label fontLabel;
    public Label mouseSpeedLabel;

    @Autowired
    private Current current;

    @Autowired
    private AsciiDocController asciiDocController;

    @FXML
    private void saveConfig(ActionEvent actionEvent) throws ConfigurationException {
        PropertiesConfiguration configuration = new PropertiesConfiguration("application.properties");
        configuration.setProperty("editor.fontsize", Double.valueOf(fontSizeSlider.getValue()).intValue());
        configuration.setProperty("editor.scroll.speed", mouseSpeedSlider.getValue());
        configuration.setProperty("editor.theme", themeSelector.getValue());
        File file = Paths.get(System.getProperty("user.home")).resolve("asciidocfx.properties").toFile();
        configuration.save(file);

        asciiDocController.getFontSize().setValue(Double.valueOf(fontSizeSlider.getValue()).toString());
        asciiDocController.getScrollSpeed().setValue(String.valueOf(mouseSpeedSlider.getValue()));
        asciiDocController.getTheme().setValue(themeSelector.getValue());

        if(Objects.nonNull(current.currentEngine()))
        current.currentEngine().executeScript(String.format(asciiDocController.getLoadConfig()
                , String.valueOf(asciiDocController.getFontSize().get())
                , String.valueOf(asciiDocController.getTheme().get())
                , String.valueOf(asciiDocController.getScrollSpeed().get())));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        themeSelector.getItems().clear();
        themeSelector.getItems().addAll("ace", "github", "eclipse", "ambiance", "chaos", "chrome", "clouds"
                , "cobalt", "dawn", "dreamweaver", "katzenmilch", "kuroir"
                , "merbivore", "monokai", "terminal", "textmate", "tomorrow"
                , "twilight", "xcode");
        fontSizeSlider.valueProperty().addListener((observableValue, number, number2) -> {
            fontLabel.textProperty().setValue(String.valueOf(number2.intValue()));
        });
        mouseSpeedSlider.valueProperty().addListener((observableValue, number, number2) -> {
            mouseSpeedLabel.textProperty().setValue(String.format("%.2f",Double.valueOf(number2.doubleValue())));
        });

    }

    public ComboBox<String> getThemeSelector() {
        return themeSelector;
    }

    public Slider getMouseSpeedSlider() {
        return mouseSpeedSlider;
    }

    public Slider getFontSizeSlider() {
        return fontSizeSlider;
    }
}
