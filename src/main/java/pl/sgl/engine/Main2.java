package pl.sgl.engine;

import pl.sgl.engine.GameTest.*;
import pl.sgl.engine.ui.Text;
import pl.sgl.engine.ui.UIElement;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Main2 extends Game {

    Player player;
    Sprite stoneBlock;
    Sprite downBlocks;
    Sprite leftBlocks;
    Sprite rightBlocks;
    PlatformManager platformManager;
    CoinManager coinManager;
    EnemyManager enemyManager;


    boolean run = false;

    public Main2() {
        //w 32 * 40  h: 32 * 30
        super("Test", 1280, 960, Color.BLACK);
        setRenderPixelArt();
        audio.mute();



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

        //coinManager after platforms bc it base on platforms posX
        platformManager = new PlatformManager();
        coinManager = new CoinManager(platformManager.platforms);

        player = new Player();
        addGameObject(player.sprite);

        enemyManager = new EnemyManager(platformManager.platforms);

        audio.load("bg_music",  "/textures/brackeys_platformer_assets/music/time_for_adventure.wav");
//        audio.load("shoot", "/audio/zap-hiphop-a.wav");

        audio.loop("bg_music"); // Start muzyki w tle


    }
    @Override
    protected void update() {
        if (keyboard.isKeyPressed(KeyEvent.VK_Q)) currentGame.cam.zoom += 0.1;
        if (keyboard.isKeyPressed(KeyEvent.VK_E)) currentGame.cam.zoom -= 0.1;

        if (run) {
            moveDown();
        }

        colisionWithEnv();

        playerInput();

        ColisionManager.coins_player(player, coinManager);
        ColisionManager.enemies_player(player, enemyManager);

        player.playerAnimationLogic();


        if (run) {
            platformManager.generatePlatforms();
        }
        cycle();

        super.update();
    }

    public void cycle () {
        for( Platform p : platformManager.platforms) {
            if (p.y <= platformManager.startPosY) {
                coinManager.recycle(p);
                enemyManager.recycle(p);
            }
        }
    }

    public void moveDown() {
        if (downBlocks.y > 1500) {
            downBlocks.y = 1500;
        } else {
            downBlocks.moveByVelocity();

        }
        platformManager.move(deltaTime);
        coinManager.move(deltaTime);
        enemyManager.move(deltaTime);
    }

    public void playerInput() {
        if (keyboard.isKeyDown(KeyEvent.VK_A)) {
            player.sprite.velocityX = -400;
            player.sprite.setScaleX(-4);
        }
        if (keyboard.isKeyDown(KeyEvent.VK_D)) {
            player.sprite.velocityX = 400;
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
            Game.instance.audio.play("jump");
        }

        if (keyboard.isKeyPressed(KeyEvent.VK_W) && player.playerStatus.equals("onGround")) {
            player.sprite.velocityY = -750 *1.5 ;
            player.playerStatus = "jumping";
            Game.instance.audio.play("jump");
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
            player.playerCollideWith = platformManager.platforms.get(indexOfPlatformPLayerHaveColision);
            run = true;
        }

        if(player.playerStatus.equals("falling") && player.doubleJump) {
            player.playerStatus = "jumping";
        }
    }

    private int isCollidingWithPlatofrms() {
//        if(Colision.colisionWithListOfSprites(player, platforms)) return true;
        for (int i = 0; i < platformManager.platforms.size(); i++) {
            GameObject p = platformManager.platforms.get(i);
            if (Colision.checkCollision(player.sprite, p) && (player.sprite.y + (double) player.sprite.getHeight()/2) < p.y && player.sprite.velocityY > 0) {
                return i; // Zwraca indeks platformy, z którą nastąpiła kolizja
            }
        }
        return -1; // Zwraca -1, jeśli nie wykryto żadnej kolizji
    }

    // Metoda pomocnicza sprawdzająca kolizję z listą bloków
    private boolean isCollidingWithWall() {

        if (Colision.checkCollision(player.sprite, leftBlocks)) return true;
        if (Colision.checkCollision(player.sprite, rightBlocks)) return true;

        return false;
    }

    private boolean isCollidingWithdDown() {
        if (Colision.checkCollision(player.sprite, downBlocks)) return true;

        return false;
    }


    public static void main(String[] args) {
        new Main2().start();
    }
}