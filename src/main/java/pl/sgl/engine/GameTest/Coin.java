package pl.sgl.engine.GameTest;

import pl.sgl.engine.Sprite;
import pl.sgl.engine.animation.AnimatedSprite;

public class Coin extends AnimatedSprite {
    public int value;
    public Coin(float x, float y, double speed, int value){
        super(x, y, speed);
        this.value = value;
    }
}
