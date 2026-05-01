package pl.sgl.engine.GameTest;

import pl.sgl.engine.GameObject;
import pl.sgl.engine.Sprite;

import java.awt.*;
import java.util.List;

public class Player extends Sprite {

    public double gravity = 5;
    public Player(String pathToTexture, float x, float y) {
        super(pathToTexture, x, y);
    }

    /**
     * Zwraca najwyższy punkt (Y) hitboxa na danej współrzędnej X.
     * @param worldX - Współrzędna X gracza (np. środek stóp)
     * @param hitbox - Obiekt Shape zwrócony przez getRotatedShape()

     * @return Dokładna wysokość podłoża lub -1 jeśli nie ma kontaktu
     */
    public double getSurfaceYAt(double worldX, Shape hitbox, double feetY, double playerHalfHeight) {
        // Szukamy w górę o 30% wysokości gracza i w dół o 50%
        double lookUp = playerHalfHeight * 0.5;
        double lookDown = playerHalfHeight * 1.0;

        double startY = feetY - lookUp;

        // Zwiększamy precyzję skanowania - przy małych obiektach skok co 0.5px jest bezpieczniejszy
        for (double testY = startY; testY < feetY + lookDown; testY += 0.5) {
            // Zmniejszamy sensor do 1x1 piksela dla małych obiektów
            if (hitbox.intersects(worldX - 0.5, testY - 0.5, 1, 1)) {
                return testY;
            }
        }
        return -1;
    }


    public void updateCalc(double dt, List<GameObject> sprites) {
        this.lastX = x;
        this.lastY = y;

        // 1. Obliczamy wymiary uwzględniając skalę (Dynamicznie!)
        double hW = (width / 2.0) * scaleX;
        double hH = (height / 2.0) * scaleY;

        double nextX = x + velocityX * dt;

        // Rozstaw nóg - zawsze 50% szerokości od środka, niezależnie od skali
        double footL = nextX - (hW);
        double footR = nextX + (hW);
        double feetY = y + hH;

        boolean onGround = false;

        for (GameObject obj : sprites) {
            if (obj == this || !obj.visible) continue;

            Shape obstacleHitbox = obj.getRotatedShape();

            // 2. Szybki test (AABB) również musi być proporcjonalny
            if (!obstacleHitbox.getBounds2D().intersects(nextX - hW, y - hH, hW * 2, hH * 2 + hH)) {
                continue;
            }

            // 3. Szukamy podłoża przekazując hH
            double gYL = getSurfaceYAt(footL, obstacleHitbox, feetY, hH);
            double gYR = getSurfaceYAt(footR, obstacleHitbox, feetY, hH);

            if (gYL != -1 || gYR != -1) {
                double highestGround = Double.MAX_VALUE;
                if (gYL != -1) highestGround = Math.min(highestGround, gYL);
                if (gYR != -1) highestGround = Math.min(highestGround, gYR);

                // Przyklejenie do ziemi
                this.y = highestGround - hH;
                this.velocityY = 0;
                onGround = true;

                if (gYL != -1 && gYR != -1) {
                    this.rotation = Math.toDegrees(Math.atan2(gYR - gYL, footR - footL));
                }
                break;
            }
        }

        if (!onGround) {
            velocityY += 900 * dt;
            y += velocityY * dt;
            rotation *= 0.9;
        }
        x = nextX;
    }
}
