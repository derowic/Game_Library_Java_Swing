package pl.sgl.engine;

import java.awt.*;
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
            if(a != g) {
                if (colision(a, g)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Rectangle getHitbox(Sprite a, Sprite b) {
        return new Rectangle((int)a.x + 10, (int)a.y + 5, a.width - 20, b.height - 10);
    }

    /*
    AABB
    [  ]




     */
}
