package io.github.projectopa2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

class Enemy {
    public Sprite sprite;         // Criação da Sprite
    public Vector2 gridPosition;  // Posição do inimigo

    public Enemy(Sprite sprite, Vector2 gridPosition) {
        this.sprite = sprite;
        this.gridPosition = gridPosition;             //Posição Inicial
    }

    public static Enemy createEnemy(Texture enemyTexture, int gridX, int gridY) { // Função para criar novos inimigos

        Sprite enemySprite = new Sprite(enemyTexture);  //Criar nova Sprite com a textura do inimigo
        enemySprite.setSize(1, 1);          // Tamanho da sprite no mapa
        enemySprite.setPosition(gridX, gridY);          // Posição no mapa

        return new Enemy(enemySprite, new Vector2(gridX, gridY)); //Devolve um inimigo com a sua sprite e a sua posição no mapa
    }

}
