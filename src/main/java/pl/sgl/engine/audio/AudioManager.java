package pl.sgl.engine.audio;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private Map<String, AudioClip> sounds = new HashMap<>();

    public void load(String name, String path) {
        sounds.put(name, new AudioClip(path));
    }

    public void play(String name) {
        if (sounds.containsKey(name)) {
            sounds.get(name).play();
        }
    }

    public void loop(String name) {
        if (sounds.containsKey(name)) {
            sounds.get(name).loop();
        }
    }

    public void stop(String name) {
        if (sounds.containsKey(name)) {
            sounds.get(name).stop();
        }
    }
}