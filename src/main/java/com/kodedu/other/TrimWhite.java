package com.kodedu.other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Created by usta on 16.03.2015.
 */
public class TrimWhite {

    private static final Logger logger = LoggerFactory.getLogger(TrimWhite.class);

    public BufferedImage trim(BufferedImage img) {
        BufferedImage trimmedImage = null;
        try {
            trimmedImage = AutoCrop.autoCrop(img, 5);
        } catch (Exception e) {
            logger.warn("Trim failed for image: {}", img, e);
            trimmedImage = tryTrimAlternative(img);
        }
        return trimmedImage;
    }

    private BufferedImage tryTrimAlternative(BufferedImage img) {
        try {
            int width = getTrimmedWidth(img) + 5;
            int height = getTrimmedHeight(img) + 5;

            BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = newImg.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            return newImg;
        } catch (Exception e) {
            logger.warn("Trim failed for image: {}", img, e);
            return img;
        }
    }

    private int getTrimmedWidth(BufferedImage img) {
        int height = img.getHeight();
        int width = img.getWidth();
        int trimmedWidth = 0;

        for (int i = 0; i < height; i++) {
            for (int j = width - 1; j >= 0; j--) {
                if (!ColorUtils.match(Color.WHITE, img.getRGB(j, i), 5) &&
                        j > trimmedWidth) {
                    trimmedWidth = j;
                    break;
                }
            }
        }

        return trimmedWidth;
    }

    private int getTrimmedHeight(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int trimmedHeight = 0;

        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j >= 0; j--) {
                if (!ColorUtils.match(Color.WHITE, img.getRGB(i, j), 5) &&
                        j > trimmedHeight) {
                    trimmedHeight = j;
                    break;
                }
            }
        }

        return trimmedHeight;
    }

}
