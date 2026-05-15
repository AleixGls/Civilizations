// ==================== Spearman.java ====================
package civilizations;

public class Spearman extends AttackUnit {
	public Spearman(int defenseTech, int attackTech) {
		super(calculateArmor(defenseTech), calculateDamage(attackTech));
	}

	public Spearman() {
		super(ARMOR_SPEARMAN, BASE_DAMAGE_SPEARMAN);
	}

	private static int calculateArmor(int tech) {
		int bonus = tech * PLUS_ARMOR_SPEARMAN_BY_TECHNOLOGY;
		return ARMOR_SPEARMAN + (ARMOR_SPEARMAN * bonus / 100);
	}

	private static int calculateDamage(int tech) {
		int bonus = tech * PLUS_ATTACK_SPEARMAN_BY_TECHNOLOGY;
		return BASE_DAMAGE_SPEARMAN + (BASE_DAMAGE_SPEARMAN * bonus / 100);
	}

	public int getFoodCost() {
		return FOOD_COST_SPEARMAN;
	}

	public int getWoodCost() {
		return WOOD_COST_SPEARMAN;
	}

	public int getIronCost() {
		return IRON_COST_SPEARMAN;
	}

	public int getManaCost() {
		return MANA_COST_SPEARMAN;
	}

	public int getChanceGeneratingWaste() {
		return CHANCE_GENERATING_WASTE_SPEARMAN;
	}

	public int getChanceAttackAgain() {
		return CHANCE_ATTACK_AGAIN_SPEARMAN;
	}
}