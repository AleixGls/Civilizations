// ==================== Civilization.java ====================
package civilizations;

import java.util.ArrayList;

public class Civilization implements Variables {
    private int technologyDefense;
    private int technologyAttack;
    private int wood;
    private int iron;
    private int food;
    private int mana;
    private int magicTower;
    private int church;
    private int farm;
    private int smithy;
    private int carpentry;
    private int battles;
    private ArrayList<MilitaryUnit>[] army; // Array de 9 ArrayLists

    @SuppressWarnings("unchecked")
    public Civilization() {
        this.technologyDefense = 0;
        this.technologyAttack = 0;
        this.wood = 0;
        this.iron = 0;
        this.food = 0;
        this.mana = 0;
        this.magicTower = 0;
        this.church = 0;
        this.farm = 0;
        this.smithy = 0;
        this.carpentry = 0;
        this.battles = 0;
        this.army = new ArrayList[9];
        for (int i = 0; i < 9; i++) {
            army[i] = new ArrayList<>();
        }
    }

    // Getters y setters básicos (omitidos por brevedad, se pueden generar)
    public int getTechnologyDefense() { return technologyDefense; }
    public int getTechnologyAttack() { return technologyAttack; }
    public int getWood() { return wood; }
    public int getIron() { return iron; }
    public int getFood() { return food; }
    public int getMana() { return mana; }
    public int getMagicTower() { return magicTower; }
    public int getChurch() { return church; }
    public int getFarm() { return farm; }
    public int getSmithy() { return smithy; }
    public int getCarpentry() { return carpentry; }
    public int getBattles() { return battles; }
    public ArrayList<MilitaryUnit>[] getArmy() { return army; }

    public void setWood(int wood) { this.wood = wood; }
    public void setIron(int iron) { this.iron = iron; }
    public void setFood(int food) { this.food = food; }
    public void setMana(int mana) { this.mana = mana; }
    public void setBattles(int battles) { this.battles = battles; }

    // Métodos para añadir edificios
    public void newFarm() throws ResourceException {
        if (food >= FOOD_COST_FARM && wood >= WOOD_COST_FARM && iron >= IRON_COST_FARM) {
            food -= FOOD_COST_FARM;
            wood -= WOOD_COST_FARM;
            iron -= IRON_COST_FARM;
            farm++;
        } else {
            throw new ResourceException("Recursos insuficientes para construir Granja");
        }
    }

    public void newCarpentry() throws ResourceException {
        if (food >= FOOD_COST_CARPENTRY && wood >= WOOD_COST_CARPENTRY && iron >= IRON_COST_CARPENTRY) {
            food -= FOOD_COST_CARPENTRY;
            wood -= WOOD_COST_CARPENTRY;
            iron -= IRON_COST_CARPENTRY;
            carpentry++;
        } else {
            throw new ResourceException("Recursos insuficientes para construir Carpintería");
        }
    }

    public void newSmithy() throws ResourceException {
        if (food >= FOOD_COST_SMITHY && wood >= WOOD_COST_SMITHY && iron >= IRON_COST_SMITHY) {
            food -= FOOD_COST_SMITHY;
            wood -= WOOD_COST_SMITHY;
            iron -= IRON_COST_SMITHY;
            smithy++;
        } else {
            throw new ResourceException("Recursos insuficientes para construir Herreria");
        }
    }

    public void newMagicTower() throws ResourceException {
        if (food >= FOOD_COST_MAGICTOWER && wood >= WOOD_COST_MAGICTOWER && iron >= IRON_COST_MAGICTOWER) {
            food -= FOOD_COST_MAGICTOWER;
            wood -= WOOD_COST_MAGICTOWER;
            iron -= IRON_COST_MAGICTOWER;
            magicTower++;
        } else {
            throw new ResourceException("Recursos insuficientes para construir Torre Mágica");
        }
    }

    public void newChurch() throws ResourceException {
        if (food >= FOOD_COST_CHURCH && wood >= WOOD_COST_CHURCH && iron >= IRON_COST_CHURCH) {
            food -= FOOD_COST_CHURCH;
            wood -= WOOD_COST_CHURCH;
            iron -= IRON_COST_CHURCH;
            church++;
        } else {
            throw new ResourceException("Recursos insuficientes para construir Iglesia");
        }
    }

    // Mejoras de tecnología
    public void upgradeTechnologyDefense() throws ResourceException {
        int costIron = UPGRADE_BASE_DEFENSE_TECHNOLOGY_IRON_COST + (technologyDefense * UPGRADE_PLUS_DEFENSE_TECHNOLOGY_IRON_COST);
        int costWood = UPGRADE_BASE_DEFENSE_TECHNOLOGY_WOOD_COST + (technologyDefense * UPGRADE_PLUS_DEFENSE_TECHNOLOGY_WOOD_COST);
        if (iron >= costIron && wood >= costWood) {
            iron -= costIron;
            wood -= costWood;
            technologyDefense++;
        } else {
            throw new ResourceException("Recursos insuficientes para mejorar tecnología de defensa");
        }
    }

    public void upgradeTechnologyAttack() throws ResourceException {
        int costIron = UPGRADE_BASE_ATTACK_TECHNOLOGY_IRON_COST + (technologyAttack * UPGRADE_PLUS_ATTACK_TECHNOLOGY_IRON_COST);
        int costWood = UPGRADE_BASE_ATTACK_TECHNOLOGY_WOOD_COST + (technologyAttack * UPGRADE_PLUS_ATTACK_TECHNOLOGY_WOOD_COST);
        if (iron >= costIron && wood >= costWood) {
            iron -= costIron;
            wood -= costWood;
            technologyAttack++;
        } else {
            throw new ResourceException("Recursos insuficientes para mejorar tecnología de ataque");
        }
    }

