package pl.sgl.engine.ui;

import pl.sgl.engine.MouseHandler;

import java.awt.*;

public class Slider extends UIElement {
    public float value = 0.5f; // 0.0 do 1.0
    private boolean dragging = false;

    public Slider(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    @Override
    public void update(MouseHandler mouse) {
        if (mouse.isButtonDown(1) && bounds.contains(mouse.getX(), mouse.getY())) {
            dragging = true;
        }
        if (!mouse.isButtonDown(1)) dragging = false;

        if (dragging) {
            // Obliczamy wartość na podstawie pozycji myszy względem paska
            float mouseRelX = mouse.getX() - bounds.x;
            value = mouseRelX / (float)bounds.width;

            // Ograniczenie 0-1
            if (value < 0) value = 0;
            if (value > 1) value = 1;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.fillRect(bounds.x, bounds.y + bounds.height/2 - 2, bounds.width, 4); // Linia tła

        g.setColor(Color.WHITE);
        int handleX = bounds.x + (int)(value * bounds.width) - 5;
        g.fillRect(handleX, bounds.y, 10, bounds.height); // Uchwyt
    }
}
