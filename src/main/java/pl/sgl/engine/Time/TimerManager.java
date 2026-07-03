package pl.sgl.engine.Time;

import java.util.ArrayList;
import java.util.List;

public class TimerManager {
    private static List<Timer> timers = new ArrayList<>();

    public static List<Timer> getTimers() {
        return timers;
    }

    public static void setTimers(List<Timer> timers) {
        TimerManager.timers = timers;
    }

    public static void addTimer(Timer timer) {
        timers.add(timer);
    }

    public static void dispose() {
        timers.clear();
    }
}
