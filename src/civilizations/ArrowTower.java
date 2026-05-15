// ==================== ArrowTower.java ====================
package civilizations;

public class ArrowTower extends DefenseUnit {
    public ArrowTower(int defenseTech, int attackTech) {
        super(calculateArmor(defenseTech), calculateDamage(attackTech));
    }
    public ArrowTower() {
        super(ARMOR_ARROWTOWER, BASE_DAMAGE_ARROWTOWER);
    }
    private static int calculateArmor(int tech) {
        int bonus = tech * PLUS_ARMOR_ARROWTOWER_BY_TECHNOLOGY;
        return ARMOR_ARROWTOWER + (ARMOR_ARROWTOWER * bonus / 100);
    }
    private static int calculateDamage(int tech) {
        int bonus = tech * PLUS_ATTACK_ARROWTOWER_BY_TECHNOLOGY;
        return BASE_DAMAGE_ARROWTOWER + (BASE_DAMAGE_ARROWTOWER * bonus / 100);
    }
    @Override public int getFoodCost() { return FOOD_COST_ARROWTOWER; }
    @Override public int getWoodCost() { return WOOD_COST_ARROWTOWER; }
    @Override public int getIronCost() { return IRON_COST_ARROWTOWER; }
    @Override public int getManaCost() { return MANA_COST_ARROWTOWER; }
    @Override public int getChanceGeneratingWaste() { return CHANCE_GENERATING_WASTE_ARROWTOWER; }
    @Override public int getChanceAttackAgain() { return CHANCE_ATTACK_AGAIN_ARROWTOWER; }
}