package io.github.projectopa2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    public Sprite sprite;               // Criação da Sprite
    public Vector2 gridPosition;        // Posição do inimigo
    public float moveDelay;             // Delay do movimento para cada inimigo
    public float moveTimer;             // Timer de movimento para cada inimigo
    public MovementStrategy strategy;   // Tipo de movimentação a utilizar para o inimigo

    public Enemy(Texture texture, int x, int y, float moveDelay) {
        this.gridPosition = new Vector2(x, y);
        this.sprite = new Sprite(texture);
        this.sprite.setSize(1, 1);
        this.sprite.setPosition(x, y);
        this.moveDelay = moveDelay;
        this.moveTimer = 0f;
    }

    //Validação se o inimigo pode mover
    public boolean canMove(float delta) {
        moveTimer += delta;             // Adiciona ao timer de movimento o delta (tempo passado de frame para frame)
        if (moveTimer >= moveDelay) {   // Se o Timer for maior ou igual ao delay
            moveTimer = 0;              // Reset do timer de movimento
            return true;                // Devolve true
        }
        return false;                   // Devolve falso se o inimigo ainda não poder avançar (Se o Timer for menor que o delay)
    }

    public void setStrategy(MovementStrategy strategy) { // função para devinir o tipo de movimentação a ser utilizada pelo inimigo
        this.strategy = strategy;
    }

    public MapNode getNextStep(GameMap map, MapNode playerNode) { // função para devolver a proxima node (próximo passo) do inimigo basedo na Estratégia utilizada para esse tipo de inimigo
        return strategy.getNextNode(this, map, playerNode);
    }
}
