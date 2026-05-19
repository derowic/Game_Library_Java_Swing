package pl.sgl.engine;

import pl.sgl.engine.texture.Texture;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class GameObject {
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
    public boolean visible = true;
    protected double pivotX = Double.NaN;
    protected double pivotY = Double.NaN;
    public Texture texture;
    public boolean active = true; // Flaga określająca stan obiektu
    public Rectangle hitbox = new Rectangle();

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
    }

    public GameObject(GameObject go) {
        this.x = go.x;
        this.y = go.y;
        this.lastX = go.lastX;
        this.lastY = go.lastY;
        this.rotation = go.rotation;
        this.didTeleport = go.didTeleport;
        this.velocityX = go.velocityX;
        this.velocityY = go.velocityY;
        this.scaleX = go.scaleX;
        this.scaleY = go.scaleY;
        this.width =go.width;
        this.height = go.height;
        this.showHitBox = go.showHitBox;
        this.visible = go.visible;
        this.pivotX = go.pivotX;
        this.pivotY = go.pivotY;
        this.texture = go.texture;
        this.active = go.active;
        this.hitbox = go.hitbox;
    }


    public GameObject getCopy () {
        return new GameObject(this);
    }

    public void update(double deltaTime) {}

    public void draw(Graphics2D g, double alpha) {
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

        // 2. Tworzymy izolowaną kopię Graphics2D
        Graphics2D g2d = (Graphics2D) g.create();

        // 3. Obliczamy wymiary po skalowaniu
        int fW = (int)(width);
        int fH = (int)(height);

        // 4. Wyznaczamy punkt obrotu (Pivot) - identycznie jak w getRotatedShape

        // 1. Wyznaczamy pivot (współrzędne lokalne obrazka)
        double pX = Double.isNaN(pivotX) ? width / 2.0 : pivotX;
        double pY = Double.isNaN(pivotY) ? height / 2.0 : pivotY;

        g2d.translate(drawX, drawY);
        if (rotation != 0) {
            g2d.rotate(Math.toRadians(rotation));
        }
//        if (rotation != 0) {
//            g2d.rotate(Math.toRadians(rotation)); // B. Obróć wokół (0,0), czyli wokół pozycji dX, dY
//        }
//        g2d.scale(scaleX, scaleY);       // C. Skaluj wokół (0,0)

        // 5. TRANSFORMACJE (Kolejność: Translate -> Rotate)
//        if (Double.isNaN(pivotX) || Double.isNaN(pivotY)) {
//            g2d.translate(width / 2, height / 2);
        g2d.scale(scaleX, scaleY);
//            g2d.translate(-width / 2, -height / 2);
//        } else {
//            g2d.translate(pX, pY);
//            g2d.scale(scaleX, scaleY);
//            g2d.translate(-pX, -pY);
//        }

        // 4. SKALOWANIE - wokół pivotu
        // Aby skalować względem środka, a nie lewego górnego rogu:
        // Przesuwamy do pivotu, skalujemy, wracamy.

        // 6. RYSOWANIE OBRAZKA (od 0,0 bo g2d jest już przesunięte)
        g2d.drawImage(texture.image, (int)-pX, (int)-pY, width, height, null);

        // W metodzie draw (na samym końcu przed g2d.dispose)
        g2d.setColor(Color.YELLOW);
        g2d.fillRect((int)-5, (int)(height/2), 10, 10); // To narysuje kropkę tam, gdzie sensor szuka ziemi
        g2d.dispose();
        // 7. RYSOWANIE HITBOXA (Lokalnie!)
        if (showHitBox) {
//            System.out.println("render");
            g2d = (Graphics2D) g.create();
            Shape collisionShape = getRotatedShape();

            // Rysujemy kształt na worldG
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(1.0f)); // Cienka linia
            g2d.draw(collisionShape); // To narysuje obramowanie

            // Opcjonalnie: półprzezroczyste wypełnienie
            g2d.setColor(new Color(255, 0, 0, 50));
            g2d.fill(collisionShape);
            g2d.dispose();
        }
    }

    public void scale() {

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

        // 3. Obliczenie Pivotu (identycznie jak w draw)
        double pX = Double.isNaN(pivotX) ? width / 2.0 : pivotX;
        double pY = Double.isNaN(pivotY) ? height / 2.0 : pivotY;

        // 1. Przesunięcie do świata
        at.translate(drawX, drawY);

        // 4. Obrót
        if (rotation != 0) {
            at.rotate(Math.toRadians(rotation));
        }

//        if (Double.isNaN(pivotX) || Double.isNaN(pivotY)) {
//            at.translate(width / 2, height / 2);
        at.scale(scaleX, scaleY);
//            at.translate(-width / 2, -height / 2);
//        } else {
//            at.translate(pX, pY);
//            at.scale(scaleX, scaleY);
//            at.translate(-pX, -pY);
//        }

        // 5. Tworzymy lokalny przeskalowany prostokąt
        Rectangle2D.Double scaledLocalRect = new Rectangle2D.Double(
                hitbox.x + (int)-pX,
                hitbox.y + (int)-pY,
                hitbox.width ,
                hitbox.height
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
//        hitbox.setSize(hitbox.width, (int) (hitbox.height * scaleY));
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
