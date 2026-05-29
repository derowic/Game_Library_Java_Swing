package pl.sgl.engine.GameTest;

import pl.sgl.engine.animation.AnimatedSprite;
import pl.sgl.engine.animation.Animation;

public class Player  {
    public AnimatedSprite sprite;
    public String playerStatus = "falling";

    public Player(){
        Animation idle = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,0,32,32,4);
        Animation walk = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,64,32,32,8);
        Animation roll = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,160,32,32,8);
        Animation jump = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",128,64,32,32,1);
        sprite = new AnimatedSprite( 600, 880, 0.1); // zmiana klatki co 0.1 sekundy
        sprite.addAnimation("idle", idle);
        sprite.addAnimation("walk", walk);
        sprite.addAnimation("roll", roll);
        sprite.addAnimation("jump", jump);
        sprite.setAnimation("roll");
        sprite.setScaleX(-4);
        sprite.setScaleY(4);
        sprite.getRotatedShape();
        sprite.setPivot(sprite.width/2, sprite.height/2);
//        player.showHitBox = true;
    }

    public void playerAnimationLogic() {
        if( sprite.velocityX != 0) {
            sprite.setAnimation("walk");
        } else {
            sprite.setAnimation("idle");
        }

        if ((playerStatus.equals("falling") || playerStatus.equals("jumping")) && sprite.velocityY != 0) {
            sprite.setAnimation("jump");
        } else {
            sprite.setAnimation("idle");
        }
    }


}
