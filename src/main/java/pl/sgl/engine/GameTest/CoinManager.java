package pl.sgl.engine.GameTest;

import pl.sgl.engine.Game;
import pl.sgl.engine.GameObject;
import pl.sgl.engine.SceneManager;
import pl.sgl.engine.animation.Animation;
import pl.sgl.engine.audio.AudioManager;

import java.util.ArrayList;
import java.util.List;

public class CoinManager {
    public static List<Coin> coins = new ArrayList<>();

    public CoinManager(List<Platform> platforms) {
//        for(Platform p : platforms) {
//            generate(p);
//        }

        //        audio.load("bg_music", "/audio/alex-productions-racing-sport-gaming-racing(chosic.com).wav");
//        audio.load("shoot", "/audio/zap-hiphop-a.wav");
//
//        audio.loop("bg_music"); // Start muzyki w tle
        AudioManager.load("coin",  "/textures/brackeys_platformer_assets/sounds/coin.wav");
    }

    public static void generate(Platform platform) {
        Coin c = new Coin((float) ((float) platform.x + platform.getWidth() / 2 * platform.scaleX), (float) platform.y, 0.1, 1);
        Animation coinAnim = new Animation("/textures/brackeys_platformer_assets/sprites/coin.png", 0, 0, 16, 16, 12);
        c.addAnimation("base", coinAnim);
        c.scale(4, 4);
        c.setPivotByProcent(0.5,0.5);
        c.setPosition(platform.x + platform.getWidth() / 2 - c.getWidth() / 2, platform.y - c.getHeight()/2);
        c.playAnimationInCycle();

        c.velocityY = platform.velocityY;

        coins.add(c);
        SceneManager.getScene("Game").addGameObject(c);
    }

    public void recycle (Platform platform) {
        for (Coin c: coins) {
            if (c.y >= 1000 ) {
                c.setPosition(platform.x + platform.getWidth() / 2 - c.getWidth() / 2, platform.y - c.getHeight());
                c.velocityY = platform.velocityY;
//                System.out.println("add coind");
            }
        }
    }

    public void move (double deltaTime) {
        for (Coin s : coins) {
            if(s.y < 1500) {
                s.moveByVelocity();
            }
        }
    }


}
