package gui;

import bbdd.SaveData;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class StartScreen extends JFrame {
    public StartScreen() {
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
            SaveData loaded = loadGame();
            if (loaded != null) {
                dispose();
                new CivilizationsGUI(loaded.getCivilization(), loaded.getBattleHistory());
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró partida guardada.\nSe iniciará nueva partida.", "Error", JOptionPane.WARNING_MESSAGE);
                dispose();
                new CivilizationsGUI(null, null);
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

    private SaveData loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("savegame.ser"))) {
            return (SaveData) ois.readObject();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StartScreen::new);
    }
}