package bbdd;

import civilizations.Battle;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BattleStatsTable {

    private Database db;
    private int civilization_id;
    private Battle battle;

    public BattleStatsTable(Database db, int civilization_id, Battle battle) {
        this.db = db;
        this.civilization_id = civilization_id;
        this.battle = battle;
    }

    public void insertRow() {
        String insertQuery =
            "INSERT INTO Battle_stats (" +
            "  civilization_id, num_battle, resource_wood_acquired, resource_iron_acquired" +
            ") VALUES (?, ?, ?, ?)";

        System.out.println("Inserting battle stats into DB...");

        try (PreparedStatement ps = db.getConnection().prepareStatement(insertQuery)) {

            ps.setInt(1, civilization_id);
            ps.setInt(2, battle.getBattleNumber());
            ps.setInt(3, battle.getWaste()[0]); // wood
            ps.setInt(4, battle.getWaste()[1]); // iron

            ps.executeUpdate();
            System.out.println("Insertion in Battle_stats executed.");

        } catch (SQLException e) {
            System.out.println("Error inserting battle stats: " + e.getMessage());
        }
    }
}