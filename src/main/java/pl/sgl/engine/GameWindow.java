package pl.sgl.engine;

import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;

public class GameWindow {

    private JFrame frame;
    private Canvas canvas;
    public BufferStrategy bs;
    public Graphics2D g;

    public GameWindow(String title, int width, int height) {

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
        g.fillRect(0, 0, 800, 600);  // czyść ekran
    }

    public void render() {
        // rysuj obiekty gry...
        // ───────────────────────────────────────

        g.dispose(); // zawsze zwalniaj Graphics
        bs.show();   // zamień bufory – tylny staje się widocznym
    }

    public void initInput(InputHandler input) {
        canvas.addKeyListener(input);
        canvas.requestFocus(); // Ważne: Canvas musi mieć focus, żeby czytać klawisze
    }
}