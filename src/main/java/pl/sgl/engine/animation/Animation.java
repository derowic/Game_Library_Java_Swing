package pl.sgl.engine.animation;

import pl.sgl.engine.texture.TextureLoader;

import java.awt.image.BufferedImage;

public class Animation {
    BufferedImage[] frames;

    public Animation(String pathToSpriteSheet, int x, int y, int width, int height, int framesCount) {
        frames = TextureLoader.loadOneAnimation(pathToSpriteSheet, x, y, width, height, framesCount);
    }
}
