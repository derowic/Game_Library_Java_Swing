package pl.sgl.engine;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener, MouseMotionListener {

    private int mouseX, mouseY;
    private final boolean[] buttons = new boolean[10];
    private final boolean[] buttonsLast = new boolean[10];

    // Dane o transformacji
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    // Metoda, którą wywoła silnik po obliczeniu skali w renderze
    public void setTransformation(double scale, int offsetX, int offsetY) {
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    ///gettery, aby uwzględniały skalowanie (o którym pisaliśmy wcześniej)
    public int getX() { return (int) ((mouseX - offsetX) / scale); }
    public int getY() { return (int) ((mouseY - offsetY) / scale); }


    public boolean isButtonDown(int button) {
        if (button < 0 || button >= buttons.length) return false;
        return buttons[button];
    }

    public boolean isButtonPressed(int button) {
        if (button < 0 || button >= buttons.length) return false;
        return buttons[button] && !buttonsLast[button];
    }

    // Metoda wywoływana przez silnik na końcu każdego update'u
    public void update() {
        System.arraycopy(buttons, 0, buttonsLast, 0, buttons.length);
    }

    // MouseMotionListener - śledzenie ruchu
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // MouseListener - przyciski
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() < buttons.length) {
            buttons[e.getButton()] = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() < buttons.length) {
            buttons[e.getButton()] = false;
        }
    }

    // Nieużywane, ale wymagane przez interfejsy
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}