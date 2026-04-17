package pl.sgl.engine;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GameObject {
    public double x = 0;
    public double y = 0;
    public double lastX = 0;
    public double lastY = 0;
    public double rotation = 0; // Dodajmy rotację, sprite'y często jej potrzebują
    public boolean didTeleport = false;
    double velocityX = 0.0;
    double velocityY = 0.0;
    double scaleX = 1.0;
    double scaleY= 1.0;
    protected int width = 0;
    protected int height =0;
    public BufferedImage image;

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
//        info();
    }

    public void update(double deltaTime) {}

    public void draw(Graphics2D g2d, double alpha) {
        double drawX;
        double drawY;
        if (didTeleport) {
//            renderX = (float) renderState.x;
            // Interpolacja pozycji
            drawX = x;
            drawY = y;
        } else {
            drawX = lastX + (x - lastX) * (float) alpha;
            drawY = lastY + (y - lastY) * (float) alpha;
        }
        g2d.translate(drawX, drawY);
    }

    public void scale() {

    }
}
