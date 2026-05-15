// ==================== Battle.java ====================
package civilizations;

import java.util.*;

public class Battle implements Variables {
    private ArrayList<MilitaryUnit> civilizationArmy;
    private ArrayList<MilitaryUnit> enemyArmy;
    private ArrayList<MilitaryUnit>[][] armies; // [2][9] pero usamos listas
    private StringBuilder battleDevelopment;
    private int[][] initialCostFleet; // [2][3] (comida, madera, hierro)
    private int initialNumberUnitsCivilization;
    private int initialNumberUnitsEnemy;
    private int[] wasteWoodIron; // [0] madera, [1] hierro
    private int enemyDrops;
    private int civilizationDrops;
    private int[][] resourcesLooses; // [2][4]
    private int[][] initialArmies; // [2][9]
    private int[] actualNumberUnitsCivilization;
    private int[] actualNumberUnitsEnemy;
    private Random random;

    @SuppressWarnings("unchecked")
    public Battle(ArrayList<MilitaryUnit>[] civArmy, ArrayList<MilitaryUnit> enemyArmyList) {
        this.civilizationArmy = new ArrayList<>();
        this.enemyArmy = new ArrayList<>();
        // Copiar ejércitos
        for (int i=0; i<9; i++) {
            for (MilitaryUnit u : civArmy[i]) {
                this.civilizationArmy.add(u);
            }
        }
        this.enemyArmy.addAll(enemyArmyList);
        this.random = new Random();
        this.battleDevelopment = new StringBuilder();
        this.initialCostFleet = new int[2][3];
        this.resourcesLooses = new int[2][4];
        this.initialArmies = new int[2][9];
        this.actualNumberUnitsCivilization = new int[9];
        this.actualNumberUnitsEnemy = new int[9];
        this.wasteWoodIron = new int[2];
        initInitialArmies();
        initialFleetNumber();
        calculateInitialCosts();
    }

    private void initInitialArmies() {
        for (MilitaryUnit u : civilizationArmy) {
            int type = getUnitType(u);
            if (type != -1) initialArmies[0][type]++;
        }
        for (MilitaryUnit u : enemyArmy) {
            int type = getUnitType(u);
            if (type != -1 && type <= 3) initialArmies[1][type]++; // enemigo solo ataque
        }
        System.arraycopy(initialArmies[0], 0, actualNumberUnitsCivilization, 0, 9);
        System.arraycopy(initialArmies[1], 0, actualNumberUnitsEnemy, 0, 9);
    }

    private int getUnitType(MilitaryUnit u) {
        if (u instanceof Swordsman) return 0;
        if (u instanceof Spearman) return 1;
        if (u instanceof Crossbow) return 2;
        if (u instanceof Cannon) return 3;
        if (u instanceof ArrowTower) return 4;
        if (u instanceof Catapult) return 5;
        if (u instanceof RocketLauncherTower) return 6;
        if (u instanceof Magician) return 7;
        if (u instanceof Priest) return 8;
        return -1;
    }

    private void initialFleetNumber() {
        initialNumberUnitsCivilization = civilizationArmy.size();
        initialNumberUnitsEnemy = enemyArmy.size();
    }

    private void calculateInitialCosts() {
        initialCostFleet[0] = fleetResourceCost(civilizationArmy);
        initialCostFleet[1] = fleetResourceCost(enemyArmy);
    }

    private int[] fleetResourceCost(ArrayList<MilitaryUnit> army) {
        int food=0, wood=0, iron=0;
        for (MilitaryUnit u : army) {
            food += u.getFoodCost();
            wood += u.getWoodCost();
            iron += u.getIronCost();
        }
        return new int[]{food, wood, iron};
    }

    private int remainderPercentageFleet(ArrayList<MilitaryUnit> army, int initialTotal) {
        return (army.size() * 100) / initialTotal;
    }

    private int getGroupDefender(ArrayList<MilitaryUnit> army, boolean isCivilization) {
        int maxType = isCivilization ? 9 : 4;
        int[] counts = new int[maxType];
        for (MilitaryUnit u : army) {
            int t = getUnitType(u);
            if (t >=0 && t < maxType) counts[t]++;
        }
        int total = 0;
        for (int c : counts) total += c;
        if (total == 0) return -1;
        int rand = random.nextInt(total) + 1;
        int acum = 0;
        for (int i=0; i<counts.length; i++) {
            acum += counts[i];
            if (acum >= rand) return i;
        }
        return 0;
    }

