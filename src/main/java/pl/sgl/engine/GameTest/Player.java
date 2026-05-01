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
     * @param currentY - Obecna pozycja Y gracza
     * @return Dokładna wysokość podłoża lub -1 jeśli nie ma kontaktu
     */
    public double getSurfaceYAt(double worldX, Shape hitbox, double currentY) {
        // Definiujemy zakres poszukiwań (np. 30 pikseli w górę i w dół od stóp)
        double searchRange = 30.0;
        double startY = currentY - 15.0; // Zaczynamy nieco nad obecną pozycją

        // Skanujemy z góry na dół
        for (double testY = startY; testY < startY + searchRange; testY += 1.0) {
            // Klucz: Twoja funkcja getRotatedShape zwraca Shape,
            // a każdy Shape w Javie ma ultra-szybką metodę contains()
            if (hitbox.contains(worldX, testY)) {
                return testY; // Znaleźliśmy górną krawędź!
            }
        }

        return -1; // Brak kontaktu na tej współrzędnej X
    }


    protected void updateCalc(double dt, List<Sprite> sprites) {
        super.update(dt); // Przesuwa lastX, lastY i oblicza nowe X na podstawie velocity

        double nextX = x + velocityX * dt;
        double footL = nextX + (width * 0.2); // Lewa noga
        double footR = nextX + (width * 0.8); // Prawa noga

        boolean onGround = false;

        // Przeszukujemy obiekty (np. z listy currentGame.sprites)
        for (GameObject obj : sprites) {
            if (obj == this) continue; // Nie koliduj ze samym sobą

            // Pobieramy ten skomplikowany, obrócony hitbox przeszkody
            Shape obstacleHitbox = obj.getRotatedShape((float)obj.x, (float)obj.y);

            // Sprawdzamy wysokość dla obu nóg
            double groundYL = getSurfaceYAt(footL, obstacleHitbox, y + height);
            double groundYR = getSurfaceYAt(footR, obstacleHitbox, y + height);

            // Jeśli choć jedna noga dotyka podłoża
            if (groundYL != -1 || groundYR != -1) {
                // Wybieramy najwyższy punkt styku (wyższe podłoże wygrywa)
                double targetY = Math.min(
                        (groundYL == -1 ? Double.MAX_VALUE : groundYL),
                        (groundYR == -1 ? Double.MAX_VALUE : groundYR)
                );

                // Ustawiamy gracza na tej wysokości
                y = targetY - height;
                velocityY = 0;
                onGround = true;

                // Opcjonalnie: pochylenie gracza do kąta rampy
                if (groundYL != -1 && groundYR != -1) {
                    double angle = Math.atan2(groundYR - groundYL, footR - footL);
                    this.rotation = Math.toDegrees(angle);
                }

                break; // Znaleźliśmy podłoże, przerywamy pętlę po obiektach
            }
        }

        if (!onGround) {
            y += velocityY * dt;
            velocityY += gravity * dt; // Spadanie
            rotation = 0; // Powrót do pionu w powietrzu
        }

        x = nextX;
    }
}
