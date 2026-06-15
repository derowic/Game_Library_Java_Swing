package pl.sgl.engine.GameTest;

import pl.sgl.engine.*;
import pl.sgl.engine.audio.AudioManager;
import pl.sgl.engine.ui.Button;
import pl.sgl.engine.ui.UIManager;

import java.awt.event.KeyEvent;

public class MenuScene extends Scene {

    Button startButton;

    public MenuScene(String name) {
        super(name);
    }

    @Override
    public void init() {
        startButton = new Button("Start", 500, 600, 100, 50);
        startButton.setOnClick(() -> {
            System.out.println("Kliknięto Start! Przełączam scenę...");
            SceneManager.setScene("Game");
        });
        ui.addElement(startButton);

        Button exitButton = new Button("Wyjdź", 500, 670, 100, 50);
        exitButton.setOnClick(() -> System.exit(0));
        ui.addElement(exitButton);
    }

    @Override
    public void update(double dt) {
        super.update(dt);

//        if(startButton.isClicked(Game.mouse)) {
//            SceneManager.setScene("Game");
//        }

    }

}
