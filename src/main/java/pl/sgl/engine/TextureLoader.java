package pl.sgl.engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextureLoader {
    private static Map<String, BufferedImage> textures = new HashMap<>();

    public static BufferedImage load(String path) {
        if (textures.containsKey(path)) return textures.get(path);

        try {
            BufferedImage img = ImageIO.read(TextureLoader.class.getResourceAsStream(path));
            textures.put(path, img);
            return img;
        } catch (IOException | NullPointerException e) {
            System.err.println("Nie udało się załadować tekstury: " + path);
            return null;
        }
    }

    public static BufferedImage[] loadSheet(String path, int width, int height) {
        BufferedImage sheet = load(path);
        int cols = sheet.getWidth() / width;
        int rows = sheet.getHeight() / height;
        BufferedImage[] frames = new BufferedImage[cols * rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                frames[i * cols + j] = sheet.getSubimage(j * width, i * height, width, height);
            }
        }
        return frames;
    }
}