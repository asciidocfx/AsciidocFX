package com.kodedu.other;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Automatically crops extraneous space from an image.
 *
 * @author <a href="http://efsavage.com">Eric F. Savage</a>, <a
 *         href="mailto:code@efsavage.com">code@efsavage.com</a>.
 */
public class AutoCrop {

    private static final Logger log = Logger.getLogger(AutoCrop.class.getName());

    /**
     * Crops an image based on the value of the top left pixel.
     *
     * @param image
     *            The image to crop.
     * @param fuzziness
     *            The fuzziness allowed for minor deviations (~5 is
     *            recommended).
     * @return The new image data, cropped.
     */
    public static BufferedImage autoCrop(final BufferedImage image, final int fuzziness) {
        final Color color = new Color(image.getRGB(0, 0));
        boolean stop = false;
        int cropTop = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (!ColorUtils.match(color, image.getRGB(x, y), fuzziness)) {
                    stop = true;
                    break;
                }
            }
            if (stop) {
                if (y > 0) {
                    cropTop = y - 1;
                }
                break;
            }
        }

        log.finest("Cropping top " + cropTop + " rows");

        stop = false;
        int cropBot = image.getHeight();
        for (int y = (image.getHeight() - 1); y >= 0; y--) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (!ColorUtils.match(color, image.getRGB(x, y), fuzziness)) {
                    stop = true;
                    break;
                }
            }
            if (stop) {
                if (y < image.getHeight()) {
                    cropBot = y + 1;
                }
                break;
            }
        }

        log.finest("Cropping bottom " + (image.getHeight() - cropBot) + " rows");

        stop = false;
        int cropLeft = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (!ColorUtils.match(color, image.getRGB(x, y), fuzziness)) {
                    stop = true;
                    break;
                }
            }
            if (stop) {
                if (x > 0) {
                    cropLeft = x - 1;
                }
                break;
            }
        }

        log.finest("Cropping left " + cropLeft + " rows");

        stop = false;
        int cropRight = 0;
        for (int x = (image.getWidth() - 1); x >= 0; x--) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (!ColorUtils.match(color, image.getRGB(x, y), fuzziness)) {
                    stop = true;
                    break;
                }
            }
            if (stop) {
                if (x < image.getWidth()) {
                    cropRight = x + 1;
                }
                break;
            }
        }

        log.finest("Cropping right " + (image.getWidth() - cropRight) + " rows");

        return image.getSubimage(cropLeft, cropTop, cropRight - cropLeft, cropBot - cropTop);
    }

    /**
     * Crops an image based on the value of the top left pixel.
     *
     * @param data
     *            The image data.
     * @param fuzziness
     *            The fuzziness allowed for minor deviations (~5 is
     *            recommended).
     * @return The new image data, cropped.
     * @throws IOException
     *             If the image could not be read.
     */
    public static byte[] autoCrop(final byte[] data, final int fuzziness) throws IOException {
        final BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
        final BufferedImage cropped = autoCrop(image, fuzziness);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(cropped, "png", out);
        return out.toByteArray();
    }

}
