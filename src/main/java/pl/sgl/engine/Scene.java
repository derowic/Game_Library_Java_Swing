package pl.sgl.engine;

import pl.sgl.engine.ui.UIManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected UIManager ui = new UIManager();
    protected List<GameObject> objects = new ArrayList<>();

    public abstract void init(); // Wywoływane raz przy wejściu do sceny
    public abstract void update(double dt);
    public abstract void renderUI(Graphics2D g);

    public UIManager getUi() { return ui; }
    public List<GameObject> getObjects() { return objects; }
}