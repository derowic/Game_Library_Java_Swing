package pl.sgl.engine;

import pl.sgl.engine.GameTest.Player;
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
import java.util.ArrayList;
import java.util.List;

public class Main2 extends Game {
    private AnimatedSprite playerWalk;
    private volatile int score = 0;
    Sprite s2;
    Sprite player;
    double x = 0;
    double y=0;

    public Main2() {
        super("Test", 1280, 720, Color.BLACK);
        // Ładujemy raz przy starcie
        s2 = new Sprite("/textures/ship2.png", 600, 500);

        s2.showHitBox = true;
       s2.setPivot(300, 100);
//        s2.rotation = 180;
        s2.setScaleX(0.25);
        s2.setScaleY(0.5);
        addGameObject(s2);

        player = new Sprite("/textures/ship2.png", 600, -300);

        player.showHitBox = true;
//        s2.scaleX =2 ;

        //obrót wokół środka górnej krawędzi
//        s2.setPivot(100, 100);
        //s2.rotation = 45;

        player.setScaleX(0.25);
        player.setScaleY(0.25);
        addGameObject(player);

        Animation an = new Animation("/textures/mario-walk.png",0,0,61,64,3);
        playerWalk = new AnimatedSprite( 600, 100, 0.1); // zmiana klatki co 0.1 sekundy
        playerWalk.addAnimation("walk", an);
        playerWalk.showHitBox = true;
//        playerWalk.rotation = 45;
//        playerWalk.scaleX = 2;
//        playerWalk.setScaleX(2);
        playerWalk.getRotatedShape();
        addGameObject(playerWalk);
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
            if (keyboard.isKeyDown(KeyEvent.VK_S)) currentGame.sprites.get(0).velocityY = 100;
            ;
            if (keyboard.isKeyDown(KeyEvent.VK_A)) s2.velocityX = -100;
            if (keyboard.isKeyDown(KeyEvent.VK_D)) s2.velocityX = 100;
            ;


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
            if (mouse.isButtonDown(MouseEvent.BUTTON1)) {
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
//                currentGame.sprites.get(1).visible = !currentGame.sprites.get(1).visible;
            }

            if (Colision.colisionWithListOfSprites(currentGame.sprites.get(1), currentGame.sprites)) {
//                System.out.println("Colision");
            }

            // Aktualizacja UI




            if (keyboard.isKeyDown(KeyEvent.VK_Q)) currentGame.zoom += 0.1;
            if (keyboard.isKeyDown(KeyEvent.VK_E)) currentGame.zoom -= 0.1;



        }


        if (keyboard.isKeyDown(KeyEvent.VK_T)) {
            currentGame.sprites.get(0).scaleX += 0.05;
//            currentGame.sprites.get(0).scaleY += 0.05;
        }

        if (keyboard.isKeyDown(KeyEvent.VK_G)) {
            currentGame.sprites.get(0).scaleX -= 0.05;
            currentGame.sprites.get(0).scaleY -= 0.05;
        }

        if (keyboard.isKeyDown(KeyEvent.VK_Y)) {
            currentGame.sprites.get(0).rotation += 1;
        }

        if (keyboard.isKeyDown(KeyEvent.VK_H)) {
            currentGame.sprites.get(0).rotation -= 1;
        }
//        player.updateCalc(deltaTime, sprites);

        if(Colision.checkCollision(s2, player)) {
            System.out.println("colsion ");
        }

        movePlayer();
        moveCamera();
        super.update();
    }

    public static void main(String[] args) {
        new Main2().start();
    }

    public void moveCamera() {
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
    }

    public void movePlayer() {
        if (keyboard.isKeyDown(KeyEvent.VK_LEFT)) {
            player.velocityX = -10;
        }
        if (keyboard.isKeyDown(KeyEvent.VK_RIGHT)) {
            player.velocityX = 100;
        }

        if (keyboard.isKeyDown(KeyEvent.VK_UP)) {
            player.velocityY = -1;
        }
        if (keyboard.isKeyDown(KeyEvent.VK_DOWN)) {
            player.velocityY = 1;
        }
    }
}