package io.github.projectopa2;

public interface MovementStrategy { // Interface para o Inimigo para o movimentar
    MapNode getNextNode(Enemy enemy, GameMap map, MapNode playerNode);
}