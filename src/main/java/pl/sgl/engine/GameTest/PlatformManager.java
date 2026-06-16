package pl.sgl.engine.GameTest;

import pl.sgl.engine.FillMode;
import pl.sgl.engine.Game;
import pl.sgl.engine.GameObject;
import pl.sgl.engine.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlatformManager {
    public List<Platform> platforms = new ArrayList<>();
    int startPosX;
    public int startPosY = 960;
    Random rand = new Random();
    int min = 32, max = 1248;

    public PlatformManager() {
        startPosX = rand.nextInt((max - min) + 1) + min;
        generatePlatforms();

//        Platform p = generatePlatform(32, 1000,4);
//        platforms.add(p);
//        Game.instance.addGameObject(p);
//        System.out.println("p.width: " + p.getWidth());
//        System.out.println("p.height: " + p.getHeight());
    }

    public Platform generatePlatform (int randPosX, int basePos, int randPlatformLenght) {
        Platform platform = new Platform("/textures/brackeys_platformer_assets/sprites/world_tileset.png", (float) randPosX, (float) (basePos -200));
        platform.setPivot(0,0);
        platform.setTextureRegion(128,16,16,16);
        platform.setSpriteSize(randPlatformLenght * 16,16, FillMode.TILE);
        platform.setScaleX(2);
        platform.setScaleY(2);
        platform.velocityY = GameScene.fallingSpeed;

        if ( rand.nextInt(100) <= 50) {
            EnemyManager.generate(platform);
        }

        if ( rand.nextInt(100) <= 25) {
            CoinManager.generate(platform);
        }
//        platform.showHitBox = true;

//        System.out.println("   platform witdth: " + platform.getWidth());
//        System.out.println("    platformhitbox width" + platform.frameWidth);

        return platform;
    }


    public int getRandomPlatformPosition(int randPlatformLenght) {
        int min;
        int max;

        // place platform opossite side
        if (startPosX > 640 ) {
            min = 32;
            max = 600;
        } else {
            min = 600;
            max = 1248 - randPlatformLenght;
        }

        try {
            int randPosX = rand.nextInt((max - min) + 1) + min;
            startPosX = randPosX;
            return  randPosX;
        } catch (Exception e) {

            System.out.println("Wystąpił inny, nieznany błąd: " + e.getMessage());
        } finally {

        }
        return 32;
    }

    public Platform getRandomPlatform() {
        //64
        //1248
        int min0 = 4, max0 = 6;
        int randPlatformLenght= rand.nextInt((max0- min0) + 1) + min0;
        int randPosX = getRandomPlatformPosition(randPlatformLenght * 32);
        startPosX = randPosX;
        Platform platform = generatePlatform(startPosX, (int) startPosY, randPlatformLenght);

        SceneManager.getScene("Game").addGameObject(platform);
        return platform;
    }


    public void generatePlatforms() {
        if (platforms.size() <= 1) {

            for(int i = 0; i < 20; i++) {
                platforms.add(getRandomPlatform());
                startPosY -= 100;
            }

        } else {

            for (GameObject p : platforms) {
                if(p.y >= 1000) {
                    p.setPosition(getRandomPlatformPosition((int) (p.getWidth() * 2)), startPosY);
                    p.velocityY = GameScene.fallingSpeed;
                    if ( rand.nextInt(100) <= 50) {
                        EnemyManager.generate((Platform) p);
                    }

                    if ( rand.nextInt(100) <= 25) {
                        CoinManager.generate((Platform) p);
                    }
                }
            }

        }
    }



    public void move( double deltaTime) {
        for (Platform p : platforms) {
            p.moveByVelocity();
        }
    }
}
