package me.primooctopus33.octohack.util;

import java.awt.Color;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import org.lwjgl.opengl.GL11;

public abstract class ColorUtil {
    public static int toARGB(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int HSBtoRGB(float h, float s, float b) {
        return Color.HSBtoRGB(h, s, b);
    }

    public static Color alpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static int GenRainbow() {
        float[] hue = new float[]{(float)(System.currentTimeMillis() % 11520L) / 11520.0f};
        int rgb = Color.HSBtoRGB(hue[0], 1.0f, 1.0f);
        int red = rgb >> 16 & 0xFF;
        int green = rgb >> 8 & 0xFF;
        int blue = rgb & 0xFF;
        int color = ColorUtil.toRGBA(red, green, blue, 255);
        return color;
    }

    public static int staticRainbow(float offset, Color color) {
        double timer = (double)System.currentTimeMillis() % 1750.0 / 850.0;
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = (float)((double)hsb[2] * Math.abs(((double)offset + timer) % 1.0 - (double)0.55f) + (double)0.45f);
        return Color.HSBtoRGB(hsb[0], hsb[1], brightness);
    }

    public static int toRGBA(double r, double g, double b, double a) {
        return ColorUtil.toRGBA((float)r, (float)g, (float)b, (float)a);
    }

    public static int toRGBA(int r, int g, int b) {
        return ColorUtil.toRGBA(r, g, b, 255);
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static int toRGBA(float r, float g, float b, float a) {
        return ColorUtil.toRGBA((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), (int)(a * 255.0f));
    }

    public static void glColor(Color color) {
        GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
    }

    public static int getRainbow(int speed, float s) {
        float hue = System.currentTimeMillis() % (long)speed;
        return Color.getHSBColor(hue / (float)speed, s, 1.0f).getRGB();
    }

    public static Color rainbow(int delay) {
        double rainbowState = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0);
        return Color.getHSBColor((float)((rainbowState %= 360.0) / 360.0), ClickGui.getInstance().rainbowSaturation.getValue().floatValue() / 255.0f, ClickGui.getInstance().rainbowBrightness.getValue().floatValue() / 255.0f);
    }

    public static Color newAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static int toRGBA(float[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return ColorUtil.toRGBA(colors[0], colors[1], colors[2], colors[3]);
    }

    public static int toRGBA(double[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return ColorUtil.toRGBA((float)colors[0], (float)colors[1], (float)colors[2], (float)colors[3]);
    }

    public static int toRGBA(Color color) {
        return ColorUtil.toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static abstract class Colors {
        public static final int WHITE = ColorUtil.toRGBA(255, 255, 255, 155);
        public static final int BLACK = ColorUtil.toRGBA(0, 0, 0, 155);
        public static final int RED = ColorUtil.toRGBA(255, 0, 0, 155);
        public static final int GREEN = ColorUtil.toRGBA(0, 255, 0, 155);
        public static final int BLUE = ColorUtil.toRGBA(0, 0, 255, 155);
        public static final int ORANGE = ColorUtil.toRGBA(255, 128, 0, 100);
        public static final int PURPLE = ColorUtil.toRGBA(105, 13, 173, 100);
        public static final int GRAY = ColorUtil.toRGBA(169, 169, 169, 155);
        public static final int DARK_RED = ColorUtil.toRGBA(64, 0, 0, 155);
        public static final int YELLOW = ColorUtil.toRGBA(255, 255, 0, 155);
        public static final int PINK = ColorUtil.toRGBA(255, 120, 203, 100);
        public static final int RAINBOW = Integer.MIN_VALUE;
    }
}
