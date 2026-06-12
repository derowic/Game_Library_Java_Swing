package pl.sgl.engine.ui;

import pl.sgl.engine.InputHandler;
import pl.sgl.engine.MouseHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UIPanel extends UIElement {
    private List<UIElement> children = new ArrayList<>();
    public boolean visible = true;

    public UIPanel(int x, int y, int w, int h) {
        this.bounds = new Rectangle(x, y, w, h);
    }

    public void addElement(UIElement e) { children.add(e); }

    @Override
    public void update(InputHandler input, MouseHandler mouse) {
        if (!visible) return;
        for (UIElement e : children) e.update(input, mouse);
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        // Opcjonalnie: g.fillRect(...) - tło panelu
        for (UIElement e : children) e.draw(g);
    }
}