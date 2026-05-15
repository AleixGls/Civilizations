package bbdd;

import civilizations.Battle;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EnemyArmyTable {

    private Database db;
    private int civilization_id;
    private Battle battle;

    private static final String[] ENEMY_UNIT_NAMES = {
        "Swordsman", "Spearman", "Crossbow", "Cannon"
    };

    public EnemyArmyTable(Database db, int civilization_id, Battle battle) {
        this.db = db;
        this.civilization_id = civilization_id;
        this.battle = battle;
    }

    public void insertRow() {
        String insertQuery =
            "INSERT INTO Enemy_attack_stats (" +
            "  civilization_id, num_battle, type, initial, drops" +
            ") VALUES (?, ?, ?, ?, ?)";

        System.out.println("Inserting enemy army stats into DB...");

        int[][] initialArmies = battle.getInitialArmies();
        int[] actual = battle.getActualNumberUnitsEnemy();

        try (PreparedStatement ps = db.getConnection().prepareStatement(insertQuery)) {

            for (int i = 0; i < ENEMY_UNIT_NAMES.length; i++) {
                int initial = initialArmies[1][i];
                if (initial > 0) {
                    int drops = initial - actual[i];
                    ps.setInt(1,    civilization_id);
                    ps.setInt(2,    battle.getBattleNumber());
                    ps.setString(3, ENEMY_UNIT_NAMES[i]);
                    ps.setInt(4,    initial);
                    ps.setInt(5,    drops);
                    ps.executeUpdate();
                }
            }

            System.out.println("Enemy army stats inserted.");

        } catch (SQLException e) {
            System.out.println("Error inserting enemy army stats: " + e.getMessage());
        }
    }
}