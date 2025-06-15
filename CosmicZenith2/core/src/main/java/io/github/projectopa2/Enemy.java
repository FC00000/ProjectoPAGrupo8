package io.github.projectopa2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

class Enemy {
    public Sprite sprite;
    public Vector2 gridPosition;
    public float moveDelay;
    public float moveTimer;


    public Enemy(Texture texture, int x, int y, float moveDelay) {
        this.gridPosition = new Vector2(x, y);
        this.sprite = new Sprite(texture);
        this.sprite.setSize(1, 1);
        this.sprite.setPosition(x, y);
        this.moveDelay = moveDelay;
        this.moveTimer = 0f;
    }

    public boolean canMove(float delta) {
        moveTimer += delta;
        if (moveTimer >= moveDelay) {
            moveTimer = 0;
            return true;
        }
        return false;
    }

}
