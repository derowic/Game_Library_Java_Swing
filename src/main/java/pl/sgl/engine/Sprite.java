package pl.sgl.engine;

import java.awt.image.BufferedImage;

public class Sprite {
    public BufferedImage image;
    public double x;
    public double y;
    public double lastX;
    public double lastY;
    public int width, height;
    public double rotation; // Dodajmy rotację, sprite'y często jej potrzebują
    public boolean didTeleport = false;
    double velocityX = 0.0;
    double velocityY = 0.0;

    public Sprite(BufferedImage image, float x, float y) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
        this.width = image.getWidth();
        this.height = image.getHeight();
        info();

    }

    public void info()
    {
        System.out.println("width: " +width);
        System.out.println("height: " +height);
    }

    public void update(double deltaTime)
    {
        this.lastX = this.x;
        this.x += (velocityX * deltaTime);

        this.lastY = this.y;
        this.y += (velocityY * deltaTime);

    }
}