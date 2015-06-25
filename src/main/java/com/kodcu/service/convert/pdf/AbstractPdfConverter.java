package com.kodcu.service.convert.pdf;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.convert.DocumentConverter;
import com.kodcu.service.convert.docbook.DocBookConverter;
import com.kodcu.service.ui.IndikatorService;
import javafx.stage.FileChooser;
import org.apache.fop.apps.FOURIResolver;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.cli.InputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by usta on 26.07.2014.
 */
public abstract class AbstractPdfConverter implements DocumentConverter<String> {

    private final Logger logger = LoggerFactory.getLogger(AbstractPdfConverter.class);

    private final ApplicationController asciiDocController;
    private final DocBookConverter docBookConverter;
    private final IndikatorService indikatorService;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final Current current;
    private final PathResolverService pathResolverService;

    private Path pdfPath;

    public AbstractPdfConverter(final ApplicationController asciiDocController, final DocBookConverter docBookConverter,
                                final IndikatorService indikatorService,
                                final ThreadService threadService, final DirectoryService directoryService, final Current current, PathResolverService pathResolverService) {
        this.asciiDocController = asciiDocController;
        this.docBookConverter = docBookConverter;
        this.indikatorService = indikatorService;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.current = current;
        this.pathResolverService = pathResolverService;
    }

    protected void produce(boolean askPath, InputHandler handler, FopFactory fopFactory, Path docbookTempfile) {
        final Path currentTabPath = current.currentPath().get();
        final Path currentTabPathDir = currentTabPath.getParent();
        final String tabText = current.getCurrentTabText().replace("*", "").trim();
        threadService.runActionLater(() -> {
            if (askPath) {
                final FileChooser fileChooser = directoryService.newFileChooser("Save PDF file");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
                pdfPath = fileChooser.showSaveDialog(null).toPath();
            } else
                pdfPath = currentTabPathDir.resolve(tabText + ".pdf");

            threadService.runTaskLater(() -> {
                indikatorService.startCycle();
                logger.debug("PDF conversion started");
                try (FileOutputStream outputStream = new FileOutputStream(pdfPath.toFile());) {
                    FOUserAgent userAgent = new FOUserAgent(fopFactory);
                    userAgent.setURIResolver(new FOURIResolver(true) {
                        @Override
                        public Source resolve(String href, String base) throws TransformerException {
                            if (Objects.nonNull(href)) {
                                try {
                                    Path path = Paths.get(URI.create(href));
                                    if (!Files.exists(path)) {

                                        Path tryThis = currentTabPathDir.resolve(path.subpath(0, path.getNameCount()));

                                        if (Files.exists(tryThis)) {
                                            return super.resolve(tryThis.toUri().toString(), base);
                                        }

                                        if(pathResolverService.isImage(path)){
                                            Optional<Path> first = IOHelper.find(currentTabPathDir, Integer.MAX_VALUE, (p, attr) -> p.getFileName().equals(path.getFileName())).findFirst();
                                            if (first.isPresent())
                                                return super.resolve(first.map(Path::toUri).map(URI::toString).get(), base);
                                        }

                                    }
                                } catch (Exception e) {
                                    logger.error("Problem occured while converting to PDF",e);
                                }
                            }

                            return super.resolve(href, base);
                        }
                    });
                    handler.renderTo(userAgent, "application/pdf", outputStream);
                    Files.deleteIfExists(docbookTempfile);
                } catch (Exception e) {
                    logger.error("Problem occured while converting to PDF", e);
                } finally {

                    indikatorService.completeCycle();
                    logger.debug("PDF conversion ended");

                    threadService.runActionLater(() -> {
                        asciiDocController.getRecentFilesList().remove(pdfPath.toString());
                        asciiDocController.getRecentFilesList().add(0, pdfPath.toString());
                    });
                }
            });
        });
    }

}
