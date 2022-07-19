package com.kodedu.other;

/*
 *  Copyright 2012 Eric F. Savage, code@efsavage.com
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import java.awt.Color;

/**
 * Utilities for dealing with colors.
 *
 * @author <a href="http://efsavage.com">Eric F. Savage</a>, <a
 *         href="mailto:code@efsavage.com">code@efsavage.com</a>.
 */
public class ColorUtils {

    /**
     * Returns the largest absolute difference between the red, green and blue
     * values of the two colors.
     *
     * @param first
     *            The first color to test.
     * @param second
     *            The second color to test.
     * @return The largest absolute difference between the red, green and blue
     *         values of the two colors, will be a value between 0 and 255.
     */
    public static int getMaxDistance(final Color first, final Color second) {
        int distance = Math.abs(first.getRed() - second.getRed());
        if (Math.abs(first.getGreen() - second.getGreen()) > distance) {
            distance = Math.abs(first.getGreen() - second.getGreen());
        }
        if (Math.abs(first.getBlue() - second.getBlue()) > distance) {
            distance = Math.abs(first.getBlue() - second.getBlue());
        }
        return Math.abs(distance);
    }

    /**
     * Determines if a supplied RGB value is within a certain allowed distance
     * from a Color.
     *
     * @param color
     *            The color to test.
     * @param rgb
     *            The RGB value to test.
     * @param fuzzy
     *            The fuzziness of the match, i.e. the largest allowed
     *            {@link #getMaxDistance(Color, Color)}.
     * @return true if the two colors are within the fuzziness allowance,
     *         otherwise false.
     */
    public static boolean match(final Color color, final int rgb, final int fuzzy) {
        final Color test = new Color(rgb, true);
        final int distance = getMaxDistance(color, test);
        return distance < fuzzy;
    }

}