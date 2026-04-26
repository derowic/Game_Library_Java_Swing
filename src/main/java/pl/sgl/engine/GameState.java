package pl.sgl.engine;

import pl.sgl.engine.TileMaps.TileMap;
import pl.sgl.engine.ui.UIElement;

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
//    // Kolekcja wszystkich obiektów do narysowania w danej klatce
//    public final List<Primitive> entities;
//
//    public GameState(List<Primitive> entities) {
//        // Robimy kopię listy, aby była niemutowalna
//        this.entities = new ArrayList<>(entities);
//    }
    public final List<Primitive> entities;
    public final List<GameObject> sprites;
    public List<UIElement> UIElements = new ArrayList<>();
    public TileMap tileMap;

    public double camX=0;
    public double camY=0;

    public GameState(List<Primitive> primitives, List<Sprite> sprites) {
        this.entities = new ArrayList<>(primitives);
        this.sprites = new ArrayList<>(sprites);
    }

    public GameState(){
        entities = new ArrayList<>();
        sprites = new ArrayList<>();
    }


    public GameState(List<GameObject> sprites, List<UIElement> UIElements, TileMap tileMap, double camX, double camY) {
        this.entities = new ArrayList<>();
        this.sprites = new ArrayList<>(sprites);
        this.UIElements = new ArrayList<>(UIElements);
        this.camX = camX;
        this.camY = camY;
        this.tileMap = tileMap;
    }
}
