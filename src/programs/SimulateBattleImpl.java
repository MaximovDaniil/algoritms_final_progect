package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog; // Позволяет логировать. Использовать после каждой атаки юнита

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        List<Unit> playerUnits = playerArmy.getUnits();
        List<Unit> computerUnits = computerArmy.getUnits();

        while (hasAliveUnits(playerUnits) && hasAliveUnits(computerUnits)) {

            List<Unit> allUnits = new ArrayList<>();
            allUnits.addAll(playerUnits);
            allUnits.addAll(computerUnits);

            Collections.sort(allUnits, new Comparator<Unit>() {
                @Override
                public int compare(Unit first, Unit second) {
                    return Integer.compare(second.getBaseAttack(), first.getBaseAttack());
                }
            });

            for (Unit unit : allUnits) {
                if (!unit.isAlive()) {
                    continue;
                }

                Unit target = unit.getProgram().attack();

                if (printBattleLog != null) {
                    printBattleLog.printBattleLog(unit, target);
                }

                if (!hasAliveUnits(playerUnits) || !hasAliveUnits(computerUnits)) {
                    return;
                }
            }
        }
    }

    private boolean hasAliveUnits(List<Unit> units) {
        for (Unit unit : units) {
            if (unit.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public static class GeneratePresetImpl implements GeneratePreset {

        @Override
        public Army generate(List<Unit> unitList, int maxPoints) {
            Army computerArmy = new Army();
            List<Unit> selectedUnits = new ArrayList<>();
            int currentPoints = 0;

            Collections.sort(unitList, (first, second) -> {
                double effectivenessFirst = ((double) first.getBaseAttack() / first.getCost()) +
                        ((double) first.getHealth() / first.getCost());
                double effectivenessSecond = ((double) second.getBaseAttack() / second.getCost()) +
                        ((double) second.getHealth() / second.getCost());
                return Double.compare(effectivenessSecond, effectivenessFirst);
            });

            for (Unit unitType : unitList) {
                int unitsAdded = 0;
                while (unitsAdded < 11 && currentPoints + unitType.getCost() <= maxPoints) {
                    Unit newUnit = new Unit(
                            unitType.getName() + "_" + unitsAdded,
                            unitType.getUnitType(),
                            unitType.getHealth(),
                            unitType.getBaseAttack(),
                            unitType.getCost(),
                            unitType.getAttackType(),
                            new HashMap<>(),
                            new HashMap<>(),
                            -1,
                            -1
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
}