package io.github.projectopa2;
import com.badlogic.gdx.graphics.Texture;

public class EnemyFactory {
    public static Enemy createEnemy(String type, Texture texture, int x, int y) {
        switch (type) {
            case "fast":
                return new Enemy(texture, x, y, 0.5f); // fast enemy
            case "slow":
                return new Enemy(texture, x, y, 1.5f); // slow enemy
            case "normal":
            default:
                return new Enemy(texture, x, y, 1.0f); // default speed
        }
    }
}
