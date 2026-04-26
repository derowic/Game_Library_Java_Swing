package pl.sgl.engine.ui;

import java.awt.*;

public class Text extends UIElement {

    public int x, y;
    public Font font;
    public Color color = Color.WHITE;
    public boolean hasShadow = true;

    public Text(String text, int x, int y, int fontSize) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.font = new Font("Arial", Font.PLAIN, fontSize);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setFont(font);

        if (hasShadow) {
            g.setColor(new Color(0, 0, 0, 150)); // Półprzezroczysty czarny
            g.drawString(text, x + 2, y + 2); // Cień przesunięty o 2px
        }

        g.setColor(color);
        g.drawString(text, x, y);
    }
}
