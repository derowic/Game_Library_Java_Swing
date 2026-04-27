package pl.sgl.engine;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class Colision {

    public static boolean colision(GameObject a, GameObject b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public static boolean checkCollision(Shape shape1, Shape shape2) {
        // 1. Szybki test (Broad Phase)
        // Jeśli proste prostokąty otaczające (AABB) się nie stykają,
        // to te obrócone tym bardziej. To oszczędza dużo procesora.
        if (!shape1.getBounds2D().intersects(shape2.getBounds2D())) {
            return false;
        }

        // 2. Precyzyjny test (Narrow Phase)
        Area area1 = new Area(shape1);
        Area area2 = new Area(shape2);

        // Zostawia w area1 tylko to, co pokrywa się z area2
        area1.intersect(area2);

        // Jeśli wynikowy obszar nie jest pusty, mamy kolizję
        return !area1.isEmpty();
    }

    public static boolean colisionWithListOfSprites(GameObject a, List<GameObject> sprites) {
        Shape shapeA = a.getRotatedShape();

        for(GameObject g : sprites) {
            if(a != g) {
                // Generujemy kształt obiektu G
                Shape shapeG = g.getRotatedShape();
                if (checkCollision(shapeA, shapeG)) return true;
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
