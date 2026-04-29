package pl.sgl.engine.ui;

import pl.sgl.engine.InputHandler;
import pl.sgl.engine.MouseHandler;

import java.awt.*;

public class UIElement {
    public Rectangle bounds;
    public String text;
    public boolean isHovered = false;
    public boolean hasFocus = false;

    public void update(MouseHandler mouse) {

    }
    public void update(InputHandler input, MouseHandler mouse){}
    public void draw(Graphics2D g) {}
    public boolean isClicked(MouseHandler mouse) { return false; }
    // Zwraca true, jeśli myszka jest nad tym elementem
    public boolean isMouseOver(MouseHandler mouse) {

        return bounds.contains(mouse.getUIX(), mouse.getUIY());
    }
}
