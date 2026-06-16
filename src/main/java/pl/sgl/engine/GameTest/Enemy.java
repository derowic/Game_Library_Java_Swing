package pl.sgl.engine.GameTest;

import pl.sgl.engine.Game;
import pl.sgl.engine.GameObject;
import pl.sgl.engine.SceneManager;
import pl.sgl.engine.animation.AnimatedSprite;
import pl.sgl.engine.animation.Animation;

public class Enemy extends AnimatedSprite {
    int minX, maxX;

    public Enemy(Platform p){
        super(p.x, p.y, 0.1);
        Animation walk = new Animation("/textures/brackeys_platformer_assets/sprites/slime_green.png",0,32,24,16,4);
//        showHitBox = true;
        addAnimation("walk", walk);
        playAnimationInCycle();

        scale(4, 4);
        setPivot(frameWidth/2,frameHeight/2+2);
        setPosition(p.x + getWidth(), p.y - getHeight()/2);

        minX = (int) ((int) p.x + getWidth()/2);
        maxX = (int) (p.x + p.getWidth() - getWidth()/2);
        velocityX = 0.25;
    }

    public void ai() {
        if(x >= maxX) {
            velocityX *= -1;
            setScaleX(-4);
        }
        if(x <= minX) {
            velocityX *= -1;
            setScaleX(4);
        }
        move(velocityX,0);

    }
}