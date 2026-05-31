package pl.sgl.engine;

import pl.sgl.engine.GameTest.Coin;
import pl.sgl.engine.GameTest.Platform;
import pl.sgl.engine.GameTest.Player;
import pl.sgl.engine.animation.Animation;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main2 extends Game {

    Player player;
    Sprite stoneBlock;
    Sprite downBlocks;
    Sprite leftBlocks;
    Sprite rightBlocks;
    public List<Platform> platforms = new ArrayList<>();
    public List<Coin> coins = new ArrayList<>();
    int startPos;
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




        player = new Player();
        addGameObject(player.sprite);

        generatePlatforms();
    }
    @Override
    protected void update() {
        if (keyboard.isKeyDown(KeyEvent.VK_Q)) currentGame.cam.zoom += 0.1;
        if (keyboard.isKeyDown(KeyEvent.VK_E)) currentGame.cam.zoom -= 0.1;

        if (run) {
            movePlatformsDown();
        }
        colisionWithEnv();

        playerInput();
        player.playerAnimationLogic();


        if (run) {
            generatePlatforms();
        }

        super.update();
    }

    public void movePlatformsDown() {
        for (Platform p : platforms) {
            p.moveByVelocity(deltaTime);

            if (downBlocks.y > 1500) {
                downBlocks.y = 1500;
            } else {
                downBlocks.moveByVelocity(deltaTime);

            }
        }

        for (Coin s : coins) {
            s.moveByVelocity(deltaTime);
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
//            System.out.println(startPos);
//            System.out.println(randPosX);
            return  randPosX;
        } catch (Exception e) {
//            System.out.println("max: " + max + " min: " + min + " lenght "+ randPlatformLenght);
            System.out.println("Wystąpił inny, nieznany błąd: " + e.getMessage());
        } finally {
//            System.out.println("max: " + max + " min: " + min);
        }
        return 32;
    }

    public Platform getRandomPlatform(double basePos) {
        //64
        //1248
        int min0 = 4, max0 = 6;
        int randPlatformLenght= rand.nextInt((max0- min0) + 1) + min0;
        int min = 0;
        int max = 0;

        int randPosX = getRandomPlatformPosition(randPlatformLenght * 32);
        startPos = randPosX;


        Platform platform = new Platform("/textures/brackeys_platformer_assets/sprites/world_tileset.png", (float) randPosX, (float) (basePos -200));
        platform.setPivot(0,0);
        platform.setTextureRegion(128,16,16,16);
        platform.setSpriteSize(randPlatformLenght * 16,16, FillMode.TILE);
        platform.setScaleX(2);
        platform.setScaleY(2);
        platform.velocityY = 100;
//        platform.showHitBox = true;
        addGameObject(platform);

        Coin c = new Coin((float) ((float) platform.x + platform.width/2 *platform.scaleX), (float) platform.y, 0.1, 1);
        Animation coinAnim = new Animation("/textures/brackeys_platformer_assets/sprites/coin2.png",0,0, 16,16,12);
        c.addAnimation("base", coinAnim);
        c.setPosition(((float) platform.x + platform.width/2 *platform.scaleX), (float) platform.y - platform.height *2);
        c.playAnimationInCycle();
        c.scale(4,4);
        c.velocityY = 100;
        c.setPosition(player.sprite.x + player.sprite.width, 850);
        coins.add(c);
        addGameObject(c);

        return platform;
    }

    public void playerInput() {
        if (keyboard.isKeyDown(KeyEvent.VK_A)) {

            player.sprite.velocityX = -400;
//            if(player.velocityX <= -400) {
//                player.velocityX = -400;
//            }

            player.sprite.setScaleX(-4);
        }
        if (keyboard.isKeyDown(KeyEvent.VK_D)) {
            player.sprite.velocityX = 400;
//            if(player.velocityX >= 400) {
//                player.velocityX = 400;
//            }

            player.sprite.setScaleX(4);
        }

        if (!keyboard.isKeyDown(KeyEvent.VK_D) && !keyboard.isKeyDown(KeyEvent.VK_A)) {
            player.sprite.velocityX = 0;
        }

        if (keyboard.isKeyPressed(KeyEvent.VK_W) && player.doubleJump && (player.playerStatus.equals("jumping") || player.playerStatus.equals("falling"))) {
            player.sprite.velocityY = -750 *1.5;
            player.sprite.setAnimation("roll");
            player.sprite.playAnimation();
            player.playerStatus = "jumping";
            player.doubleJump = false;
        }

        if (keyboard.isKeyPressed(KeyEvent.VK_W) && player.playerStatus.equals("onGround")) {
            player.sprite.velocityY = -750 *1.5 ;
            player.playerStatus = "jumping";
        }
    }

    public void colisionWithEnv(){

        if (player.playerCollideWith != null && run) {
            // Obliczamy o ile platforma przesunęła się w tej klatce
            double deltaX = player.playerCollideWith.velocityX * deltaTime;
            double deltaY = player.playerCollideWith.velocityY * deltaTime;

            // Dodajemy to przesunięcie do gracza ZANIM obliczymy jego własny ruch
            player.sprite.x += deltaX;
            player.sprite.y += deltaY;
        }

        double oldX = player.sprite.x;
        player.sprite.x += player.sprite.velocityX * deltaTime;

        // Sprawdzamy kolizję po ruchu w X
        if (isCollidingWithWall()) {
            player.sprite.x = oldX;      // Cofamy ruch w X
            player.sprite.velocityX = 0; // Zatrzymujemy się na ścianie
        }

        // Sprawdzamy kolizję po ruchu w X
        int indexOfPlatformPLayerHaveColision =  isCollidingWithPlatofrms();
        if ( indexOfPlatformPLayerHaveColision > -1) {
            player.sprite.x = oldX;      // Cofamy ruch w X
            player.sprite.velocityX = 0; // Zatrzymujemy się na ścianie
        }

        // --- RUCH PO OSI Y ---

        player.sprite.velocityY += 25; // Stała grawitacji (dostosuj wartość)
        double oldY = player.sprite.y;
        player.sprite.y += player.sprite.velocityY * deltaTime;
        player.playerStatus = "falling";

        // Sprawdzamy kolizję po ruchu w Y
        if (isCollidingWithdDown()) {
            player.playerStatus = "onGround".trim();
            player.doubleJump = true;
            player.sprite.y = oldY;      // Cofamy ruch w Y
            player.sprite.velocityY = 0; // Zatrzymujemy opadanie/skok
            player.playerCollideWith = null;
        }

        // Sprawdzamy kolizję po ruchu w Y z plaftormami
        indexOfPlatformPLayerHaveColision = isCollidingWithPlatofrms();
        if (indexOfPlatformPLayerHaveColision > -1) {
            // Jeśli spadaliśmy (velocityY > 0), to uderzyliśmy w ziemię
            player.playerStatus = "onGround".trim();
            player.doubleJump = true;
            player.sprite.velocityY = 0;
            player.sprite.y = oldY;
            player.playerCollideWith = platforms.get(indexOfPlatformPLayerHaveColision);
            run = true;
        }

        if(player.playerStatus.equals("falling") && player.doubleJump) {
            player.playerStatus = "jumping";
        }
    }

    private int isCollidingWithPlatofrms() {
//        if(Colision.colisionWithListOfSprites(player, platforms)) return true;
        for (int i = 0; i < platforms.size(); i++) {
            GameObject p = platforms.get(i);
            if (Colision.checkCollision(player.sprite, p) && (player.sprite.y + (double) player.sprite.height) < p.y && player.sprite.velocityY > 0) {
                return i; // Zwraca indeks platformy, z którą nastąpiła kolizja
            }
        }
        return -1; // Zwraca -1, jeśli nie wykryto żadnej kolizji
    }

    // Metoda pomocnicza sprawdzająca kolizję z listą bloków
    private boolean isCollidingWithWall() {

        if (Colision.checkCollision(player.sprite, leftBlocks)) return true;
        if (Colision.checkCollision(player.sprite, rightBlocks)) return true;
        // ... tutaj dodaj resztę bloków z listy 'blocks'
        return false;
    }

    private boolean isCollidingWithdDown() {
        if (Colision.checkCollision(player.sprite, downBlocks)) return true;
        // ... tutaj dodaj resztę bloków z listy 'blocks'
        return false;
    }


    public static void main(String[] args) {
        new Main2().start();
    }
}