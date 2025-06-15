package io.github.projectopa2;
import java.util.Random;

public class WanderStrategy implements MovementStrategy {                                       // Implementa a interface MovementStrategy
    private Random random = new Random();                                                       // Gerador de numeros random para escolher uma node à volta do inimigo

    @Override
    public MapNode getNextNode(Enemy enemy, GameMap map, MapNode playerNode) {                  // Função do movimento
        MapNode current = map.nodes[(int) enemy.gridPosition.x][(int) enemy.gridPosition.y];    // Acede à node relativa às coordenadas do inimigo
        return current.neighbors.get(random.nextInt(current.neighbors.size));                   // Devolve uma node random tendo em conta o total de Vizinhos desta Node
    }
}
