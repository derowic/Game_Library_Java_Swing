package pl.sgl.engine.Animation;

import pl.sgl.engine.GameObject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AnimatedSprite extends GameObject {
    private BufferedImage[] frames;
    private int currentFrame = 0;
    private double frameTimer = 0;
    private double frameDuration = 0.1;
    private boolean loop = true;
    private boolean play = false;

    public AnimatedSprite(BufferedImage[] frames, double speed, double x, double y) {
        super(x,y);
        this.frames = frames;
        this.frameDuration = speed;
        this.width = frames[0].getWidth();
        this.height = frames[0].getHeight();
        playAnimation();
    }

    public void update(double deltaTime) {

        this.lastX = this.x;
        this.x += (velocityX * deltaTime);

        this.lastY = this.y;
        this.y += (velocityY * deltaTime);


        animation(deltaTime);
    }

    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }

    @Override
    public void draw(Graphics2D g2d, double alpha){
        super.draw(g2d,alpha);

        // 2. scaling
        int finalWidth = (int) (width * scaleX);
        int finalHeight = (int) (height * scaleY);

        if (rotation !=0) {
            g2d.rotate(Math.toRadians(rotation), finalWidth /2, finalHeight /2 );
        }
        g2d.drawImage((Image) getCurrentFrame(), (int) this.x, (int) this.y, finalWidth, finalHeight, null);
    }


    private void animation(double deltaTime)
    {
        if (play) {
            frameTimer += deltaTime;
            if (frameTimer >= frameDuration) {
                frameTimer = 0;
                currentFrame++;
                if (currentFrame >= frames.length) {
                    if (loop) {
                        currentFrame = 0;
                    } else {
                        currentFrame = frames.length - 1;
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
}
