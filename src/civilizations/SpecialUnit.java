// ==================== SpecialUnit.java ====================
package civilizations;

public abstract class SpecialUnit implements MilitaryUnit, Variables {
    protected int armor;        // siempre 0
    protected int initialArmor; // siempre 0
    protected int baseDamage;
    protected int experience;

    public SpecialUnit(int armor, int baseDamage) {
        this.armor = 0;
        this.initialArmor = 0;
        this.baseDamage = baseDamage;
        this.experience = 0;
    }

    public SpecialUnit() {
        this.armor = 0;
        this.initialArmor = 0;
        this.baseDamage = 0;
        this.experience = 0;
    }

    @Override
    public int attack() {
        int bonus = (experience * PLUS_ATTACK_UNIT_PER_EXPERIENCE_POINT) / 100;
        // Los magos tienen un bono adicional por tecnología en su ataque
        if (this instanceof Magician) {
            // El bono se aplica en la subclase, aquí dejamos solo experiencia
        }
        return baseDamage * (100 + bonus) / 100;
    }

    @Override
    public void takeDamage(int receivedDamage) {
        armor -= receivedDamage; // siempre 0, queda negativo y se elimina
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
}