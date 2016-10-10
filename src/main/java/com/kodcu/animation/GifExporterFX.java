package com.kodcu.animation;

/**
 * Created by usta on 10.10.2016.
 */

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * @author william
 */
@Component
public class GifExporterFX {

    private final ThreadService threadService;

    @Autowired
    public GifExporterFX(ThreadService threadService) {
        this.threadService = threadService;
    }

    private BufferedImage latestBufferedImage;

    public ScheduledFuture<?> captureNow(Node target, Path outputDirectory, int timeBetweenFramesMS, boolean loopContinuously) throws Exception {
        ImageOutputStream output = new FileImageOutputStream(outputDirectory.toFile());
        GifSequenceWriter gifWriter = new GifSequenceWriter(output, 3, timeBetweenFramesMS, loopContinuously);

        ScheduledFuture<?> future = threadService.scheduleWithDelay(() -> {

            Semaphore semaphore = new Semaphore(1);

            int w = (int) target.getBoundsInParent().getWidth();
            int h = (int) target.getBoundsInParent().getHeight();
            WritableImage img = new WritableImage(w, h);

            threadService.runActionLater(() -> {
                try {
                    target.snapshot(null, img);
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                    threadService.runTaskLater(() -> {
                        boolean isSame = IOHelper.isSameImage(bufferedImage, latestBufferedImage);
                        latestBufferedImage = bufferedImage;
                        if (!isSame) {
                            try {
                                gifWriter.writeToSequence(bufferedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    semaphore.release();
                } catch (Exception ex) {
                    Logger.getLogger(GifExporterFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, timeBetweenFramesMS, TimeUnit.MILLISECONDS);

        threadService.start(() -> {
            try {
                future.get();
            } catch (Exception e) {
            }


            IOHelper.close(gifWriter, output);
        });

        return future;


    }
}