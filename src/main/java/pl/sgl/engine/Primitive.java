package pl.sgl.engine;

import java.awt.*;

public class Primitive {
    public final float x, y, lastX, lastY;
    public final int width, height;
    public final Color color;
    public final String type; // np. "RECT", "CIRCLE"

    public Primitive(float x, float y, float lastX, float lastY, int w, int h, Color c, String type) {
        this.x = x; this.y = y;
        this.lastX = lastX; this.lastY = lastY;
        this.width = w; this.height = h;
        this.color = c; this.type = type;
    }
}
