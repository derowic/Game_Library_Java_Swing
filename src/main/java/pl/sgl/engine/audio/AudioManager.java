package pl.sgl.engine.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioManager {
    private static Map<String, AudioClip> sounds = new HashMap<>();
    private static boolean muteEvrything = false;

    public static void load(String name, String path) {
        sounds.put(name, new AudioClip(path));
    }

    public static void play(String name) {
        if(!muteEvrything) {
            if (sounds.containsKey(name)) {
                sounds.get(name).play();
            }
        }
    }

    public static void loop(String name) {
        if(!muteEvrything) {
            if (sounds.containsKey(name)) {
                sounds.get(name).loop();
            }
        }
    }

    public static void stop(String name) {
        if (sounds.containsKey(name)) {
            sounds.get(name).stop();
        }
    }

    public static void stopAll(){
        for (Map.Entry<String, AudioClip> entry : sounds.entrySet()) {
            String name = entry.getKey();
            AudioClip clip = entry.getValue();
            clip.stop();
        }

    }

    public static void clearAllData() {
        sounds.clear();
    }

    public static void mute() {
        muteEvrything = true;
        stopAll();
    }

    public static void unMute() {
        muteEvrything = false;
    }

    public boolean isLooping(String name) {
        if (sounds.containsKey(name)) {
            return sounds.get(name).isLooping();
        }
        return false;
    }

    // Metoda zwracająca listę nazw wszystkich zapętlonych dźwięków
    public static List<String> getLoopingSounds() {
        List<String> loopingList = new ArrayList<>();
        for (Map.Entry<String, AudioClip> entry : sounds.entrySet()) {
            if (entry.getValue().isLooping()) {
                loopingList.add(entry.getKey());
            }
        }
        return loopingList;
    }
}