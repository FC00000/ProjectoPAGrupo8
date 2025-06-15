package io.github.projectopa2;
import com.badlogic.gdx.utils.Array;

public class MapNode {
    public int x, y;
    public boolean isWall;
    public Array<MapNode> neighbors;

    public MapNode(int x, int y, boolean isWall) {
        this.x = x;
        this.y = y;
        this.isWall = isWall;
        this.neighbors = new Array<>();
    }
}
