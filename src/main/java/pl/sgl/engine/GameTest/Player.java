package pl.sgl.engine.GameTest;

import pl.sgl.engine.GameObject;
import pl.sgl.engine.animation.AnimatedSprite;
import pl.sgl.engine.animation.Animation;

public class Player  {
    public AnimatedSprite sprite;
    public String playerStatus = "falling";
    public boolean doubleJump = false;
    public GameObject playerCollideWith;

    public Player(){
        Animation idle = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,0,32,32,4);
        Animation walk = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,64,32,32,8);
        Animation roll = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,160,32,32,8);
        Animation jump = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",128,64,32,32,1);
        Animation fall = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,0,32,32,1);
        sprite = new AnimatedSprite( 600, 780, 0.1); // zmiana klatki co 0.1 sekundy
        sprite.showHitBox = true;
        sprite.addAnimation("idle", idle);
        sprite.addAnimation("walk", walk);
        sprite.addAnimation("roll", roll);
        sprite.addAnimation("jump", jump);
        sprite.addAnimation("fall", fall);
        sprite.setAnimation("roll");
        sprite.setPivotByProcent(0.5,0.5);
        sprite.scale(4,4);
        sprite.getRotatedShape();
//        sprite.rotation = 45;
//        sprite.rotation = 45;

//        sprite.setPosition(64, sprite.y);
        sprite.playAnimationInCycle();
//        player.showHitBox = true;

    }

    public void playerAnimationLogic() {
//        System.out.println("player status: "+playerStatus);
//        System.out.println("player velocuity Y:" + sprite.velocityY);

        if (doubleJump) {
            if (sprite.velocityX != 0 && playerStatus.equals("onGround")) {
                sprite.setAnimation("walk");
            } else {
                sprite.setAnimation("idle");
            }

            if (playerStatus.equals("onGround")) {
                sprite.playAnimationInCycle();
            }

            if (playerStatus.equals("jumping") && sprite.velocityY <= 0) {
                sprite.setAnimation("jump");
            }

            if (playerStatus.equals("falling") && sprite.velocityY > 0) {
                sprite.setAnimation("fall");
                sprite.playAnimation();
            }
        }

    }


}
