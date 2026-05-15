package bbdd;

import civilizations.Civilization;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CivilizationStatsTable {

    private Database db;
    private Civilization civilization;

    public CivilizationStatsTable(Database db, Civilization civilization) {
        this.db = db;
        this.civilization = civilization;
    }

    // INSERT — called when starting a new game
    public int insertRow() {
        String insertQuery =
            "INSERT INTO Civilization_stats (" +
            "  wood_amount, iron_amount, food_amount, mana_amount," +
            "  magicTower_counter, church_counter, farm_counter," +
            "  smithy_counter, carpentry_counter," +
            "  technology_defense_level, technology_attack_level, battles_counter" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println("Inserting new civilization into DB...");
        int generatedId = -1;

        try (PreparedStatement ps = db.getConnection().prepareStatement(
                insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1,  civilization.getWood());
            ps.setInt(2,  civilization.getIron());
            ps.setInt(3,  civilization.getFood());
            ps.setInt(4,  civilization.getMana());
            ps.setInt(5,  civilization.getMagicTower());
            ps.setInt(6,  civilization.getChurch());
            ps.setInt(7,  civilization.getFarm());
            ps.setInt(8,  civilization.getSmithy());
            ps.setInt(9,  civilization.getCarpentry());
            ps.setInt(10, civilization.getTechnologyDefense());
            ps.setInt(11, civilization.getTechnologyAttack());
            ps.setInt(12, civilization.getBattles());

            ps.executeUpdate();
            System.out.println("Civilization inserted in Civilization_stats.");

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
                System.out.println("Generated civilization_id: " + generatedId);
            }

        } catch (SQLException e) {
            System.out.println("Error inserting civilization: " + e.getMessage());
        }

        return generatedId;
    }

    // UPDATE — called when saving mid-game or after a battle
    public void updateAttributes() {
        String updateQuery =
            "UPDATE Civilization_stats SET" +
            "  wood_amount = ?, iron_amount = ?, food_amount = ?, mana_amount = ?," +
            "  magicTower_counter = ?, church_counter = ?, farm_counter = ?," +
            "  smithy_counter = ?, carpentry_counter = ?," +
            "  technology_defense_level = ?, technology_attack_level = ?," +
            "  battles_counter = ?" +
            " WHERE civilization_id = ?";

        System.out.println("Updating civilization in DB...");

        try (PreparedStatement ps = db.getConnection().prepareStatement(updateQuery)) {

            ps.setInt(1,  civilization.getWood());
            ps.setInt(2,  civilization.getIron());
            ps.setInt(3,  civilization.getFood());
            ps.setInt(4,  civilization.getMana());
            ps.setInt(5,  civilization.getMagicTower());
            ps.setInt(6,  civilization.getChurch());
            ps.setInt(7,  civilization.getFarm());
            ps.setInt(8,  civilization.getSmithy());
            ps.setInt(9,  civilization.getCarpentry());
            ps.setInt(10, civilization.getTechnologyDefense());
            ps.setInt(11, civilization.getTechnologyAttack());
            ps.setInt(12, civilization.getBattles());
            ps.setInt(13, GlobalContext.civilization_id);

            ps.executeUpdate();
            System.out.println("Civilization_stats updated.");

        } catch (SQLException e) {
            System.out.println("Error updating civilization: " + e.getMessage());
        }
    }
}