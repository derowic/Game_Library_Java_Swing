package pl.sgl.engine;

import pl.sgl.engine.GameTest.*;
import pl.sgl.engine.audio.AudioManager;
import pl.sgl.engine.ui.Text;
import pl.sgl.engine.ui.UIElement;
import pl.sgl.engine.ui.UIManager;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Main2 {
    public static Game init(String title, String icon, int width, int height, Color bc) {

        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Moja Gra"); // Dla MacOS
        System.setProperty("sun.java2d.uiScale", "true"); // Dla poprawnego skalowania na Linux/Win

        Game game = new Game(title, icon, width, height, bc);
        game.setRenderPixelArt();

        GameScene gameScene = new GameScene("Game");
        Scene scene = new Scene("Game2");
        MenuScene menuScene = new MenuScene("Menu");
        DeathScene deathScene = new DeathScene("Death");


        SceneManager.setScene("Menu");
        AudioManager.mute();

        return game;
    }

    public static void main(String[] args) {
        init( "Test", "/textures/brackeys_platformer_assets/sprites/knight_icon.png", 1280, 960, Color.BLACK).start();
    }
}