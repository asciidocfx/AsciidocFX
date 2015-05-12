package com.kodcu.service.convert.ebook;

import com.icl.saxon.TransformerFactoryImpl;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.convert.docbook.DocBookConverter;
import com.kodcu.service.ui.IndikatorService;
import javafx.stage.FileChooser;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeroturnaround.zip.ZipUtil;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.joox.JOOX.$;

/**
 * Created by usta on 30.08.2014.
 */
@Component
public class EpubConverter {

    private final Logger logger = LoggerFactory.getLogger(EpubConverter.class);

    private final ApplicationController asciiDocController;
    private final Current current;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final IndikatorService indikatorService;
    private final DocBookConverter docBookConverter;
    private final PathResolverService pathResolverService;

    private Path epubPath;

    @Autowired
    public EpubConverter(final ApplicationController asciiDocController, final Current current, final ThreadService threadService,
                         final DirectoryService directoryService, final IndikatorService indikatorService, final DocBookConverter docBookConverter, PathResolverService pathResolverService) {
        this.asciiDocController = asciiDocController;
        this.current = current;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.indikatorService = indikatorService;
        this.docBookConverter = docBookConverter;
        this.pathResolverService = pathResolverService;
    }

    public CompletableFuture<Path> produceEpub3() {
        return produceEpub3(false);
    }

    public CompletableFuture<Path> produceEpub3Temp() {
        return produceEpub3(false, true);
    }

    public CompletableFuture<Path> produceEpub3(boolean askPath) {
        return produceEpub3(askPath, false);
    }

    private CompletableFuture<Path> produceEpub3(boolean askPath, boolean isTemp) {

        CompletableFuture<Path> completableFuture = new CompletableFuture<>();

        try {

            Path currentTabPath = current.currentPath().get();
            Path currentTabPathDir = currentTabPath.getParent();
            Path configPath = asciiDocController.getConfigPath();
            String tabText = current.getCurrentTabText().replace("*", "").trim();

            if (askPath) {
                FileChooser fileChooser = directoryService.newFileChooser("Save Epub file");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EPUB", "*.epub"));
                epubPath = fileChooser.showSaveDialog(null).toPath();
            } else if (isTemp) {
                epubPath = IOHelper.createTempFile(".epub");
            } else
                epubPath = currentTabPathDir.resolve(tabText + ".epub");

            threadService.runTaskLater(() -> {

                try {
                    if (!isTemp)
                        indikatorService.startCycle();

                    Path epubTemp = Files.createTempDirectory("epub");

                    TransformerFactory factory = new TransformerFactoryImpl();

                    File xslFile = configPath.resolve("docbook/epub3/chunk.xsl").toFile();
                    StreamSource xslSource = new StreamSource(xslFile);

                    Transformer transformer = factory.newTransformer(xslSource);

                    docBookConverter.convert(false, docbook -> {

                        threadService.runTaskLater(() -> {
                            transformer.setParameter("base.dir", epubTemp.resolve("OEBPS").toString());
                            try (StringReader reader = new StringReader(docbook);) {
                                StreamSource xmlSource = new StreamSource(reader);
                                IOHelper.transform(transformer, xmlSource, new StreamResult());
                            }

                            Path containerXml = epubTemp.resolve("META-INF/container.xml");

                            Match root = IOHelper.$(containerXml.toFile());
                            root
                                    .find("rootfile")
                                    .attr("full-path", "OEBPS/package.opf");

                            StringBuilder builder = new StringBuilder();
                            builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

                            Match wrapper = $("wrapper");
                            wrapper.append(root);
                            builder.append(wrapper.content());

                            IOHelper.matchWrite(root, containerXml.toFile());

                            IOHelper.writeToFile(containerXml, builder.toString(), TRUNCATE_EXISTING, WRITE);

                            Path epubOut = epubTemp.resolve("book.epub");

                            Stream<Path> imageStream = IOHelper.find(currentTabPathDir, Integer.MAX_VALUE, (p, attr) -> pathResolverService.isImage(p));

                            imageStream.forEach(img -> {
                                IOHelper.copyFile(img.toFile(), epubTemp.resolve("OEBPS").resolve(currentTabPathDir.relativize(img)).toFile());
                            });

                            IOHelper.copyDirectoryToDirectory(configPath.resolve("docbook/images/callouts").toFile(), epubTemp.resolve("OEBPS/images")
                                    .toFile());
                            ZipUtil.pack(epubTemp.toFile(), epubOut.toFile());
                            ZipUtil.removeEntry(epubOut.toFile(), "book.epub");

                            IOHelper.move(epubOut, epubPath, StandardCopyOption.REPLACE_EXISTING);

                            if (!isTemp) {
                                indikatorService.completeCycle();
                                threadService.runActionLater(() -> {
                                    asciiDocController.getRecentFilesList().remove(epubPath.toString());
                                    asciiDocController.getRecentFilesList().add(0, epubPath.toString());
                                });
                            }
                        });
                    });

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    completableFuture.complete(epubPath);
                }

            });

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            indikatorService.completeCycle();
        }

        return completableFuture;
    }
}
