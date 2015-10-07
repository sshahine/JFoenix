package com.jfoenix.skins;
/*
 * Copyright (c) 2010, 2015, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.util.List;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.stage.StageHelper;

/**
 * Some basic utilities which need to be in java (for shifting operations or
 * other reasons), which are not toolkit dependent.
 *
 */
public class Utils {

    /***************************************************************************
     *                                                                         *
     * Math-related utilities                                                  *
     *                                                                         *
     **************************************************************************/

    /**
     * Simple utility function which clamps the given value to be strictly
     * between the min and max values.
     */
    public static float clamp(float min, float value, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Simple utility function which clamps the given value to be strictly
     * between the min and max values.
     */
    public static int clamp(int min, int value, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Simple utility function which clamps the given value to be strictly
     * between the min and max values.
     */
    public static double clamp(double min, double value, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Simple utility function which clamps the given value to be strictly
     * above the min value.
     */
    public static double clampMin(double value, double min) {
        if (value < min) return min;
        return value;
    }

    /**
     * Simple utility function which clamps the given value to be strictly
     * under the max value.
     */
    public static int clampMax(int value, int max) {
        if (value > max) return max;
        return value;
    }

    /**
     * Utility function which returns either {@code less} or {@code more}
     * depending on which {@code value} is closer to. If {@code value}
     * is perfectly between them, then either may be returned.
     */
    public static double nearest(double less, double value, double more) {
        double lessDiff = value - less;
        double moreDiff = more - value;
        if (lessDiff < moreDiff) return less;
        return more;
    }

    /***************************************************************************
     *                                                                         *
     * String-related utilities                                                *
     *                                                                         *
     **************************************************************************/

    /**
     * Helper to remove leading and trailing quotes from a string.
     * Works with single or double quotes. 
     */
    public static String stripQuotes(String str) {
        if (str == null) return str;
        if (str.length() == 0) return str;

        int beginIndex = 0;
        final char openQuote = str.charAt(beginIndex);        
        if ( openQuote == '\"' || openQuote=='\'' ) beginIndex += 1;

        int endIndex = str.length();
        final char closeQuote = str.charAt(endIndex - 1);
        if ( closeQuote == '\"' || closeQuote=='\'' ) endIndex -= 1;

        if ((endIndex - beginIndex) < 0) return str;

        // note that String.substring returns "this" if beginIndex == 0 && endIndex == count
        // or a new string that shares the character buffer with the original string.
        return str.substring(beginIndex, endIndex);
    }

    /**
     * Because mobile doesn't have string.split(s) function, this function
     * was written.
     */
    public static String[] split(String str, String separator) {
        if (str == null || str.length() == 0) return new String[] { };
        if (separator == null || separator.length() == 0) return new String[] { };
        if (separator.length() > str.length()) return new String[] { };

        java.util.List<String> result = new java.util.ArrayList<String>();

        int index = str.indexOf(separator);
        while (index >= 0) {
            String newStr = str.substring(0, index);
            if (newStr != null && newStr.length() > 0) {
                result.add(newStr);
            }
            str = str.substring(index + separator.length());
            index = str.indexOf(separator);
        }

        if (str != null && str.length() > 0) {
            result.add(str);
        }

        return result.toArray(new String[] { });
    }

    /**
     * Because mobile doesn't have string.contains(s) function, this function
     * was written.
     */
    public static boolean contains(String src, String s) {
        if (src == null || src.length() == 0) return false;
        if (s == null || s.length() == 0) return false;
        if (s.length() > src.length()) return false;

        return src.indexOf(s) > -1;
    }

    /***************************************************************************
     *                                                                         *
     * Color-related utilities                                                 *
     *                                                                         *
     **************************************************************************/

    /**
     * Calculates a perceptual brightness for a color between 0.0 black and 1.0 while
     */
    public static double calculateBrightness(Color color) {
          return  (0.3*color.getRed()) + (0.59*color.getGreen()) + (0.11*color.getBlue());
    }

    /**
     * Derives a lighter or darker of a given color.
     *
     * @param c           The color to derive from
     * @param brightness  The brightness difference for the new color -1.0 being 100% dark which is always black, 0.0 being
     *                    no change and 1.0 being 100% lighter which is always white
     */
    public static Color deriveColor(Color c, double brightness) {
        double baseBrightness = calculateBrightness(c);
        double calcBrightness = brightness;
        // Fine adjustments to colors in ranges of brightness to adjust the contrast for them
        if (brightness > 0) {
            if (baseBrightness > 0.85) {
                calcBrightness = calcBrightness * 1.6;
            } else if (baseBrightness > 0.6) {
                // no change
            } else if (baseBrightness > 0.5) {
                calcBrightness = calcBrightness * 0.9;
            } else if (baseBrightness > 0.4) {
                calcBrightness = calcBrightness * 0.8;
            } else if (baseBrightness > 0.3) {
                calcBrightness = calcBrightness * 0.7;
            } else {
                calcBrightness = calcBrightness * 0.6;
            }
        } else {
            if (baseBrightness < 0.2) {
                calcBrightness = calcBrightness * 0.6;
            }
        }
        // clamp brightness
        if (calcBrightness < -1) { calcBrightness = -1; } else if (calcBrightness > 1) {calcBrightness = 1;}
        // window two take the calculated brightness multiplyer and derive color based on source color
        double[] hsb = RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue());
        // change brightness
        if (calcBrightness > 0) { // brighter
            hsb[1] *= 1 - calcBrightness;
            hsb[2] += (1 - hsb[2]) * calcBrightness;
        } else { // darker
            hsb[2] *=  calcBrightness + 1;
        }
        // clip saturation and brightness
        if (hsb[1] < 0) { hsb[1] = 0;} else if (hsb[1] > 1) {hsb[1] = 1;}
        if (hsb[2] < 0) { hsb[2] = 0;} else if (hsb[2] > 1) {hsb[2] = 1;}
        // convert back to color
        Color c2 = Color.hsb((int)hsb[0], hsb[1], hsb[2],c.getOpacity());
        return Color.hsb((int)hsb[0], hsb[1], hsb[2],c.getOpacity());

     /*   var hsb:Number[] = RGBtoHSB(c.red,c.green,c.blue);
        // change brightness
        if (brightness > 0) {
            //var bright:Number = brightness * (1-calculateBrightness(c));
            var bright:Number = if (calculateBrightness(c)<0.65 and brightness > 0.5) {
                    if (calculateBrightness(c)<0.2) then brightness * 0.55 else brightness * 0.7
            } else brightness;
            // brighter
            hsb[1] *= 1 - bright;
            hsb[2] += (1 - hsb[2]) * bright;
        } else {
            // darker
            hsb[2] *= brightness+1;
        }
        // clip saturation and brightness
        if (hsb[1] < 0) { hsb[1] = 0;} else if (hsb[1] > 1) {hsb[1] = 1}
        if (hsb[2] < 0) { hsb[2] = 0;} else if (hsb[2] > 1) {hsb[2] = 1}
        // convert back to color
        return Color.hsb(hsb[0],hsb[1],hsb[2]) */
    }

    /**
     * interpolate at a set {@code position} between two colors {@code color1} and {@code color2}.
     * The interpolation is done is linear RGB color space not the default sRGB color space.
     */
    private static Color interpolateLinear(double position, Color color1, Color color2) {
        Color c1Linear = convertSRGBtoLinearRGB(color1);
        Color c2Linear = convertSRGBtoLinearRGB(color2);
        return convertLinearRGBtoSRGB(Color.color(
            c1Linear.getRed()     + (c2Linear.getRed()     - c1Linear.getRed())     * position,
            c1Linear.getGreen()   + (c2Linear.getGreen()   - c1Linear.getGreen())   * position,
            c1Linear.getBlue()    + (c2Linear.getBlue()    - c1Linear.getBlue())    * position,
            c1Linear.getOpacity() + (c2Linear.getOpacity() - c1Linear.getOpacity()) * position
        ));
    }

    /**
     * Get the color at the give {@code position} in the ladder of color stops
     */
    private static Color ladder(final double position, final Stop[] stops) {
        Stop prevStop = null;
        for (int i=0; i<stops.length; i++) {
            Stop stop = stops[i];
            if(position <= stop.getOffset()){
                if (prevStop == null) {
                    return stop.getColor();
                } else {
                    return interpolateLinear((position-prevStop.getOffset())/(stop.getOffset()-prevStop.getOffset()), prevStop.getColor(), stop.getColor());
                }
            }
            prevStop = stop;
        }
        // position is greater than biggest stop, so will we biggest stop's color
        return prevStop.getColor();
    }

    /**
     * Get the color at the give {@code position} in the ladder of color stops
     */
    public static Color ladder(final Color color, final Stop[] stops) {
        return ladder(calculateBrightness(color), stops);
    }

    public static double[] HSBtoRGB(double hue, double saturation, double brightness) {
        // normalize the hue
        double normalizedHue = ((hue % 360) + 360) % 360;
        hue = normalizedHue/360;

        double r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = brightness;
        } else {
            double h = (hue - Math.floor(hue)) * 6.0;
            double f = h - java.lang.Math.floor(h);
            double p = brightness * (1.0 - saturation);
            double q = brightness * (1.0 - saturation * f);
            double t = brightness * (1.0 - (saturation * (1.0 - f)));
            switch ((int) h) {
                case 0:
                    r = brightness;
                    g = t;
                    b = p;
                    break;
                case 1:
                    r = q;
                    g = brightness;
                    b = p;
                    break;
                case 2:
                    r = p;
                    g = brightness;
                    b = t;
                    break;
                case 3:
                    r = p;
                    g = q;
                    b = brightness;
                    break;
                case 4:
                    r = t;
                    g = p;
                    b = brightness;
                    break;
                case 5:
                    r = brightness;
                    g = p;
                    b = q;
                    break;
            }
        }
        double[] f = new double[3];
        f[0] = r;
        f[1] = g;
        f[2] = b;
        return f;
    }

    public static double[] RGBtoHSB(double r, double g, double b) {
        double hue, saturation, brightness;
        double[] hsbvals = new double[3];
        double cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        double cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = cmax;
        if (cmax != 0)
            saturation = (double) (cmax - cmin) / cmax;
        else
            saturation = 0;

        if (saturation == 0) {
            hue = 0;
        } else {
            double redc = (cmax - r) / (cmax - cmin);
            double greenc = (cmax - g) / (cmax - cmin);
            double bluec = (cmax - b) / (cmax - cmin);
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0 + redc - bluec;
            else
                hue = 4.0 + greenc - redc;
            hue = hue / 6.0;
            if (hue < 0)
                hue = hue + 1.0;
        }
        hsbvals[0] = hue * 360;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    /**
     * Helper function to convert a color in sRGB space to linear RGB space.
     */
    public static Color convertSRGBtoLinearRGB(Color color) {
        double[] colors = new double[] { color.getRed(), color.getGreen(), color.getBlue() };
        for (int i=0; i<colors.length; i++) {
            if (colors[i] <= 0.04045) {
                colors[i] = colors[i] / 12.92;
            } else {
                colors[i] = Math.pow((colors[i] + 0.055) / 1.055, 2.4);
            }
        }
        return Color.color(colors[0], colors[1], colors[2], color.getOpacity());
    }

    /**
     * Helper function to convert a color in linear RGB space to SRGB space.
     */
    public static Color convertLinearRGBtoSRGB(Color color) {
        double[] colors = new double[] { color.getRed(), color.getGreen(), color.getBlue() };
        for (int i=0; i<colors.length; i++) {
            if (colors[i] <= 0.0031308) {
                colors[i] = colors[i] * 12.92;
            } else {
                colors[i] = (1.055 * Math.pow(colors[i], (1.0 / 2.4))) - 0.055;
            }
        }
        return Color.color(colors[0], colors[1], colors[2], color.getOpacity());
    }

    /** helper function for calculating the sum of a series of numbers */
    public static double sum(double[] values) {
        double sum = 0;
        for (double v : values) sum = sum+v;
        return sum / values.length;
    }

    public static Point2D pointRelativeTo(Node parent, Node node, HPos hpos,
            VPos vpos, double dx, double dy, boolean reposition)
    {
        final double nodeWidth = node.getLayoutBounds().getWidth();
        final double nodeHeight = node.getLayoutBounds().getHeight();
        return pointRelativeTo(parent, nodeWidth, nodeHeight, hpos, vpos, dx, dy, reposition);
    }

    public static Point2D pointRelativeTo(Node parent, double anchorWidth,
            double anchorHeight, HPos hpos, VPos vpos, double dx, double dy,
            boolean reposition)
    {
        final Bounds parentBounds = getBounds(parent);
        Scene scene = parent.getScene();
        NodeOrientation orientation = parent.getEffectiveNodeOrientation();

        if (orientation == NodeOrientation.RIGHT_TO_LEFT) {
            if (hpos == HPos.LEFT) {
                hpos = HPos.RIGHT;
            } else if (hpos == HPos.RIGHT) {
                hpos = HPos.LEFT;
            }
            dx *= -1;
        }

        double layoutX = positionX(parentBounds, anchorWidth, hpos) + dx;
        final double layoutY = positionY(parentBounds, anchorHeight, vpos) + dy;

        if (orientation == NodeOrientation.RIGHT_TO_LEFT && hpos == HPos.CENTER) {
            //TODO - testing for an instance of Stage seems wrong but works for menus
            if (scene.getWindow() instanceof Stage) {
                layoutX = layoutX + parentBounds.getWidth() - anchorWidth;
            } else {
                layoutX = layoutX - parentBounds.getWidth() - anchorWidth;
            }
        }

        if (reposition) {
            return pointRelativeTo(parent, anchorWidth, anchorHeight, layoutX, layoutY, hpos, vpos);
        } else {
            return new Point2D(layoutX, layoutY);
        }
    }

    /**
     * This is the fallthrough function that most other functions fall into. It takes
     * care specifically of the repositioning of the item such that it remains onscreen
     * as best it can, given it's unique qualities.
     *
     * As will all other functions, this one returns a Point2D that represents an x,y
     * location that should safely position the item onscreen as best as possible.
     *
     * Note that <code>width</code> and <height> refer to the width and height of the
     * node/popup that is needing to be repositioned, not of the parent.
     *
     * Don't use the BASELINE vpos, it doesn't make sense and would produce wrong result.
     */
    public static Point2D pointRelativeTo(Object parent, double width,
            double height, double screenX, double screenY, HPos hpos, VPos vpos)
    {
        double finalScreenX = screenX;
        double finalScreenY = screenY;
        final Bounds parentBounds = getBounds(parent);

        // ...and then we get the bounds of this screen
        final Screen currentScreen = getScreen(parent);
        final Rectangle2D screenBounds =
                hasFullScreenStage(currentScreen)
                        ? currentScreen.getBounds()
                        : currentScreen.getVisualBounds();

        // test if this layout will force the node to appear outside
        // of the screens bounds. If so, we must reposition the item to a better position.
        // We firstly try to do this intelligently, so as to not overlap the parent if
        // at all possible.
        if (hpos != null) {
            // Firstly we consider going off the right hand side
            if ((finalScreenX + width) > screenBounds.getMaxX()) {
                finalScreenX = positionX(parentBounds, width, getHPosOpposite(hpos, vpos));
            }

            // don't let the node go off to the left of the current screen
            if (finalScreenX < screenBounds.getMinX()) {
                finalScreenX = positionX(parentBounds, width, getHPosOpposite(hpos, vpos));
            }
        }

        if (vpos != null) {
            // don't let the node go off the bottom of the current screen
            if ((finalScreenY + height) > screenBounds.getMaxY()) {
                finalScreenY = positionY(parentBounds, height, getVPosOpposite(hpos,vpos));
            }

            // don't let the node out of the top of the current screen
            if (finalScreenY < screenBounds.getMinY()) {
                finalScreenY = positionY(parentBounds, height, getVPosOpposite(hpos,vpos));
            }
        }

        // --- after all the moving around, we do one last check / rearrange.
        // Unlike the check above, this time we are just fully committed to keeping
        // the item on screen at all costs, regardless of whether or not that results
        /// in overlapping the parent object.
        if ((finalScreenX + width) > screenBounds.getMaxX()) {
            finalScreenX -= (finalScreenX + width - screenBounds.getMaxX());
        }
        if (finalScreenX < screenBounds.getMinX()) {
            finalScreenX = screenBounds.getMinX();
        }
        if ((finalScreenY + height) > screenBounds.getMaxY()) {
            finalScreenY -= (finalScreenY + height - screenBounds.getMaxY());
        }
        if (finalScreenY < screenBounds.getMinY()) {
            finalScreenY = screenBounds.getMinY();
        }

        return new Point2D(finalScreenX, finalScreenY);
    }

    /**
     * Utility function that returns the x-axis position that an object should be positioned at,
     * given the parents screen bounds, the width of the object, and
     * the required HPos.
     */
    private static double positionX(Bounds parentBounds, double width, HPos hpos) {
        if (hpos == HPos.CENTER) {
            // this isn't right, but it is needed for root menus to show properly
            return parentBounds.getMinX();
        } else if (hpos == HPos.RIGHT) {
            return parentBounds.getMaxX();
        } else if (hpos == HPos.LEFT) {
            return parentBounds.getMinX() - width;
        } else {
            return 0;
        }
    }

    /**
     * Utility function that returns the y-axis position that an object should be positioned at,
     * given the parents screen bounds, the height of the object, and
     * the required VPos.
     *
     * The BASELINE vpos doesn't make sense here, 0 is returned for it.
     */
    private static double positionY(Bounds parentBounds, double height, VPos vpos) {
        if (vpos == VPos.BOTTOM) {
            return parentBounds.getMaxY();
        } else if (vpos == VPos.CENTER) {
            return parentBounds.getMinY();
        } else if (vpos == VPos.TOP) {
            return parentBounds.getMinY() - height;
        } else {
            return 0;
        }
    }

    /**
     * To facilitate multiple types of parent object, we unfortunately must allow for
     * Objects to be passed in. This method handles determining the bounds of the
     * given Object. If the Object type is not supported, a default Bounds will be returned.
     */
    private static Bounds getBounds(Object obj) {
        if (obj instanceof Node) {
            final Node n = (Node)obj;
            Bounds b = n.localToScreen(n.getLayoutBounds());
            return b != null ? b : new BoundingBox(0, 0, 0, 0);
        } else if (obj instanceof Window) {
            final Window window = (Window)obj;
            return new BoundingBox(window.getX(), window.getY(), window.getWidth(), window.getHeight());
        } else {
            return new BoundingBox(0, 0, 0, 0);
        }
    }

    /*
     * Simple utitilty function to return the 'opposite' value of a given HPos, taking
     * into account the current VPos value. This is used to try and avoid overlapping.
     */
    private static HPos getHPosOpposite(HPos hpos, VPos vpos) {
        if (vpos == VPos.CENTER) {
            if (hpos == HPos.LEFT){
                return HPos.RIGHT;
            } else if (hpos == HPos.RIGHT){
                return HPos.LEFT;
            } else if (hpos == HPos.CENTER){
                return HPos.CENTER;
            } else {
                // by default center for now
                return HPos.CENTER;
            }
        } else {
            return HPos.CENTER;
        }
    }

    /*
     * Simple utitilty function to return the 'opposite' value of a given VPos, taking
     * into account the current HPos value. This is used to try and avoid overlapping.
     */
    private static VPos getVPosOpposite(HPos hpos, VPos vpos) {
        if (hpos == HPos.CENTER) {
            if (vpos == VPos.BASELINE){
                return VPos.BASELINE;
            } else if (vpos == VPos.BOTTOM){
                return VPos.TOP;
            } else if (vpos == VPos.CENTER){
                return VPos.CENTER;
            } else if (vpos == VPos.TOP){
                return VPos.BOTTOM;
            } else {
                // by default center for now
                return VPos.CENTER;
            }
        } else {
            return VPos.CENTER;
        }
    }

    public static boolean hasFullScreenStage(final Screen screen) {
        final List<Stage> allStages = StageHelper.getStages();

        for (final Stage stage: allStages) {
            if (stage.isFullScreen() && (getScreen(stage) == screen)) {
                return true;
            }
        }

        return false;
    }

    /*
     * Returns true if the primary Screen has QVGA dimensions, in landscape or portrait mode.
     */
    public static boolean isQVGAScreen() {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        return ((bounds.getWidth() == 320 && bounds.getHeight() == 240) ||
                (bounds.getWidth() == 240 && bounds.getHeight() == 320));
    }

    /**
     * This function attempts to determine the best screen given the parent object
     * from which we are wanting to position another item relative to. This is particularly
     * important when we want to keep items from going off screen, and for handling
     * multiple monitor support.
     */
    public static Screen getScreen(Object obj) {
        final Bounds parentBounds = getBounds(obj);

        final Rectangle2D rect = new Rectangle2D(
                parentBounds.getMinX(),
                parentBounds.getMinY(),
                parentBounds.getWidth(),
                parentBounds.getHeight());

        return getScreenForRectangle(rect);
    }

    public static Screen getScreenForRectangle(final Rectangle2D rect) {
        final List<Screen> screens = Screen.getScreens();

        final double rectX0 = rect.getMinX();
        final double rectX1 = rect.getMaxX();
        final double rectY0 = rect.getMinY();
        final double rectY1 = rect.getMaxY();

        Screen selectedScreen;

        selectedScreen = null;
        double maxIntersection = 0;
        for (final Screen screen: screens) {
            final Rectangle2D screenBounds = screen.getBounds();
            final double intersection =
                    getIntersectionLength(rectX0, rectX1,
                                          screenBounds.getMinX(),
                                          screenBounds.getMaxX())
                        * getIntersectionLength(rectY0, rectY1,
                                                screenBounds.getMinY(),
                                                screenBounds.getMaxY());

            if (maxIntersection < intersection) {
                maxIntersection = intersection;
                selectedScreen = screen;
            }
        }

        if (selectedScreen != null) {
            return selectedScreen;
        }

        selectedScreen = Screen.getPrimary();
        double minDistance = Double.MAX_VALUE;
        for (final Screen screen: screens) {
            final Rectangle2D screenBounds = screen.getBounds();
            final double dx = getOuterDistance(rectX0, rectX1,
                                               screenBounds.getMinX(),
                                               screenBounds.getMaxX());
            final double dy = getOuterDistance(rectY0, rectY1,
                                               screenBounds.getMinY(),
                                               screenBounds.getMaxY());
            final double distance = dx * dx + dy * dy;

            if (minDistance > distance) {
                minDistance = distance;
                selectedScreen = screen;
            }
        }

        return selectedScreen;
    }

    public static Screen getScreenForPoint(final double x, final double y) {
        final List<Screen> screens = Screen.getScreens();

        // first check whether the point is inside some screen
        for (final Screen screen: screens) {
            // can't use screen.bounds.contains, because it returns true for
            // the min + width point
            final Rectangle2D screenBounds = screen.getBounds();
            if ((x >= screenBounds.getMinX())
                    && (x < screenBounds.getMaxX())
                    && (y >= screenBounds.getMinY())
                    && (y < screenBounds.getMaxY())) {
                return screen;
            }
        }

        // the point is not inside any screen, find the closest screen now
        Screen selectedScreen = Screen.getPrimary();
        double minDistance = Double.MAX_VALUE;
        for (final Screen screen: screens) {
            final Rectangle2D screenBounds = screen.getBounds();
            final double dx = getOuterDistance(screenBounds.getMinX(),
                                               screenBounds.getMaxX(),
                                               x);
            final double dy = getOuterDistance(screenBounds.getMinY(),
                                               screenBounds.getMaxY(),
                                               y);
            final double distance = dx * dx + dy * dy;
            if (minDistance >= distance) {
                minDistance = distance;
                selectedScreen = screen;
            }
        }

        return selectedScreen;
    }

    private static double getIntersectionLength(
            final double a0, final double a1,
            final double b0, final double b1) {
        // (a0 <= a1) && (b0 <= b1)
        return (a0 <= b0) ? getIntersectionLengthImpl(b0, b1, a1)
                          : getIntersectionLengthImpl(a0, a1, b1);
    }

    private static double getIntersectionLengthImpl(
            final double v0, final double v1, final double v) {
        // (v0 <= v1)
        if (v <= v0) {
            return 0;
        }

        return (v <= v1) ? v - v0 : v1 - v0;
    }

    private static double getOuterDistance(
            final double a0, final double a1,
            final double b0, final double b1) {
        // (a0 <= a1) && (b0 <= b1)
        if (a1 <= b0) {
            return b0 - a1;
        }

        if (b1 <= a0) {
            return b1 - a0;
        }

        return 0;
    }

    private static double getOuterDistance(final double v0,
                                           final double v1,
                                           final double v) {
        // (v0 <= v1)
        if (v <= v0) {
            return v0 - v;
        }

        if (v >= v1) {
            return v - v1;
        }

        return 0;
    }

    /***************************************************************************
     *                                                                         *
     * Miscellaneous utilities                                                 *
     *                                                                         *
     **************************************************************************/

    public static boolean assertionEnabled() {
        boolean assertsEnabled = false;
        assert assertsEnabled = true;  // Intentional side-effect !!!

        return assertsEnabled;
    }

    /**
     * Returns true if the operating system is a form of Windows.
     */
    public static boolean isWindows(){
        return PlatformUtil.isWindows();
    }

    /**
     * Returns true if the operating system is a form of Mac OS.
     */
    public static boolean isMac(){
        return PlatformUtil.isMac();
    }

    /**
     * Returns true if the operating system is a form of Unix, including Linux.
     */
    public static boolean isUnix(){
        return PlatformUtil.isUnix();
    }

    /***************************************************************************
     *                                                                         *
     * Unicode-related utilities                                               *
     *                                                                         *
     **************************************************************************/

    public static String convertUnicode(String src) {
        /** The input buffer, index of next character to be read,
         *  index of one past last character in buffer.
         */
        char[] buf;
        int bp;
        int buflen;

        /** The current character.
         */
        char ch;

        /** The buffer index of the last converted unicode character
         */
        int unicodeConversionBp = -1;
        
        buf = src.toCharArray();
        buflen = buf.length;
        bp = -1;

        char[] dst = new char[buflen];
        int dstIndex = 0;

        while (bp < buflen - 1) {
            ch = buf[++bp];
            if (ch == '\\') {
                if (unicodeConversionBp != bp) {
                    bp++; ch = buf[bp];
                    if (ch == 'u') {
                        do {
                            bp++; ch = buf[bp];
                        } while (ch == 'u');
                        int limit = bp + 3;
                        if (limit < buflen) {
                            char c = ch;
                            int result = Character.digit(c, 16);
                            if (result >= 0 && c > 0x7f) {
                                //lexError(pos+1, "illegal.nonascii.digit");
                                ch = "0123456789abcdef".charAt(result);
                            }
                            int d = result;
                            int code = d;
                            while (bp < limit && d >= 0) {
                                bp++; ch = buf[bp];
                                char c1 = ch;
                                int result1 = Character.digit(c1, 16);
                                if (result1 >= 0 && c1 > 0x7f) {
                                    //lexError(pos+1, "illegal.nonascii.digit");
                                    ch = "0123456789abcdef".charAt(result1);
                                }
                                d = result1;
                                code = (code << 4) + d;
                            }
                            if (d >= 0) {
                                ch = (char)code;
                                unicodeConversionBp = bp;
                            }
                        }
                        //lexError(bp, "illegal.unicode.esc");
                    } else {
                        bp--;
                        ch = '\\';
                    }
                }
            }
            dst[dstIndex++] = ch;
        }
        
        return new String(dst, 0, dstIndex);
    }
}
