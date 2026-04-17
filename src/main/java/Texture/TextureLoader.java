package Texture;

import pl.sgl.engine.Animation.Animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextureLoader {
    private static Map<String, BufferedImage> textures = new HashMap<>();
    private static Map<String, BufferedImage[]> animations = new HashMap<>();

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

    public static BufferedImage[] loadOneAnimation(String path,int x, int y, int width, int height, int framesCount) {
        if (animations.containsKey(path)) return animations.get(path);

        BufferedImage sheet = load(path);

        if (framesCount == 0) {
            framesCount = sheet.getWidth() / width;
        }

        BufferedImage[] frames = new BufferedImage[framesCount];

        System.out.println("frames count: " + framesCount);

        for (int i = 0; i < framesCount; i++) {
            frames[i] = sheet.getSubimage(x + i * width, y, width, height);
        }

        animations.put(path, frames);

        return frames;
    }

    public static Rectangle getTightHitbox(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        int minX = width, minY = height, maxX = -1, maxY = -1;
        boolean foundAlpha = false;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Pobierz kolor piksela (ARGB)
                int argb = img.getRGB(x, y);
                // Wyciągnij kanał Alpha (najstarsze 8 bitów)
                int alpha = (argb >> 24) & 0xff;

                // Jeśli piksel nie jest w pełni przezroczysty (np. alpha > 10)
                if (alpha > 10) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                    foundAlpha = true;
                }
            }
        }

        if (!foundAlpha) return new Rectangle(0, 0, width, height);

        // Zwraca prostokąt relatywny do obrazka (0,0 to lewy górny róg grafiki)
        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
}