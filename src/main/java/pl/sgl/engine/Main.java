package pl.sgl.engine;

import pl.sgl.engine.TextureLoader;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
    
public class Main extends Game {
    // Lista obiektów logicznych (w świecie gry)
//    private List<MyPlayer> players = new ArrayList<>();
//
    private BufferedImage playerShip;
    private AnimatedSprite playerWalk;

    List<GameObject> sprites = new ArrayList<>();


    public Main() {
        super();
        // Ładujemy raz przy starcie
        playerShip = TextureLoader.load("/textures/ship2.png");
        sprites.add(new Sprite(playerShip, 300, 600));
        sprites.get(0).scaleX = 0.25;
        sprites.get(0).rotation = 45;

        BufferedImage[] frames = TextureLoader.loadSheet("/textures/mario-walk.png", 100,100);
        playerWalk = new AnimatedSprite(frames, 0.1, 200,200); // zmiana klatki co 0.1 sekundy
        sprites.add(playerWalk);
    }
    @Override
    protected void update() {

        // 1. Logika poruszania
//        for(MyPlayer p : players) {
//            p.move();
//        }

//        // 2. Przygotowanie danych do Snapshota
//        List<Primitive> toRender = new ArrayList<>();
//        //for(MyPlayer p : players) {
//            toRender.add(new Primitive(
//                    0,0,50,0,
//                    50, 50, Color.RED, "RECT"
//            ));
//        //}
//
//        // 3. Przesłanie do silnika
//        //this.publishState(new GameState(toRender));
//        this.currentSnapshot = new GameState(toRender, new ArrayList<>());


        if(sprites.get(0).y < -50)
        {
            sprites.get(0).y = 600;
        }
        sprites.get(0).velocityY = -200;
        // Dodajemy sprite gracza do snapshota

        //sprites.get(0).velocityY = -200;

//        // 1. Aktualizujemy czas animacji
//        playerWalk.update(deltaTime);
//
//        // 2. Pobieramy aktualną grafikę
//        BufferedImage currentImg = playerWalk.getCurrentFrame();
//
//        // 3. Dodajemy do Snapshota
//        List<Sprite> sprites = new ArrayList<>();
//        sprites.add(this.sprites.get(0));
//        sprites.add(new Sprite(
//                currentImg,
//                (float)200, (float)200
//        ));

        this.currentSnapshot = new GameState(sprites);

        super.update();
    }

    public static void main(String[] args) {
        new Main().start();
    }

//    @Override
//    protected void renderOverlay(Graphics2D g) {
//        g.setColor(Color.WHITE);
//        g.drawString("Punkty: 100", 10, 20);
//    }
}
