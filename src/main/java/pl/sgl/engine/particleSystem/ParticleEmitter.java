package pl.sgl.engine.particleSystem;

import pl.sgl.engine.animation.Animation;

import java.util.ArrayList;
import java.awt.Color;
import java.util.List;


public class ParticleEmitter {
    public List< Particle> particles = new ArrayList<>();
    private int maxParticles;
    private int x;
    private int y;
    private Animation anim;

    public ParticleEmitter() {

    }

    public ParticleEmitter(int particlesMaxNumber) {
        this.maxParticles = particlesMaxNumber;

    }


    public ParticleEmitter(int particlesMaxNumber, float x, float y, float vx, float vy, float life, Color color, float size) {
        this.maxParticles = particlesMaxNumber;
        for (int i = 0; i < particlesMaxNumber; i++) particles.add(new Particle(x,y,vx,vy,life, (1.0f / life),color,size));
    }

    public void emit(float x, float y, float vx, float vy, float life, Color color, float size) {
        for (Particle p : particles) {
            if (!p.active) {
                p.active = true;
                p.x = p.lastX = x;
                p.y = p.lastY = y;
                p.vx = vx;
                p.vy = vy;
                p.life = 1.0f;
                p.decay = 1.0f / life;
                p.color = color;
                p.size = size;
                return;
            }
        }
    }

    public void update(double dt) {
        for (Particle p : particles) p.update(dt);
    }

    // Do przesyłania do snapshota - przesyłamy tylko aktywne dane (kopie)
    public List<Particle> getActiveParticles() {
        List<Particle> data = new ArrayList<>();
        for (Particle p : particles) {
            if (p.active) data.add(new Particle(p));
        }
        return data;
    }
}