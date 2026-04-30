package pl.sgl.engine;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public abstract class GameObject {
    public double x = 0;
    public double y = 0;
    public double lastX = 0;
    public double lastY = 0;
    public double rotation = 0; // Dodajmy rotację, sprite'y często jej potrzebują
    public boolean didTeleport = false;
    public double velocityX = 0.0;
    public double velocityY = 0.0;
    protected double scaleX = 1.0;
    protected double scaleY= 1.0;
    protected int width = 0;
    protected int height =0;
    public boolean showHitBox = false;
    protected double drawX;
    protected double drawY;
    protected boolean visible = true;
    protected double pivotX = Double.NaN;
    protected double pivotY = Double.NaN;

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
//        info();
    }

    public void update(double deltaTime) {}

    public void draw(Graphics2D g2d, double alpha) {
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

    // WERSJA DLA LOGIKI (używa surowych x, y)
    public Shape getRotatedShape() {
        return getRotatedShape((float)this.x, (float)this.y);
    }

    // WERSJA UNIWERSALNA
    public Shape getRotatedShape(float drawX, float drawY) {
        AffineTransform at = new AffineTransform();

        // 1. Przesunięcie do świata
        at.translate(drawX, drawY);

        // 2. Skalowanie wymiarów do obliczenia pivotu
        double fW = width ;
        double fH = height;

        // 3. Obliczenie Pivotu (identycznie jak w draw)
        double pX, pY;
        if (Double.isNaN(pivotX) || Double.isNaN(pivotY)) {
            pX = fW / 2.0;
            pY = fH / 2.0;
        } else {
            pX = pivotX ;
            pY = pivotY ;
        }

        // 4. Obrót
        if (rotation != 0) {
            at.rotate(Math.toRadians(rotation), pX, pY);
        }

        at.translate(width/2, height/2);
        at.scale(scaleX, scaleY);
        at.translate(-width/2, -height/2);

        // 5. Tworzymy lokalny przeskalowany prostokąt
        Rectangle rec = getCalculatedAutoHitBoxes();
        Rectangle2D.Double scaledLocalRect = new Rectangle2D.Double(
                rec.x ,
                rec.y ,
                rec.width ,
                rec.height
        );

        return at.createTransformedShape(scaledLocalRect);
    }

    public boolean checkCollision(Sprite s) {
        Area area1 = new Area(getRotatedShape());
        Area area2 = new Area(s.getRotatedShape());

        // Oblicz część wspólną obu kształtów
        area1.intersect(area2);

        // Jeśli część wspólna nie jest pusta -> mamy kolizję!
        return !area1.isEmpty();
    }

//    public Shape getRotatedShape(float drawX, float drawY) {
//        // 1. Pobieramy Tight Hitbox (współrzędne lokalne względem obrazka)
//        Rectangle rec = getCalculatedAutoHitBoxes();
//
//        // 2. Tworzymy transformację
//        AffineTransform at = new AffineTransform();
//
//        // 3. Przesuwamy do zinterpolowanej pozycji na ekranie
//        at.translate(drawX, drawY);
//
//        // 4. Obracamy wokół ŚRODKA OBRAZKA (nie środka hitboxa!)
//        // Bardzo ważne: Punkt obrotu musi być IDENTYCZNY jak w metodzie draw()
//        // Zazwyczaj jest to środek całej grafiki (width/2, height/2)
//        at.rotate(Math.toRadians(rotation), this.width / 2.0, this.height / 2.0);
//
//        // 5. Tworzymy bazowy prostokąt hitboxa w jego lokalnych współrzędnych
//        // Używamy rec.x i rec.y, bo tight hitbox może być przesunięty względem (0,0)
//        Rectangle2D baseHitbox = new Rectangle2D.Double(rec.x, rec.y, rec.width, rec.height);
//
//        // 6. Zwracamy przetransformowany kształt
//        return at.createTransformedShape(baseHitbox);
//    }

    public void hide () {
        visible = false;
    }

    public void show () {
        visible = true;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    public void setPivot(double x, double y) {
        this.pivotX = x;
        this.pivotY = y;
    }

    public void resetPivotToCenter() {
        this.pivotX = Double.NaN;
        this.pivotY = Double.NaN;
    }


//    public abstract void getCalculateAutoHitBoxes();
}
