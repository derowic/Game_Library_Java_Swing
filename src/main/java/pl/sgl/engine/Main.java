package pl.sgl.engine;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Game {
    // Lista obiektów logicznych (w świecie gry)
//    private List<MyPlayer> players = new ArrayList<>();
//
    public Main(){
        super();
    }
    @Override
    protected void update() {

        // 1. Logika poruszania
//        for(MyPlayer p : players) {
//            p.move();
//        }

        // 2. Przygotowanie danych do Snapshota
        List<Primitive> toRender = new ArrayList<>();
        //for(MyPlayer p : players) {
            toRender.add(new Primitive(
                    0,0,50,0,
                    50, 50, Color.RED, "RECT"
            ));
        //}

        // 3. Przesłanie do silnika
        //this.publishState(new GameState(toRender));
        this.currentSnapshot = new GameState(toRender, new ArrayList<>());

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
