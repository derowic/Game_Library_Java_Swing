package pl.sgl.engine;

import java.util.List;

public class Colision {

    public static boolean colision(GameObject a, GameObject b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public static boolean colisionWithListOfSprites(GameObject a, List<GameObject> sprites) {
        for(GameObject g : sprites) {
            if (colision(a, g)) {
                return true;
            }
        }
        return false;
    }

    /*
    AABB
    [  ]




     */
}
