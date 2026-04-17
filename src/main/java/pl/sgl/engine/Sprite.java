package pl.sgl.engine;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sprite extends GameObject {

    public Sprite(BufferedImage image, float x, float y) {
        super(x,y);
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
//        info();
    }

    public Sprite(BufferedImage image) {
        super(0,0);
        this.image = image;
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
        g2d.drawImage((Image) image, 0,0, finalWidth, finalHeight, null);
    }
}