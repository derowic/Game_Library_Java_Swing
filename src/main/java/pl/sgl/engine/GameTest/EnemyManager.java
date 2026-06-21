package pl.sgl.engine.GameTest;

import pl.sgl.engine.Game;
import pl.sgl.engine.GameObject;
import pl.sgl.engine.SceneManager;
import pl.sgl.engine.animation.Animation;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager {
    public static List<Enemy> enemies = new ArrayList<>();


    public EnemyManager(List<Platform> platforms) {
    }


    public static void generate(Platform platform) {
        System.out.println("enemies size: " + enemies.size());
        if (enemies.size() < 10) {
            Enemy enemy = new Enemy(platform);
            enemy.velocityY = platform.velocityY;

            enemies.add(enemy);
            SceneManager.getScene("Game").addGameObject(enemy);
        }
    }

//    public void recycle (Platform platform) {
//        for (Enemy c: enemies) {
//            if (c.y >= 1000 ) {
//                c.setPosition(platform.x + c.getWidth(), platform.y - c.getHeight()/2);
//                c.velocityY = platform.velocityY;
//                c.minX = (int) ((int) platform.x + c.getWidth()/2);
//                c.maxX = (int) (platform.x + platform.getWidth() - c.getWidth()/2);
//            }
//        }
//    }

    public void move() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            if(e.y <= GameScene.player.sprite.y + 1000) {
                e.moveByVelocity();
                e.ai();
            } else {
                deleteEnemy(e); // Teraz możesz bezpiecznie wywołać swoją metodę
            }
        }
    }

    public static void deleteEnemy(Enemy go) {
        enemies.remove(go);
        go.destroy();
    }
}
