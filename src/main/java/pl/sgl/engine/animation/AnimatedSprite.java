package pl.sgl.engine.animation;

import pl.sgl.engine.texture.TextureLoader;
import pl.sgl.engine.GameObject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class AnimatedSprite extends GameObject {
    protected HashMap<String, Animation> animations = new HashMap<>();
    private String currentPlayedAnimation = "";
    private int currentFrame = 0;
    private double frameTimer = 0;
    private double frameDuration = 0.1;
    private boolean loop = true;
    private boolean play = false;
    private Rectangle baseHitbox; // Zmieniono nazwę na baseHitbox (oryginalne wymiary)

    public AnimatedSprite(double speed, double x, double y) {
        super(x, y);
        this.frameDuration = speed;
        playAnimation();
    }

    public void playAnimation() {
        play = true;
    }

    public void addAnimation(String animationName, Animation anim) {
        animations.put(animationName, anim);
        if (currentPlayedAnimation.equals("")) {
            currentPlayedAnimation = animationName;
            // Ustawiamy bazowe wymiary na podstawie pierwszej klatki
            this.width = anim.frames[0].getWidth();
            this.height = anim.frames[0].getHeight();
            // Zapamiętujemy bazowy hitbox (nieprzeskalowany)
            baseHitbox = TextureLoader.getTightHitbox(anim.frames[0]);
        }
    }

    @Override
    public void update(double deltaTime) {
        this.lastX = this.x;
        this.x += (velocityX * deltaTime);
        this.lastY = this.y;
        this.y += (velocityY * deltaTime);

        updateAnimationLogic(deltaTime);
    }

    private void updateAnimationLogic(double deltaTime) {
        if (play && !currentPlayedAnimation.equals("")) {
            frameTimer += deltaTime;
            if (frameTimer >= frameDuration) {
                frameTimer = 0;
                currentFrame++;
                Animation anim = animations.get(currentPlayedAnimation);
                if (currentFrame >= anim.frames.length) {
                    if (loop) currentFrame = 0;
                    else currentFrame = anim.frames.length - 1;
                }
            }
        }
    }

    public BufferedImage getCurrentFrame() {
        if (currentPlayedAnimation.equals("") || !animations.containsKey(currentPlayedAnimation)) return null;
        return animations.get(currentPlayedAnimation).frames[currentFrame];
    }

    @Override
    public void draw(Graphics2D g, double alpha) {
        BufferedImage frame = getCurrentFrame();
        if (frame == null) return;

        // 1. Interpolacja pozycji
        float dX = (float) (lastX + (x - lastX) * alpha);
        float dY = (float) (lastY + (y - lastY) * alpha);

        Graphics2D g2d = (Graphics2D) g.create();

        // 2. Obliczamy wymiary końcowe
        int fW = (int)(width * scaleX);
        int fH = (int)(height * scaleY);

        // 3. Wyznaczamy Pivot (identycznie jak w klasie Sprite)
        double pX, pY;
        if (Double.isNaN(pivotX) || Double.isNaN(pivotY)) {
            pX = fW / 2.0;
            pY = fH / 2.0;
        } else {
            pX = pivotX * scaleX;
            pY = pivotY * scaleY;
        }

        // 4. Transformacje
        g2d.translate(dX, dY);
        if (rotation != 0) {
            g2d.rotate(Math.toRadians(rotation), pX, pY);
        }

        // 5. Rysowanie klatki animacji
        g2d.drawImage(frame, 0, 0, fW, fH, null);

        // 6. Rysowanie Hitboxa (Debug)
        if (showHitBox && baseHitbox != null) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2.0f));

            // SKALUJEMY wymiary hitboxa do rysowania
            int rx = (int)(baseHitbox.x * scaleX);
            int ry = (int)(baseHitbox.y * scaleY);
            int rw = (int)(baseHitbox.width * scaleX);
            int rh = (int)(baseHitbox.height * scaleY);

            g2d.drawRect(rx, ry, rw, rh);
            g2d.setColor(new Color(255, 0, 0, 50));
            g2d.fillRect(rx, ry, rw, rh);
        }

        g2d.dispose();
    }

    @Override
    public Rectangle getCalculatedAutoHitBoxes() {
        return baseHitbox;
    }

    // Usunięto modyfikowanie baseHitbox w setterach - teraz skala jest używana tylko przy rysowaniu i kolizjach
    @Override
    public void setScaleY(double scaleY) { this.scaleY = scaleY; }
    @Override
    public void setScaleX(double scaleX) { this.scaleX = scaleX; }

    public void setAnimation(String name) {
        if (animations.containsKey(name) && !currentPlayedAnimation.equals(name)) {
            currentPlayedAnimation = name;
            currentFrame = 0;
            frameTimer = 0;
        }
    }
}