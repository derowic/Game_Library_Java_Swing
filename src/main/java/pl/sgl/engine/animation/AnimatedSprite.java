package pl.sgl.engine.animation;

import pl.sgl.engine.texture.Texture;
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
        texture = new Texture(getCurrentFrame());
        if (texture == null) return;

       super.draw(g, alpha);
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