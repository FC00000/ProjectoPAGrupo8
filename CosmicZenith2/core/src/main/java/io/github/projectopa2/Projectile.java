package io.github.projectopa2;
import com.badlogic.gdx.math.Vector2;

class Projectile {
    public Vector2 position;  // posição inicial do laser
    public Vector2 direction;   // direção e movimento do laser

    public Projectile(Vector2 position, Vector2 direction) {
        this.position = new Vector2(position);
        this.direction = new Vector2(direction);
    }
}
