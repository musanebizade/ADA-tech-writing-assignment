package ada.slicing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Usage: Main &lt;input image&gt; &lt;rows start:stop[:step]&gt; &lt;cols start:stop[:step]&gt; &lt;output.png&gt;
 * Example: Main input.png 100:400 ::2 out.png
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: Main <input image> <rows> <cols> <output.png>");
            System.exit(1);
        }
        try {
            BufferedImage image = ImageIO.read(new File(args[0]));
            if (image == null) {
                throw new IOException("unsupported or unreadable image: " + args[0]);
            }
            int[][] pixels = toMatrix(image);
            int[][] result = Slicer.slice(pixels, args[1], args[2]);
            ImageIO.write(toImage(result), "png", new File(args[3]));
            System.out.printf("java: %dx%d -> %dx%d, saved %s%n",
                    pixels.length, pixels[0].length, result.length, result[0].length, args[3]);
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /** ARGB pixels as a [height][width] matrix. */
    private static int[][] toMatrix(BufferedImage image) {
        int[][] m = new int[image.getHeight()][image.getWidth()];
        for (int y = 0; y < m.length; y++) {
            image.getRGB(0, y, m[y].length, 1, m[y], 0, m[y].length);
        }
        return m;
    }

    private static BufferedImage toImage(int[][] m) {
        BufferedImage image = new BufferedImage(m[0].length, m.length, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < m.length; y++) {
            image.setRGB(0, y, m[y].length, 1, m[y], 0, m[y].length);
        }
        return image;
    }
}
