// ==================== AttackUnit.java ====================
package civilizations;

public abstract class AttackUnit implements MilitaryUnit, Variables {
    protected int armor;
    protected int initialArmor;
    protected int baseDamage;
    protected int experience;
    protected boolean sanctified;

    public AttackUnit(int armor, int baseDamage) {
        this.armor = armor;
        this.initialArmor = armor;
        this.baseDamage = baseDamage;
        this.experience = 0;
        this.sanctified = false;
    }

    public AttackUnit() {
        this.armor = 0;
        this.initialArmor = 0;
        this.baseDamage = 0;
        this.experience = 0;
        this.sanctified = false;
    }

    @Override
    public int attack() {
        int bonus = (experience * PLUS_ATTACK_UNIT_PER_EXPERIENCE_POINT) / 100;
        int sanctifyBonus = sanctified ? PLUS_ATTACK_UNIT_SANCTIFIED : 0;
        return baseDamage * (100 + bonus + sanctifyBonus) / 100;
    }

    @Override
    public void takeDamage(int receivedDamage) {
        armor -= receivedDamage;
    }

    @Override
    public int getActualArmor() {
        return armor;
    }

    @Override
    public void resetArmor() {
        this.armor = this.initialArmor;
    }

    @Override
    public void setExperience(int n) {
        this.experience = n;
    }

    @Override
    public int getExperience() {
        return experience;
    }

    public void setSanctified(boolean sanctified) {
        this.sanctified = sanctified;
    }

    public boolean isSanctified() {
        return sanctified;
    }
}