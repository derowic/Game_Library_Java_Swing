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
    public int x = 0;

    public Main() {
        super();
        // Ładujemy raz przy starcie
        playerShip = TextureLoader.load("/textures/ship.png");
    }
    @Override
    protected void update(double DT) {

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

        List<Sprite> sprites = new ArrayList<>();

        // Dodajemy sprite gracza do snapshota
        sprites.add(new Sprite(
                playerShip,
                (float)x, (float)300,
                (float)x+1, (float)300,
                64, 64, 0
        ));

        double velocity = 200.0;
        x += (int) (velocity * DT);
        if(x >600)
        {
            x=0;
        }

        this.currentSnapshot = new GameState(sprites);

        super.update(DT);
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
