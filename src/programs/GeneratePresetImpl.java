package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army computerArmy = new Army();
        List<Unit> selectedUnits = new ArrayList<>();
        int currentPoints = 0;

        unitList.sort((first, second) -> {
            double effectivenessFirst = ((double) first.getBaseAttack() / first.getCost()) +
                    ((double) first.getHealth() / first.getCost());
            double effectivenessSecond = ((double) second.getBaseAttack() / second.getCost()) +
                    ((double) second.getHealth() / second.getCost());
            return Double.compare(effectivenessSecond, effectivenessFirst);
        });

        Set<String> occupiedCoords = new HashSet<>();
        Random random = new Random();

        for (Unit unitType : unitList) {
            int unitsAdded = 0;

            while (unitsAdded < 11 && (currentPoints + unitType.getCost()) <= maxPoints) {

                int coordX;
                int coordY;
                String positionKey;
                do {
                    coordX = random.nextInt(3);
                    coordY = random.nextInt(21);
                    positionKey = coordX + "," + coordY;
                } while (occupiedCoords.contains(positionKey));

                occupiedCoords.add(positionKey);

                Unit newUnit = new Unit(
                        unitType.getName() + " " + unitsAdded,
                        unitType.getUnitType(),
                        unitType.getHealth(),
                        unitType.getBaseAttack(),
                        unitType.getCost(),
                        unitType.getAttackType(),
                        new HashMap<>(),
                        new HashMap<>(),
                        coordX,
                        coordY
                );

                newUnit.setProgram(unitType.getProgram());

                selectedUnits.add(newUnit);
                currentPoints += unitType.getCost();
                unitsAdded++;
            }
        }

        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(currentPoints);
        return computerArmy;
    }
}