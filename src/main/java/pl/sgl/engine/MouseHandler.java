package pl.sgl.engine;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener, MouseMotionListener {

    private int mouseX, mouseY;
    private final boolean[] buttons = new boolean[10];

    public int getX() { return mouseX; }
    public int getY() { return mouseY; }

    public boolean isButtonDown(int button) {
        if (button < 0 || button >= buttons.length) return false;
        return buttons[button];
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