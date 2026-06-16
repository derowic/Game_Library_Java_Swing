package pl.sgl.engine.GameTest;

import pl.sgl.engine.*;

import java.util.ArrayList;
import java.util.List;

public class EnvManager {

    List<Sprite> background = new ArrayList<>();
    List<GameObject> sideWalls = new ArrayList<>();
    Sprite downBlocks;
    int startY = 0;

    public EnvManager() {

        for(int i = 0; i < 6; i++) {
            Sprite stoneBlock = new Sprite("/textures/brackeys_platformer_assets/sprites/stone_block.png", 0, startY - ((i) * 480));
            stoneBlock.setPivot(0, 0);

            stoneBlock.setSpriteSize(640, 480, FillMode.TILE);
            stoneBlock.setScaleX(2);
            stoneBlock.setScaleY(2);
            stoneBlock.velocityY = 100;
            background.add(stoneBlock);
            SceneManager.getScene("Game").addGameObject(stoneBlock);
        }


        downBlocks = new Sprite("/textures/brackeys_platformer_assets/sprites/world_tileset.png", 0, 928);
        downBlocks.setPivot(0, 0);
        downBlocks.setTextureRegion(32, 32, 16, 16);
        downBlocks.setSpriteSize(640, 16, FillMode.TILE);
        downBlocks.setScaleX(2);
        downBlocks.setScaleY(2);
        downBlocks.velocityY = 50;
        SceneManager.getScene("Game").addGameObject(downBlocks);


        startY = 0;
        for(int i = 0; i < 6; i++) {
            Sprite leftBlocks = new Sprite("/textures/brackeys_platformer_assets/sprites/world_tileset.png", 0, startY - ((i) * 480));
            leftBlocks.setPivot(0, 0);
            leftBlocks.setTextureRegion(32, 32, 16, 16);
            leftBlocks.setSpriteSize(16, 480, FillMode.TILE);
            leftBlocks.setScaleX(2);
            leftBlocks.setScaleY(2);
            leftBlocks.velocityY = 100;
            sideWalls.add(leftBlocks);
            SceneManager.getScene("Game").addGameObject(leftBlocks);

            Sprite rightBlocks = new Sprite("/textures/brackeys_platformer_assets/sprites/world_tileset.png", 1248, startY - ((i) * 480));
            rightBlocks.setPivot(0, 0);
            rightBlocks.setTextureRegion(32, 32, 16, 16);
            rightBlocks.setSpriteSize(16, 480, FillMode.TILE);
            rightBlocks.setScaleX(2);
            rightBlocks.setScaleY(2);
            rightBlocks.velocityY = 100;
            sideWalls.add(rightBlocks);
            SceneManager.getScene("Game").addGameObject(rightBlocks);
        }
        startY = -5* 480;

    }

    public void move() {
        for(GameObject go : background) {
            go.moveByVelocity();
        }

        if (downBlocks.y > 1200) {
            downBlocks.y = 1200;
        } else {
            downBlocks.moveByVelocity();

        }

        for(GameObject go : sideWalls) {
            go.moveByVelocity();
        }

        recycle();
    }

    public void recycle() {
        for(GameObject go : background) {
            if( go.y >= 928) {
                go.setPosition(go.x, startY);
                go.velocityY = GameScene.fallingSpeed;
            }
        }

        for(GameObject go : sideWalls) {
            if( go.y >= 928) {
                go.setPosition(go.x, startY);
                go.velocityY = GameScene.fallingSpeed;
            }
        }
    }

    public boolean isCollidingWithWall(Player player) {

        if (Colision.colisionWithListOfSprites(player.sprite, sideWalls)) return true;

        return false;
    }

    public boolean isCollidingWithdDown(Player player) {
        if (Colision.checkCollision(player.sprite, downBlocks)) return true;

        return false;
    }
}
