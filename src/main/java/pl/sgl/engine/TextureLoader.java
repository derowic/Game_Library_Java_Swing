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
}