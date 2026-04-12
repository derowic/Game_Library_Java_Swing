package engine;

import java.awt.*;

public class Game implements Runnable {

    private GameWindow window;
    private volatile boolean running = false;
    private Thread gameThread;

    public Game() {
        window = new GameWindow("Moja Gra", 800, 600);
        window.show();
    }

    public synchronized void start() {
        if (running) return;   // zabezpieczenie przed podwójnym startem
        running = true;
                                // poprzez this przekazujesz cały obiekt Game z oknem do wątku
        gameThread = new Thread(this, "GameLoop");
        gameThread.setDaemon(true); // wątek zamknie się razem z aplikacją
        gameThread.start();
    }

    @Override
    public void run() {
        final double TARGET_FPS   = 60.0;
        final double NS_PER_FRAME = 1_000_000_000.0 / TARGET_FPS;

        long lastTime = System.nanoTime();
        double delta  = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / NS_PER_FRAME;
            lastTime = now;

            // update tyle razy ile minęło klatek
            while (delta >= 1) {
                update();
                delta--;
            }
            window.prepareToRender();

            // prostokąt
            window.g.setColor(Color.RED);
            window.g.fillRect(100, 100, 50, 50);       // wypełniony
            window.g.drawRect(200, 100, 50, 50);       // tylko obramowanie

            // koło (tak naprawdę elipsa)
            window.g.setColor(Color.BLUE);
            window.g.fillOval(300, 100, 60, 60);       // wypełnione
            window.g.drawOval(400, 100, 60, 60);       // tylko obramowanie

            // linia
            window.g.setColor(Color.GREEN);
            window.g.drawLine(0, 300, 800, 300);       // od (0,300) do (800,300)

            // tekst
            window.g.setColor(Color.WHITE);
            window.g.setFont(new Font("Arial", Font.PLAIN, 24));
            window.g.drawString("Witaj w grze!", 100, 400);

            render();
        }
    }

    private void update() {
        // logika gry – ruch, kolizje, AI...
    }

    private void render() {
        window.render();
    }

    public static void main(String[] args) {
        new Game().start();
    }
}
