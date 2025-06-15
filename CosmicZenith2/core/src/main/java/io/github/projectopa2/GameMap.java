package io.github.projectopa2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;


public class GameMap {

    private static GameMap instance;  //  Instance Singleton

    public MapNode[][] nodes; // Grid 2D com as nodes
    public Array<MapNode> allNodes = new Array<>(); // Lista das nodes do mapa
    public MapNode spawnNode;       // Posição do spawn de cada mapa
    public MapNode portalNode;      // Posição do portal de cada mapa
    public MapNode treasureNode;    // Posição do tesouro de cada mapa (Apenas para o mapa final)

    public Array<Enemy> enemies;

    public static GameMap generateFirstMap(Texture enemyTexture, int width, int height) { // Primeiro mapa
        GameMap map = new GameMap();
        map.nodes = new MapNode[width][height];

        // Paredes do mapa
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                boolean isWall = (x == 8 && y >= 1 && y <= 7 && y != 6 && y != 4 && y != 2) ||
                    (x == 11 && y >= 1 && y <= 7 && y != 7 && y != 5 && y != 3 && y != 1) ||
                    (x == 5 && y >= 1 && y <= 7 && y != 6 && y != 4 && y != 2) ||
                    (x == 2 && y >= 1 && y <= 7 && y != 7 && y != 5 && y != 3 && y != 1);

                MapNode node = new MapNode(x, y, isWall); // Adiciona as cordenadas e o estado da parede a uma node
                map.nodes[x][y] = node;      // Adiciona a node ao Grid 2D das nodes
                map.allNodes.add(node);      // Adiciona a node á lista das nodes
            }
        }

        connectNeighbors(map, width, height); // Junta as todas as nodes que não têm paredes

        map.spawnNode = map.nodes[0][4];    // Onde o jogador começa neste mapa
        map.portalNode = map.nodes[15][4];  // Onde está o portal neste mapa
        map.treasureNode = null;            // Onde está o tesouro neste mapa (Não existe)

        map.enemies = new Array<>();        // Lista dos inimigos
        map.enemies.add(EnemyFactory.createEnemy("FastChase",enemyTexture, 15, 8)); //Cria um novo inimigo e coloca-o na lista

        return map;
    }

    public static GameMap generateSecondMap(Texture enemyTexture, int width, int height) {
        GameMap map = new GameMap();
        map.nodes = new MapNode[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean isWall = (x == 5 || x == 6) && y == 5;

                if ((x == 5 || x == 10) && y >= 2 && y <= 7) isWall = true;
                if (y == 5 && x >= 6 && x <= 9 && x != 7 && x != 8) isWall = true;


                MapNode node = new MapNode(x, y, isWall);
                map.nodes[x][y] = node;
                map.allNodes.add(node);
            }
        }

        connectNeighbors(map, width, height);

        map.spawnNode = map.nodes[1][1];
        map.portalNode = map.nodes[13][4];
        map.treasureNode = null;

        map.enemies = new Array<>();
        map.enemies.add(EnemyFactory.createEnemy("NormalWander" , enemyTexture, 14, 1));
        map.enemies.add(EnemyFactory.createEnemy("NormalWander" ,  enemyTexture, 8, 8));
        return map;
    }

    public static GameMap generateThirdMap(Texture enemyTexture, int width, int height) {
        GameMap map = new GameMap();
        map.nodes = new MapNode[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
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
        map.enemies.add(EnemyFactory.createEnemy("SlowChase",enemyTexture, 5, 5));
        map.enemies.add(EnemyFactory.createEnemy("SlowChase",enemyTexture, 10, 2));
        map.enemies.add(EnemyFactory.createEnemy("FastChase",enemyTexture, 14, 8));

        return map;
    }

    //Função para conectar todas as nodes do mapa entre si e evitar as paredes
    private static void connectNeighbors(GameMap map, int width, int height) {
        for (int x = 0; x < width; x++) {                                           // Para todos os X do mapa
            for (int y = 0; y < height; y++) {                                      // Para todos os Y do mapa
                MapNode node = map.nodes[x][y];                                     // Acede à node nas coordenadas
                if (node.isWall) continue;                                          // Se for uma parede, sair do loop (Não fazer nada)
                for (int dx = -1; dx <= 1; dx++) {                                  // Para todas as nodes à volta das coordenadas atuais (3x3)
                    for (int dy = -1; dy <= 1; dy++) {
                        if (Math.abs(dx + dy) != 1) continue;                       // Se o absoluto de dx+dy da node for diferente de 1 , sair do loop, isto considera a propria node e todas as nodes diagonais (Se quisermos adicionar movimento diagonal esta linha pode ser alterada)
                        int nx = x + dx, ny = y + dy;                               // Calcula as coordenadas do novo Neighbor/Vizinho
                        if (nx >= 0 && ny >= 0 && nx < width && ny < height) {      // Avançar apenas se o a node estiver dentro do mapa
                            MapNode neighbor = map.nodes[nx][ny];                   // Acede à node nas coordenadas
                            if (!neighbor.isWall) node.neighbors.add(neighbor);     // Se a node não for parede adiciona-a como Neighbor/Vizinha
                        }
                    }
                }
            }
        }
    }
}