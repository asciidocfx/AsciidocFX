package com.kodedu.component;

import com.kodedu.config.EditorConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.service.ThreadService;
import com.sun.javafx.scene.control.skin.ContextMenuContent;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * Created by usta on 15.06.2015.
 */
public abstract class ViewPanel extends AnchorPane {

    private final Logger logger = LoggerFactory.getLogger(ViewPanel.class);

    protected final ThreadService threadService;
    protected final ApplicationController controller;
    protected final Current current;
    protected final EditorConfigBean editorConfigBean;
    protected WebView webView;

    protected static final BooleanProperty stopScrolling = new SimpleBooleanProperty(false);
    protected static final BooleanProperty stopJumping = new SimpleBooleanProperty(false);

    @Value("${application.generic.url}")
    private String genericUrl;

    protected ViewPanel(ThreadService threadService, ApplicationController controller, Current current, EditorConfigBean editorConfigBean) {
        this.threadService = threadService;
        this.controller = controller;
        this.current = current;
        this.editorConfigBean = editorConfigBean;
    }

    @PostConstruct
    public void afterViewInit() {
        threadService.runActionLater(() -> {
            this.getChildren().add(getWebView());
            initializeMargins();
            initializePreviewContextMenus();
        });
    }

    public void enableScrollingAndJumping() {
        stopScrolling.setValue(false);
        stopJumping.setValue(false);
    }

    public void disableScrollingAndJumping() {
        stopScrolling.setValue(true);
        stopJumping.setValue(true);
    }

    private static CheckMenuItem detachPreviewItem;

