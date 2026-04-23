package pl.sgl.engine;

import pl.sgl.engine.animation.AnimatedSprite;
import pl.sgl.engine.animation.Animation;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Engine {
    // Lista obiektów logicznych (w świecie gry)
//    private List<MyPlayer> players = new ArrayList<>();
//
    private BufferedImage playerShip;
    private AnimatedSprite playerWalk;


    double x = 0;
    double y=0;


    public Main() {
        super("Test", 1280, 720);
        // Ładujemy raz przy starcie
//        playerShip = TextureLoader.load();
        Sprite s2 = new Sprite("/textures/ship2.png", 470, 100);
//        s2.rotate(45);
        s2.rotation = 45;
        s2.showHitBox = true;
        addGameObject(s2);
        stressTest();

//        for (GameObject s : gameObjects) {
//            Rectangle rec = s.getCalculatedAutoHitBoxes();
////
//            System.out.println((int) s.x);
//            System.out.println((int) s.y);
//            System.out.println((int) (s.x + rec.width));
//            System.out.println((int) (s.y + rec.width));
//        }



//        audio.load("bg_music", "/audio/alex-productions-racing-sport-gaming-racing(chosic.com).wav");
//        audio.load("shoot", "/audio/zap-hiphop-a.wav");
//
//        audio.loop("bg_music"); // Start muzyki w tle


    }
    @Override
    protected void update() {
//        if(sprites.get(0).y < -50)
//        {
////            sprites.get(0).y = 600;
//        }

        if(currentGame.sprites.get(1).checkCollision((Sprite) currentGame.sprites.get(0))) {
//            System.out.println("Colision");
        }

        if (input.isKeyDown(KeyEvent.VK_ESCAPE)) {
            //this.stopRunning();
            this.setWindow();
//            System.exit(0);
        };


        if (input.isKeyDown(KeyEvent.VK_W))  currentGame.sprites.get(0).velocityY = -100;;
        if (input.isKeyDown(KeyEvent.VK_S))  currentGame.sprites.get(0).velocityY = 100;;
        if (input.isKeyDown(KeyEvent.VK_A)) currentGame.sprites.get(0).velocityX = -100;;
        if (input.isKeyDown(KeyEvent.VK_D)) currentGame.sprites.get(0).velocityX = 100;;

        if (input.isKeyDown(KeyEvent.VK_LEFT))  {

            System.out.println("left");
            System.out.println(this.currentSnapshot.camX);
            this.x -=1;
        };
        if (input.isKeyDown(KeyEvent.VK_RIGHT))  this.x+=1;;;
        if (input.isKeyDown(KeyEvent.VK_UP)) this.y -=1;
        if (input.isKeyDown(KeyEvent.VK_DOWN)) this.y +=1;;

        currentGame.camX = x;
        currentGame.camY = y;

        if (!input.isKeyDown(KeyEvent.VK_W) && !input.isKeyDown(KeyEvent.VK_S))  currentGame.sprites.get(0).velocityY = 0;;
        if (!input.isKeyDown(KeyEvent.VK_D) && !input.isKeyDown(KeyEvent.VK_A)) currentGame.sprites.get(0).velocityX = 0;;

        if (input.isKeyDown(KeyEvent.VK_SPACE)) {
            // Strzał, skok itp.
        }

        // Sprawdzenie pozycji
        int mx = mouse.getX();
        int my = mouse.getY();

        // Reakcja na lewy przycisk myszy (MouseEvent.BUTTON1)
        if (mouse.isButtonDown(java.awt.event.MouseEvent.BUTTON1)) {
            // Przykładowo: postać teleportuje się tam, gdzie klikniemy
           currentGame.sprites.get(0).x = mx;
           currentGame.sprites.get(0).y = my;
        }

        Rectangle enemyHitbox = new Rectangle((int)currentGame.sprites.get(1).x, (int)currentGame.sprites.get(1).y, 50, 50);

        // Sprawdź czy kursor jest wewnątrz hitboxa I czy kliknięto przycisk
        if (enemyHitbox.contains(mouse.getX(), mouse.getY())) {
            if (mouse.isButtonDown(1)) {
                System.out.println("Trafiłeś przeciwnika!");
            }
        }

        if (input.isKeyPressed(KeyEvent.VK_SPACE)) {
            audio.play("shoot"); // Dźwięk strzału przy spacji
        }


        super.update();
    }

    public static void main(String[] args) {
        new Main().start();
    }

    public void stressTest()
    {
        Random rand = new Random();
        Animation an = new Animation("/textures/mario-walk.png",0,0, 100, 100, 3);
//        BufferedImage[] frames = TextureLoader.loadSheet();
        for(int i =0; i<1; i++) {

            playerWalk = new AnimatedSprite(0.1, 400, 50); // zmiana klatki co 0.1 sekundy
            playerWalk.addAnimation("walk", an);
            playerWalk.showHitBox = true;
            addGameObject(playerWalk);
        }
    }
}
