package bbdd;

import civilizations.Battle;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CivilizationArmyTable {

    private Database db;
    private int civilization_id;
    private Battle battle;

    private static final String[] UNIT_NAMES = {
        "Swordsman", "Spearman", "Crossbow", "Cannon",
        "ArrowTower", "Catapult", "RocketLauncherTower",
        "Magician", "Priest"
    };

    public CivilizationArmyTable(Database db, int civilization_id, Battle battle) {
        this.db = db;
        this.civilization_id = civilization_id;
        this.battle = battle;
    }

    public void insertRow() {
        String insertQuery =
            "INSERT INTO Civilization_attack_stats (" +
            "  civilization_id, num_battle, type, initial, drops" +
            ") VALUES (?, ?, ?, ?, ?)";

        System.out.println("Inserting civilization army stats into DB...");

        int[][] initialArmies = battle.getInitialArmies();
        int[] actual = battle.getActualNumberUnitsCivilization();

        try (PreparedStatement ps = db.getConnection().prepareStatement(insertQuery)) {

            for (int i = 0; i < UNIT_NAMES.length; i++) {
                int initial = initialArmies[0][i];
                if (initial > 0) {
                    int drops = initial - actual[i];
                    ps.setInt(1,    civilization_id);
                    ps.setInt(2,    battle.getBattleNumber());
                    ps.setString(3, UNIT_NAMES[i]);
                    ps.setInt(4,    initial);
                    ps.setInt(5,    drops);
                    ps.executeUpdate();
                }
            }

            System.out.println("Civilization army stats inserted.");

        } catch (SQLException e) {
            System.out.println("Error inserting civilization army stats: " + e.getMessage());
        }
    }
}