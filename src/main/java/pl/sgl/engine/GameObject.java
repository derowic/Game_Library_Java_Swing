package pl.sgl.engine;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

public abstract class GameObject {
    public double x = 0;
    public double y = 0;
    public double lastX = 0;
    public double lastY = 0;
    public double rotation = 0; // Dodajmy rotację, sprite'y często jej potrzebują
    public boolean didTeleport = false;
    public double velocityX = 0.0;
    public double velocityY = 0.0;
    public double scaleX = 1.0;
    public double scaleY= 1.0;
    protected int width = 0;
    protected int height =0;
    public boolean showHitBox = false;

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
//        info();
    }

    public void update(double deltaTime) {}

    public void draw(Graphics2D g2d, double alpha) {
        double drawX;
        double drawY;
        if (didTeleport) {
//            renderX = (float) renderState.x;
            // Interpolacja pozycji
            drawX = x;
            drawY = y;
        } else {
            drawX = lastX + (x - lastX) * (float) alpha;
            drawY = lastY + (y - lastY) * (float) alpha;
        }
        g2d.translate(drawX, drawY);
    }

    public void scale() {

    }

    public Rectangle getCalculatedAutoHitBoxes() {
        return new Rectangle(0,0,0,0);
    }

    public Rectangle getRotatedBounds() {
        double rad = Math.toRadians(rotation);
        double sin = Math.abs(Math.sin(rad));
        double cos = Math.abs(Math.cos(rad));

        // Nowa szerokość i wysokość prostokąta, który pomieści obrócony kształt
        int newW = (int) Math.floor(width * cos + height * sin);
        int newH = (int) Math.floor(width * sin + height * cos);

        // Wyśrodkowanie (zakładając, że obracasz wokół środka)
        int newX = (int) ((width - newW) / 2.0);
        int newY = (int) ((height - newH) / 2.0);

        return new Rectangle(newX, newY, newW, newH);
    }

    public Shape getRotatedShape() {
        Rectangle rec = getCalculatedAutoHitBoxes();
        // 1. Tworzymy transformację
        AffineTransform at = new AffineTransform();

        // 2. Przesuwamy do środka obiektu (żeby obracać wokół centrum)
        double centerX = x + rec.x + rec.width / 2.0;
        double centerY = y + rec.y + rec.height / 2.0;
        at.translate(centerX, centerY);

        // 3. Obracamy o zadany kąt
        at.rotate(Math.toRadians(rotation));

        // 4. Przesuwamy z powrotem, aby (0,0) było w rogu prostokąta
        at.translate(-rec.width / 2.0, -rec.height / 2.0);

        // 5. Tworzymy bazowy prostokąt (bez rotacji)
        Rectangle baseRect = new Rectangle(0, 0, rec.width, rec.height);

        // 6. Zwracamy OBRÓCONY kształt (jako Path2D)
        return at.createTransformedShape(baseRect);
    }

    public boolean checkCollision(Sprite s) {
        Area area1 = new Area(getRotatedShape());
        Area area2 = new Area(s.getRotatedShape());

        // Oblicz część wspólną obu kształtów
        area1.intersect(area2);

        // Jeśli część wspólna nie jest pusta -> mamy kolizję!
        return !area1.isEmpty();
    }

//    public abstract void getCalculateAutoHitBoxes();
}
