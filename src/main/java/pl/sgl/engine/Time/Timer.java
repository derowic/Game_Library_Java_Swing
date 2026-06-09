package pl.sgl.engine.Time;

import pl.sgl.engine.Game;

public class Timer {
    public double time =0;
    public double delay =0;
    public boolean tick = false;
    public boolean run = false;

    public Timer (double delay) {
        this.delay = delay;
        Game.instance.addTimer(this);
    }

    public void update (double deltaTime) {
        if (run) {
            time += deltaTime;
            if (time >= delay) {
                time = 0;
                tick = true;
            }
        }
    }

    public void start() {
        run = true;
        time = 0;
        tick = false;
    }

    public void stop () {
        run = false;
    }

    public boolean check() {
        return tick;
    }
}
