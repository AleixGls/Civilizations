// ==================== Swordsman.java ====================
package civilizations;

public class Swordsman extends AttackUnit {
    public Swordsman(int defenseTech, int attackTech) {
        super(calculateArmor(defenseTech), calculateDamage(attackTech));
    }

    public Swordsman() {
        super(ARMOR_SWORDSMAN, BASE_DAMAGE_SWORDSMAN);
    }

    private static int calculateArmor(int tech) {
        int bonus = tech * PLUS_ARMOR_SWORDSMAN_BY_TECHNOLOGY;
        return ARMOR_SWORDSMAN + (ARMOR_SWORDSMAN * bonus / 100);
    }

    private static int calculateDamage(int tech) {
        int bonus = tech * PLUS_ATTACK_SWORDSMAN_BY_TECHNOLOGY;
        return BASE_DAMAGE_SWORDSMAN + (BASE_DAMAGE_SWORDSMAN * bonus / 100);
    }

    public int getFoodCost() { return FOOD_COST_SWORDSMAN; }
    public int getWoodCost() { return WOOD_COST_SWORDSMAN; }
    public int getIronCost() { return IRON_COST_SWORDSMAN; }
    public int getManaCost() { return MANA_COST_SWORDSMAN; }
    public int getChanceGeneratingWaste() { return CHANCE_GENERATING_WASTE_SHORDSMAN; }
    public int getChanceAttackAgain() { return CHANCE_ATTACK_AGAIN_SHORDSMAN; }
}