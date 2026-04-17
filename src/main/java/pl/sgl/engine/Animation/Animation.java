package pl.sgl.engine.Animation;

import Texture.TextureLoader;

import java.awt.image.BufferedImage;

public class Animation {
    BufferedImage[] frames;

    public Animation(String pathToSpriteSheet, int x, int y, int width, int height, int framesCount) {
        frames = TextureLoader.loadOneAnimation(pathToSpriteSheet, x, y, width, height, framesCount);
    }
}
