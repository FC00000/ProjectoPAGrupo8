package io.github.projectopa2;
import com.badlogic.gdx.math.Vector2;

public class ChasePlayerStrategy implements MovementStrategy {                                  // Implementa a interface MovementStrategy
    @Override
    public MapNode getNextNode(Enemy enemy, GameMap map, MapNode playerNode) {                  // função para determinar a próxima melhor node para o caminho dos inimigos
        MapNode current = map.nodes[(int) enemy.gridPosition.x][(int) enemy.gridPosition.y];    // Vai buscar a node do inimigo no momento
        MapNode best = current;                                                                 // Node que vai ser devolvida no fim da função, começa no inicio (current)
        float bestDistance = Float.MAX_VALUE;                                                   // Vareavel para ser utilizada para a melhor distancia
        for (MapNode neighbor : current.neighbors) {                                            // Para cada node vizinha
            float dist = Vector2.dst2(neighbor.x, neighbor.y, playerNode.x, playerNode.y);      // Calcula a distancia entre as duas nodes A e B (Node vizinha para a node final(Player))
            if (dist < bestDistance) {                                                          // Se a distancia da node vizinha for menor que a melhor distancia
                bestDistance = dist;                                                            // Altera a melhor distancia para a distancia da vizinha até à final
                best = neighbor;                                                                // Altera a melhor node para a vizinha
            }
        }
        return best;                                                                            // Devolve a melhor node para o inimigo se deslocar
    }
}
