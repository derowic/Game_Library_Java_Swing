package pl.sgl.engine;

import java.awt.*;

public class Game implements Runnable {

    // To jest nasz JEDEN bezpieczny punkt styku między wątkami
//    private volatile GameState buffer1 = new GameState(0, 0, 0, 0);
//    private volatile GameState buffer2 = new GameState(0, 0, 0, 0);
//    private volatile boolean bufferInUse = true;
    protected volatile GameState currentSnapshot = new GameState();

    private GameWindow window;
    private volatile boolean running = false;

    //ustawienia czasu
    private final int TICKS_PER_SECOND = 60;
    private final double SKIP_TICKS = 1_000_000_000.0 / TICKS_PER_SECOND;
    // Czas trwania jednego ticku w sekundach (dla 60 TPS to ~0.0166s)
    protected final double DT = 1.0 / TICKS_PER_SECOND;

    //dane interpolacji
    private double x,y;
    private double lastX, lastY;
    private double interpolation;
    private Thread gameThread;
    private volatile long lastTickTime;

    // Wartości do wyświetlenia (volatile, bo czytane/pisane przez różne wątki)
    private volatile int currentFPS = 0;
    private volatile int currentTPS = 0;

    // Liczniki robocze
    private int frameCount = 0;
    private int tickCount = 0;

    // Czas ostatniego pomiaru
    private long lastTimer = System.currentTimeMillis();

    public Game() {
        window = new GameWindow("Moja Gra", 800, 600);
        window.show();
    }

    public synchronized void stopRunning() {
        this.running = false;
    }

    public synchronized void start() {
        if (running) return;   // zabezpieczenie przed podwójnym startem
        running = true;
        lastTickTime = System.nanoTime();

        // Wątek LOGIKI (Update)
        Thread logicThread = new Thread(this, "LogicThread");
        logicThread.start();
                                // poprzez this przekazujesz cały obiekt Game z oknem do wątku
        startRenderLoop();
    }

    private void startRenderLoop() {
        Thread renderThread = new Thread(() -> {
            // 1. Definiujemy parametry limitu
            final double TARGET_FPS = 120.0;
            final double NS_PER_FRAME = 1_000_000_000.0 / TARGET_FPS;
            long lastFrameTime = System.nanoTime();

            while (running) {
                // Obliczanie interpolacji (alpha)
                // Sprawdzamy jak daleko jesteśmy między jednym tickiem a drugim
                // Szukamy czasu od ostatniego ticku logiki
                // W uproszczeniu obliczamy to na podstawie aktualnego czasu i oczekiwanego nastepnego ticku
                // Ale najprościej: (teraz - czas_ostatniego_ticku) / czas_trwania_ticku
                // Dla uproszczenia w tym modelu użyjemy przybliżenia:
                //double alpha = (double)(System.nanoTime() - (long)(System.nanoTime() / SKIP_TICKS * SKIP_TICKS)) / SKIP_TICKS;
                double alpha = (double)(System.nanoTime() - lastTickTime) / SKIP_TICKS;
                if (alpha > 1.0) alpha = 1.0;
                else if (alpha < 0.0) alpha = 0.0;
                // W profesjonalnych silnikach lepiej przekazać precyzyjny timestamp z pętli update
                // Na potrzeby przykładu, użyjemy zmiennej 'interpolation' wyliczanej w renderze
                render(alpha);

                // 4. CZEKAMY, aby utrzymać limit FPS
                // Obliczamy, kiedy powinna pojawić się następna klatka
                long targetTime = lastFrameTime + (long)NS_PER_FRAME;

                while (System.nanoTime() < targetTime) {
                    // Jeśli zostało dużo czasu (np. więcej niż 2ms), śpimy, żeby ulżyć CPU
                    long timeLeft = (targetTime - System.nanoTime()) / 1_000_000;
                    if (timeLeft > 2) {
                        try { Thread.sleep(1); } catch (InterruptedException e) {}
                    } else {
                        // Jeśli zostało bardzo mało czasu, tylko oddajemy procesor (większa precyzja)
                        Thread.yield();
                    }
                }

                lastFrameTime = System.nanoTime();
            }
        }, "RenderThread");

        renderThread.start();
    }

//    @Override
//    public void run() {
//        final double TARGET_FPS   = 60.0;
//        final double NS_PER_FRAME = 1_000_000_000.0 / TARGET_FPS;
//
//        long lastTime = System.nanoTime();
//        double delta  = 0;
//
//        while (running) {
//            long now = System.nanoTime();
//            delta += (now - lastTime) / NS_PER_FRAME;
//            lastTime = now;
//
//            // update tyle razy ile minęło klatek
//            while (delta >= 1) {
//                update();
//                delta--;
//            }
//            window.prepareToRender();
//
//            // prostokąt
//            window.g.setColor(Color.RED);
//            window.g.fillRect(100, 100, 50, 50);       // wypełniony
//            window.g.drawRect(200, 100, 50, 50);       // tylko obramowanie
//
//            // koło (tak naprawdę elipsa)
//            window.g.setColor(Color.BLUE);
//            window.g.fillOval(300, 100, 60, 60);       // wypełnione
//            window.g.drawOval(400, 100, 60, 60);       // tylko obramowanie
//
//            // linia
//            window.g.setColor(Color.GREEN);
//            window.g.drawLine(0, 300, 800, 300);       // od (0,300) do (800,300)
//
//            // tekst
//            window.g.setColor(Color.WHITE);
//            window.g.setFont(new Font("Arial", Font.PLAIN, 24));
//            window.g.drawString("Witaj w grze!", 100, 400);
//
//            render();
//        }
//    }

