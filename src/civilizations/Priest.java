// ==================== Priest.java ====================
package civilizations;

public class Priest extends SpecialUnit {
    public Priest(int defenseTech, int attackTech) {
        super(0, 0); // Los sacerdotes no atacan
    }
    public Priest() {
        super(0, 0);
    }
    @Override public int attack() { return 0; }
    @Override public int getFoodCost() { return FOOD_COST_PRIEST; }
    @Override public int getWoodCost() { return WOOD_COST_PRIEST; }
    @Override public int getIronCost() { return IRON_COST_PRIEST; }
    @Override public int getManaCost() { return MANA_COST_PRIEST; }
    @Override public int getChanceGeneratingWaste() { return CHANCE_GENERATING_WASTE_PRIEST; }
    @Override public int getChanceAttackAgain() { return CHANCE_ATTACK_AGAIN_PRIEST; }
}