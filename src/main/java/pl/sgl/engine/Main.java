package pl.sgl.engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Main extends Game {
    // Lista obiektów logicznych (w świecie gry)
//    private List<MyPlayer> players = new ArrayList<>();
//
    private BufferedImage playerShip;

    List<Sprite> sprites = new ArrayList<>();


    public Main() {
        super();
        // Ładujemy raz przy starcie
        playerShip = TextureLoader.load("/textures/ship2.png");
        sprites.add(new Sprite(playerShip, 300, 600));
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
