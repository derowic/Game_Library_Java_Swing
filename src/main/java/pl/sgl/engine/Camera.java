package pl.sgl.engine;

import pl.sgl.engine.GameTest.GameScene;
import pl.sgl.engine.math.Vector2D;

public class Camera {
    public double x=0;
    public double lastX=0;
    public double y=0;
    public double lastY=0;
    public double zoom = 1.0;
    public double lastZoom = 1.0;
    public Vector2D velocity = new Vector2D(0,0);
    private GameObject followedObject;

    // Ta metoda musi być wywołana RAZ na początku Engine.update()
    public void prepareForUpdate() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZoom = this.zoom;
    }

    // Metody modyfikujące NIE mogą dotykać lastX/lastY
    public void update(double deltaTime) {
        this.x += (velocity.x * deltaTime);
        this.y += (velocity.y * deltaTime);

        if(followedObject != null) {
            followObject(deltaTime);
        }
    }

    public void followObject(double deltaTime) {

        // 1. Zapisujemy stary stan pod interpolację (Kluczowe!)
        double lastY = y;

        // 2. Wyznaczamy cel (środek ekranu)
        // Gracz.y - (Wysokość okna / 2)
        double targetY = followedObject.y - (ConfigureData.oldHeight / 2.0);

        // 3. Wygładzanie ruchu (Lerp)
        // Zamiast cam.y = targetY (sztywne przypięcie), robimy płynne dążenie
        double lerpFactor = 5.0; // Prędkość śledzenia
        y += (targetY - y) * lerpFactor * deltaTime;
    }

    public void setPose(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public void zoomIn(double z) {

        this.zoom += z;
        if(this.zoom <= 0.1) {
            this.zoom = 0.1;
        }
    }

    public void zoomOut(double z) {

        this.zoom += z;
        if(this.zoom <= 0.1) {
            this.zoom = 0.1;
        }
    }

    public GameObject getFollowedObject() {
        return followedObject;
    }

    public void setFollowedObject(GameObject followedObject) {
        this.followedObject = followedObject;
    }
}
