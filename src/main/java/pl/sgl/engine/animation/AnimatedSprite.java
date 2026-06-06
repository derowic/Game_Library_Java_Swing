package pl.sgl.engine.animation;

import pl.sgl.engine.texture.Texture;
import pl.sgl.engine.texture.TextureLoader;
import pl.sgl.engine.GameObject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class AnimatedSprite extends GameObject {
    protected HashMap<String, Animation> animations = new HashMap<>();
    public String currentPlayedAnimation = "";
    private int currentFrame = 0;
    private double frameTimer = 0;
    private double frameDuration = 0.1;
    private boolean play = false;
    private boolean playCycle = false;
//    private Rectangle baseHitbox; // Zmieniono nazwę na baseHitbox (oryginalne wymiary)

    public AnimatedSprite(double x, double y, double speed) {
        super(x, y);
        this.frameDuration = speed;
        playAnimation();
//        hitbox = texture.getHitBox();
    }
    public void playAnimation() {
        play = true;
        playCycle = false;
        currentFrame = 0;
    }

    public void playAnimationInCycle() {
        play = false;
        playCycle = true;
    }

    public void addAnimation(String animationName, Animation anim) {
        animations.put(animationName, anim);
        if (currentPlayedAnimation.equals("")) {
            currentPlayedAnimation = animationName;
            // Ustawiamy bazowe wymiary na podstawie pierwszej klatki
            this.width = anim.frames[0].getWidth();
            this.height = anim.frames[0].getHeight();
            texture = new Texture(getCurrentFrame());
            if( hitbox.width == 0 && hitbox.height ==0) {
                hitbox = texture.getHitBox();
//                hitbox = new Rectangle(0,0, texture.image.getWidth(), texture.image.getHeight());
                this.srcX = 0;
                this.srcY = 0;
                this.srcW = texture.image.getWidth();
                this.srcH = texture.image.getHeight();
            }
            // Zapamiętujemy bazowy hitbox (nieprzeskalowany)
//            baseHitbox = TextureLoader.getTightHitbox(anim.frames[0]);
        }
    }



    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        updateAnimationLogic(deltaTime);
    }

    private void updateAnimationLogic(double deltaTime) {
        if ((play || playCycle) && !currentPlayedAnimation.equals("")) {
            frameTimer += deltaTime;
            if (frameTimer >= frameDuration) {
                frameTimer = 0;
                currentFrame++;
                Animation anim = animations.get(currentPlayedAnimation);
                if (currentFrame >= anim.frames.length) {
                    if (playCycle) currentFrame = 0;
                    else currentFrame = anim.frames.length - 1;
                }
            }
        }
        texture = new Texture(getCurrentFrame());
        if (texture == null) return;
    }

    public BufferedImage getCurrentFrame() {
        if (currentPlayedAnimation.equals("") || !animations.containsKey(currentPlayedAnimation)) return null;
        return animations.get(currentPlayedAnimation).frames[currentFrame];
    }

//    @Override
//    public void draw(Graphics2D g, double alpha) {
//
//
//       super.draw(g, alpha);
//    }

//    @Override
//    public Rectangle getCalculatedAutoHitBoxes() {
//        return baseHitbox;
//    }

    // Usunięto modyfikowanie baseHitbox w setterach - teraz skala jest używana tylko przy rysowaniu i kolizjach
//    @Override
//    public void setScaleY(double scaleY) { this.scaleY = scaleY; }
//    @Override
//    public void setScaleX(double scaleX) { this.scaleX = scaleX; }

    public void setAnimation(String name) {
        if (animations.containsKey(name) && !currentPlayedAnimation.equals(name)) {
            currentPlayedAnimation = name;
            currentFrame = 0;
            frameTimer = 0;
        }
    }
}