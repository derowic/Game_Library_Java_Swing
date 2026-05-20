package pl.sgl.engine;

import pl.sgl.engine.texture.Texture;

import java.awt.*;

public class Sprite extends GameObject {


    public Sprite(String pathToTexture, float x, float y) {
        super(x,y);
        texture = new Texture(pathToTexture);
        width = texture.image.getWidth();
        height = texture.image.getHeight();
        System.out.println(width);
        hitbox = texture.getHitBox();
        System.out.println(texture.rectangle);
        this.srcX = 0;
        this.srcY = 0;
        this.srcW = texture.image.getWidth();
        this.srcH = texture.image.getHeight();
//        info();
    }

    public void info()
    {
        System.out.println("width: " +width);
        System.out.println("height: " +height);
    }



//    @Override
//    public void setScaleY(double scaleY) {
//        this.scaleY = scaleY;
//        // USUNIĘTO: texture.rectangle.height *= scaleY; // NIGDY TEGO NIE RÓB
//    }
//
//    @Override
//    public void setScaleX(double scaleX) {
//        this.scaleX = scaleX;
//        // USUNIĘTO: texture.rectangle.width *= scaleX; // NIGDY TEGO NIE RÓB
//    }

    public boolean intersects(Sprite s) {
        return this.texture.rectangle.intersects(s.texture.rectangle);
    }

    public void rotate(double angle) {
        rotation = angle;
        texture.rectangle = getRotatedBounds();
    }
}