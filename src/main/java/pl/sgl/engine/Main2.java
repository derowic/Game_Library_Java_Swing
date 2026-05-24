package pl.sgl.engine;

import pl.sgl.engine.animation.AnimatedSprite;
import pl.sgl.engine.animation.Animation;
import pl.sgl.engine.math.Vector2D;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main2 extends Game {
    private AnimatedSprite player;
    Sprite stoneBlock;
    Sprite downBlocks;
    Sprite leftBlocks;
    Sprite rightBlocks;
    public List<GameObject> platforms = new ArrayList<>();
    Boolean playerOnGround = true;
    int startPos = 64;
    Random rand = new Random();
    float startPosY = 960;
    boolean run = false;

    public Main2() {
        //w 32 * 40  h: 32 * 30
        super("Test", 1280, 960, Color.BLACK);
        setRenderPixelArt();

        int min = 32, max = 1248;
        startPos = rand.nextInt((max - min) + 1) + min;

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
        downBlocks.velocityY = 50;
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


        generatePlatforms();

        Animation idle = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,0,32,32,4);
        Animation walk = new Animation("/textures/brackeys_platformer_assets/sprites/knight.png",0,64,32,32,8);
        player = new AnimatedSprite( 600, 880, 0.1); // zmiana klatki co 0.1 sekundy
        player.addAnimation("idle", idle);
        player.addAnimation("walk", walk);
        player.setAnimation("walk");
        player.setScaleX(-4);
        player.setScaleY(4);
        player.getRotatedShape();
        player.setPivot(player.width/2, player.height/2);
//        player.showHitBox = true;
        addGameObject(player);
    }
    @Override
    protected void update() {
        if (keyboard.isKeyDown(KeyEvent.VK_Q)) currentGame.cam.zoom += 0.1;
        if (keyboard.isKeyDown(KeyEvent.VK_E)) currentGame.cam.zoom -= 0.1;

        if (run) {
            movePlatformsDown();

        }
        playerInput();
        colisionWithOutline();
        if (run) {
            generatePlatforms();
        }

        super.update();
    }

    public void movePlatformsDown() {
        for (GameObject p : platforms) {
            p.moveByVelocity(deltaTime);
            if (downBlocks.y > 1500) {
                downBlocks.y = 1500;
            } else {
                downBlocks.moveByVelocity(deltaTime);
            }
        }
    }

    public void generatePlatforms() {
        if (platforms.size() <= 1) {

            for(int i =0; i < 20; i++) {
                platforms.add(getRandomPlatform(startPosY));
                startPosY -= 100;
            }
//            startPosY -= 100;
        } else {
            for (GameObject p : platforms) {
                if(p.y > 1000) {
                    p.setPosition(getRandomPlatformPosition((p.width * 2)), startPosY);
                }
            }
        }
    }

    public int getRandomPlatformPosition(int randPlatformLenght) {
        int min;
        int max;

        // place platform opossite side
        if (startPos > 640 ) {
            min = 32;
            max = 600;
        } else {
            min = 600;
            max = 1248 - randPlatformLenght;
        }

        try {
            int randPosX = rand.nextInt((max - min) + 1) + min;
            startPos = randPosX;
            System.out.println(startPos);
            System.out.println(randPosX);
            return  randPosX;
        } catch (Exception e) {
            System.out.println("max: " + max + " min: " + min + " lenght "+ randPlatformLenght);
            System.out.println("Wystąpił inny, nieznany błąd: " + e.getMessage());
        } finally {
            System.out.println("max: " + max + " min: " + min);
        }
        return 32;
    }

    public Sprite getRandomPlatform(double basePos) {
        //64
        //1248
        int min0 = 4, max0 = 6;
        int randPlatformLenght= rand.nextInt((max0- min0) + 1) + min0;
        int min = 0;
        int max = 0;

        int randPosX = getRandomPlatformPosition(randPlatformLenght * 32);
        startPos = randPosX;


        Sprite platform = new Sprite("/textures/brackeys_platformer_assets/sprites/world_tileset.png", (float) randPosX, (float) (basePos -200));
        platform.setPivot(0,0);
        platform.setTextureRegion(128,16,16,16);
        platform.setSpriteSize(randPlatformLenght * 16,16, FillMode.TILE);
        platform.setScaleX(2);
        platform.setScaleY(2);
        platform.velocityY = 100;
//        platform.showHitBox = true;
        addGameObject(platform);

        return platform;
    }

    public void playerInput() {
        if (keyboard.isKeyDown(KeyEvent.VK_A)) {

            player.velocityX = -400;
//            if(player.velocityX <= -400) {
//                player.velocityX = -400;
//            }
            player.setAnimation("walk");
            player.setScaleX(-4);
        }
        if (keyboard.isKeyDown(KeyEvent.VK_D)) {
            player.velocityX = 400;
//            if(player.velocityX >= 400) {
//                player.velocityX = 400;
//            }
            player.setAnimation("walk");
            player.setScaleX(4);
        }

        if (!keyboard.isKeyDown(KeyEvent.VK_D) && !keyboard.isKeyDown(KeyEvent.VK_A)) {
            player.setAnimation("idle");
            player.velocityX = 0;
        }

        if (keyboard.isKeyDown(KeyEvent.VK_W) && playerOnGround) {
            player.velocityY = -750;
        }
//        if (keyboard.isKeyDown(KeyEvent.VK_S)) {
//            player.move(0,5);
//        }
    }

    public void colisionWithOutline(){
        // 2. Aplikacja grawitacji (dodajemy do velocityY)
        player.velocityY += 20; // Stała grawitacji (dostosuj wartość)

        // --- RUCH PO OSI X ---
        double oldX = player.x;
//        if(player.velocityX < 0) {
//            player.x += -400 * deltaTime;
//        }
//        if(player.velocityX > 0) {
//            player.x += 400 * deltaTime;
//        }
        player.x += player.velocityX * deltaTime;

        // Sprawdzamy kolizję po ruchu w X
        if (isCollidingWithWall()) {
            player.x = oldX;      // Cofamy ruch w X
            player.velocityX = 0; // Zatrzymujemy się na ścianie
        }

        // Sprawdzamy kolizję po ruchu w X
        if (isCollidingWithPlatofrms()) {
            player.x = oldX;      // Cofamy ruch w X
            player.velocityX = 0; // Zatrzymujemy się na ścianie
        }

        // --- RUCH PO OSI Y ---
        double oldY = player.y;
        player.y += player.velocityY * deltaTime;
        playerOnGround = false;

        // Sprawdzamy kolizję po ruchu w Y
        if (isCollidingWithdDown()) {
            // Jeśli spadaliśmy (velocityY > 0), to uderzyliśmy w ziemię
            if (player.velocityY > 0) {
                playerOnGround = true;
            }
            player.y = oldY;      // Cofamy ruch w Y
            player.velocityY = 0; // Zatrzymujemy opadanie/skok
        }

        // Sprawdzamy kolizję po ruchu w Y
        if (isCollidingWithPlatofrms()) {
            // Jeśli spadaliśmy (velocityY > 0), to uderzyliśmy w ziemię
            if (player.velocityY > 0) {
                playerOnGround = true;
            }
            player.y = oldY;      // Cofamy ruch w Y
            player.velocityY = 0; // Zatrzymujemy opadanie/skok
            run = true;
        }
    }

    private boolean isCollidingWithPlatofrms() {
//        if(Colision.colisionWithListOfSprites(player, platforms)) return true;
        for (GameObject p : platforms) {
            if(Colision.checkCollision(player,p) && (player.y+ (double) player.height /2) < p.y && player.velocityY > 0) {
                return true;
            }
        }
        return false;
    }

    // Metoda pomocnicza sprawdzająca kolizję z listą bloków
    private boolean isCollidingWithWall() {

        if (Colision.checkCollision(player, leftBlocks)) return true;
        if (Colision.checkCollision(player, rightBlocks)) return true;
        // ... tutaj dodaj resztę bloków z listy 'blocks'
        return false;
    }

    private boolean isCollidingWithdDown() {
        if (Colision.checkCollision(player, downBlocks)) return true;
        // ... tutaj dodaj resztę bloków z listy 'blocks'
        return false;
    }


    public static void main(String[] args) {
        new Main2().start();
    }
}