package com.kodedu.service.extension.impl;

import com.kodedu.config.ExtensionConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.other.TrimWhite;
import com.kodedu.service.ThreadService;
import com.kodedu.service.cache.BinaryCacheService;
import com.kodedu.service.extension.MermaidService;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component(MermaidService.label)
public class MermaidServiceImpl implements MermaidService {

    private final Logger logger = LoggerFactory.getLogger(MermaidService.class);

    private Current current;
    private ApplicationController controller;
    private final ExtensionConfigBean extensionConfigBean;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private BinaryCacheService binaryCacheService;

    @Value("${application.mermaid.url}")
    private String mermaidUrl;

    private Map<String, Integer> rerenderMap = new ConcurrentHashMap<>();

    @Autowired
    public MermaidServiceImpl(final Current current, final ApplicationController controller, ExtensionConfigBean extensionConfigBean) {
        this.current = current;
        this.controller = controller;
        this.extensionConfigBean = extensionConfigBean;
    }

    @Override
    public void createMermaidDiagram(String mermaidContent, String type, String imagesDir, String imageTarget, String nodename, boolean rerender) {
        Objects.requireNonNull(imageTarget);

        boolean cachedResource = imageTarget.contains("/afx/cache");

        if (rerender) {
            String md5 = cachedResource ? imageTarget : DigestUtils.md5DigestAsHex(mermaidContent.getBytes());
            Integer renderCount = rerenderMap.compute(md5, (s, n) -> n == null ? 1 : (++n));
            if (renderCount > 3) {
                logger.error("Can't render image in more than 3 attempts.");
                return;
            }
        }

        if (!imageTarget.endsWith(".png") && !cachedResource) {
            return;
        }

        Integer cacheHit = current.getCache().get(imageTarget);

        int hashCode = Objects.hash(imageTarget, imagesDir, type, mermaidContent);
        if (Objects.isNull(cacheHit) || hashCode != cacheHit) {

            Path path = current.currentTab().getParentOrWorkdir();

            threadService.runActionLater(() -> {
                WebView webView = new WebView();
                webView.setLayoutX(-42000);
                webView.setLayoutY(-42000);
                webView.setMinSize(0, 0);

                int zoom = extensionConfigBean.getDefaultImageZoom();

                webView.setPrefSize(3000, 3000);
//                webView.setZoom(zoom);

                controller.getRootAnchor().getChildren().add(webView);

                threadService.runActionLater(() -> {
                    webView.getEngine().load(String.format(mermaidUrl, controller.getPort()));
                });

                webView.getEngine().setOnAlert(event -> {
                    String data = event.getData();
                    if ("READY".equals(data)) {
                        final JSObject window = (JSObject) webView.getEngine().executeScript("window");
                        window.setMember("webview", webView);
                        window.call("renderMermaid", mermaidContent);
                    } else if ("RENDERED".equals(data)) {
                        WritableImage writableImage = webView.snapshot(new SnapshotParameters(), null);
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        threadService.runActionLater(() -> {
                            controller.getRootAnchor().getChildren().remove(webView);
                        });

                        threadService.runTaskLater(() -> {
                            TrimWhite trimWhite = new TrimWhite();
                            BufferedImage trimmed = trimWhite.trim(bufferedImage);
                            if (isImageEmpty(trimmed)) {
                                createMermaidDiagram(mermaidContent, type, imagesDir, imageTarget, nodename, true);
                                return;
                            }

                            if (!cachedResource) {
                                Path treePath = path.resolve(imageTarget);
                                IOHelper.createDirectories(path.resolve(imagesDir));
                                IOHelper.imageWrite(trimmed, "png", treePath.toFile());
                            } else {
                                binaryCacheService.putBinary(imageTarget, trimmed);
                            }

                            current.getCache().put(imageTarget, hashCode);
                        });

                    } else {
                        logger.error(data);
                    }
                });
            });

        }


    }

    private boolean isImageEmpty(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        for (int i = 0; i < height; i++) {
            for (int j = width - 1; j >= 0; j--) {
                if (bufferedImage.getRGB(j, i) != Color.WHITE.getRGB()) {
                    return false;
                }
            }
        }
        return true;
    }
}
