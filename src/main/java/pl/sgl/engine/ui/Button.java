package pl.sgl.engine.ui;

import pl.sgl.engine.MouseHandler;

import java.awt.*;

public class Button extends UIElement {

    public String text;
    public Font font;
    public Color normalColor = Color.GRAY;
    public Color hoverColor = Color.LIGHT_GRAY;
    public Color textColor = Color.WHITE;

    public Button(String text, int x, int y, int width, int height, Font font, Color normalColor, Color hoverColor, Color textColor) {
        this.text = text;
        this.bounds = new Rectangle(x, y, width, height);
        this.font = font;
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
    }

    public Button(String text, int x, int y, int width, int height) {
        this.text = text;
        this.bounds = new Rectangle(x, y, width, height);
        this.font = new Font("Arial", Font.BOLD, 16);
    }

    @Override
    //when mouse pointer is on the button isHovered = true
    public void update(MouseHandler mouse) {
        isHovered = bounds.contains(mouse.getX(), mouse.getY());
    }

    @Override
    //when mouse click method check if this button is clicked
    public boolean isClicked(MouseHandler mouse) {
        return isHovered && mouse.isButtonDown(1);
    }

    @Override
    public void draw(Graphics2D g) {
        // draw diffrent background-color if button is hovered or not
        g.setColor(isHovered ? hoverColor : normalColor);
        g.fill(bounds);

        // draw border
        g.setColor(Color.BLUE);
        g.draw(bounds);

        // draw centred text
        g.setFont(font);
        g.setColor(textColor);

        FontMetrics fm = g.getFontMetrics(font);
        // calc position X (center of the button - half of the text length)
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        //calc position y (center of the button - half of the text height)
        int textY = bounds.y + ((bounds.height - fm.getHeight()) / 2) + fm.getAscent();

        g.drawString(text, textX, textY);
    }
}
