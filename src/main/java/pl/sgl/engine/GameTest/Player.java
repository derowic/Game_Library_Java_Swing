package pl.sgl.engine.GameTest;

import pl.sgl.engine.Colision;
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
    public double getSurfaceYAt(double worldX, Shape hitbox, double feetY, double range) {
        // Skanujemy od poziomu kolan aż po głęboki dół pod stopami
        double startY = feetY - (range / 2.0);

        for (double testY = startY; testY < startY + range; testY += 1.0) {
            // Mały sensor 2x2 piksele, aby na pewno trafić w krawędź Shape'a
            if (hitbox.intersects(worldX - 1, testY - 1, 2, 2)) {
                return testY;
            }
        }
        return -1;
    }


    public void updateCalc2(double dt, List<GameObject> sprites) {
        this.lastX = x;
        this.lastY = y;

        double hW = (width / 2.0) * scaleX;
        double hH = (height / 2.0) * scaleY;
        double nextX = x + velocityX * dt;

        // Rozstaw nóg (sensory) - im węziej, tym stabilniej na skosach
        double footOffset = hW * 0.7;
        double footL = nextX - footOffset;
        double footR = nextX + footOffset;

        // feetY musi być bardzo nisko, żeby sensor zaczął szukać od dołu
        double feetY = y + hH;

        boolean onGround = false;

        for (GameObject obj : sprites) {
            if (obj == this || !obj.visible) continue;

            Shape obstacleHitbox = obj.getRotatedShape();

            // 1. Szukamy podłoża (z dużym zakresem lookUp/lookDown)
            double gYL = getSurfaceYAt(footL, obstacleHitbox, feetY, hH * 3.0);
            double gYR = getSurfaceYAt(footR, obstacleHitbox, feetY, hH * 3.0);

            if (gYL != -1 && gYR != -1) {
                // 1. Obliczamy kąt nachylenia
                double angleRad = Math.atan2(gYR - gYL, footR - footL);
                this.rotation = Math.toDegrees(angleRad);

                // 2. OBLICZAMY KOREKTĘ WYSOKOŚCI (Vertical Offset)
                // Musimy wiedzieć, jak bardzo "urósł" dół postaci w pionie przez obrót.
                // hH - połowa wysokości, hW - połowa szerokości
                // footOffset - odległość sensora od środka X (u Ciebie to hW * 0.5 lub podobnie)

                double footOffsetFromCenter = hW * 0.5; // dystans sensorów od środka

                // To jest kluczowy wzór:
                double rotatedVerticalOffset = Math.abs(footOffsetFromCenter * Math.sin(angleRad)) + Math.abs(hH * Math.cos(angleRad));

                // 3. USTAWIAMY POZYCJĘ
                double averageGroundY = (gYL + gYR) / 2.0;
                this.y = averageGroundY - rotatedVerticalOffset;

                this.velocityY = 0;
                onGround = true;
                System.out.println("two");
                break;
            }
            else if (gYL != -1 || gYR != -1) {
                // Jeśli tylko jedna noga dotyka (krawędź)
                double actualY = (gYL != -1) ? gYL : gYR;
                this.y = actualY - hH; // Tutaj bez rotacji, bo spadamy jedną nogą
                this.velocityY = 0;
                onGround = true;
                System.out.println("one");
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

    public void updateCalc(double dt, List<GameObject> sprites) {
        // --- 1. ZAPISANIE STANU (dla interpolacji) ---
        this.lastX = x;
        this.lastY = y;

        // --- 2. APLIKACJA SIŁ (Grawitacja i wejście) ---
        // Musisz stale dodawać grawitację do prędkości pionowej
        double gravity = 900.0; // stała grawitacji
        velocityY += gravity * dt;

//        // Obsługa chodzenia (przykładowo)
//        double speed = 300.0;
//        if (input.isKeyDown(KeyEvent.VK_D)) velocityX = speed;
//        else if (input.isKeyDown(KeyEvent.VK_A)) velocityX = -speed;
//        else velocityX *= 0.8; // tarcie powietrza, żeby się nie ślizgać wiecznie

        // --- 3. RUCH WSTĘPNY ---
        // Zmieniamy pozycję o wyliczoną prędkość.
        // Teraz gracz "wpada" w podłoże, a solver poniżej go wyciągnie.
        x += velocityX * dt;
        y += velocityY * dt;

        boolean onGround = false;

        // --- 4. ROZWIĄZYWANIE KOLIZJI (Twoja logika z wektorem normalnym) ---
        for (GameObject obj : sprites) {
            if (obj == this || !obj.visible) continue;

            Shape playerShape = this.getRotatedShape((float)x, (float)y);
            Shape obstacleShape = obj.getRotatedShape();

            if (Colision.checkCollision(playerShape, obstacleShape)) {
                // A. Oblicz wektor normalny
                double rad = Math.toRadians(obj.rotation);
                double nx = Math.sin(rad);
                double ny = -Math.cos(rad);

                // B. WYPYCHANIE (Depenetration)
                int safetyCounter = 0;
                while (Colision.checkCollision(this.getRotatedShape((float)x, (float)y), obstacleShape) && safetyCounter < 15) {
                    x += nx * 0.5;
                    y += ny * 0.5;
                    safetyCounter++;
                }

                // C. RZUTOWANIE PRĘDKOŚCI (Sliding)
                double dotProduct = (velocityX * nx + velocityY * ny);
                if (dotProduct < 0) {
                    velocityX -= dotProduct * nx;
                    velocityY -= dotProduct * ny;
                }

                // D. Czy to podłoże?
                if (ny < -0.5) {
                    onGround = true;
                    this.rotation = Math.toDegrees(Math.atan2(nx, -ny));
                } else {
                    velocityX = 0;
                    // ny blisko 0.0 np. miedzy -0.5 a 0.5 to jest zderzenie z  ściana
                }
            }
        }

        // Jeśli gracz jest w powietrzu, powoli wraca do pionu
        if (!onGround) {
            rotation *= 0.95;
        } else {
            // Mały trik: jeśli stoimy na ziemi, zerujemy bardzo małe prędkości Y
            if (Math.abs(velocityY) < 0.1) velocityY = 0;
        }
    }
}
