package pl.sgl.engine;

import pl.sgl.engine.texture.Texture;

public class Sprite extends GameObject {
    public Sprite(String pathToTexture, float x, float y) {
        super(x,y);
        texture = new Texture(pathToTexture);
        frameWidth = texture.image.getWidth();
        frameHeight = texture.image.getHeight();
        hitbox = texture.getHitBox();
        this.srcX = 0;
        this.srcY = 0;
        this.srcW = texture.image.getWidth();
        this.srcH = texture.image.getHeight();
    }

    public Sprite(String pathToTexture, float x, float y, int texX, int texY, int texWidth, int texHeight) {
        super(x,y);
        texture = new Texture(pathToTexture, texX, texY, texWidth, texHeight);
        frameWidth = texture.image.getWidth();
        frameHeight = texture.image.getHeight();
        hitbox = texture.getHitBox();
        this.srcX = 0;
        this.srcY = 0;
        this.srcW = texture.image.getWidth();
        this.srcH = texture.image.getHeight();
    }

    public Sprite(Sprite s) {
        super(s.x,s.y);
        texture = s.texture;
        frameWidth = texture.image.getWidth();
        frameHeight = texture.image.getHeight();
        hitbox = texture.getHitBox();
        this.srcX = 0;
        this.srcY = 0;
        this.srcW = texture.image.getWidth();
        this.srcH = texture.image.getHeight();
//        refreshTiledCache();
//        info();
    }

    public Sprite() {
        super(0,0);
    }

    public void info()
    {
        System.out.println("width: " + frameWidth);
        System.out.println("height: " + frameHeight);
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