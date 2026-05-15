package bbdd;

import civilizations.Battle;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BattleLogTable {

    private Database db;
    private int civilization_id;
    private Battle battle;

    public BattleLogTable(Database db, int civilization_id, Battle battle) {
        this.db = db;
        this.civilization_id = civilization_id;
        this.battle = battle;
    }

    public void insertRow() {
        String insertQuery =
            "INSERT INTO Battle_log (" +
            "  civilization_id, num_battle, num_line, log_entry" +
            ") VALUES (?, ?, ?, ?)";

        System.out.println("Inserting battle log into DB...");

        String[] lines = battle.getBattleDevelopment().split("\n");

        try (PreparedStatement ps = db.getConnection().prepareStatement(insertQuery)) {

            for (int i = 0; i < lines.length; i++) {
                ps.setInt(1, civilization_id);
                ps.setInt(2, battle.getBattleNumber());
                ps.setInt(3, i + 1);
                ps.setString(4, lines[i]);
                ps.executeUpdate();
            }

            System.out.println("Battle_log inserted: " + lines.length + " lines.");

        } catch (SQLException e) {
            System.out.println("Error inserting battle log: " + e.getMessage());
        }
    }
}