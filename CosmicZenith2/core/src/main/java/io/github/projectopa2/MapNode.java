package io.github.projectopa2;
import com.badlogic.gdx.utils.Array;

public class MapNode {
    public int x, y;                 // As cordenadas do mapa de nodes
    public boolean isWall;           // Booleano para validar se é uma parede
    public Array<MapNode> neighbors; // Lista das nodes vizinhas

    public MapNode(int x, int y, boolean isWall) {
        this.x = x;
        this.y = y;
        this.isWall = isWall;
        this.neighbors = new Array<>();
    }
}
