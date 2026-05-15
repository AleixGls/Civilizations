// ==================== Magician.java ====================
package civilizations;

public class Magician extends SpecialUnit {
    private int attackTech;

    public Magician(int defenseTech, int attackTech) {
        super(0, calculateDamage(attackTech));
        this.attackTech = attackTech;
    }
    public Magician() {
        super(0, BASE_DAMAGE_MAGICIAN);
        this.attackTech = 0;
    }
    private static int calculateDamage(int tech) {
        int bonus = tech * PLUS_ATTACK_MAGICIAN_BY_TECHNOLOGY;
        return BASE_DAMAGE_MAGICIAN + (BASE_DAMAGE_MAGICIAN * bonus / 100);
    }
    @Override
    public int attack() {
        int bonusExp = (getExperience() * PLUS_ATTACK_UNIT_PER_EXPERIENCE_POINT) / 100;
        int bonusTech = attackTech * PLUS_ATTACK_MAGICIAN_BY_TECHNOLOGY;
        return baseDamage * (100 + bonusExp + bonusTech) / 100;
    }
    @Override public int getFoodCost() { return FOOD_COST_MAGICIAN; }
    @Override public int getWoodCost() { return WOOD_COST_MAGICIAN; }
    @Override public int getIronCost() { return IRON_COST_MAGICIAN; }
    @Override public int getManaCost() { return MANA_COST_MAGICIAN; }
    @Override public int getChanceGeneratingWaste() { return CHANCE_GENERATING_WASTE_MAGICIAN; }
    @Override public int getChanceAttackAgain() { return CHANCE_ATTACK_AGAIN_MAGICIAN; }
}