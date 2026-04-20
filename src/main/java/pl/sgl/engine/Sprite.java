package pl.sgl.engine;

import pl.sgl.engine.texture.Texture;

import java.awt.*;

public class Sprite extends GameObject {

    public Texture texture;
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
    public void draw(Graphics2D g2d, double alpha){
        super.draw(g2d,alpha);

        // 2. scaling
        int finalWidth = (int) (width * scaleX);
        int finalHeight = (int) (height * scaleY);

        if (rotation !=0) {
            g2d.rotate(Math.toRadians(rotation), finalWidth /2, finalHeight /2 );
        }
        g2d.drawImage((Image) texture.image, 0,0, finalWidth, finalHeight, null);
    }

    @Override
    public Rectangle getCalculatedAutoHitBoxes() {
        return texture.getHitBox();
    }

    public boolean intersects(Sprite s) {
        return this.texture.rectangle.intersects(s.texture.rectangle);
    }

    public void rotate(double angle) {
        rotation = angle;
        texture.rectangle = getRotatedBounds();
    }
}