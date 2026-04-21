package pl.sgl.engine.animation;

import pl.sgl.engine.texture.TextureLoader;
import pl.sgl.engine.GameObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class AnimatedSprite extends GameObject {
//    private BufferedImage[] frames;
    protected HashMap<String, Animation> animations = new HashMap<>();
    private String currentPlayedAnimation = "";
    private int currentFrame = 0;
    private double frameTimer = 0;
    private double frameDuration = 0.1;
    private boolean loop = true;
    private boolean play = false;
    private Rectangle globalHitbox;

    public AnimatedSprite(double speed, double x, double y) {
        super(x,y);
        this.frameDuration = speed;

        playAnimation();
    }

    public void addAnimation(String animationName, Animation anim) {
        animations.put(animationName, anim);
        if (currentPlayedAnimation.equals("")) {
            currentPlayedAnimation = animationName;
            this.width = animations.get(animationName).frames[0].getWidth();
            this.height = animations.get(animationName).frames[0].getHeight();
            globalHitbox = TextureLoader.getTightHitbox(animations.get(animationName).frames[0]);
        }
    }

    public void update(double deltaTime) {

        this.lastX = this.x;
        this.x += (velocityX * deltaTime);

        this.lastY = this.y;
        this.y += (velocityY * deltaTime);


        animation(deltaTime);
    }

    public BufferedImage getCurrentFrame() {
        return animations.get(currentPlayedAnimation).frames[currentFrame];
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
        g2d.drawImage(getCurrentFrame(), 0, 0, fW, fH, null);

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
//        g2d.drawImage((Image) getCurrentFrame(), 0, 0, finalWidth, finalHeight, null);
//
//        if (rotation !=0) {
//            g2d.rotate(Math.toRadians(-rotation), finalWidth /2, finalHeight /2 );
//        }
//        g2d.translate(-drawX, -drawY);
//    }

    private void animation(double deltaTime) {
        if (play) {
            frameTimer += deltaTime;
            if (frameTimer >= frameDuration) {
                frameTimer = 0;
                currentFrame++;
                if (currentFrame >= animations.get(currentPlayedAnimation).frames.length) {
                    if (loop) {
                        currentFrame = 0;
                    } else {
                        currentFrame = animations.get(currentPlayedAnimation).frames.length - 1;
                    }
                }
            }
        }
    }

    public void playAnimation() {
        play = true;
    }

    public void stopAnimation() {
        play = false;
    }

    public void resetAnimation() {
        currentFrame = 0;
        frameTimer = 0;
    }

    @Override
    public Rectangle getCalculatedAutoHitBoxes() {
        return globalHitbox;
    }
}
