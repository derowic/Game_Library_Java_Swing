package pl.sgl.engine.GameTest;

import pl.sgl.engine.SceneManager;
import pl.sgl.engine.Sprite;
import pl.sgl.engine.Time.Timer;

import java.util.ArrayList;
import java.util.List;

public class HeartManager {

    public int health = 10;
    List<Sprite> deadHearts = new ArrayList<>();
    List<Sprite> hearts = new ArrayList<>();
    public Timer hurtTimer= new Timer(0.5);

    public HeartManager () {

        for( int i = 0; i < health; i++) {
            Sprite heart = new Sprite("/textures/hearts/hearts.png", 20 + i * 40, 20, 16,0,16,16);
//        platform.setTextureRegion(128,16,16,16);
//        platform.setSpriteSize(randPlatformLenght * 16,16, FillMode.TILE);
            heart.setScaleX(3);
            heart.setScaleY(3);
            SceneManager.getScene("Game").getUi().addUiObject(heart);
            deadHearts.add(heart);
        }

        for( int i = 0; i < health; i++) {
            Sprite heart = new Sprite("/textures/hearts/hearts.png", 20 + i * 40, 20, 0,0,16,16);
//        platform.setTextureRegion(128,16,16,16);
//        platform.setSpriteSize(randPlatformLenght * 16,16, FillMode.TILE);
            heart.setScaleX(3);
            heart.setScaleY(3);
            SceneManager.getScene("Game").getUi().addUiObject(heart);
            hearts.add(heart);
        }
    }

    public void getHit() {
        if(!GameScene.player.playerStatus.equals("hurt") && hurtTimer.check()) {
            health--;

            if (health <= 0) {
                SceneManager.setScene("Death");
            }

            hearts.get(health).hide();
            hurtTimer.reset();
        }
    }
}
