package com.delta.depend.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * The {@code VCodeGenerator} class create random verify code image
 *
 * @author Jim Zhang
 * @since Delta1.0
 */
@SuppressWarnings("ALL")
public final class VCodeGenerator {
    private VCodeGenerator() {
    }

    /**
     * Generate random string by given length.
     * ('O' and 'I' are replaced by '0' and '1')
     *
     * @param length length of verify code
     * @return {@Code String}
     */
    public static String generateRandomString(int length) {
        StringBuilder sbd = new StringBuilder();
        Random ran = new Random();
        for (int i = 0; i < length; i++) {
            int ranDigit = ran.nextInt(36);
            if (ranDigit >= 10) {
                sbd.append((char) (ranDigit + 55));
            } else {
                sbd.append((char) (ranDigit + 48));
            }
        }
        return sbd.toString().replaceAll("O", "0").replaceAll("I", "1");
    }

    /**
     * Generate verify code image by given string.
     *
     * @param str length of verify code
     * @return {@Code BufferedImage}
     */
    public static BufferedImage getVerifyCodeImage(String str) {

        final int WIDTH = 21 * str.length();
        final int HEIGHT = 35;
        final int FONT_SIZE = 28;

        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        Random random = new Random();

        if (random.nextInt(10) < 5) {
            int xs, ys, xe, ye;
            if (random.nextInt(2) == 0) {
                for (int i = 0; i < 32; i++) {
                    g.setColor(new Color(0x666666));
                    xs = 0;
                    xe = WIDTH / 32 * i * 2;
                    ye = 0;
                    ys = WIDTH / 32 * i * 2;
                    g.drawLine(xs, ys, xe, ye);
                }
            } else {
                for (int i = 0; i < 32; i++) {
                    g.setColor(new Color(0xAAAAAA));
                    ys = (int) (WIDTH / 32.0) * i;
                    ye = (int) (WIDTH / 32.0) * i;
                    xs = 0;
                    xe = WIDTH;
                    g.drawLine(xs, ys, xe, ye);
                }
            }
        } else {
            float yawpRate = 0.04f;
            int area = (int) (yawpRate * WIDTH * HEIGHT);
            for (int i = 0; i < area; i++) {
                int x = random.nextInt(WIDTH);
                int y = random.nextInt(HEIGHT);
                img.setRGB(x, y, 0x999999);
            }
        }

        for (int i = 0; str.length() > i; i++) {
            switch (random.nextInt(3)) {
                case 0:
                    g.setFont(new Font("Microsoft YaHei", Font.ITALIC, FONT_SIZE));
                    break;
                case 1:
                    g.setFont(new Font("Microsoft YaHei", Font.PLAIN, FONT_SIZE));
                    break;
                case 2:
                    g.setFont(new Font("Microsoft YaHei", Font.BOLD, FONT_SIZE));
                    break;
            }
            g.setColor(generateRandomColor());
            g.drawString("" + str.charAt(i), i * (FONT_SIZE / 2 + 3) + random.nextInt(10), FONT_SIZE);
        }

        return img;
    }

    /**
     * color random rules custom by user.
     *
     * @return random color sequence
     */
    private static Color generateRandomColor() {
        Random random = new Random();
        int grey = random.nextInt(77);
        return new Color(33 + grey, 33 + grey, 33 + grey);
    }
}
