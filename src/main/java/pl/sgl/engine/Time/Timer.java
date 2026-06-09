package pl.sgl.engine.Time;

import pl.sgl.engine.Game;

public class Timer {
    public double time =0;
    public double delay =0;
    public boolean tick = false;

    public Timer () {
        Game.instance.addTimer(this);
    }

    public void update (double deltaTime) {
        time += deltaTime;
        if(time >= delay) {
            time = 0;
            tick = true;
        }
    }

    public boolean check() {
        return tick;
    }

}
