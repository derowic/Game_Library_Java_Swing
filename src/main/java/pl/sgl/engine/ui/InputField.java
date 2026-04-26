package pl.sgl.engine.ui;

import pl.sgl.engine.InputHandler;
import pl.sgl.engine.MouseHandler;

import java.awt.*;
import java.awt.event.KeyEvent;

public class InputField extends UIElement {
    public StringBuilder text = new StringBuilder();
    public boolean hasFocus = false;
    private int cursorTimer = 0;

    // NOWE POLA
    private int cursorIndex = 0; // Pozycja kursora (od 0 do text.length())
    private int scrollOffset = 0; // Ile pikseli przesunęliśmy tekst w lewo
    private int fontSize;
    private Font font;

    public InputField(int x, int y, int width, int height,  int fontSize) {
        this.bounds = new Rectangle(x, y, width, height);
        this.fontSize = fontSize;
        // Tworzymy obiekt fontu raz
        this.font = new Font("Arial", Font.PLAIN, fontSize);
    }

    // Metoda do zmiany rozmiaru w trakcie działania gry
    public void setFontSize(int size) {
        this.fontSize = size;
        this.font = new Font("Arial", Font.PLAIN, size);
    }

    @Override
    public void update(InputHandler input, MouseHandler mouse) {
        if (mouse.isButtonPressed(1)) {
            boolean previousHasFocus = hasFocus;
            hasFocus = bounds.contains(mouse.getX(), mouse.getY());
            if(hasFocus != previousHasFocus) {
                input.resetCharBuffer();
            }
        }

        if (hasFocus) {

            // 1. PORUSZANIE KURSOREM (Strzałki)
            if (input.isKeyPressed(KeyEvent.VK_LEFT)) {
                if (cursorIndex > 0) cursorIndex--;
                cursorTimer = 0; // zresetuj miganie przy ruchu
            }
            if (input.isKeyPressed(KeyEvent.VK_RIGHT)) {
                if (cursorIndex < text.length()) cursorIndex++;
                cursorTimer = 0;
            }

            // 2. USUWANIE (Backspace i Delete)
            if (input.isKeyPressed(KeyEvent.VK_BACK_SPACE)) {
                if (cursorIndex > 0) {
                    text.deleteCharAt(cursorIndex - 1);
                    cursorIndex--;
                }
            }
            if (input.isKeyPressed(KeyEvent.VK_DELETE)) {
                if (cursorIndex < text.length()) {
                    text.deleteCharAt(cursorIndex);
                }
            }

            // 3. WPISYWANIE
            char c;
            while ((c = input.getNextChar()) != '\0') {
                if (c >= 32 && c <= 126) { // Tylko znaki drukowalne
                    text.insert(cursorIndex, c);
                    cursorIndex++;
                }
            }

            if (input.isKeyPressed(KeyEvent.VK_ENTER)) {
                hasFocus = false;
            }

            cursorTimer = (cursorTimer + 1) % 60;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        // 1. Rysowanie ramki
        g.setColor(hasFocus ? Color.YELLOW : Color.WHITE);
        g.draw(bounds);

        // Ustawiamy font
//        g.setFont(new Font("Arial", Font.PLAIN, 20));
//        FontMetrics fm = g.getFontMetrics();
//        int padding = 5;

        g.setFont(this.font); // Używamy naszego pola
        FontMetrics fm = g.getFontMetrics(); // To pobierze wymiary dla nowego rozmiaru
        int padding = 5;

        // 2. OBLICZANIE PRZEWIJANIA (SCROLLING)
        // Obliczamy gdzie fizycznie znajduje się kursor w pikselach
        String textBeforeCursor = text.substring(0, cursorIndex);
        int cursorPixelPos = fm.stringWidth(textBeforeCursor);

        // Jeśli kursor wyjdzie poza prawą krawędź pola
        if (cursorPixelPos - scrollOffset > bounds.width - padding * 2) {
            scrollOffset = cursorPixelPos - (bounds.width - padding * 2);
        }
        // Jeśli kursor wyjdzie poza lewą krawędź
        if (cursorPixelPos - scrollOffset < 0) {
            scrollOffset = cursorPixelPos;
        }

        // 3. RYSOWANIE Z "PRZYCIĘCIEM" (Clipping)
        // Tworzymy obszar, poza którym nic się nie narysuje (żeby tekst nie wyciekł z ramki)
        Shape oldClip = g.getClip();
        g.setClip(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);

        // Rysujemy tekst z uwzględnieniem offsetu
        int textY = bounds.y + (bounds.height - fm.getHeight()) / 2 + fm.getAscent();
        g.setColor(Color.WHITE);
        g.drawString(text.toString(), bounds.x + padding - scrollOffset, textY);

        // 4. RYSOWANIE KURSORA
        if (hasFocus && cursorTimer < 30) {
            g.setColor(Color.CYAN);
            int cx = bounds.x + padding + cursorPixelPos - scrollOffset;
            g.drawLine(cx, bounds.y + padding, cx, bounds.y + bounds.height - padding);
        }

        // Resetujemy clip do stanu poprzedniego
        g.setClip(oldClip);
    }
}