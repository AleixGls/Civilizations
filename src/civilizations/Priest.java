// ==================== Priest.java ====================
package civilizations;

public class Priest extends SpecialUnit {
	public Priest(int defenseTech, int attackTech) {
		super(0, 0); // Los sacerdotes no atacan
	}

	public Priest() {
		super(0, 0);
	}

	public int attack() {
		return 0;
	}

	public int getFoodCost() {
		return FOOD_COST_PRIEST;
	}

	public int getWoodCost() {
		return WOOD_COST_PRIEST;
	}

	public int getIronCost() {
		return IRON_COST_PRIEST;
	}

	public int getManaCost() {
		return MANA_COST_PRIEST;
	}

	public int getChanceGeneratingWaste() {
		return CHANCE_GENERATING_WASTE_PRIEST;
	}

	public int getChanceAttackAgain() {
		return CHANCE_ATTACK_AGAIN_PRIEST;
	}
}