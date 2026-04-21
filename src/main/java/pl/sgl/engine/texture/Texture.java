package pl.sgl.engine.texture;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Texture {
    public BufferedImage image = null;
    public Rectangle rectangle;

    public Texture(String path) {
        image = TextureLoader.load(path);
    }

    public Rectangle getHitBox(){
        if (rectangle != null) {
            return rectangle;
        }
        if(image != null) {
            rectangle = TextureLoader.getTightHitbox(image);
        } else {
            System.out.println("No texture to calculate hitboxes");
        }

        return new Rectangle(0,0,0,0);
    }
}
