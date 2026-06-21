package pl.sgl.engine.GameTest;

import pl.sgl.engine.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlatformManager {
    public List<Platform> platforms = new ArrayList<>();
    int startPosX;
    public int startPosY = 960;
    Random rand = new Random();
    int min = 32, max = 1248;
    Platform lastRecycledPlatform;
    int deathHeight;

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

            for(int i = 0; i < 18; i++) {
                platforms.add(getRandomPlatform());
                startPosY -= 100;
            }
            lastRecycledPlatform = platforms.get(platforms.size() - 1);

            System.out.println("last start pos:" + startPosY);

        } else {

            for (Platform p : platforms) {
                if(p.y >= GameScene.player.sprite.y + 1000 || p.y > deathHeight + 1000) {
//                    System.out.println("last start pos2 :" + startPosY);

                    if (p.y < deathHeight) {
                        deathHeight = (int) p.y;
                    }

                    setDeath();
//                    System.out.println(GameScene.player.sprite.y);
                    p.setPosition(getRandomPlatformPosition((int) (p.getWidth() * 2)), lastRecycledPlatform.y - 100);

                    lastRecycledPlatform = p;

                    if (rand.nextInt(100) <= 50) {
                        EnemyManager.generate((Platform) p);
                        System.out.println("gen enemy");
                    }

                    if (rand.nextInt(100) <= 25) {
                        CoinManager.generate((Platform) p);
                    }

//                    startPosY -= 100;

//                    platforms.get(0).setPosition(0, GameScene.player.sprite.y);
//                    platforms.get(0).velocityY = 0;
                }


            }

            setDeath();
        }
    }

    public void move() {
        for (Platform p : platforms) {
            p.moveByVelocity();
        }
    }

    public void setDeath() {
        if (GameScene.player.sprite.y > deathHeight + 2000) {
            SceneManager.setScene("Death");
            System.out.println("death height " + deathHeight);
        }
    }
}
