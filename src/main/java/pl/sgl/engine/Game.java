package pl.sgl.engine;

import pl.sgl.engine.GameTest.Player;
import pl.sgl.engine.audio.AudioManager;
import pl.sgl.engine.particleSystem.Particle;
import pl.sgl.engine.particleSystem.ParticleEmitter;
import pl.sgl.engine.ui.InputField;
import pl.sgl.engine.ui.UIElement;

import java.awt.*;
import java.awt.geom.Rectangle2D;
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

    public Game(String title, int width, int height, Color bc) {
        window = new Window(title, width, height, bc);
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
            final double TARGET_FPS = 240.0;
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

        for (UIElement e : currentGame.uiManager.getElements()) {
            if(e.getClass() == InputField.class) {
                e.update(keyboard,mouse);
            } else {
                e.update(mouse);
            }
        }
        for (GameObject s : currentGame.sprites) {
            if (!s.active) {
                currentGame.sprites.remove(s);
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

        currentGame.cam.prepareForUpdate();
        currentGame.cam.update(deltaTime);
        ConfigureData.zoom = currentGame.cam.zoom;
        ConfigureData.camX = currentGame.cam.x;
        ConfigureData.camY = currentGame.cam.y;
        // 3. PUBLIKUJEMY - Podmieniamy całe pudełko (to jest bezpieczne dzięki volatile)
        this.currentSnapshot = new GameState(snapshotSprites, currentGame.emitters, currentGame.uiManager, currentGame.tileMap, currentGame.cam);

        keyboard.update();
        mouse.update();
    }

    public Rectangle2D.Double getVisibleWorldRect(double alpha) {
        GameState s = this.currentSnapshot;

        // 1. Interpolacja parametrów kamery
        float drawZoom = (float) (s.cam.lastZoom + (s.cam.zoom - s.cam.lastZoom) * alpha);
        float drawCamX = (float) (s.cam.lastX + (s.cam.x - s.cam.lastX) * alpha);
        float drawCamY = (float) (s.cam.lastY + (s.cam.y - s.cam.lastY) * alpha);

        // Twoja rozdzielczość wirtualna
        int vW = ConfigureData.oldWidth;
        int vH = ConfigureData.oldHeight;

        // 2. Obliczamy jak dużo świata widać przez Zoom
        double visibleW = vW / drawZoom;
        double visibleH = vH / drawZoom;

        // 3. Obliczamy lewy górny róg widoku w świecie
        // Skoro zoomujemy do środka, to widok rozszerza/zwęża się symetrycznie
        double x = drawCamX + (vW / 2.0) - (visibleW / 2.0);
        double y = drawCamY + (vH / 2.0) - (visibleH / 2.0);

        // Dodajemy mały margines (padding) np. 100px, żeby obiekty nie znikały
        // gwałtownie przy samym brzegu przez rotację
        return new Rectangle2D.Double(x - 100, y - 100, visibleW + 200, visibleH + 200);
    }

    private void render(double alpha) {
        if (isSwitching) return;

        frameCount++; // Zwiększamy licznik przy każdym wywołaniu renderu
        window.prepareToRender();

        Graphics2D g = window.g;



//        // Ustawiamy hinty raz dla całego świata (to przyspiesza!)


        try {
            // Rozmiar wirtualny (Twojej gry)
            int virtualW = ConfigureData.oldWidth;;
            int virtualH = ConfigureData.oldHeight;

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
            drawSensors(alpha);

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

    private void drawSensors(double alpha) {
        for (GameObject s : currentGame.sprites) {
            if (s instanceof Player && s.showHitBox) { // Rysujemy tylko dla gracza
                Graphics2D debugG = (Graphics2D) window.g.create(); // worldG to Twoja grafika z kamerą

                // 1. Obliczamy pozycję wizualną identycznie jak w logice
                float dX = (float) (s.lastX + (s.x - s.lastX) * alpha);
                float dY = (float) (s.lastY + (s.y - s.lastY) * alpha);
                float hW = (float) (s.width / 2.0 * s.scaleX);
                float hH = (float) (s.height / 2.0 * s.scaleY);

                // 2. Punkty nóg
                float footL = dX - (hW);
                float footR = dX + (hW);
                float feetY = dY + hH;

                // 3. Zakres skanowania (identyczny jak w getSurfaceYAt)
                float lookUp = hH * 0.5f;
                float lookDown = hH * 1.0f;

                // Rysujemy lewy sensor (niebieski)
                debugG.setColor(Color.CYAN);
                debugG.drawLine((int)footL, (int)(feetY - lookUp), (int)footL, (int)(feetY + lookDown));

                // Rysujemy prawy sensor (magenta)
                debugG.setColor(Color.MAGENTA);
                debugG.drawLine((int)footR, (int)(feetY - lookUp), (int)footR, (int)(feetY + lookDown));

                // Rysujemy kropki w miejscu "idealnego" styku
                debugG.setColor(Color.YELLOW);
                debugG.fillOval((int)footL - 2, (int)feetY - 2, 4, 4);
                debugG.fillOval((int)footR - 2, (int)feetY - 2, 4, 4);

                debugG.dispose();
            }
        }
    }

    private void renderWorld(double alpha) {
        GameState renderState = this.currentSnapshot;
        // --- KLUCZ: Obliczamy wizualne parametry raz dla całej metody ---
        float drawZoom = (float) (renderState.cam.lastZoom + (renderState.cam.zoom - renderState.cam.lastZoom) * alpha);
        float drawCamX = (float) (renderState.cam.lastX + (renderState.cam.x - renderState.cam.lastX) * alpha);
        float drawCamY = (float) (renderState.cam.lastY + (renderState.cam.y - renderState.cam.lastY) * alpha);

        // Pobieramy viewport używając tych samych wartości (przekażemy je jako argumenty)
        Rectangle2D.Double viewport = getVisibleWorldRect(alpha);
        // 1. Obliczamy zinterpolowany zoom
//        float drawZoom = (float) (renderState.zoom+ (renderState.zoom) * alpha);


        // TWORZYMY JEDNĄ KOPIĘ DLA CAŁEGO ŚWIATA (Kamera / Globalne przesunięcie)
        Graphics2D worldG = (Graphics2D) window.g.create();



        // --- MAGIA ZOOMU (Wyśrodkowanego) ---
        // Pobieramy rozmiar wirtualny (np. 1280x720)
        int vW = ConfigureData.oldWidth;;
        int vH = ConfigureData.oldHeight;

        // A. Przesuwamy punkt (0,0) na środek ekranu wirtualnego
        worldG.translate(vW / 2.0, vH / 2.0);
        // B. Skalujemy świat (Zoom)
        worldG.scale(drawZoom, drawZoom);
        // C. Cofamy przesunięcie (teraz środek świata jest na środku ekranu)
        worldG.translate(-vW / 2.0, -vH / 2.0);


        // Globalne przesunięcie (Kamera + Twoje testowe 200px)
//        worldG.translate(0 - renderState.camX, 0 - renderState.camY);
        worldG.translate(-drawCamX, -drawCamY); // <--- Używamy drawCamX/Y!

        drawDebugGrid(worldG);

        // 1. Pobieramy aktualny prostokąt widoczności


        // 2. Rysowanie Mapy (użytkownik musi ją dodać do klasy Main)
        if (renderState.tileMap != null) {
            renderState.tileMap.draw(worldG, renderState.cam.x, renderState.cam.y, window.getCanvas().getWidth(), window.getCanvas().getHeight());
        }


        for (GameObject s : renderState.sprites) {
            // Wywołujemy draw, przekazując mu worldG.
            // Metoda s.draw() sama zajmie się stworzeniem swojej izolowanej kopii.
            if (s.visible) {
                float dX = (float) (s.lastX + (s.x - s.lastX) * alpha);
                float dY = (float) (s.lastY + (s.y - s.lastY) * alpha);

                // Culling - sprawdzenie widoczności
                if (s.isVisibleOnScreen(viewport, dX, dY)) {
                    s.draw(worldG, alpha);
                }
            }
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
        window.g.drawString("Delta Time: " + String.format("%.2f", alpha), 10, 60);
    }

    private void drawDebugGrid(Graphics2D g) {
        GameState s = this.currentSnapshot;

        // 1. Parametry siatki
        int gridSize = 100; // Linia co 100 pikseli
        int worldLimit = 5000; // Jak daleko siatka ma sięgać

        // Pobieramy zinterpolowany zoom dla poprawnego wyświetlania tekstu
        float drawZoom = (float) (s.cam.lastZoom + (s.cam.zoom - s.cam.lastZoom) * deltaTime);

        // 2. Ustawienia linii pomocniczych (cienkie i szare)
        g.setStroke(new BasicStroke(1.0f / drawZoom)); // Skalujemy grubość linii, by zawsze miała 1px na ekranie
        g.setFont(new Font("Arial", Font.PLAIN, (int)(12 / drawZoom))); // Skalujemy czcionkę
        FontMetrics fm = g.getFontMetrics();

        for (int i = -worldLimit; i <= worldLimit; i += gridSize) {
            // --- LINIE PIONOWE (Oś X) ---
            if (i == 0) g.setColor(Color.GREEN); // Oś Y (X=0) jest zielona
            else g.setColor(new Color(100, 100, 100, 50)); // Reszta szara, półprzezroczysta

            g.drawLine(i, -worldLimit, i, worldLimit);

            // Etykiety X (liczby)
            g.setColor(Color.WHITE);
            g.drawString(i + "px", i + 2, (int)s.cam.y + fm.getAscent() + 5);

            // --- LINIE POZIOME (Oś Y) ---
            if (i == 0) g.setColor(Color.RED); // Oś X (Y=0) jest czerwona
            else g.setColor(new Color(100, 100, 100, 50));

            g.drawLine(-worldLimit, i, worldLimit, i);

            // Etykiety Y (liczby)
            g.setColor(Color.WHITE);
            g.drawString(i + "px", (int)s.cam.y + 5, i - 2);
        }

        // 3. Punkt ZERO (0,0) - mały celownik
        g.setColor(Color.YELLOW);
        int crossSize = 10;
        g.drawLine(-crossSize, 0, crossSize, 0);
        g.drawLine(0, -crossSize, 0, crossSize);
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
    public void setRenderPixelArt(){
        window.typeOfRenderingSprites = "pixelart";
    }

    public void setRenderWithSmooth() {
        window.typeOfRenderingSprites = "normal";
    }

}