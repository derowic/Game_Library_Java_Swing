package pl.sgl.engine.GameTest;

import pl.sgl.engine.Colision;
import pl.sgl.engine.GameObject;


public class ColisionManager {

    public static void coins_player(Player player, CoinManager coinManager) {
        for (int i = 0; i < coinManager.coins.size(); i++) {
            GameObject p = coinManager.coins.get(i);
            if(p.isActive()) {
                if (Colision.checkCollision(player.sprite, p)) {
                    player.coins++;
                    p.destroy();
                    player.coinsNumberLabel.setText(String.valueOf(player.coins));

                }
            }
        }
    }

    public static void enemies_player(Player player, EnemyManager enemyManager) {
        for (int i = 0; i < enemyManager.enemies.size(); i++) {
            GameObject p = enemyManager.enemies.get(i);
            if(p.isActive()) {
                if (Colision.checkCollision(player.sprite, p)) {
                    if ((player.sprite.y + (double) player.sprite.getHeight() / 2) < p.y && player.sprite.velocityY >= 0) {
                        System.out.println(p.velocityY);
                        p.destroy();
                        player.sprite.velocityY = -400 *1.5;
                        System.out.println("killed enemy");
                    } else {
                        //add timer that will check by 0.1 if player i hited
                        player.health--;
                        System.out.println("Get hit: "+player.health);
                        if(player.sprite.x <= p.x) {
                            player.sprite.velocityX = -750 *1.5;
                            player.sprite.velocityY = -400 *1.5;
                        } else {
                            player.sprite.velocityX = 750 *1.5;
                            player.sprite.velocityY = -400 *1.5;
                        }
                        player.sprite.moveByVelocity();
                        player.sprite.setAnimation("hurt");
                        player.playerStatus = "hurt";
                        player.animTiemr.start();
                        player.hurt = true;
                    }
                }
            }
        }
    }
}
