package pl.sgl.engine;

import pl.sgl.engine.texture.Texture;

import java.awt.*;

public class Sprite extends GameObject {


    public Sprite(String pathToTexture, float x, float y) {
        super(x,y);
        texture = new Texture(pathToTexture);
        width = texture.image.getWidth();
        height = texture.image.getHeight();
        getCalculatedAutoHitBoxes();
//        info();
    }

    public void info()
    {
        System.out.println("width: " +width);
        System.out.println("height: " +height);
    }

    @Override
    public void update(double deltaTime)
    {
        this.lastX = this.x;
        this.x += (velocityX * deltaTime);

        this.lastY = this.y;
        this.y += (velocityY * deltaTime);

    }

    @Override
    public Rectangle getCalculatedAutoHitBoxes() {

        return texture.getHitBox();
    }

    @Override
    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
        // USUNIĘTO: texture.rectangle.height *= scaleY; // NIGDY TEGO NIE RÓB
    }

    @Override
    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
        // USUNIĘTO: texture.rectangle.width *= scaleX; // NIGDY TEGO NIE RÓB
    }

    public boolean intersects(Sprite s) {
        return this.texture.rectangle.intersects(s.texture.rectangle);
    }

    public void rotate(double angle) {
        rotation = angle;
        texture.rectangle = getRotatedBounds();
    }
}