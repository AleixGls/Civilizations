package gui;

import civilizations.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class RightPanel extends JPanel {
    private CivilizationsGUI gui;
    private JButton viewThreatBtn, battleBtn, reportsBtn, exitBtn;

    public RightPanel(CivilizationsGUI gui) {
        this.gui = gui;
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Acciones", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.WHITE));
        setPreferredSize(new Dimension(220, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        // Edificios con costes
        String[] buildNames = {"Granja", "Carpintería", "Herrería", "Torre Mágica", "Iglesia"};
        int[] foodCosts = {Variables.FOOD_COST_FARM, Variables.FOOD_COST_CARPENTRY, Variables.FOOD_COST_SMITHY,
                           Variables.FOOD_COST_MAGICTOWER, Variables.FOOD_COST_CHURCH};
        int[] woodCosts = {Variables.WOOD_COST_FARM, Variables.WOOD_COST_CARPENTRY, Variables.WOOD_COST_SMITHY,
                           Variables.WOOD_COST_MAGICTOWER, Variables.WOOD_COST_CHURCH};
        int[] ironCosts = {Variables.IRON_COST_FARM, Variables.IRON_COST_CARPENTRY, Variables.IRON_COST_SMITHY,
                           Variables.IRON_COST_MAGICTOWER, Variables.IRON_COST_CHURCH};

        for (int i = 0; i < buildNames.length; i++) {
            final int type = i;
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.DARK_GRAY);
            panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JButton btn = new JButton("Construir " + buildNames[i]);
            styleButton(btn);
            btn.addActionListener(e -> buildBuilding(type));

            JPanel costPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
            costPanel.setBackground(Color.DARK_GRAY);
            costPanel.add(new JLabel(gui.loadIcon("food.png", 16, 16)));
            costPanel.add(new JLabel(String.valueOf(foodCosts[i])));
            costPanel.add(new JLabel(gui.loadIcon("wood.png", 16, 16)));
            costPanel.add(new JLabel(String.valueOf(woodCosts[i])));
            costPanel.add(new JLabel(gui.loadIcon("iron.png", 16, 16)));
            costPanel.add(new JLabel(String.valueOf(ironCosts[i])));

            panel.add(btn, BorderLayout.CENTER);
            panel.add(costPanel, BorderLayout.SOUTH);

            add(panel, gbc);
            gbc.gridy++;
        }

        add(Box.createVerticalStrut(10), gbc);
        gbc.gridy++;

        viewThreatBtn = new JButton("Ver Amenaza");
        battleBtn = new JButton("¡LIBAR BATALLA!");
        reportsBtn = new JButton("Informes");
        exitBtn = new JButton("Salir");

        for (JButton btn : new JButton[]{viewThreatBtn, battleBtn, reportsBtn, exitBtn}) {
            styleButton(btn);
            add(btn, gbc);
            gbc.gridy++;
        }

        viewThreatBtn.addActionListener(e -> gui.viewThreat());
        battleBtn.addActionListener(e -> gui.startBattle());
        reportsBtn.addActionListener(e -> gui.showBattleReports());
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(Color.DARK_GRAY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void buildBuilding(int type) {
        Civilization civ = gui.getCivilization();
        try {
            switch (type) {
                case 0: civ.newFarm(); gui.appendLog("Granja construida.\n"); break;
                case 1: civ.newCarpentry(); gui.appendLog("Carpintería construida.\n"); break;
                case 2: civ.newSmithy(); gui.appendLog("Herrería construida.\n"); break;
                case 3: civ.newMagicTower(); gui.appendLog("Torre Mágica construida.\n"); break;
                case 4: civ.newChurch(); gui.appendLog("Iglesia construida.\n"); break;
            }
            gui.refreshAll();
        } catch (ResourceException ex) {
            JOptionPane.showMessageDialog(gui, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() { }
}