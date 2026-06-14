package pl.sgl.engine;

import pl.sgl.engine.ui.UIManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    protected UIManager ui = new UIManager();
    protected List<GameObject> objects = new ArrayList<>();
    protected Camera cam = new Camera();
    public double deltaTime;

    public Scene() {
    }

    public Camera getCam() {
        return cam;
    }

    public void setCam(Camera cam) {
        this.cam = cam;
    }

    public void init() {

    }
    public void update(double dt) {
        deltaTime = dt;
    }
    public void renderUI(Graphics2D g) {

    }

    public UIManager getUi() { return ui; }
    public List<GameObject> getObjects() { return objects; }

    public void addGameObject(GameObject go) {
        objects.add(go);
    }
}