    // Métodos genéricos para añadir unidades
    private void addUnits(int type, int n, int foodCost, int woodCost, int ironCost, int manaCost,
                          java.util.function.BiFunction<Integer, Integer, MilitaryUnit> creator) throws ResourceException, BuildingException {
        if (type == 7 && magicTower == 0) { // Magician
            throw new BuildingException("Se necesita al menos una Torre Mágica para crear magos");
        }
        if (type == 8 && church == 0) { // Priest
            throw new BuildingException("Se necesita al menos una Iglesia para crear sacerdotes");
        }

        int possible = n;
        int availableFood = food / foodCost;
        int availableWood = woodCost > 0 ? wood / woodCost : Integer.MAX_VALUE;
        int availableIron = ironCost > 0 ? iron / ironCost : Integer.MAX_VALUE;
        int availableMana = manaCost > 0 ? mana / manaCost : Integer.MAX_VALUE;
        possible = Math.min(possible, availableFood);
        possible = Math.min(possible, availableWood);
        possible = Math.min(possible, availableIron);
        possible = Math.min(possible, availableMana);
        if (possible < n) {
            throw new ResourceException("Recursos insuficientes. Solo se pudieron añadir " + possible + " de " + n);
        }
        // Restar recursos
        food -= foodCost * possible;
        wood -= woodCost * possible;
        iron -= ironCost * possible;
        mana -= manaCost * possible;
        for (int i = 0; i < possible; i++) {
            army[type].add(creator.apply(technologyDefense, technologyAttack));
        }
    }

    public void newSwordsman(int n) throws ResourceException, BuildingException {
        addUnits(0, n, FOOD_COST_SWORDSMAN, WOOD_COST_SWORDSMAN, IRON_COST_SWORDSMAN, MANA_COST_SWORDSMAN,
                (def, atk) -> new Swordsman(def, atk));
    }
    public void newSpearman(int n) throws ResourceException, BuildingException {
        addUnits(1, n, FOOD_COST_SPEARMAN, WOOD_COST_SPEARMAN, IRON_COST_SPEARMAN, MANA_COST_SPEARMAN,
                (def, atk) -> new Spearman(def, atk));
    }
    public void newCrossbow(int n) throws ResourceException, BuildingException {
        addUnits(2, n, FOOD_COST_CROSSBOW, WOOD_COST_CROSSBOW, IRON_COST_CROSSBOW, MANA_COST_CROSSBOW,
                (def, atk) -> new Crossbow(def, atk));
    }
    public void newCannon(int n) throws ResourceException, BuildingException {
        addUnits(3, n, FOOD_COST_CANNON, WOOD_COST_CANNON, IRON_COST_CANNON, MANA_COST_CANNON,
                (def, atk) -> new Cannon(def, atk));
    }
    public void newArrowTower(int n) throws ResourceException, BuildingException {
        addUnits(4, n, FOOD_COST_ARROWTOWER, WOOD_COST_ARROWTOWER, IRON_COST_ARROWTOWER, MANA_COST_ARROWTOWER,
                (def, atk) -> new ArrowTower(def, atk));
    }
    public void newCatapult(int n) throws ResourceException, BuildingException {
        addUnits(5, n, FOOD_COST_CATAPULT, WOOD_COST_CATAPULT, IRON_COST_CATAPULT, MANA_COST_CATAPULT,
                (def, atk) -> new Catapult(def, atk));
    }
    public void newRocketLauncher(int n) throws ResourceException, BuildingException {
        addUnits(6, n, FOOD_COST_ROCKETLAUNCHERTOWER, WOOD_COST_ROCKETLAUNCHERTOWER, IRON_COST_ROCKETLAUNCHERTOWER, MANA_COST_ROCKETLAUNCHERTOWER,
                (def, atk) -> new RocketLauncherTower(def, atk));
    }
    public void newMagician(int n) throws ResourceException, BuildingException {
        addUnits(7, n, FOOD_COST_MAGICIAN, WOOD_COST_MAGICIAN, IRON_COST_MAGICIAN, MANA_COST_MAGICIAN,
                (def, atk) -> new Magician(def, atk));
    }
    public void newPriest(int n) throws ResourceException, BuildingException {
        addUnits(8, n, FOOD_COST_PRIEST, WOOD_COST_PRIEST, IRON_COST_PRIEST, MANA_COST_PRIEST,
                (def, atk) -> new Priest(def, atk));
    }

    public void printStats() {
        System.out.println("=== ESTADO DE LA CIVILIZACIÓN ===");
        System.out.println("Tecnología defensa: " + technologyDefense);
        System.out.println("Tecnología ataque: " + technologyAttack);
        System.out.println("Recursos: Comida=" + food + " Madera=" + wood + " Hierro=" + iron + " Maná=" + mana);
        System.out.println("Edificios: Granjas=" + farm + " Carpinterías=" + carpentry + " Herreries=" + smithy +
                           " Torres Mágicas=" + magicTower + " Iglesias=" + church);
        System.out.println("Ejército:");
        String[] names = {"Espadachines","Lanceros","Ballestas","Cañones","Torres flecha","Catapultas","Lanzacohetes","Magos","Sacerdotes"};
        for (int i=0; i<army.length; i++) {
            System.out.println("  " + names[i] + ": " + army[i].size());
        }
        System.out.println("Batallas libradas: " + battles);
    }
}