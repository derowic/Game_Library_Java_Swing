package pl.sgl.engine;

import pl.sgl.engine.audio.AudioManager;
import pl.sgl.engine.particleSystem.Particle;
import pl.sgl.engine.particleSystem.ParticleEmitter;
import pl.sgl.engine.ui.InputField;
import pl.sgl.engine.ui.UIElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game implements Runnable {
    //buffor to send data to rendering function
    protected GameState currentSnapshot = new GameState();
    // W klasie Engine
    protected GameState currentGame = new GameState();

    public Window window;
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

    public InputHandler keyboard = new InputHandler();
    protected MouseHandler mouse = new MouseHandler();
    private final Object renderLock = new Object();
    private boolean isSwitching = false;
    public String windowMode = "window";

    public Game(String title, int width, int height) {
        window = new Window(title, width, height);
        window.initInput(keyboard);  // Klawiatura
        window.initMouse(mouse);  // Myszka
        window.show();
//        window.typeOfRenderingSprites = "pixelart";
    }

    public void addGameObject(GameObject g) {
        currentGame.sprites.add(g);
    }

    public void addGameObject(ParticleEmitter emitter) {
        currentGame.emitters.add(emitter);
    }

    public void toggleFullScreen(String mode) {
        System.out.println(mode);
        if (isSwitching) return; // Blokada, jeśli proces trwa

        if(mode.equals(windowMode)) return;
//
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastToggleTime < COOLDOWN_MS) {
//            return; // Ignoruj wywołanie, jeśli nastąpiło zbyt szybko
//        }
//        lastToggleTime = currentTime;

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
        windowMode = mode;
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
            long diffrence = now - lastTime;
            lastTime = now;

            accumulator += diffrence;

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
                e.printStackTrace();
            }
        }

    }


    protected void update() {
        // Zapisujemy poprzedni stan przed aktualizacją

        lastTickTime = System.nanoTime();



        // 2. Aktualizuj UI
        currentGame.uiManager.update(keyboard, mouse);

        //sprawdz czy coś klikniete, jeśli tak to sprawdz co i wyczysc kliniecie aby logika spritów nie wiedziała ze kliniete

//        W profesjonalnych silnikach gier ten mechanizm nazywa się Input Consumption (pochłanianie wejścia) lub Event Bubbling.
//                Polega on na tym, że zdarzenia wejściowe (kliknięcia, klawisze) przechodzą przez warstwy gry od najwyższej (UI)
//    do najniższej (Świat gry). Jeśli wyższa warstwa "skonsumuje" kliknięcie, niższa o nim nie wie.


        for (UIElement e : currentGame.uiManager.getElements()) {
            if(e.getClass() == InputField.class) {
                e.update(keyboard,mouse);
            } else {
                e.update(mouse);
            }
        }
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

        for (ParticleEmitter emitter : currentGame.emitters) {
            emitter.update(deltaTime);
        }



        // 2. STWÓRZ SNAPSHOT (Zdjęcie)
        // Tworzymy nową listę, która zawiera KOPIE stanów obiektów
        // (W uproszczeniu: kopiujemy referencje do nowej listy,
        // ale profesjonalnie kopiuje się wartości x, y do nowych obiektów-struktur)
        List<GameObject> snapshotSprites = new ArrayList<>(currentGame.sprites);

        ConfigureData.zoom = currentGame.zoom;
        ConfigureData.camX = currentGame.camX;
        ConfigureData.camY = currentGame.camY;
        // 3. PUBLIKUJEMY - Podmieniamy całe pudełko (to jest bezpieczne dzięki volatile)
        this.currentSnapshot = new GameState(snapshotSprites, currentGame.emitters, currentGame.uiManager, currentGame.tileMap, currentGame.camX, currentGame.camY, currentGame.zoom, currentGame.lastZoom);

        keyboard.update();
        mouse.update();
    }

    private void render(double alpha) {
        if (isSwitching) return;

        frameCount++; // Zwiększamy licznik przy każdym wywołaniu renderu
        window.prepareToRender();

        Graphics2D g = window.g;



//        // Ustawiamy hinty raz dla całego świata (to przyspiesza!)


        try {
            // Rozmiar wirtualny (Twojej gry)
            int virtualW = window.oldWidth;
            int virtualH = window.oldHeight;

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

            // 2. Transformacja: Najpierw przesuń do środka, potem skaluj
            g.translate(offsetX, offsetY);
            g.scale(scale, scale);

            GameState renderState= this.currentSnapshot;

//            for (Primitive e : renderState.entities) {
//                //System.out.println(e);
//                // INTERPOLACJA dla każdego obiektu z osobna
//                float drawX = e.lastX + (e.x - e.lastX) * (float)alpha;
//                float drawY = e.lastY + (e.y - e.lastY) * (float)alpha;
//
//                window.g.setColor(e.color);
//
//                if ("RECT".equals(e.type)) {
//                    window.g.fillRect((int)drawX, (int)drawY, e.width, e.height);
//                } else if ("CIRCLE".equals(e.type)) {
//                    window.g.fillOval((int)drawX, (int)drawY, e.width, e.height);
//                }
//            }



            for (ParticleEmitter emitter : renderState.emitters) {


                for (Particle e : emitter.getActiveParticles()) {
                    float drawX =e.lastX + (e.x - e.lastX) * (float) alpha;
                    float drawY =e.lastY + (e.y - e.lastY) * (float) alpha;

                    window.g.setColor(e.color);
                    window.g.fillRect((int) drawX, (int) drawY, (int) e.size, (int) e.size);
                }
            }


            renderWorld(alpha);
            renderUI(window.g);
            renderStats(alpha);



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

        // 1. Obliczamy zinterpolowany zoom
//        float drawZoom = (float) (renderState.zoom+ (renderState.zoom) * alpha);
        float drawZoom = (float) renderState.zoom;

        // TWORZYMY JEDNĄ KOPIĘ DLA CAŁEGO ŚWIATA (Kamera / Globalne przesunięcie)
        Graphics2D worldG = (Graphics2D) window.g.create();



        // --- MAGIA ZOOMU (Wyśrodkowanego) ---
        // Pobieramy rozmiar wirtualny (np. 1280x720)
        int vW = window.oldWidth;
        int vH = window.oldHeight;

        // A. Przesuwamy punkt (0,0) na środek ekranu wirtualnego
        worldG.translate(vW / 2.0, vH / 2.0);
        // B. Skalujemy świat (Zoom)
        worldG.scale(drawZoom, drawZoom);
        // C. Cofamy przesunięcie (teraz środek świata jest na środku ekranu)
        worldG.translate(-vW / 2.0, -vH / 2.0);


        // Globalne przesunięcie (Kamera + Twoje testowe 200px)
        worldG.translate(0 - renderState.camX, 0 - renderState.camY);

        // 2. Rysowanie Mapy (użytkownik musi ją dodać do klasy Main)
        if (renderState.tileMap != null) {
            renderState.tileMap.draw(worldG, renderState.camX, renderState.camY, window.getCanvas().getWidth(), window.getCanvas().getHeight());
        }
        for (GameObject s : renderState.sprites) {
            // Wywołujemy draw, przekazując mu worldG.
            // Metoda s.draw() sama zajmie się stworzeniem swojej izolowanej kopii.
            if (!s.visible) continue;

            s.draw(worldG, alpha);
        }

        worldG.dispose(); // Zwalniamy świat
    }

    private void renderUI(Graphics2D g) {
        GameState renderState = this.currentSnapshot;

        if (currentGame.uiManager.isMouseCaptured() || currentGame.uiManager.isKeyboardCaptured()) {
            window.getCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            window.getCanvas().setCursor(Cursor.getDefaultCursor());
        }

        for (UIElement e : renderState.uiManager.getElements()) {
            e.draw(g);
        }
    }

    private void renderStats(double alpha){
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
    }
//    public boolean isKeyPressed(int keyCode) {
//        return input.isKeyPressed(keyCode);
//    }

//    public static void main(String[] args) {
//        new Game().start();
//    }

    // Lista obiektów logicznych (w świecie gry)
//    private List<MyPlayer> players = new ArrayList<>();
//

}