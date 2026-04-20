package pl.sgl.engine.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class AudioClip {
    private Clip clip;

    public AudioClip(String path) {
        try {
            // Pobieramy plik z resources
            InputStream is = AudioClip.class.getResourceAsStream(path);
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            System.err.println("Błąd ładowania dźwięku: " + path);
            e.printStackTrace();
        }
    }

    // Odtwórz raz (efekt dźwiękowy)
    public void play() {
        if (clip == null) return;
        stop(); // Zatrzymaj jeśli jeszcze gra
        clip.setFramePosition(0); // Przewiń do początku
        clip.start();
    }

    // Odtwórz w pętli (muzyka w tle)
    public void loop() {
        if (clip == null) return;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    // Regulacja głośności (od 0.0 do 1.0)
    public void setVolume(float volume) {
        if (clip == null) return;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
    }
}