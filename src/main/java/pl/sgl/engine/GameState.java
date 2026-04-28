package pl.sgl.engine;

import pl.sgl.engine.TileMaps.TileMap;
import pl.sgl.engine.ui.UIElement;
import pl.sgl.engine.ui.UIManager;

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
//    public List<UIElement> UIElements = new ArrayList<>();
    public TileMap tileMap;
    public UIManager uiManager = new UIManager();

    public double camX=0;
    public double camY=0;
    public double zoom = 1.0;
    public double lastZoom = 1.0;
// Zaktualizuj konstruktor GameState, aby przyjmował te wartości.

    public GameState(List<Primitive> primitives, List<Sprite> sprites) {
        this.entities = new ArrayList<>(primitives);
        this.sprites = new ArrayList<>(sprites);
    }

    public GameState(){
        entities = new ArrayList<>();
        sprites = new ArrayList<>();
    }


    public GameState(List<GameObject> sprites, UIManager ui, TileMap tileMap, double camX, double camY, double zoom, double lastZoom) {
        this.entities = new ArrayList<>();
        this.sprites = new ArrayList<>(sprites);
        this.uiManager = ui;
        this.camX = camX;
        this.camY = camY;
        this.tileMap = tileMap;
        this.zoom = zoom;
        this.lastZoom = lastZoom;
    }
}