package pl.sgl.engine.audio;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private Map<String, AudioClip> sounds = new HashMap<>();
    private boolean muteEvrything = false;

    public void load(String name, String path) {
        sounds.put(name, new AudioClip(path));
    }

    public void play(String name) {
        if(!muteEvrything) {
            if (sounds.containsKey(name)) {
                sounds.get(name).play();
            }
        }
    }

    public void loop(String name) {
        if(!muteEvrything) {
            if (sounds.containsKey(name)) {
                sounds.get(name).loop();
            }
        }
    }

    public void stop(String name) {
        if (sounds.containsKey(name)) {
            sounds.get(name).stop();
        }
    }

    public void stopAll(){
        for (Map.Entry<String, AudioClip> entry : sounds.entrySet()) {
            String name = entry.getKey();
            AudioClip clip = entry.getValue();
            clip.stop();
        }

    }

    public void clearAllData() {
        sounds.clear();
    }

    public void mute() {
        muteEvrything = true;
    }

    public void unMute() {
        muteEvrything = false;
    }
}