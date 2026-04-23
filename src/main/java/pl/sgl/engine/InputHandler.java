package pl.sgl.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    // Tablica wszystkich możliwych klawiszy (standardowo 256 lub 65536 dla Unicode)
    private final boolean[] keys = new boolean[65536];
    private final boolean[] keysLast = new boolean[65536];


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
        // Zazwyczaj nieużywane w grach, keyPressed jest lepsze
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
}
