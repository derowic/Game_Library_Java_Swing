package pl.sgl.engine;

import java.awt.image.BufferedImage;

public class Sprite {
    public final BufferedImage image;
    public final float x, y, lastX, lastY;
    public final int width, height;
    public final float rotation; // Dodajmy rotację, sprite'y często jej potrzebują
    public boolean didTeleport = false;

    public Sprite(BufferedImage image, float x, float y, float lastX, float lastY, int w, int h, float rotation) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.lastX = lastX;
        this.lastY = lastY;
        this.width = w;
        this.height = h;
        this.rotation = rotation;
    }
}