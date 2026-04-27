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
        // 1. Obliczamy interpolację RAZ
        float dX = (float) (lastX + (x - lastX) * alpha);
        float dY = (float) (lastY + (y - lastY) * alpha);

        // 2. TWORZYMY KOPIĘ DLA TEGO OBIEKTU
        Graphics2D g2d = (Graphics2D) g.create();

        // 3. TRANSFORMACJE
        g2d.translate(dX, dY);
        int fW = (int)(width * scaleX);
        int fH = (int)(height * scaleY);

        if (rotation != 0) {
            g2d.rotate(Math.toRadians(rotation), fW / 2.0, fH / 2.0);
        }

        // 4. RYSOWANIE OBRAZKA
        g2d.drawImage(texture.image, 0, 0, fW, fH, null);

        // 5. RYSOWANIE HITBOXA (Używamy tego samego g2d!)
        if (showHitBox) {
            // Ponieważ g2d jest już przesunięte (translate) i obrócone (rotate),
            // rysujemy hitbox od punktu 0,0 względem obiektu!
            Rectangle rec = getCalculatedAutoHitBoxes();

            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2.0f));
            // Rysujemy prostokąt hitboxa (uwzględniając jego przesunięcie wewnątrz grafiki)
            g2d.drawRect(rec.x, rec.y, rec.width, rec.height);

            g2d.setColor(new Color(255, 0, 0, 50));
            g2d.fillRect(rec.x, rec.y, rec.width, rec.height);
        }

        // 6. ZWALNIAMY KOPIĘ
        g2d.dispose();
    }
//    public void draw(Graphics2D g2d, double alpha){
//        super.draw(g2d,alpha);
//
//        // 2. scaling
//        int finalWidth = (int) (width * scaleX);
//        int finalHeight = (int) (height * scaleY);
//
//        if (rotation !=0) {
//            g2d.rotate(Math.toRadians(rotation), finalWidth /2, finalHeight /2 );
//        }
//        g2d.drawImage((Image) texture.image, 0,0, finalWidth, finalHeight, null);
//
//        if (rotation !=0) {
//            g2d.rotate(Math.toRadians(-rotation), finalWidth /2, finalHeight /2 );
//        }
//        g2d.translate(-drawX, -drawY);
//    }

    @Override
    public Rectangle getCalculatedAutoHitBoxes() {

        return texture.getHitBox();
    }

    @Override
    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
        texture.rectangle.height *= scaleY;
    }

    @Override
    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
        texture.rectangle.width *= scaleX;
    }

    public boolean intersects(Sprite s) {
        return this.texture.rectangle.intersects(s.texture.rectangle);
    }

    public void rotate(double angle) {
        rotation = angle;
        texture.rectangle = getRotatedBounds();
    }
}