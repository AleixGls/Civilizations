// ==================== Cannon.java ====================
package civilizations;

public class Cannon extends AttackUnit {
	public Cannon(int defenseTech, int attackTech) {
		super(calculateArmor(defenseTech), calculateDamage(attackTech));
	}

	public Cannon() {
		super(ARMOR_CANNON, BASE_DAMAGE_CANNON);
	}

	private static int calculateArmor(int tech) {
		int bonus = tech * PLUS_ARMOR_CANNON_BY_TECHNOLOGY;
		return ARMOR_CANNON + (ARMOR_CANNON * bonus / 100);
	}

	private static int calculateDamage(int tech) {
		int bonus = tech * PLUS_ATTACK_CANNON_BY_TECHNOLOGY;
		return BASE_DAMAGE_CANNON + (BASE_DAMAGE_CANNON * bonus / 100);
	}

	
	public int getFoodCost() {
		return FOOD_COST_CANNON;
	}

	
	public int getWoodCost() {
		return WOOD_COST_CANNON;
	}

	
	public int getIronCost() {
		return IRON_COST_CANNON;
	}

	
	public int getManaCost() {
		return MANA_COST_CANNON;
	}

	
	public int getChanceGeneratingWaste() {
		return CHANCE_GENERATING_WASTE_CANNON;
	}

	
	public int getChanceAttackAgain() {
		return CHANCE_ATTACK_AGAIN_CANNON;
	}
}