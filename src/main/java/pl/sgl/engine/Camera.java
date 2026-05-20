package pl.sgl.engine;

import pl.sgl.engine.math.Vector2D;

public class Camera {
    public double x=0;
    public double lastX=0;
    public double y=0;
    public double lastY=0;
    public double zoom = 1.0;
    public double lastZoom = 1.0;
    public Vector2D velocity = new Vector2D(0,0);

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
    }

    public void setPose(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public void addZoom(double z) {
        this.zoom += z;
    }
}
