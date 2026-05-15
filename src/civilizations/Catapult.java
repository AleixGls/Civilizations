// ==================== Catapult.java ====================
package civilizations;

public class Catapult extends DefenseUnit {
	public Catapult(int defenseTech, int attackTech) {
		super(calculateArmor(defenseTech), calculateDamage(attackTech));
	}

	public Catapult() {
		super(ARMOR_CATAPULT, BASE_DAMAGE_CATAPULT);
	}

	private static int calculateArmor(int tech) {
		int bonus = tech * PLUS_ARMOR_CATAPULT_BY_TECHNOLOGY;
		return ARMOR_CATAPULT + (ARMOR_CATAPULT * bonus / 100);
	}

	private static int calculateDamage(int tech) {
		int bonus = tech * PLUS_ATTACK_CATAPULT_BY_TECHNOLOGY;
		return BASE_DAMAGE_CATAPULT + (BASE_DAMAGE_CATAPULT * bonus / 100);
	}

	
	public int getFoodCost() {
		return FOOD_COST_CATAPULT;
	}

	
	public int getWoodCost() {
		return WOOD_COST_CATAPULT;
	}

	
	public int getIronCost() {
		return IRON_COST_CATAPULT;
	}

	
	public int getManaCost() {
		return MANA_COST_CATAPULT;
	}

	
	public int getChanceGeneratingWaste() {
		return CHANCE_GENERATING_WASTE_CATAPULT;
	}

	
	public int getChanceAttackAgain() {
		return CHANCE_ATTACK_AGAIN_CATAPULT;
	}
}