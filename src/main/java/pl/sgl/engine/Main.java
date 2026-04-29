package pl.sgl.engine;

import pl.sgl.engine.TileMaps.TileMap;
import pl.sgl.engine.TileMaps.TileMapLoader;
import pl.sgl.engine.animation.AnimatedSprite;
import pl.sgl.engine.animation.Animation;
import pl.sgl.engine.particleSystem.ParticleEmitter;
import pl.sgl.engine.ui.*;
import pl.sgl.engine.ui.Button;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Main extends Game {
    // Lista obiektów logicznych (w świecie gry)
//    private List<MyPlayer> players = new ArrayList<>();
//
    private BufferedImage playerShip;
    private AnimatedSprite playerWalk;
    private UIElement startButton;
    private UIElement scoreLabel;
    private UIElement inputField;
    private UIElement slider;
    private volatile int score = 0;
    private TileMap tileMap;

    private TileMap level1;

    private ParticleEmitter emitter;


    double x = 0;
    double y=0;
    private boolean fullscreenKeyPressed = false;


    public Main() {
        super("Test", 1280, 720);
        // Ładujemy raz przy starcie
        Sprite s2 = new Sprite("/textures/ship2.png", 640, 100);

        s2.showHitBox = true;
//        s2.scaleX =2 ;

        //obrót wokół środka górnej krawędzi
        s2.setPivot((s2.width*s2.scaleX)/2,0);
        s2.rotation = 45;
        s2.setScaleX(2);
        addGameObject(s2);

        Animation an = new Animation("/textures/mario-walk.png",0,0,61,64,3);
        playerWalk = new AnimatedSprite(0.1, 400, 50); // zmiana klatki co 0.1 sekundy
        playerWalk.addAnimation("walk", an);
        playerWalk.showHitBox = true;
        playerWalk.rotation = 45;
//        playerWalk.scaleX = 2;
        playerWalk.setScaleX(2);
        addGameObject(playerWalk);

        startButton = new Button("ZAGRAJ", 300, 250, 200, 50);
        scoreLabel = new Text( "PUNKTY: 0", 20, 40, 24);
        inputField = new InputField(600, 450, 200, 25, 12);
        slider = new Slider(600, 550, 200, 50);

        currentGame.uiManager.addElement(startButton);
        currentGame.uiManager.addElement(scoreLabel);
        currentGame.uiManager.addElement(inputField);
        currentGame.uiManager.addElement(slider);

//        currentGame.tileMap = new TileMap("", 64);


        // 1. Wczytujemy dane kafelków z JSON
        int[][] data = TileMapLoader.loadMap("/tileMaps/dungeon3.json");
        // 2. Tworzymy obiekt TileMap (używając Twojej klasy, którą pisaliśmy wcześniej)
        // Zakładamy, że konstruktor przyjmuje tablicę int[][], ścieżkę do obrazka i rozmiar kafelka
        level1 = new TileMap(data, "/tileMaps/dungeon_tile.png", 16);
        level1.collidableTiles = TileMapLoader.loadCollisionsFromTileset("/tileMaps/dungeon3.json");
        System.out.println(level1.collidableTiles);
        currentGame.tileMap = level1;
//        audio.load("bg_music", "/audio/alex-productions-racing-sport-gaming-racing(chosic.com).wav");
//        audio.load("shoot", "/audio/zap-hiphop-a.wav");
//
//        audio.loop("bg_music"); // Start muzyki w tle

//        tileMap = new TileMap();
//        tileMap.dr

        emitter = new ParticleEmitter(100, 20, 500,25,-25,1, Color.ORANGE, 50);
        addGameObject(emitter);


    }
    @Override
    protected void update() {

        if (!currentGame.uiManager.isKeyboardCaptured() && !currentGame.uiManager.isMouseCaptured()) {
//            System.out.println("mouse " + currentGame.uiManager.isMouseCaptured());
            // W pętli update
            if (keyboard.isKeyPressed(KeyEvent.VK_ESCAPE)) {
                System.out.println("switch");
                this.toggleFullScreen("window");
//            System.exit(0);
            }

            if (keyboard.isKeyPressed(KeyEvent.VK_F)) {
                System.out.println("switch2");
                this.toggleFullScreen("fullscreen");
            }

            if (keyboard.isKeyDown(KeyEvent.VK_W)) currentGame.sprites.get(0).velocityY = -100;
            ;
            if (keyboard.isKeyDown(KeyEvent.VK_S)) currentGame.sprites.get(0).velocityY = 100;
            ;
            if (keyboard.isKeyDown(KeyEvent.VK_A)) currentGame.sprites.get(0).velocityX = -100;
            ;
            if (keyboard.isKeyDown(KeyEvent.VK_D)) {
                currentGame.sprites.get(0).velocityX = 100;
            }
            ;

            if (keyboard.isKeyDown(KeyEvent.VK_LEFT)) {
                this.x -= 1;
            }
            ;

            if (keyboard.isKeyDown(KeyEvent.VK_RIGHT)) this.x += 1;
            ;
            ;
            if (keyboard.isKeyDown(KeyEvent.VK_UP)) this.y -= 1;
            if (keyboard.isKeyDown(KeyEvent.VK_DOWN)) this.y += 1;
            ;

            currentGame.camX = x;
            currentGame.camY = y;

            if (!keyboard.isKeyDown(KeyEvent.VK_W) && !keyboard.isKeyDown(KeyEvent.VK_S))
                currentGame.sprites.get(0).velocityY = 0;
            ;
            if (!keyboard.isKeyDown(KeyEvent.VK_D) && !keyboard.isKeyDown(KeyEvent.VK_A))
                currentGame.sprites.get(0).velocityX = 0;
            ;

            if (keyboard.isKeyPressed(KeyEvent.VK_SPACE)) {
                // Strzał, skok itp.
            }

            // Sprawdzenie pozycji
            int mx = mouse.getWorldX();
            int my = mouse.getWorldY();

            // Reakcja na lewy przycisk myszy (MouseEvent.BUTTON1)
            if (mouse.isButtonDown(java.awt.event.MouseEvent.BUTTON1)) {
                // Przykładowo: postać teleportuje się tam, gdzie klikniemy
                currentGame.sprites.get(0).x = mx;
                currentGame.sprites.get(0).y = my;
            }

            Rectangle enemyHitbox = new Rectangle((int) currentGame.sprites.get(1).x, (int) currentGame.sprites.get(1).y, 50, 50);

            // Sprawdź czy kursor jest wewnątrz hitboxa I czy kliknięto przycisk
            if (enemyHitbox.contains(mouse.getUIX(), mouse.getUIY())) {
                if (mouse.isButtonDown(1)) {
                    System.out.println("Trafiłeś przeciwnika!");
                }
            }

            if (keyboard.isKeyPressed(KeyEvent.VK_SPACE)) {
                audio.play("shoot"); // Dźwięk strzału przy spacji
            }

            if (keyboard.isKeyPressed(KeyEvent.VK_H)) {
//            audio.play("shoot"); // Dźwięk strzału przy spacji
                currentGame.sprites.get(1).visible = !currentGame.sprites.get(1).visible;
            }

            if (Colision.colisionWithListOfSprites(currentGame.sprites.get(1), currentGame.sprites)) {
                System.out.println("Colision");
            }

            // Aktualizacja UI



            boolean tmp = false;
            tmp = currentGame.tileMap.isCollidingWithWall(
                    mx,
                    my,
                    20,
                    20
//                currentGame.sprites.get(0).x,
//                currentGame.sprites.get(0).y,
//                currentGame.sprites.get(0).width,
//                currentGame.sprites.get(0).height
            );

            if (tmp == true) {
//                System.out.println("kolizja z tilemapa");
            }

//        System.out.println(currentGame.sprites.get(0).x);

//        System.out.println(tmp);

            if (keyboard.isKeyDown(KeyEvent.VK_Q)) currentGame.zoom += 0.1;
            if (keyboard.isKeyDown(KeyEvent.VK_E)) currentGame.zoom -= 0.1;


            if (mouse.isButtonPressed(MouseEvent.BUTTON1)) {
                for (int i = 0; i < 50; i++) {
                    float angle = (float) (Math.random() * Math.PI * 2);
                    float speed = (float) ((Math.random() * 200 + 50));
                    emitter.emit(200, 500, (float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed, 0.5f, Color.ORANGE, 5);
                }

                System.out.println("emit");
            }
        }

        if (mouse.isButtonPressed(MouseEvent.BUTTON1) && startButton.isClicked(mouse)) {
            score++;
            scoreLabel.text = "PUNKTY: " + score;
        }

        super.update();
    }

    public static void main(String[] args) {
        new Main().start();
    }
}