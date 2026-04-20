package pl.sgl.engine;

import pl.sgl.engine.Animation.AnimatedSprite;
import pl.sgl.engine.Animation.Animation;

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

    List<GameObject> sprites = new ArrayList<>();


    public Main() {
        super();
        // Ładujemy raz przy starcie
//        playerShip = TextureLoader.load();
        Sprite s2 = new Sprite("/textures/ship2.png", 470, 100);
//        s2.rotate(45);
        s2.rotation = 45;
        sprites.add(s2);
//        sprites.get(0).scaleX = 0.25;

//        sprites.get(0).velocityY = -200;
        sprites.get(0).showHitBox = true;

        stressTest();

        for (GameObject s : sprites) {
            Rectangle rec = s.getCalculatedAutoHitBoxes();
//
            System.out.println((int) s.x);
            System.out.println((int) s.y);
            System.out.println((int) (s.x + rec.width));
            System.out.println((int) (s.y + rec.width));

        }
    }
    @Override
    protected void update() {
//        if(sprites.get(0).y < -50)
//        {
////            sprites.get(0).y = 600;
//        }

        if(sprites.get(1).checkCollision((Sprite) sprites.get(0))) {
            System.out.println("Colision");
        }

        if (input.isKeyDown(KeyEvent.VK_W))  sprites.get(0).velocityY = -100;;
        if (input.isKeyDown(KeyEvent.VK_S))  sprites.get(0).velocityY = 100;;
        if (input.isKeyDown(KeyEvent.VK_A)) sprites.get(0).velocityX = -100;;
        if (input.isKeyDown(KeyEvent.VK_D)) sprites.get(0).velocityX = 100;;

        if (!input.isKeyDown(KeyEvent.VK_W) && !input.isKeyDown(KeyEvent.VK_S))  sprites.get(0).velocityY = 0;;
        if (!input.isKeyDown(KeyEvent.VK_D) && !input.isKeyDown(KeyEvent.VK_A)) sprites.get(0).velocityX = 0;;

        if (input.isKeyDown(KeyEvent.VK_SPACE)) {
            // Strzał, skok itp.
        }


        this.currentSnapshot = new GameState(sprites);

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
            sprites.add(playerWalk);
        }
    }
}
