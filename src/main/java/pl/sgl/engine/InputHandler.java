package pl.sgl.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;

public class InputHandler implements KeyListener {
    // Tablica wszystkich możliwych klawiszy (standardowo 256 lub 65536 dla Unicode)
    private boolean[] keys = new boolean[65536];
    private boolean[] keysLast = new boolean[65536];

    // --- DODATEK: Kolejka na wpisane litery ---
    private final Queue<Character> charBuffer = new LinkedList<>();

    // Sprawdza, czy klawisz jest aktualnie trzymany
    public boolean isKeyDown(int keyCode) {
        if (keyCode < 0 || keyCode >= keys.length) return false;
        return keys[keyCode];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < keys.length) {
            keys[code] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < keys.length) {
            keys[code] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // --- DODATEK: Przechwytywanie liter ---
        char c = e.getKeyChar();
        // Ignorujemy znaki specjalne, które obsłużymy przez isKeyPressed
        if (c != KeyEvent.CHAR_UNDEFINED && c != '\b' && c != '\n') {
            synchronized (charBuffer) {
                charBuffer.add(c);
            }
        }
    }

    // Metoda wywoływana raz na końcu każdego update'u w Game.java
    public void update() {
        System.arraycopy(keys, 0, keysLast, 0, keys.length);
    }

    // Sprawdza, czy klawisz został WŁAŚNIE wciśnięty (klatka temu nie był)
    public boolean isKeyPressed(int keyCode) {
//        System.out.println("");
//        System.out.println(keyCode);
//        System.out.println(isKeyDown(keyCode));
//        System.out.println(!keysLast[keyCode]);
        return isKeyDown(keyCode) && !keysLast[keyCode];
    }

    // --- DODATEK: Metoda do pobierania liter ---
    public char getNextChar() {
        synchronized (charBuffer) {
            if (charBuffer.isEmpty()) return '\0';
            return charBuffer.poll();
        }
    }

    public void resetCharBuffer() {
        charBuffer.clear();
    }

    // W InputHandler.java
    public void consumeKey(int keyCode) {
        if (keyCode >= 0 && keyCode < keys.length) {
            keys[keyCode] = false;
            keysLast[keyCode] = true; // Ustawiamy true, żeby isKeyPressed również zwróciło false
        }
    }

    public void reset() {
        boolean[] keys = new boolean[65536];
        boolean[] keysLast = new boolean[65536];
    }
}
