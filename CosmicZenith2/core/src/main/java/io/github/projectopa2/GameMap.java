package io.github.projectopa2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class GameMap {
    public MapNode[][] nodes;
    public Array<MapNode> allNodes = new Array<>();
    public MapNode spawnNode;
    public MapNode portalNode;
    public MapNode treasureNode;

    public Array<Enemy> enemies;

    public static GameMap generateFirstMap(Texture enemyTexture, int width, int height) {
        GameMap map = new GameMap();
        map.nodes = new MapNode[width][height];

        // Create all nodes and mark walls
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean isWall = (x == 8 && y >= 1 && y <= 7 && y != 6 && y != 4 && y != 2) ||
                        (x == 11 && y >= 1 && y <= 7 && y != 7 && y != 5 && y != 3 && y != 1) ||
                        (x == 5 && y >= 1 && y <= 7 && y != 6 && y != 4 && y != 2) ||
                        (x == 2 && y >= 1 && y <= 7 && y != 7 && y != 5 && y != 3 && y != 1);

                MapNode node = new MapNode(x, y, isWall);
                map.nodes[x][y] = node;
                map.allNodes.add(node);
            }
        }

        // Link neighbors
        connectNeighbors(map, width, height);

        map.spawnNode = map.nodes[0][4];
        map.portalNode = map.nodes[15][4];
        map.treasureNode = null;

        map.enemies = new Array<>();
        map.enemies.add(EnemyFactory.createEnemy("fast",enemyTexture, 15, 8));
        return map;
    }

    public static GameMap generateSecondMap(Texture enemyTexture, int width, int height) {
        GameMap map = new GameMap();
        map.nodes = new MapNode[width][height];

        // Create all nodes and mark walls
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean isWall = (x == 5 || x == 6) && y == 5;

                if ((x == 5 || x == 10) && y >= 2 && y <= 7) isWall = true;

                // Middle blockage with gap
                if (y == 5 && x >= 6 && x <= 9 && x != 7 && x != 8) isWall = true;


                MapNode node = new MapNode(x, y, isWall);
                map.nodes[x][y] = node;
                map.allNodes.add(node);
            }
        }

        // Link neighbors
        connectNeighbors(map, width, height);

        map.spawnNode = map.nodes[1][1];
        map.portalNode = map.nodes[13][4];
        map.treasureNode = null;

        map.enemies = new Array<>();
        map.enemies.add(EnemyFactory.createEnemy("fast",enemyTexture, 14, 1));
        map.enemies.add(EnemyFactory.createEnemy("slow",enemyTexture, 8, 8));
        return map;
    }

    public static GameMap generateThirdMap(Texture enemyTexture, int width, int height) {
        GameMap map = new GameMap();
        map.nodes = new MapNode[width][height];

        // Create all nodes and mark walls
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // More scattered walls for a complex map
                boolean isWall =
                        (x == 3 && y >= 1 && y <= 8) ||
                                (x == 7 && y >= 2 && y <= 6) ||
                                (x == 12 && y >= 0 && y <= 4) ||
                                ((x == 9 || x == 10) && y == 5);

                MapNode node = new MapNode(x, y, isWall);
                map.nodes[x][y] = node;
                map.allNodes.add(node);
            }
        }

        connectNeighbors(map, width, height);

        map.spawnNode = map.nodes[0][0];
        map.portalNode = null;
        map.treasureNode = map.nodes[14][8];

        map.enemies = new Array<>();
        map.enemies.add(EnemyFactory.createEnemy("normal",enemyTexture, 5, 5));
        map.enemies.add(EnemyFactory.createEnemy("fast",enemyTexture, 10, 2));
        map.enemies.add(EnemyFactory.createEnemy("slow",enemyTexture, 14, 8));

        return map;
    }

    private static void connectNeighbors(GameMap map, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MapNode node = map.nodes[x][y];
                if (node.isWall) continue;

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (Math.abs(dx + dy) != 1) continue;
                        int nx = x + dx, ny = y + dy;
                        if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                            MapNode neighbor = map.nodes[nx][ny];
                            if (!neighbor.isWall) node.neighbors.add(neighbor);
                        }
                    }
                }
            }
        }
    }
}
