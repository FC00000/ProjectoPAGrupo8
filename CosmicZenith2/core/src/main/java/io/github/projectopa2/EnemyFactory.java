package io.github.projectopa2;
import com.badlogic.gdx.graphics.Texture;

// Factory Design Pattern + Strategy Design Pattern
public class EnemyFactory {
    public static Enemy createEnemy(String type, Texture texture, int x, int y) {
        Enemy enemy;        // Variável que vai ser devolvida
        switch (type) {     // Diferentes tipos de inimigos que podemos criar (Diferenças na velocidade e no tipo de movimentação utilizado)
            case "FastChase":
                enemy = new Enemy(texture, x, y, 0.4f);
                enemy.setStrategy(new ChasePlayerStrategy());
                break;
            case "SlowChase":
                enemy = new Enemy(texture, x, y, 1.2f);
                enemy.setStrategy(new ChasePlayerStrategy());
                break;
            case "FastWander":
                enemy = new Enemy(texture, x, y, 0.4f);
                enemy.setStrategy(new WanderStrategy());
                break;
            case "NormalWander":
                enemy = new Enemy(texture, x, y, 0.75f);
                enemy.setStrategy(new WanderStrategy());
                break;

            default:
                enemy = new Enemy(texture, x, y, 0.75f);
                enemy.setStrategy(new ChasePlayerStrategy());
        }
        return enemy;
    }
}