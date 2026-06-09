package pl.sgl.engine.GameTest;

import pl.sgl.engine.Game;
import pl.sgl.engine.animation.Animation;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager {
    public List<Enemy> enemies = new ArrayList<>();

    public EnemyManager(List<Platform> platforms) {
        for(int i=0; i< 10 ;i++) {
            generate(platforms.get(i));
//            System.out.println("create enemey");
        }
    }

    public Enemy generate(Platform platform) {
        Enemy enemy = new Enemy(platform);
        enemy.velocityY = platform.velocityY;

        enemies.add(enemy);
        return enemy;
    }

    public void recycle (Platform platform) {
        for (Enemy c: enemies) {
            if (c.y >= 1000 ) {
//                c.setPosition(platform.x + platform.width / 2 - c.width / 2, platform.y - c.height);
//                System.out.println(" recycle  ");
//                System.out.println(c.x);
//                System.out.println(c.minX);
//                System.out.println(c.maxX);
//
//
                c.setPosition(platform.x + c.getWidth(), platform.y - c.getHeight()/2);
//                System.out.println("platform sizes:");
//                System.out.println(platform.x);
//                System.out.println(c.getWidth());
//
//
//                System.out.println("new X "+ (platform.x + c.getWidth()));
//                System.out.println(" ");
//                System.out.println(c.x);
//                System.out.println(c.minX);
//                System.out.println(c.maxX);

                c.velocityY = platform.velocityY;
                c.minX = (int) ((int) platform.x + c.getWidth()/2);
                c.maxX = (int) (platform.x + platform.getWidth() - c.getWidth()/2);

//                System.out.println("  ");
//                System.out.println(c.x);
//                System.out.println(c.minX);
//                System.out.println(c.maxX);
//                System.out.println("add enemy");
            }
        }
    }

    public void move (double deltaTime) {
//        System.out.println("  ");
        for (Enemy e : enemies) {
            if(e.y < 1500) {
                e.moveByVelocity();
            }
            e.ai();
//            System.out.println(e.velocityX);
        }
    }


}
