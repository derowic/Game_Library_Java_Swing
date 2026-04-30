package pl.sgl.engine;

import pl.sgl.engine.texture.Texture;

import java.awt.*;

public class Sprite extends GameObject {

    public Texture texture;
    public Sprite(String pathToTexture, float x, float y) {
        super(x,y);
        texture = new Texture(pathToTexture);
        width = texture.image.getWidth();
        height = texture.image.getHeight();
        getCalculatedAutoHitBoxes();
//        info();
    }

    public void info()
    {
        System.out.println("width: " +width);
        System.out.println("height: " +height);
    }

    @Override
    public void update(double deltaTime)
    {
        this.lastX = this.x;
        this.x += (velocityX * deltaTime);

        this.lastY = this.y;
        this.y += (velocityY * deltaTime);

    }

    @Override
    public void draw(Graphics2D g, double alpha) {
        // 1. Obliczamy interpolację
        float dX = (float) (lastX + (x - lastX) * alpha);
        float dY = (float) (lastY + (y - lastY) * alpha);

        // 2. Tworzymy izolowaną kopię Graphics2D
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(dX, dY);
        // 3. Obliczamy wymiary po skalowaniu
        int fW = (int)(width);
        int fH = (int)(height);

        // 4. Wyznaczamy punkt obrotu (Pivot) - identycznie jak w getRotatedShape

        double pX, pY;
        if (Double.isNaN(pivotX) || Double.isNaN(pivotY)) {
            pX = fW / 2.0;
            pY = fH / 2.0;
        } else {
            pX = pivotX ;
            pY = pivotY ;
        }

        if (rotation != 0) {
            g2d.rotate(Math.toRadians(rotation), pX, pY);
        }

        // 5. TRANSFORMACJE (Kolejność: Translate -> Rotate)
        g2d.translate(width/2, height/2);
        g2d.scale(scaleX, scaleY);
        g2d.translate(-width/2, -height/2);

        // 4. SKALOWANIE - wokół pivotu
        // Aby skalować względem środka, a nie lewego górnego rogu:
        // Przesuwamy do pivotu, skalujemy, wracamy.

        // 6. RYSOWANIE OBRAZKA (od 0,0 bo g2d jest już przesunięte)
        g2d.drawImage(texture.image,0 ,0 , fW, fH, null);

        // 7. RYSOWANIE HITBOXA (Lokalnie!)
        if (showHitBox) {
//            Rectangle rec = getRotatedShape().getBounds();// Pobiera bazowy rect (np. 0,0,16,16)
//
//            // Rysujemy na tym samym g2d, więc NIE dodajemy dX, dY.
//            // Musimy tylko przeskalować rozmiar samego prostokąta.
//            int rx = (int)(rec.x * scaleX);
//            int ry = (int)(rec.y * scaleY);
//            int rw = (int)(rec.width * scaleX);
//            int rh = (int)(rec.height * scaleY);
//
//            g2d.setColor(Color.RED);
//            g2d.setStroke(new BasicStroke(2.0f));
//            g2d.drawRect(rx, ry, rw, rh);
//
//            g2d.setColor(new Color(255, 0, 0, 50));
//            g2d.fillRect(rx, ry, rw, rh);
            g2d = (Graphics2D) g.create();
            Shape collisionShape = getRotatedShape();

            // Rysujemy kształt na worldG
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(1.0f)); // Cienka linia
            g2d.draw(collisionShape); // To narysuje obramowanie

            // Opcjonalnie: półprzezroczyste wypełnienie
            g2d.setColor(new Color(255, 0, 0, 50));
            g2d.fill(collisionShape);
        }

        g2d.dispose();
    }

    @Override
    public Rectangle getCalculatedAutoHitBoxes() {

        return texture.getHitBox();
    }

    @Override
    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
        // USUNIĘTO: texture.rectangle.height *= scaleY; // NIGDY TEGO NIE RÓB
    }

    @Override
    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
        // USUNIĘTO: texture.rectangle.width *= scaleX; // NIGDY TEGO NIE RÓB
    }

    public boolean intersects(Sprite s) {
        return this.texture.rectangle.intersects(s.texture.rectangle);
    }

    public void rotate(double angle) {
        rotation = angle;
        texture.rectangle = getRotatedBounds();
    }
}