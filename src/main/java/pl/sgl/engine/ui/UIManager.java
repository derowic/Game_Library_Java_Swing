package pl.sgl.engine.ui;

import pl.sgl.engine.InputHandler;
import pl.sgl.engine.MouseHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UIManager {
    private List<UIElement> elements = new ArrayList<>();
    private boolean mouseCaptured = false;
    private boolean keyboardCaptured = false;

    public void addElement(UIElement e) { elements.add(e); }

    public List<UIElement> getElements() {
        return elements;
    }

    public void setElements(List<UIElement> elements) {
        this.elements = elements;
    }

    public void update(InputHandler input, MouseHandler mouse) {
        mouseCaptured = false;
        keyboardCaptured = false;

        // Iterujemy OD KOŃCA (bo ostatnie dodane elementy są na wierzchu)
        for (int i = elements.size() - 1; i >= 0; i--) {
            UIElement e = elements.get(i);

            // Aktualizujemy element
            e.update(input, mouse);

            // 1. Sprawdzamy czy element UI zabiera myszkę
            if (e.isMouseOver(mouse)) {
                mouseCaptured = true;
            }

            // 2. Sprawdzamy czy element UI zabiera klawiaturę (np. InputField)
            if (e instanceof InputField && ((InputField) e).hasFocus) {
                keyboardCaptured = true;
            }
        }
//        if(mouseCaptured) {
//            mouse.reset();
//            System.out.println("mouse captured");
//        }
//        if(keyboardCaptured) {
//            input.reset();
//            System.out.println("keyborad capturee");
//        }
    }

    public boolean isMouseCaptured() { return mouseCaptured; }
    public boolean isKeyboardCaptured() { return keyboardCaptured; }
}