    private int getGroupAttacker(boolean isCivilization) {
        int[] probs = isCivilization ? CHANCE_ATTACK_CIVILIZATION_UNITS : CHANCE_ATTACK_ENEMY_UNITS;
        int total = 0;
        for (int p : probs) total += p;
        int rand = random.nextInt(total) + 1;
        int acum = 0;
        for (int i=0; i<probs.length; i++) {
            acum += probs[i];
            if (acum >= rand) return i;
        }
        return 0;
    }

    private MilitaryUnit selectRandomUnitFromGroup(ArrayList<MilitaryUnit> army, int groupType) {
        List<MilitaryUnit> candidates = new ArrayList<>();
        for (MilitaryUnit u : army) {
            if (getUnitType(u) == groupType) candidates.add(u);
        }
        if (candidates.isEmpty()) return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

    private void generateWaste(MilitaryUnit unit) {
        int chance = unit.getChanceGeneratingWaste();
        if (random.nextInt(100) < chance) {
            int woodWaste = unit.getWoodCost() * PERCENTAGE_WASTE / 100;
            int ironWaste = unit.getIronCost() * PERCENTAGE_WASTE / 100;
            wasteWoodIron[0] += woodWaste;
            wasteWoodIron[1] += ironWaste;
        }
    }

    private void updateResourcesLooses() {
        int[] currentCivCost = fleetResourceCost(civilizationArmy);
        int[] currentEnemyCost = fleetResourceCost(enemyArmy);
        resourcesLooses[0][0] = initialCostFleet[0][0] - currentCivCost[0];
        resourcesLooses[0][1] = initialCostFleet[0][1] - currentCivCost[1];
        resourcesLooses[0][2] = initialCostFleet[0][2] - currentCivCost[2];
        resourcesLooses[0][3] = resourcesLooses[0][2] + resourcesLooses[0][1]/5 + resourcesLooses[0][0]/10;
        resourcesLooses[1][0] = initialCostFleet[1][0] - currentEnemyCost[0];
        resourcesLooses[1][1] = initialCostFleet[1][1] - currentEnemyCost[1];
        resourcesLooses[1][2] = initialCostFleet[1][2] - currentEnemyCost[2];
        resourcesLooses[1][3] = resourcesLooses[1][2] + resourcesLooses[1][1]/5 + resourcesLooses[1][0]/10;
    }

    private void resetArmyArmor() {
        for (MilitaryUnit u : civilizationArmy) u.resetArmor();
        for (MilitaryUnit u : enemyArmy) u.resetArmor();
    }

    public void startBattle() {
        boolean civilizationTurn = random.nextBoolean();
        while (remainderPercentageFleet(civilizationArmy, initialNumberUnitsCivilization) > BATTLE_STOP_PERCENTAGE &&
               remainderPercentageFleet(enemyArmy, initialNumberUnitsEnemy) > BATTLE_STOP_PERCENTAGE) {
            if (civilizationTurn) {
                battleDevelopment.append("*****************CHANGE ATTACKER********************\nAttacks Civilization:\n");
                performAttack(civilizationArmy, enemyArmy, true);
            } else {
                battleDevelopment.append("*****************CHANGE ATTACKER********************\nAttacks army enemy:\n");
                performAttack(enemyArmy, civilizationArmy, false);
            }
            civilizationTurn = !civilizationTurn;
        }
        updateResourcesLooses();
        resetArmyArmor();
    }

    private void performAttack(ArrayList<MilitaryUnit> attackers, ArrayList<MilitaryUnit> defenders, boolean isCivilizationAttacker) {
        int attackerGroup = getGroupAttacker(isCivilizationAttacker);
        MilitaryUnit attacker = selectRandomUnitFromGroup(attackers, attackerGroup);
        if (attacker == null) return;
        boolean again = true;
        while (again) {
            int defenderGroup = getGroupDefender(defenders, !isCivilizationAttacker);
            MilitaryUnit defender = selectRandomUnitFromGroup(defenders, defenderGroup);
            if (defender == null) break;
            int damage = attacker.attack();
            battleDevelopment.append(getUnitName(attacker) + " attacks " + getUnitName(defender) + "\n");
            battleDevelopment.append(getUnitName(attacker) + " generates damage = " + damage + "\n");
            defender.takeDamage(damage);
            int remainingArmor = defender.getActualArmor();
            battleDevelopment.append(getUnitName(defender) + " stays with armor = " + remainingArmor + "\n");
            if (remainingArmor <= 0) {
                battleDevelopment.append("we eliminate " + getUnitName(defender) + "\n");
                defenders.remove(defender);
                generateWaste(defender);
            }
            int chanceAgain = attacker.getChanceAttackAgain();
            again = random.nextInt(100) < chanceAgain;
            if (again) battleDevelopment.append(getUnitName(attacker) + " attacks again!\n");
        }
    }

    private String getUnitName(MilitaryUnit u) {
        if (u instanceof Swordsman) return "Swordsman";
        if (u instanceof Spearman) return "Spearman";
        if (u instanceof Crossbow) return "Crossbow";
        if (u instanceof Cannon) return "Cannon";
        if (u instanceof ArrowTower) return "Arrow Tower";
        if (u instanceof Catapult) return "Catapult";
        if (u instanceof RocketLauncherTower) return "Rocket Launcher Tower";
        if (u instanceof Magician) return "Magician";
        if (u instanceof Priest) return "Priest";
        return "Unknown";
    }

    public String getBattleReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("BATTLE STATISTICS\n");
        sb.append("Army planet\nUnits\tDrops\n");
        for (int i=0; i<9; i++) {
            if (initialArmies[0][i] > 0) {
                sb.append(getUnitNameByIndex(i)).append("\t").append(initialArmies[0][i]).append("\t").append(initialArmies[0][i] - actualNumberUnitsCivilization[i]).append("\n");
            }
        }
        sb.append("Initial Army Enemy\nUnits\tDrops\n");
        for (int i=0; i<4; i++) {
            if (initialArmies[1][i] > 0) {
                sb.append(getUnitNameByIndex(i)).append("\t").append(initialArmies[1][i]).append("\t").append(initialArmies[1][i] - actualNumberUnitsEnemy[i]).append("\n");
            }
        }
        sb.append("Cost Army Civilization: Food=").append(initialCostFleet[0][0]).append(" Wood=").append(initialCostFleet[0][1]).append(" Iron=").append(initialCostFleet[0][2]).append("\n");
        sb.append("Cost Army Enemy: Food=").append(initialCostFleet[1][0]).append(" Wood=").append(initialCostFleet[1][1]).append(" Iron=").append(initialCostFleet[1][2]).append("\n");
        sb.append("Losses Army Civilization: Food=").append(resourcesLooses[0][0]).append(" Wood=").append(resourcesLooses[0][1]).append(" Iron=").append(resourcesLooses[0][2]).append("\n");
        sb.append("Losses Army Enemy: Food=").append(resourcesLooses[1][0]).append(" Wood=").append(resourcesLooses[1][1]).append(" Iron=").append(resourcesLooses[1][2]).append("\n");
        sb.append("Waste Generated: Wood ").append(wasteWoodIron[0]).append(" Iron ").append(wasteWoodIron[1]).append("\n");
        if (resourcesLooses[0][3] < resourcesLooses[1][3]) {
            sb.append("Battle Won by Civilization, We Collect Rubble\n");
        } else {
            sb.append("Battle Lost by Civilization\n");
        }
        return sb.toString();
    }

    public String getBattleDevelopment() {
        return battleDevelopment.toString();
    }

    private String getUnitNameByIndex(int i) {
        String[] names = {"Swordsman","Spearman","Crossbow","Cannon","Arrow Tower","Catapult","Rocket Launcher","Magician","Priest"};
        return names[i];
    }

    public int[] getWaste() { return wasteWoodIron; }
    public boolean isCivilizationWinner() { return resourcesLooses[0][3] < resourcesLooses[1][3]; }
}