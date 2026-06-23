package pl.sgl.engine.GameTest;

import pl.sgl.engine.*;
import pl.sgl.engine.Time.Timer;
import pl.sgl.engine.audio.AudioManager;
import pl.sgl.engine.ui.Text;
import pl.sgl.engine.ui.UIManager;

import java.awt.event.KeyEvent;

public class GameScene extends Scene {


    public static Player player;
    PlatformManager platformManager;
    CoinManager coinManager;
    EnemyManager enemyManager;
    UIManager startScreen;
    EnvManager envManager;
    static double fallingSpeed = 100;
    Timer sppedTimer = new Timer(3);
    Text platformSpeed;


    boolean run = false;

    public GameScene(String name) {
        super(name);
    }

    @Override
    public void init() {
        envManager = new EnvManager();
        //coinManager after platforms bc it base on platforms posX
        platformManager = new PlatformManager();
        coinManager = new CoinManager(platformManager.platforms);

        player = new Player();

        enemyManager = new EnemyManager(platformManager.platforms);

        AudioManager.load("bg_music",  "/textures/brackeys_platformer_assets/music/time_for_adventure.wav");

        AudioManager.loop("bg_music"); // Start muzyki w tle

        platformSpeed = new Text("Platform speed", 20, 900, 20);
        ui.addUi(platformSpeed);

    }

    @Override
    public void cleanEverything() {
        super.cleanEverything();
        init();
    }

    @Override
    public void update(double dt) {


        if (Game.keyboard.isKeyPressed(KeyEvent.VK_Q)) Game.instance.camera.zoomIn(0.1);
        if (Game.keyboard.isKeyPressed(KeyEvent.VK_E))  Game.instance.camera.zoomOut(-0.1);

        if (Game.keyboard.isKeyPressed(KeyEvent.VK_Z)) SceneManager.setScene("Game2");
        if (Game.keyboard.isKeyPressed(KeyEvent.VK_X))  SceneManager.setScene("Game");

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
//        cycle();

        if(fallingSpeed <= 250) {
            fallingSpeed += 0.8 * dt;
            platformSpeed.setText("Platform speed " + fallingSpeed);

            if (sppedTimer.check()) {


//                System.out.println("time, sped falling "+ fallingSpeed);
                sppedTimer.reset();

                for(GameObject o : objects) {
                    o.velocityY = fallingSpeed;
                }
            }
        }



        super.update(dt);

//        System.out.println(player.sprite.y);
    }

//    public void cycle () {
//        for( Platform p : platformManager.platforms) {
//            if (p.y <= platformManager.startPosY) {
//                coinManager.recycle(p);
//                enemyManager.recycle(p);
//            }
//        }
//    }

    public void moveDown() {
        envManager.move();
        platformManager.move();
        coinManager.move();
        enemyManager.move();

    }

    public void playerInput() {
        if (Game.keyboard.isKeyDown(KeyEvent.VK_A)) {
            player.sprite.velocityX = -400;
            player.sprite.setScaleX(-4);
        }
        if (Game.keyboard.isKeyDown(KeyEvent.VK_D)) {
            player.sprite.velocityX = 400;
            player.sprite.setScaleX(4);
        }

        if (!Game.keyboard.isKeyDown(KeyEvent.VK_D) && !Game.keyboard.isKeyDown(KeyEvent.VK_A)) {
            player.sprite.velocityX = 0;
        }

        if (Game.keyboard.isKeyPressed(KeyEvent.VK_W) && player.doubleJump && (player.playerStatus.equals("jumping") || player.playerStatus.equals("falling"))) {
            player.sprite.velocityY = -750 * 1.5;
            player.sprite.setAnimation("roll");
            player.sprite.playAnimation();
            player.playerStatus = "jumping";
            player.doubleJump = false;
            AudioManager.play("jump");
        }

        if (Game.keyboard.isKeyPressed(KeyEvent.VK_W) && player.playerStatus.equals("onGround")) {
            player.sprite.velocityY = -750 *1.5 ;
            player.playerStatus = "jumping";
            AudioManager.play("jump");
        }

//        if (Game.keyboard.isKeyPressed(KeyEvent.VK_F)) {
//            Game.changeDisplayMode("fullscreen");
//        }
//        if (Game.keyboard.isKeyPressed(KeyEvent.VK_ESCAPE)) {
//            Game.changeDisplayMode("window");
//        }
        if (Game.keyboard.isKeyPressed(KeyEvent.VK_P)) {
            Game.pause();
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
        if (envManager.isCollidingWithWall(player)) {
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
        if (envManager.isCollidingWithdDown(player)) {
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
}
