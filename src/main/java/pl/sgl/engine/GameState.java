package pl.sgl.engine;

import pl.sgl.engine.TileMaps.TileMap;
import pl.sgl.engine.particleSystem.Particle;
import pl.sgl.engine.particleSystem.ParticleEmitter;
import pl.sgl.engine.ui.UIElement;
import pl.sgl.engine.ui.UIManager;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    public List<Primitive> entities = new ArrayList<>();
    public List<GameObject> sprites = new ArrayList<>();
    public List<ParticleEmitter> emitters = new ArrayList<>();
//    public List<UIElement> UIElements = new ArrayList<>();
    public TileMap tileMap;
    public UIManager uiManager = new UIManager();

    public double camX=0;
    public double camY=0;
    public double zoom = 1.0;
    public double lastZoom = 1.0;
// Zaktualizuj konstruktor GameState, aby przyjmował te wartości.

    public GameState(List<Primitive> primitives, List<Sprite> sprites, List<ParticleEmitter> emitters) {
        this.entities = new ArrayList<>(primitives);
        this.sprites = new ArrayList<>(sprites);
        this.emitters = emitters;
    }

    public GameState(List<ParticleEmitter> emitters){
        this.emitters = emitters;
        entities = new ArrayList<>();
        sprites = new ArrayList<>();
    }


    public GameState(List<GameObject> sprites, List<ParticleEmitter> emitters, UIManager ui, TileMap tileMap, double camX, double camY, double zoom, double lastZoom) {
//         public Particle(float x, float y, float vx, float vy, float life, float decay, Color color, float size) {
        List<ParticleEmitter> emitters2 = new ArrayList<>();
        for (ParticleEmitter emitter : emitters) {
            ParticleEmitter emit = new ParticleEmitter();

            for (Particle e : emitter.getActiveParticles()) {
                emit.particles.add(new Particle(e.x, e.y, e.vx, e.vy, e.life, e.decay, e.color, e.size));
            }

            emitters2.add(emit);
        }
        this.emitters = emitters2;
        this.entities = new ArrayList<>();
        this.sprites = new ArrayList<>(sprites);
        this.uiManager = ui;
        this.camX = camX;
        this.camY = camY;
        this.tileMap = tileMap;
        this.zoom = zoom;
        this.lastZoom = lastZoom;
    }

    public GameState() {

    }
}