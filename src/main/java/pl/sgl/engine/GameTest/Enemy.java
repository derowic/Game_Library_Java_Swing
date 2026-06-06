package pl.sgl.engine.GameTest;

import pl.sgl.engine.Game;
import pl.sgl.engine.GameObject;
import pl.sgl.engine.animation.AnimatedSprite;
import pl.sgl.engine.animation.Animation;

public class Enemy extends AnimatedSprite {
    int minX, maxX;

    public Enemy(Platform p){
        super(p.x, p.y, 0.1);
        Animation walk = new Animation("/textures/brackeys_platformer_assets/sprites/slime_green.png",0,32,24,16,4);
        showHitBox = true;
        addAnimation("walk", walk);
        playAnimationInCycle();
        setPivot(width/2,height/2);
        scale(4, 4);
        setPosition(p.x + width/2, p.y - height /2);

        minX = (int) p.x;
        maxX = (int) (p.x + p.getWidth());

        Game.instance.addGameObject(this);
    }
}