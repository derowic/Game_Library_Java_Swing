package main.java.pl.sgl.engine;

import java.util.ArrayList;
import java.util.List;

public class GameState {
//    public final double x, y, lastX, lastY;
//    public final boolean didTeleport;
//
//    public GameState(double x, double y, double lastX, double lastY, boolean didTeleport) {
//        this.x = x; this.y = y;
//        this.lastX = lastX; this.lastY = lastY;
//        this.didTeleport = didTeleport;
//    }
//
//    public GameState(double x, double y, double lastX, double lastY) {
//        this.x = x; this.y = y;
//        this.lastX = lastX; this.lastY = lastY;
//        this.didTeleport = false;
//    }
    // Kolekcja wszystkich obiektów do narysowania w danej klatce
    public final List<Primitive> entities;

    public GameState(List<Primitive> entities) {
        // Robimy kopię listy, aby była niemutowalna
        this.entities = new ArrayList<>(entities);
    }

    public GameState(){
        entities = new ArrayList<>();
    }
}
