package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        Set<String> occupiedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                occupiedCells.add(unit.getxCoordinate() + ":" + unit.getyCoordinate());
            }
        }

        Queue<Edge> queue = new LinkedList<>();
        Edge start = new Edge(attackUnit.getxCoordinate(), attackUnit.getyCoordinate());
        queue.add(start);

        Map<Edge, Edge> parentMap = new HashMap<>();
        parentMap.put(start, null);

        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        visited[start.getX()][start.getY()] = true;

        Edge foundEnd = null;

        while (!queue.isEmpty()) {
            Edge current = queue.poll();

            if (current.getX() == targetUnit.getxCoordinate() &&
                    current.getY() == targetUnit.getyCoordinate()) {
                foundEnd = current;
                break;
            }

            int[][] directions = {
                    {0, 1}, {0, -1}, {1, 0}, {-1, 0},
                    {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
            };

            for (int[] direction : directions) {
                int nextX = current.getX() + direction[0];
                int nextY = current.getY() + direction[1];

                if (isWithinBounds(nextX, nextY) && !visited[nextX][nextY]) {
                    if (!occupiedCells.contains(nextX + ":" + nextY)) {
                        Edge nextPosition = new Edge(nextX, nextY);
                        visited[nextX][nextY] = true;
                        parentMap.put(nextPosition, current);
                        queue.add(nextPosition);
                    }
                }
            }
        }

        List<Edge> path = new ArrayList<>();
        if (foundEnd != null) {
            Edge currentEdge = foundEnd;
            while (currentEdge != null) {
                path.add(currentEdge);
                currentEdge = parentMap.get(currentEdge);
            }
            Collections.reverse(path);
            if (path.size() > 0 &&
                    path.get(0).getX() == attackUnit.getxCoordinate() &&
                    path.get(0).getY() == attackUnit.getyCoordinate()) {
                path.remove(0);
            }
        }

        return path;
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }
}