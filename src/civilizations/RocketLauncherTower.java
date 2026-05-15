// ==================== RocketLauncherTower.java ====================
package civilizations;

public class RocketLauncherTower extends DefenseUnit {
    public RocketLauncherTower(int defenseTech, int attackTech) {
        super(calculateArmor(defenseTech), calculateDamage(attackTech));
    }
    public RocketLauncherTower() {
        super(ARMOR_ROCKETLAUNCHERTOWER, BASE_DAMAGE_ROCKETLAUNCHERTOWER);
    }
    private static int calculateArmor(int tech) {
        int bonus = tech * PLUS_ARMOR_ROCKETLAUNCHERTOWER_BY_TECHNOLOGY;
        return ARMOR_ROCKETLAUNCHERTOWER + (ARMOR_ROCKETLAUNCHERTOWER * bonus / 100);
    }
    private static int calculateDamage(int tech) {
        int bonus = tech * PLUS_ATTACK_ROCKETLAUNCHERTOWER_BY_TECHNOLOGY;
        return BASE_DAMAGE_ROCKETLAUNCHERTOWER + (BASE_DAMAGE_ROCKETLAUNCHERTOWER * bonus / 100);
    }
    @Override public int getFoodCost() { return FOOD_COST_ROCKETLAUNCHERTOWER; }
    @Override public int getWoodCost() { return WOOD_COST_ROCKETLAUNCHERTOWER; }
    @Override public int getIronCost() { return IRON_COST_ROCKETLAUNCHERTOWER; }
    @Override public int getManaCost() { return MANA_COST_ROCKETLAUNCHERTOWER; }
    @Override public int getChanceGeneratingWaste() { return CHANCE_GENERATING_WASTE_ROCKETLAUNCHERTOWER; }
    @Override public int getChanceAttackAgain() { return CHANCE_ATTACK_AGAIN_ROCKETLAUNCHERTOWER; }
}