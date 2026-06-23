package pl.sgl.engine.GameTest;

import pl.sgl.engine.Scene;
import pl.sgl.engine.SceneManager;
import pl.sgl.engine.ui.Button;
import pl.sgl.engine.ui.Text;

public class DeathScene extends Scene {

    Button restartButton;
    Text deathText;

    public DeathScene(String name) {
        super(name);
    }

    @Override
    public void init() {
        restartButton = new Button("Restart", 500, 600, 100, 50);
        restartButton.setOnClick(() -> {
            System.out.println("Kliknięto Start! Przełączam scenę...");
            SceneManager.getScene("Game").cleanEverything();
            SceneManager.setScene("Game");
        });
        ui.addUi(restartButton);

        Button exitButton = new Button("Wyjdź", 500, 670, 100, 50);
        exitButton.setOnClick(() -> System.exit(0));
        ui.addUi(exitButton);

        deathText = new Text("DEATH", 500, 200, 20);
        ui.addUi(deathText);
    }

    @Override
    public void update(double dt) {
        super.update(dt);

//        if(startButton.isClicked(Game.mouse)) {
//            SceneManager.setScene("Game");
//        }

    }

}
