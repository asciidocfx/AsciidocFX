package com.kodedu.other;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by usta on 16.03.2015.
 */
public class TrimWhite {

    public BufferedImage trim(BufferedImage img) {
        BufferedImage bufferedImage = AutoCrop.autoCrop(img, 5);
        return bufferedImage;
    }

    private int getTrimmedWidth(BufferedImage img) {
        int height = img.getHeight();
        int width = img.getWidth();
        int trimmedWidth = 0;

        for (int i = 0; i < height; i++) {
            for (int j = width - 1; j >= 0; j--) {
                if (!ColorUtils.match(Color.WHITE,img.getRGB(j, i) ,5) &&
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
                if (!ColorUtils.match(Color.WHITE,img.getRGB(i, j) ,5) &&
                        j > trimmedHeight) {
                    trimmedHeight = j;
                    break;
                }
            }
        }

        return trimmedHeight;
    }

}
