package pl.sgl.engine.GameTest;

import pl.sgl.engine.Game;
import pl.sgl.engine.GameObject;
import pl.sgl.engine.SceneManager;
import pl.sgl.engine.Time.Timer;
import pl.sgl.engine.animation.AnimatedSprite;
import pl.sgl.engine.animation.Animation;
import pl.sgl.engine.audio.AudioManager;
import pl.sgl.engine.ui.Text;
import pl.sgl.engine.ui.UIElement;

public class Player  {
    public AnimatedSprite sprite;
    public String playerStatus = "falling";
    public boolean doubleJump = false;
    public GameObject playerCollideWith;


    public UIElement coinsNumberLabel;
    public int coins = 0;

    public Timer animTiemr = new Timer(0.25);
    public boolean hurt = false;
    HeartManager heartManager;

    public Player(){
        Animation idle = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,0,32,32,4);
        Animation walk = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,64,32,32,8);
        Animation roll = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,160,32,32,8);
        Animation jump = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",128,64,32,32,1);
        Animation fall = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,0,32,32,1);
        Animation getHit = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",64,192,32,32,1);
        sprite = new AnimatedSprite( 600, 840, 0.1); // zmiana klatki co 0.1 sekundy
//        sprite.showHitBox = true;
        sprite.addAnimation("idle", idle);
        sprite.addAnimation("walk", walk);
        sprite.addAnimation("roll", roll);
        sprite.addAnimation("jump", jump);
        sprite.addAnimation("fall", fall);
        sprite.addAnimation("hurt", getHit);
        sprite.setAnimation("roll");
        sprite.setPivotByProcent(0.5,0.5);
        sprite.scale(4,4);
        sprite.getRotatedShape();
//        sprite.rotation = 45;
//        sprite.rotation = 45;

//        sprite.setPosition(64, sprite.y);
        sprite.playAnimationInCycle();
//        player.showHitBox = true;
        coinsNumberLabel = new Text( "PUNKTY: 0", 20, 80, 24);
        SceneManager.getScene("Game").getUi().addUi(coinsNumberLabel);

        AudioManager.load("jump",  "/textures/brackeys_platformer_assets/sounds/jump.wav");
        AudioManager.load("hurt",  "/textures/brackeys_platformer_assets/sounds/hurt.wav");
        AudioManager.load("power_up",  "/textures/brackeys_platformer_assets/sounds/power_up.wav");

        sprite.setName("player");

        SceneManager.getScene("Game").addGameObject(sprite);

        Game.instance.camera.setFollowedObject(sprite);

        heartManager = new HeartManager();
    }

    public void playerAnimationLogic() {
        if (hurt) {
            if(animTiemr.check()) {
                hurt = false;
                sprite.setAnimation("idle");
            }
        }
        else {
            if (doubleJump) {
                if (sprite.velocityX != 0 && playerStatus.equals("onGround")) {
                    sprite.setAnimation("walk");
                    AudioManager.stop("jump");
                } else {
                    sprite.setAnimation("idle");
                    AudioManager.stop("jump");
                }

                if (playerStatus.equals("onGround")) {
                    sprite.playAnimationInCycle();
                    AudioManager.stop("jump");
                }

                if (playerStatus.equals("jumping") && sprite.velocityY <= 0) {
                    sprite.setAnimation("jump");
                }

                if (playerStatus.equals("falling") && sprite.velocityY > 0) {
                    sprite.setAnimation("fall");
                    sprite.playAnimation();
                    AudioManager.stop("jump");
                }
            }
        }



    }


}
