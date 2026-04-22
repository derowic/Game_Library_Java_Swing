package pl.sgl.engine;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Window {

    private JFrame frame;
    private Canvas canvas;
    public BufferStrategy bs;
    public Graphics2D g;
    public boolean showMouse = true;

    public Window(String title, int width, int height) {

        // 1. Włącz akcelerację sprzętową PRZED utworzeniem okna
        System.setProperty("sun.java2d.opengl", "true");

        // 2. Canvas – nasze płótno do rysowania
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setFocusable(true);      // żeby odbierał klawiaturę
        canvas.setIgnoreRepaint(true);  // wyłącz automatyczne odświeżanie Swinga

        // 3. JFrame – tylko kontener na Canvas
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setIgnoreRepaint(true);   // też wyłączamy na oknie
        frame.add(canvas);
        frame.pack();                   // dopasuj rozmiar okna do Canvas
        frame.setLocationRelativeTo(null); // wyśrodkuj na ekranie


//        setFullScreen();
    }

    // TA METODA WŁĄCZA FULLSCREEN
    public void setFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        if (gd.isFullScreenSupported()) {
            // 1. Ukrywamy okno, żeby zmienić jego styl
            frame.dispose();
            // 2. Usuwamy ramki (tytuł, przyciski zamknij/minimalizuj)
            frame.setUndecorated(true);
            // 3. Włączamy tryb pełnoekranowy
            gd.setFullScreenWindow(frame);
            // 4. Pokazujemy okno ponownie
            frame.setVisible(true);

            // WAŻNE: Po zmianie na Fullscreen Canvas musi odświeżyć BufferStrategy
            canvas.createBufferStrategy(2);
        } else {
            System.err.println("Fullscreen nie jest wspierany na tym sprzęcie.");
        }
    }

    public void setWindowedMode(int width, int height) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        // 1. Wyłączamy tryb Fullscreen w systemie
        gd.setFullScreenWindow(null);

        // 2. Musimy "zniszczyć" okno (tylko zasoby), aby zmienić jego styl
        frame.dispose();

        // 3. Przywracamy ramki okna
        frame.setUndecorated(false);

        // 4. Przywracamy rozmiar Canvasu
        canvas.setPreferredSize(new Dimension(width, height));

        // 5. Dopasowujemy ramkę do Canvasu i środkujemy
        frame.pack();
        frame.setLocationRelativeTo(null);

        // 6. Pokazujemy okno i odświeżamy BufferStrategy
        frame.setVisible(true);
        canvas.createBufferStrategy(2);
    }

    public void show() {
        frame.setVisible(true);
        // WAŻNE: BufferStrategy tworzymy dopiero po setVisible()
        // – wcześniej Canvas nie ma jeszcze zasobów graficznych
        canvas.createBufferStrategy(2); // 2 = double buffering
    }

    public Canvas getCanvas() { return canvas; }

    public void prepareToRender(){
        bs = canvas.getBufferStrategy();

        // pobierz Graphics z bufora tylnego (niewidocznego)
        g = (Graphics2D) bs.getDrawGraphics();

        // ── tutaj rysujesz ──────────────────────
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());  // czyść ekran
    }

    public void render() {
        // rysuj obiekty gry...
        // ───────────────────────────────────────
        if(!showMouse) {
            canvas.setCursor(canvas.getToolkit().createCustomCursor(
                    new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
                    new Point(0, 0), "blank"));
        }
        g.dispose(); // zawsze zwalniaj Graphics
        bs.show();   // zamień bufory – tylny staje się widocznym
    }

    public void initInput(InputHandler input) {
        canvas.addKeyListener(input);
        canvas.requestFocus(); // Ważne: Canvas musi mieć focus, żeby czytać klawisze
    }

    // W GameWindow.java:
    public void initMouse(MouseHandler mouse) {
        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);
    }
}