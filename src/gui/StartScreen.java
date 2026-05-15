package gui;

import bbdd.Database;
import bbdd.GlobalContext;
import bbdd.SaveData;
import civilizations.Civilization;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StartScreen extends JFrame {
    public StartScreen() {
        String url ="jdbc:mysql://civilizations2526.c92s82e2qeo2.eu-north-1.rds.amazonaws.com/civilizations2526?useSSL=false&allowPublicKeyRetrieval=true";
        String username = "admin";
        String pass = "Jefecolorado123";

        GlobalContext.database = new Database(url, username, pass);
    	
        setTitle("Civilizations - Inicio");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.BLACK);

        JLabel title = new JLabel("Civilizations");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton newGameBtn = new JButton("Nueva Partida");
        JButton continueBtn = new JButton("Continuar");
        styleButton(newGameBtn);
        styleButton(continueBtn);

        newGameBtn.addActionListener(e -> {
            dispose();
            new CivilizationsGUI(null, null);
        });

        continueBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                this,
                "Introduce el ID de la civilización a cargar:",
                "Cargar Partida",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int civilizationId = Integer.parseInt(input.trim());
                    SaveData loaded = loadGame(civilizationId);
                    if (loaded != null) {
                    	System.out.println("vamos allá");
                        dispose();
                        new CivilizationsGUI(loaded.getCivilization(), loaded.getBattleHistory());
                    } else {
                        JOptionPane.showMessageDialog(
                            this,
                            "No se encontró partida con ID: " + civilizationId,
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        this,
                        "ID inválido. Debe ser un número.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(newGameBtn);
        btnPanel.add(continueBtn);
        add(btnPanel, BorderLayout.CENTER);
        add(Box.createVerticalStrut(20), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(Color.DARK_GRAY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private SaveData loadGame(int civilizationId) {
        try {
            // Cargar civilización con query
            String query = "SELECT * FROM Civilization_stats WHERE civilization_id = ?";
            Database db= GlobalContext.database;
            PreparedStatement stmt = db.getConnection().prepareStatement(query);
            stmt.setInt(1, civilizationId);
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.next()) {
                rs.close();
                stmt.close();
                return null; 
            }
            
            Civilization civilization = new Civilization();
            GlobalContext.civilization_id=(rs.getInt("civilization_id"));
            civilization.setWood(rs.getInt("wood_amount"));
            civilization.setIron(rs.getInt("iron_amount"));
            civilization.setFood(rs.getInt("food_amount"));
            civilization.setMana(rs.getInt("mana_amount"));
            civilization.setMagicTower(rs.getInt("magicTower_counter"));
            civilization.setChurch(rs.getInt("church_counter"));
            civilization.setFarm(rs.getInt("farm_counter"));
            civilization.setSmithy(rs.getInt("smithy_counter"));
            civilization.setCarpentry(rs.getInt("carpentry_counter"));
            civilization.setTechnologyAttack(rs.getInt("technology_defense_level"));
            civilization.setTechnologyAttack(rs.getInt("technology_attack_level"));
            civilization.setBattles(rs.getInt("battles_counter"));
            
            rs.close();
            stmt.close();
         
            
            return new SaveData(civilization, null);
            
        } catch (SQLException e) {
            System.err.println("Error cargando partida: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StartScreen::new);
    }
}