    private void initializePreviewContextMenus() {

        CheckMenuItem stopRenderingItem = new CheckMenuItem("Stop rendering");
        CheckMenuItem stopScrollingItem = new CheckMenuItem("Stop scrolling");
        CheckMenuItem stopJumpingItem = new CheckMenuItem("Stop jumping");
        detachPreviewItem = new CheckMenuItem("Detach preview");


        stopRenderingItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            controller.stopRenderingProperty().setValue(newValue);
        });

        stopScrollingItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            stopScrolling.setValue(newValue);
        });

        stopJumpingItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            stopJumping.setValue(newValue);
        });

        detachPreviewItem.selectedProperty().bindBidirectional(editorConfigBean.detachedPreviewProperty());

        MenuItem refresh = MenuItemBuilt.item("Clear image cache").click(e -> {
            webEngine().executeScript("clearImageCache()");
        });

        getWebView().setOnContextMenuRequested(event -> {

            @SuppressWarnings("deprecation") final Iterator<Window> windows = Window.impl_getWindows();

            while (windows.hasNext()) {
                final Window window = windows.next();

                if (window instanceof ContextMenu) {

                    Optional<Node> nodeOptional = Optional.ofNullable(window)
                            .map(Window::getScene)
                            .map(Scene::getRoot)
                            .map(Parent::getChildrenUnmodifiable)
                            .filter((nodes) -> !nodes.isEmpty())
                            .map(e -> e.get(0))
                            .map(e -> e.lookup(".context-menu"));

                    if (nodeOptional.isPresent()) {
                        ObservableList<Node> childrenUnmodifiable = ((Parent) nodeOptional.get())
                                .getChildrenUnmodifiable();
                        ContextMenuContent cmc = (ContextMenuContent) childrenUnmodifiable.get(0);

                        // add new item:
                        cmc.getItemsContainer().getChildren().add(new Separator());
                        cmc.getItemsContainer().getChildren().add(cmc.new MenuItemContainer(stopRenderingItem));
                        cmc.getItemsContainer().getChildren().add(cmc.new MenuItemContainer(stopScrollingItem));
                        cmc.getItemsContainer().getChildren().add(cmc.new MenuItemContainer(stopJumpingItem));
                        cmc.getItemsContainer().getChildren().add(cmc.new MenuItemContainer(detachPreviewItem));
                    }
                }
            }
        });
    }

    protected void initializeMargins() {
        AnchorPane.setBottomAnchor(this, 0D);
        AnchorPane.setTopAnchor(this, 0D);
        AnchorPane.setLeftAnchor(this, 0D);
        AnchorPane.setRightAnchor(this, 0D);
        VBox.setVgrow(this, Priority.ALWAYS);
        AnchorPane.setBottomAnchor(getWebView(), 0D);
        AnchorPane.setTopAnchor(getWebView(), 0D);
        AnchorPane.setLeftAnchor(getWebView(), 0D);
        AnchorPane.setRightAnchor(getWebView(), 0D);
        VBox.setVgrow(getWebView(), Priority.ALWAYS);
    }

    public void browse() {
        threadService.runActionLater(() -> {
            final String documentURI = webEngine().getDocument().getDocumentURI();
            controller.browseInDesktop(documentURI);
        });
    }

    public static CheckMenuItem getDetachPreviewItem() {
        return detachPreviewItem;
    }

    public void onscroll(Object pos, Object max) {

        if (stopScrolling.get())
            return;

        threadService.runActionLater(() -> {
            runScrolling(pos, max);
        });
    }

    public abstract void runScroller(String text);

    private void runScrolling(Object pos, Object max) {

        Number position = (Number) pos; // current scroll position for editor
        Number maximum = (Number) max; // max scroll position for editor

        double currentY = (position.doubleValue() < 0) ? 0 : position.doubleValue();
        double ratio = (currentY * 100) / maximum.doubleValue();
        Integer browserMaxScroll = (Integer) webEngine().executeScript("document.documentElement.scrollHeight - document.documentElement.clientHeight;");
        double browserScrollOffset = (Double.valueOf(browserMaxScroll) * ratio) / 100.0;
        webEngine().executeScript(String.format("window.scrollTo(0, %f )", browserScrollOffset));
    }


    public WebEngine webEngine() {
        return getWebView().getEngine();
    }

    public void load(String url) {
        if (nonNull(url))
            Platform.runLater(() -> {
                webEngine().load(url);
            });
        else
            logger.error("Url is not loaded. Reason: null reference");
    }

    public void hide() {
        super.setVisible(false);
    }

    public void show() {
        super.setVisible(true);
    }

    public String getLocation() {
        return webEngine().getLocation();
    }

    public void setMember(String name, Object value) {
        getWindow().setMember(name, value);
    }

    public Object call(String methodName, Object... args) {
        return getWindow().call(methodName, args);
    }

    public Object getMember(String name) {
        return getWindow().getMember(name);
    }

    public WebView getWebView() {

        if (Objects.isNull(webView)) {
            webView = threadService.supply(WebView::new);
        }

        return webView;
    }

    public void loadJs(String... jsPaths) {
        threadService.runActionLater(() -> {
            for (String jsPath : jsPaths) {
                String format = String.format("var scriptEl = document.createElement('script');\n" +
                        "scriptEl.setAttribute('src','" + genericUrl + "');\n" +
                        "document.querySelector('body').appendChild(scriptEl);", controller.getPort(), jsPath);
                webEngine().executeScript(format);
            }
        });
    }

    public void setOnSuccess(Runnable runnable) {
        threadService.runActionLater(() -> {
            getWindow().setMember("afx", controller);
            Worker<Void> loadWorker = webEngine().getLoadWorker();
            ReadOnlyObjectProperty<Worker.State> stateProperty = loadWorker.stateProperty();
            stateProperty.addListener((observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    threadService.runActionLater(runnable);
                }
            });
        });
    }


    public JSObject getWindow() {
        return (JSObject) webEngine().executeScript("window");
    }

    public abstract void scrollByPosition(String text);

    public abstract void scrollByLine(String text);

    public void clearImageCache(Path imagePath) {

        Optional.ofNullable(imagePath)
                .map(Path::getFileName)
                .map(Path::toString)
                .ifPresent(this::clearImageCache);
    }

    ;

    public void clearImageCache(String imagePath) {

        threadService.runActionLater(() -> {
            webEngine().executeScript(String.format("clearImageCache(\"%s\")", imagePath));
        });

    }

    public static void setMarkReAtached() {
        if (nonNull(detachPreviewItem)) {
            detachPreviewItem.selectedProperty().setValue(Boolean.FALSE);
        }
    }
}
