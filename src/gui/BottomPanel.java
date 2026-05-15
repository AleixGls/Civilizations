package gui;

import civilizations.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class BottomPanel extends JPanel {
    private CivilizationsGUI gui;
    private JButton[] unitButtons;
    private String[] unitNames = {"Espadachín", "Lancero", "Ballesta", "Cañón", "Torre flecha", "Catapulta", "Lanzacohetes", "Mago", "Sacerdote"};
    private String[] unitIcons = {"swordsman.png", "spearman.png", "crossbow.png", "cannon.png", "arrowTower.png", "catapult.png", "rocketLauncher.png", "magician.png", "priest.png"};

    public BottomPanel(CivilizationsGUI gui) {
        this.gui = gui;
        setLayout(new GridLayout(2, 5, 10, 10));
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Crear Unidades", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.WHITE));
        unitButtons = new JButton[9];
        for (int i = 0; i < 9; i++) {
            final int type = i;
            unitButtons[i] = new JButton();
            unitButtons[i].setLayout(new BorderLayout());
            unitButtons[i].setBackground(Color.DARK_GRAY);
            unitButtons[i].setForeground(Color.WHITE);
            unitButtons[i].setFocusPainted(false);

            JLabel iconLabel = new JLabel(gui.loadIcon(unitIcons[i], 50, 50));
            JLabel textLabel = new JLabel(unitNames[type] + " (0)");
            textLabel.setForeground(Color.WHITE);
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel costPanel = new JPanel(new FlowLayout());
            costPanel.setBackground(Color.DARK_GRAY);
            int metalCost = Variables.IRON_COST_UNITS[type];
            int manaCost = gui.getManaCost(type);
            costPanel.add(new JLabel(gui.loadIcon("iron.png", 16, 16)));
            costPanel.add(new JLabel(String.valueOf(metalCost)));
            if (manaCost > 0) {
                costPanel.add(new JLabel(gui.loadIcon("mana.png", 16, 16)));
                costPanel.add(new JLabel(String.valueOf(manaCost)));
            }

            unitButtons[i].add(iconLabel, BorderLayout.NORTH);
            unitButtons[i].add(textLabel, BorderLayout.CENTER);
            unitButtons[i].add(costPanel, BorderLayout.SOUTH);
            unitButtons[i].addActionListener(e -> createUnits(type));
            add(unitButtons[i]);
        }
    }

    private void createUnits(int type) {
        String input = JOptionPane.showInputDialog(gui, "Cantidad de " + unitNames[type] + ":");
        if (input != null) {
            try {
                int n = Integer.parseInt(input);
                if (n <= 0) throw new NumberFormatException();
                Civilization civ = gui.getCivilization();
                switch (type) {
                    case 0: civ.newSwordsman(n); break;
                    case 1: civ.newSpearman(n); break;
                    case 2: civ.newCrossbow(n); break;
                    case 3: civ.newCannon(n); break;
                    case 4: civ.newArrowTower(n); break;
                    case 5: civ.newCatapult(n); break;
                    case 6: civ.newRocketLauncher(n); break;
                    case 7: civ.newMagician(n); break;
                    case 8: civ.newPriest(n); break;
                }
                gui.appendLog("Creadas " + n + " unidades de tipo " + unitNames[type] + "\n");
                gui.refreshAll();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(gui, "Cantidad inválida", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceException | BuildingException e) {
                JOptionPane.showMessageDialog(gui, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                gui.refreshAll();
            }
        }
    }

    public void refresh() {
        Civilization civ = gui.getCivilization();
        for (int i = 0; i < unitButtons.length; i++) {
            JButton btn = unitButtons[i];
            JLabel textLabel = (JLabel) btn.getComponent(1);
            int count = civ.getArmy()[i].size();
            textLabel.setText(unitNames[i] + " (" + count + ")");
        }
    }
}