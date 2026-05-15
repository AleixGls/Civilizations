// ==================== Crossbow.java ====================
package civilizations;

public class Crossbow extends AttackUnit {
    public Crossbow(int defenseTech, int attackTech) {
        super(calculateArmor(defenseTech), calculateDamage(attackTech));
    }
    public Crossbow() {
        super(ARMOR_CROSSBOW, BASE_DAMAGE_CROSSBOW);
    }
    private static int calculateArmor(int tech) {
        int bonus = tech * PLUS_ARMOR_CROSSBOW_BY_TECHNOLOGY;
        return ARMOR_CROSSBOW + (ARMOR_CROSSBOW * bonus / 100);
    }
    private static int calculateDamage(int tech) {
        int bonus = tech * PLUS_ATTACK_CROSSBOW_BY_TECHNOLOGY;
        return BASE_DAMAGE_CROSSBOW + (BASE_DAMAGE_CROSSBOW * bonus / 100);
    }
    @Override public int getFoodCost() { return FOOD_COST_CROSSBOW; }
    @Override public int getWoodCost() { return WOOD_COST_CROSSBOW; }
    @Override public int getIronCost() { return IRON_COST_CROSSBOW; }
    @Override public int getManaCost() { return MANA_COST_CROSSBOW; }
    @Override public int getChanceGeneratingWaste() { return CHANCE_GENERATING_WASTE_CROSSBOW; }
    @Override public int getChanceAttackAgain() { return CHANCE_ATTACK_AGAIN_CROSSBOW; }
}