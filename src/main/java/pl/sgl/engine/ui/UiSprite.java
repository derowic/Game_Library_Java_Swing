package pl.sgl.engine.ui;

import pl.sgl.engine.texture.Texture;

import java.awt.*;

public class UiSprite extends UIElement {
    public double x = 0;
    public double y = 0;
    public double rotation = 0; // Dodajmy rotację, sprite'y często jej potrzebują
    public double scaleX = 1.0;
    protected double scaleY= 1.0;
    public int frameWidth = 0;
    public int frameHeight = 0;
    public double pivotX = 0;
    public double pivotY = 0;
    public Texture texture;
    protected int srcX, srcY;
    protected int srcW, srcH;

    public void draw(Graphics2D g, double alpha) {
        double drawX = x;
        double drawY = y;

        // 2. Tworzymy izolowaną kopię Graphics2D
        Graphics2D g2d = (Graphics2D) g.create();

        // 1. Wyznaczamy pivot (współrzędne lokalne obrazka)
        double pX = Double.isNaN(pivotX) ? frameWidth / 2.0 : pivotX;
        double pY = Double.isNaN(pivotY) ? frameHeight / 2.0 : pivotY;

        g2d.translate(drawX, drawY);
        if (rotation != 0) {
            g2d.rotate(Math.toRadians(rotation));
        }

        g2d.scale(scaleX, scaleY);

        // 6. RYSOWANIE OBRAZKA (od 0,0 bo g2d jest już przesunięte)
//        g2d.drawImage(texture.image, (int)-pX, (int)-pY, width, height, null);
//        g2d.drawImage(texture.image,
//                (int)-pX, (int)-pY, (int)(-pX + width), (int)(-pY + height),
//                srcX, srcY, srcX + srcW, srcY + srcH,
//                null
//        );
        g2d.drawImage(texture.image, (int) -pX, (int) -pY, (int) (-pX + srcW), (int) (-pY + srcH),
                srcX, srcY, srcX + srcW, srcY + srcH, null);


//        // 7. RYSOWANIE HITBOXA (Lokalnie!)
//        if (showHitBox) {
////            System.out.println("render");
//            g2d = (Graphics2D) g.create();
//            Shape collisionShape = getRotatedShape();
//
//            // Rysujemy kształt na worldG
//            g2d.setColor(Color.RED);
//            g2d.setStroke(new BasicStroke(1.0f)); // Cienka linia
//            g2d.draw(collisionShape); // To narysuje obramowanie
//
//            // Opcjonalnie: półprzezroczyste wypełnienie
//            g2d.setColor(new Color(255, 0, 0, 50));
//            g2d.fill(collisionShape);
//            g2d.dispose();
//
//
//        }
    }
}