    // Pętla Logiki
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double accumulator = 0;

        while (running) {
            long now = System.nanoTime();
            long passedTime = now - lastTime;
            lastTime = now;

            accumulator += passedTime;

            int updates = 0; // Dodatkowy licznik bezpieczeństwa

            // Warunek zatrzyma się, jeśli zrobimy więcej niż 5 update'ów na raz!
            while (accumulator >= SKIP_TICKS && updates < 5) {
                update(DT);
                tickCount++;
                accumulator -= SKIP_TICKS;
                lastTickTime = System.nanoTime();

                updates++;
            }

            // Jeśli komputer jest tak tragicznie wolny, że przekroczyliśmy 5 update'ów,
            // to "wyrzucamy" zaległy czas, aby gra się nie zawiesiła.
            if (accumulator >= SKIP_TICKS) {
                accumulator = 0; // "Panika" - resetujemy zegar, gra lekko przeskoczy, ale nie zgaśnie
            }

            if (System.currentTimeMillis() - lastTimer >= 1000) {
                currentFPS = frameCount;
                currentTPS = tickCount;
                frameCount = 0;
                tickCount = 0;
                lastTimer += 1000;
            }

            try { Thread.sleep(1); } catch (InterruptedException e) {}
        }
    }



    protected void update(double dt) {
        // Zapisujemy poprzedni stan przed aktualizacją

        lastTickTime = System.nanoTime();


        // Logika ruchu (np. przesuwanie w prawo)

        for (Sprite s : currentSnapshot.sprites) {
            float diffX = (s.x - s.lastX);
            // Jeśli różnica jest większa niż np. połowa szerokości ekranu,
            // to znaczy, że kwadrat przeskoczył krawędź.
            if (Math.abs(diffX) > 100) {
                s.didTeleport = true; // Zaznaczamy, że to był skok, a nie płynny ruch
            }
        }



//        System.out.println("watek logiki");

        //currentSnapshot = new GameState(x, y, lastX, lastY, didTeleport);

        // Tutaj reszta fizyki...

//        if(bufferInUse) {
//            buffer1 = new GameState(x, y, lastX, lastY);
//        }
//        else {
//            buffer2 = new GameState(x, y, lastX, lastY);
//        }
//        bufferInUse = !bufferInUse;

        //----!!! WAŻŃE !!!----
        //Wątek renderujący, który już zaczął rysować przy użyciu starej referencji, spokojnie kończy pracę na starym obiekcie,
        // a nowa klatka renderu pobierze już nowy obiekt.
//        currentSnapshot = new GameState(x, y, lastX, lastY);
    }

    private void render(double alpha) {
        frameCount++; // Zwiększamy licznik przy każdym wywołaniu renderu
        window.prepareToRender();

        // OBLICZANIE INTERPOLOWANEJ POZYCJI
        // Dzięki temu przy 144Hz monitorze ruch będzie idealnie płynny,
        // mimo że logika działa tylko w 60Hz.
//        float renderX = (float) (lastX + (x - lastX) * alpha);
//        float renderY = (float) (lastY + (y - lastY) * alpha);


        GameState renderState= this.currentSnapshot;
//        GameState renderState = bufferInUse ? buffer2 : buffer1;
//        float renderX = (float) (renderState.lastX + (renderState.x - renderState.lastX) * alpha);
//        float renderY  = (float) (renderState.lastY + (renderState.y - renderState.lastY) * alpha);
//
//        // -----!!! DODAJ ABY PRZY NAGŁEJ ZMIANIE MIEJSCA BRAC TYLKO STARĄ POZYCJE
//        // Jeśli różnica jest większa niż np. połowa szerokości ekranu,
//        // to znaczy, że kwadrat przeskoczył krawędź.
//        float renderX;
//        if (renderState.didTeleport) {
//            renderX = (float) renderState.x; // Jeśli był teleport, nie interpoluj "drogi pomiędzy"
//        } else {
//            renderX = (float) (renderState.lastX + (renderState.x - renderState.lastX) * alpha);
//        }

        for (Primitive e : renderState.entities) {
            //System.out.println(e);
            // INTERPOLACJA dla każdego obiektu z osobna
            float drawX = e.lastX + (e.x - e.lastX) * (float)alpha;
            float drawY = e.lastY + (e.y - e.lastY) * (float)alpha;

            window.g.setColor(e.color);

            if ("RECT".equals(e.type)) {
                window.g.fillRect((int)drawX, (int)drawY, e.width, e.height);
            } else if ("CIRCLE".equals(e.type)) {
                window.g.fillOval((int)drawX, (int)drawY, e.width, e.height);
            }
        }

        // Rysowanie Sprite'ów
        for (Sprite s : renderState.sprites) {
            float drawX;
            float drawY;
            if (s.didTeleport) {
//            renderX = (float) renderState.x;
                // Interpolacja pozycji
                drawX = (float) s.x;
                drawY = s.y;
            } else {
                drawX = s.lastX + (s.x - s.lastX) * (float) alpha;
                drawY = s.lastY + (s.y - s.lastY) * (float) alpha;
            }

            if (s.rotation != 0) {
                // Rotacja wymaga przesunięcia kontekstu Graphics2D
                Graphics2D g2d = (Graphics2D) window.g.create();
                g2d.translate(drawX + (double) s.width / 2, drawY + (double) s.height / 2);
                g2d.rotate(Math.toRadians(s.rotation));
                g2d.drawImage(s.image, -s.width / 2, -s.height / 2, s.width, s.height, null);
                g2d.dispose();
            } else {
                window.g.drawImage(s.image, (int)drawX, (int)drawY, s.width, s.height, null);
            }
        }


        // Pozwól programiście narysować coś ekstra (np. UI)
        //renderOverlay(window.g);

        // Rysowanie
//        window.g.setColor(Color.RED);
//        window.g.fillRect((int)renderX, (int)renderY, 50, 50);

        // --- RYSOWANIE STATYSTYK ---
        window.g.setColor(Color.WHITE);
        // Ustawiamy tło pod tekst, żeby był czytelny
        window.g.setColor(new Color(0, 0, 0, 150)); // Półprzezroczysty czarny
        window.g.fillRect(5, 5, 100, 45);

        window.g.setColor(Color.GREEN);
        window.g.setFont(new Font("Monospaced", Font.BOLD, 14));
        window.g.drawString("FPS: " + currentFPS, 10, 20);
        window.g.drawString("TPS: " + currentTPS, 10, 40);

        // Wyświetlanie Alpha (opcjonalnie do debugowania płynności)
        window.g.setColor(Color.YELLOW);
        window.g.drawString("Alpha: " + String.format("%.2f", alpha), 10, 60);

        window.render();
    }

//    public static void main(String[] args) {
//        new Game().start();
//    }

    // Lista obiektów logicznych (w świecie gry)
//    private List<MyPlayer> players = new ArrayList<>();
//

}
