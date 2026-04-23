package pl.sgl.engine;

import pl.sgl.engine.audio.AudioManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Engine implements Runnable {
    //buffor to send data to rendering function
    protected GameState currentSnapshot = new GameState();
    // W klasie Engine
    protected GameState currentGame = new GameState();

    private Window window;
    private volatile boolean running = false;

    //ustawienia czasu
    private final int TICKS_PER_SECOND = 60;
    private final double SKIP_TICKS = 1_000_000_000.0 / TICKS_PER_SECOND;
    // Czas trwania jednego ticku w sekundach (dla 60 TPS to ~0.0166s)
    protected final double deltaTime = 1.0 / TICKS_PER_SECOND;

    //dane interpolacji
    private volatile long lastTickTime;

    // Wartości do wyświetlenia (volatile, bo czytane/pisane przez różne wątki)
    private volatile int currentFPS = 0;
    private volatile int currentTPS = 0;

    // Liczniki robocze
    private int frameCount = 0;
    private int tickCount = 0;

    // Czas ostatniego pomiaru
    private long lastTimer = System.currentTimeMillis();

    protected AudioManager audio = new AudioManager();

    protected InputHandler input = new InputHandler();
    protected MouseHandler mouse = new MouseHandler();
    private final Object renderLock = new Object();
    private boolean isSwitching = false;
    private String windowMode = "window";

    private long lastToggleTime = 0;
    private static final long COOLDOWN_MS = 500; // pół sekundy przerwy między zmianami


    public Engine(String title, int width, int height) {
        window = new Window(title, width, height);
        window.initInput(input);  // Klawiatura
        window.initMouse(mouse);  // Myszka
        window.show();
    }

    public void toggleFullScreen(String mode) {

        if (isSwitching) return; // Blokada, jeśli proces trwa

        if(mode.equals(windowMode)) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToggleTime < COOLDOWN_MS) {
            return; // Ignoruj wywołanie, jeśli nastąpiło zbyt szybko
        }
        lastToggleTime = currentTime;

        isSwitching = true;


        synchronized (renderLock) {
            if (windowMode.equals("window")) {
                window.setFullScreen();
                windowMode = "fullscreen";
            } else {
                window.setWindowedMode(1280, 720);
                windowMode = "window";
            }
        }
        System.out.println(window.getCanvas().getWidth());
        isSwitching = false;
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

    // Pętla Logiki
    @Override
    public void run() {
        var lastTime = System.nanoTime();
        double accumulator = 0;

        while (running) {
            long now = System.nanoTime();
            long passedTime = now - lastTime;
            lastTime = now;

            accumulator += passedTime;

            int updates = 0; // Dodatkowy licznik bezpieczeństwa

            // Warunek zatrzyma się, jeśli zrobimy więcej niż 5 update'ów na raz!
            while (accumulator >= SKIP_TICKS && updates < 5) {
                update();
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

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }

    }


    protected void update() {
        // Zapisujemy poprzedni stan przed aktualizacją
        lastTickTime = System.nanoTime();

        for (GameObject s : currentGame.sprites) {
            s.update(deltaTime);

            double diffX = (s.x - s.lastX);
            if (Math.abs(diffX) > 100) {
                s.didTeleport = true; // Zaznaczamy, że to był skok, a nie płynny ruch
            }
            double diffY = (s.y - s.lastY);
            if (Math.abs(diffY) > 100) {
                s.didTeleport = true; // Zaznaczamy, że to był skok, a nie płynny ruch
            }
        }
        // 2. STWÓRZ SNAPSHOT (Zdjęcie)
        // Tworzymy nową listę, która zawiera KOPIE stanów obiektów
        // (W uproszczeniu: kopiujemy referencje do nowej listy,
        // ale profesjonalnie kopiuje się wartości x, y do nowych obiektów-struktur)
        List<GameObject> snapshotSprites = new ArrayList<>(currentGame.sprites);
//        List<Primitive> snapshotEntities = new ArrayList<>(entities); // Twoja lista prymitywów

        // 3. PUBLIKUJEMY - Podmieniamy całe pudełko (to jest bezpieczne dzięki volatile)
        this.currentSnapshot = new GameState(snapshotSprites, currentGame.camX, currentGame.camY);

        input.update();
    }

    private void render(double alpha) {
        if (isSwitching) return;

        frameCount++; // Zwiększamy licznik przy każdym wywołaniu renderu
        window.prepareToRender();

        Graphics2D g = (Graphics2D) window.g;

        try {
            // Rozmiar wirtualny (Twojej gry)
            int virtualW = window.getCanvas().getWidth();
            int virtualH = window.getCanvas().getHeight();

            // Rozmiar rzeczywisty (Okna/Fullscreena)
            int screenW = window.getCanvas().getWidth();
            int screenH = window.getCanvas().getHeight();

            // Obliczamy skalę (wybieramy mniejszą, żeby obraz zmieścił się w całości)
            double scale = Math.min((double)screenW / virtualW, (double)screenH / virtualH);

            // Obliczamy przesunięcie, żeby wyśrodkować obraz (centrowanie)
            int offsetX = (int)(screenW - (virtualW * scale)) / 2;
            int offsetY = (int)(screenH - (virtualH * scale)) / 2;

            // 3. AKTUALIZUJEMY MYSZKĘ (Wysyłamy dane o skali do handlera)
            mouse.setTransformation(scale, offsetX, offsetY);

            // 1. Czyścimy tło pod czarne pasy
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, screenW, screenH);

            // 2. Transformacja: Najpierw przesuń do środka, potem skaluj
            g.translate(offsetX, offsetY);
            g.scale(scale, scale);

            GameState renderState= this.currentSnapshot;

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

            renderWorld(alpha);

    //        // Rysowanie Sprite'ów
    //        for (GameObject s : renderState.sprites) {
    //            // 1. OBLICZENIE POZYCJI (Interpolacja lub Teleport)
    //            Graphics2D g2d = (Graphics2D) window.g.create();
    //            g2d.translate(0,0);
    //            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    //            s.draw(g2d, alpha);
    //            g2d.dispose();
    //
    //            if (s.showHitBox) {
    //                g2d = (Graphics2D) window.g.create();
    //                g2d.translate(0,0);
    //                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    ////                Rectangle rec = s.getCalculatedAutoHitBoxes();
    ////
    ////                // 3. Rysuj obramowanie
    ////                window.g.setColor(Color.RED);
    ////
    ////                // Hitbox jest relatywny do pozycji Sprite'a, więc dodajemy rec.x i rec.y
    ////                window.g.drawRect(
    ////                        (int)(s.x + rec.x),
    ////                        (int)(s.y + rec.y),
    ////                        rec.width,
    ////                        rec.height
    ////                );
    //                Shape rotatedHitbox = s.getRotatedShape();
    //
    //                // C. Rysujemy obramowanie kształtu
    //                g2d.setColor(Color.RED);
    //                g2d.setStroke(new BasicStroke(2.0f)); // Opcjonalnie: grubsza linia, by była widoczna
    //                g2d.draw(rotatedHitbox); // To narysuje obrócony prostokąt!
    //
    //                // D. Opcjonalnie: Wypełnienie (półprzezroczyste)
    //                g2d.setColor(new Color(255, 0, 0, 50));
    //                g2d.fill(rotatedHitbox);
    //                g2d.dispose();
    //            }
    //
    //        for (GameObject s : renderState.sprites) {
    //            // TWORZYMY RAZ dla całego obiektu
    //            Graphics2D g2d = (Graphics2D) window.g.create();
    //
    //            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    //            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    //            g2d.translate(200,0);
    //            // 1. Rysujemy sprite'a
    //            s.draw(g2d, alpha);
    //
    //            // 2. Rysujemy hitbox na TYM SAMYM g2d (on już ma te same transformacje!)
    //            if (s.showHitBox) {
    //                // Obliczamy pozycję do rysowania (interpolacja)
    //                float drawX = (float) (s.lastX + (s.x - s.lastX) * (float)alpha);
    //                float drawY = (float) (s.lastY + (s.y - s.lastY) * (float)alpha);
    //
    //                // Ważne: s.getRotatedShape() musi być spójne z g2d
    //                Shape rotatedHitbox = s.getRotatedShape(drawX, drawY);
    //
    //                g2d.setColor(Color.RED);
    //                g2d.setStroke(new BasicStroke(2.0f));
    //                g2d.draw(rotatedHitbox);
    //
    //                g2d.setColor(new Color(255, 0, 0, 50));
    //                g2d.fill(rotatedHitbox);
    //            }
    //
    //            // ZWALNIAMY RAZ
    //            g2d.dispose();
    //        }






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

        } catch (Exception e) {
            // Ignoruj błędy podczas przełączania trybów graficznych
            e.printStackTrace();
        } finally {
            if (g != null) {
                g.dispose();
            }
        }
    }

    private void renderWorld(double alpha) {
        GameState renderState = this.currentSnapshot;

        // TWORZYMY JEDNĄ KOPIĘ DLA CAŁEGO ŚWIATA (Kamera / Globalne przesunięcie)
        Graphics2D worldG = (Graphics2D) window.g.create();

        // Ustawiamy hinty raz dla całego świata (to przyspiesza!)
        worldG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        worldG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Globalne przesunięcie (Kamera + Twoje testowe 200px)
        worldG.translate(0 - renderState.camX, 0 - renderState.camY);

        for (GameObject s : renderState.sprites) {
            // Wywołujemy draw, przekazując mu worldG.
            // Metoda s.draw() sama zajmie się stworzeniem swojej izolowanej kopii.
            s.draw(worldG, alpha);
        }

        worldG.dispose(); // Zwalniamy świat
    }
    public void input() {

    }



    public void addGameObject(GameObject g) {
        currentGame.sprites.add(g);
    }
//    public static void main(String[] args) {
//        new Game().start();
//    }

    // Lista obiektów logicznych (w świecie gry)
//    private List<MyPlayer> players = new ArrayList<>();
//

}
