package pl.sgl.engine;

import pl.sgl.engine.animation.AnimatedSprite;
import pl.sgl.engine.animation.Animation;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Main2 extends Game {
    private AnimatedSprite player;
    Sprite stoneBlock;
    Sprite downBlocks;
    Sprite leftBlocks;
    Sprite rightBlocks;

    public Main2() {
        //w 32 * 40  h: 32 * 30
        super("Test", 1280, 960, Color.BLACK);
        setRenderPixelArt();

        stoneBlock = new Sprite("/textures/brackeys_platformer_assets/sprites/stone_block.png", 0,0);
        stoneBlock.setPivot(0,0);

        stoneBlock.setSpriteSize(640,480, FillMode.TILE);
        stoneBlock.setScaleX(2);
        stoneBlock.setScaleY(2);
        addGameObject(stoneBlock);

        downBlocks = new Sprite("/textures/brackeys_platformer_assets/sprites/world_tileset.png", 0,928);
        downBlocks.setPivot(0,0);
        downBlocks.setTextureRegion(32,32,16,16);
        downBlocks.setSpriteSize(640,16, FillMode.TILE);
        downBlocks.setScaleX(2);
        downBlocks.setScaleY(2);
        addGameObject(downBlocks);

        leftBlocks = new Sprite("/textures/brackeys_platformer_assets/sprites/world_tileset.png", 0,0);
        leftBlocks.setPivot(0,0);
        leftBlocks.setTextureRegion(32,32,16,16);
        leftBlocks.setSpriteSize(16,480, FillMode.TILE);
        leftBlocks.setScaleX(2);
        leftBlocks.setScaleY(2);
        addGameObject(leftBlocks);

        rightBlocks = new Sprite("/textures/brackeys_platformer_assets/sprites/world_tileset.png", 1248,0);
        rightBlocks.setPivot(0,0);
        rightBlocks.setTextureRegion(32,32,16,16);
        rightBlocks.setSpriteSize(16,480, FillMode.TILE);
        rightBlocks.setScaleX(2);
        rightBlocks.setScaleY(2);
        addGameObject(rightBlocks);

        Animation idle = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,0,32,32,4);
        Animation walk = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,64,32,32,8);
        player = new AnimatedSprite( 640, 876, 0.1); // zmiana klatki co 0.1 sekundy
        player.addAnimation("idle", idle);
        player.addAnimation("walk", walk);
        player.setAnimation("walk");
        player.showHitBox = true;
        player.setScaleX(-4);
        player.setScaleY(4);
        player.getRotatedShape();
        addGameObject(player);


    }
    @Override
    protected void update() {

        movePlayer();
        super.update();
    }

    public void movePlayer() {
        if (keyboard.isKeyDown(KeyEvent.VK_A)) {
            player.move(-5,0);
            player.setAnimation("walk");
            player.setScaleX(-4);
        }
        if (keyboard.isKeyDown(KeyEvent.VK_D)) {
            player.move(5,0);
            player.setAnimation("walk");
            player.setScaleX(4);
        }

        if (!keyboard.isKeyDown(KeyEvent.VK_D) && !keyboard.isKeyDown(KeyEvent.VK_A)) {
            player.setAnimation("idle");
        }

        if (keyboard.isKeyDown(KeyEvent.VK_W)) {
            player.move(0,-5);
        }
        if (keyboard.isKeyDown(KeyEvent.VK_S)) {
            player.move(0,5);
        }
    }

    public static void main(String[] args) {
        new Main2().start();
    }
}