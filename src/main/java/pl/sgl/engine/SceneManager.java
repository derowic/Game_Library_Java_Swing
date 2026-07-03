package pl.sgl.engine;

import pl.sgl.engine.ui.UIElement;

import java.util.HashMap;

public class SceneManager {
    protected static String selectedScene;
    protected static Scene activeScene;
    protected static HashMap<String, Scene> scenes = new HashMap<>();

    public SceneManager() {
        new Scene("Game");
        selectedScene = "Game";
    }

    public SceneManager(String sceneName, Scene scene) {
        scenes.put(sceneName, scene);
        selectedScene = sceneName;
    }

    public static Scene getScene(String sceneName) {
        return scenes.get(sceneName);
    }

    public static void setScene(String sceneName) {
        if(scenes.containsKey(sceneName)) {
            selectedScene = sceneName;
            activeScene = scenes.get(selectedScene);
        }
    }

    public static void setScene(Scene newScene) {
        activeScene.delete();
        activeScene = newScene;
        scenes.replace(selectedScene, newScene);
    }

    public static void addScene(String sceneName, Scene scene) {
        scenes.put(sceneName, scene);
        if(selectedScene == null){
            setScene(sceneName);
        }
        activeScene = scene;
    }

    public static Scene getSelectedScene() {
        return activeScene;
    }

    public static void addGameObject(GameObject go) {
        activeScene.addGameObject(go);
    }
}
