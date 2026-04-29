package pl.sgl.engine.particleSystem;

import java.awt.*;

public class Particle {
    public float x, y, lastX, lastY;
    public float vx, vy;
    public float life;        // czas życia (np. 1.0 do 0.0)
    public float decay;       // szybkość znikania
    public Color color;
    public float size;
    public boolean active = true;

    public Particle(float x, float y, float vx, float vy, float life, float decay, Color color, float size) {
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = x;
        this.vx = vx;
        this.vy = vy;
        this.life = life;
        this.decay = decay;
        this.color = color;
        this.size = size;
    }

    public Particle(Particle p) {
        this.x =  p.x;
        this.y =  p.y;
        this.lastX =  p.x;
        this.lastY =  p.y;
        this.vx =  p.vx;
        this.vy =  p.vy;
        this.life =  p.life;
        this.decay =  p.decay;
        this.color =  p.color;
        this.size =  p.size;
    }


    public void update(double dt) {
        if (!active) return;

//        System.out.println("delta time" + dt);
        lastX = x;
        lastY = y;
        x += vx * dt;
        y += vy * dt;
        life -= decay * dt;
//        System.out.println(x);

        //System.out.println(life);

        if (life <= 0) active = false;
    }
}