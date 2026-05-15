package gui;

import civilizations.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class LeftPanel extends JPanel {
    private CivilizationsGUI gui;
    private JLabel foodLabel, woodLabel, ironLabel, manaLabel;
    private JLabel techDefLabel, techAtkLabel;
    private JLabel farmLabel, carpLabel, smithLabel, towerLabel, churchLabel;
    private JButton upgradeDefenseBtn, upgradeAttackBtn;
    private JLabel upgradeDefenseCostLabel, upgradeAttackCostLabel;

    public LeftPanel(CivilizationsGUI gui) {
        this.gui = gui;
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Gestión", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.WHITE));
        setPreferredSize(new Dimension(280, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        add(createSectionTitle("RECURSOS"), gbc);
        gbc.gridy++;
        foodLabel = createResourceLabel("Comida", "food.png");
        add(foodLabel, gbc);
        gbc.gridy++;
        woodLabel = createResourceLabel("Madera", "wood.png");
        add(woodLabel, gbc);
        gbc.gridy++;
        ironLabel = createResourceLabel("Hierro", "iron.png");
        add(ironLabel, gbc);
        gbc.gridy++;
        manaLabel = createResourceLabel("Maná", "mana.png");
        add(manaLabel, gbc);

        gbc.gridy++;
        add(createSectionTitle("TECNOLOGÍA"), gbc);
        gbc.gridy++;
        techDefLabel = new JLabel("Defensa: nivel 0");
        techDefLabel.setForeground(Color.WHITE);
        add(techDefLabel, gbc);
        gbc.gridy++;
        upgradeDefenseBtn = new JButton("Mejorar Defensa");
        styleButton(upgradeDefenseBtn);
        // Panel de coste para mejora defensa
        JPanel defCostPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        defCostPanel.setBackground(Color.DARK_GRAY);
        defCostPanel.add(new JLabel(gui.loadIcon("iron.png", 16, 16)));
        upgradeDefenseCostLabel = new JLabel("0");
        upgradeDefenseCostLabel.setForeground(Color.WHITE);
        defCostPanel.add(upgradeDefenseCostLabel);
        // Si hubiera coste de madera se añadiría aquí, pero en Variables es 0
        JPanel defButtonPanel = new JPanel(new BorderLayout());
        defButtonPanel.setBackground(Color.DARK_GRAY);
        defButtonPanel.add(upgradeDefenseBtn, BorderLayout.CENTER);
        defButtonPanel.add(defCostPanel, BorderLayout.SOUTH);
        add(defButtonPanel, gbc);
        gbc.gridy++;

        techAtkLabel = new JLabel("Ataque: nivel 0");
        techAtkLabel.setForeground(Color.WHITE);
        add(techAtkLabel, gbc);
        gbc.gridy++;
        upgradeAttackBtn = new JButton("Mejorar Ataque");
        styleButton(upgradeAttackBtn);
        JPanel atkCostPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        atkCostPanel.setBackground(Color.DARK_GRAY);
        atkCostPanel.add(new JLabel(gui.loadIcon("iron.png", 16, 16)));
        upgradeAttackCostLabel = new JLabel("0");
        upgradeAttackCostLabel.setForeground(Color.WHITE);
        atkCostPanel.add(upgradeAttackCostLabel);
        JPanel atkButtonPanel = new JPanel(new BorderLayout());
        atkButtonPanel.setBackground(Color.DARK_GRAY);
        atkButtonPanel.add(upgradeAttackBtn, BorderLayout.CENTER);
        atkButtonPanel.add(atkCostPanel, BorderLayout.SOUTH);
        add(atkButtonPanel, gbc);
        gbc.gridy++;

        gbc.gridy++;
        add(createSectionTitle("EDIFICIOS"), gbc);
        gbc.gridy++;
        farmLabel = createBuildingLabel("Granjas", "farm.png");
        add(farmLabel, gbc);
        gbc.gridy++;
        carpLabel = createBuildingLabel("Carpinterías", "carpentry.png");
        add(carpLabel, gbc);
        gbc.gridy++;
        smithLabel = createBuildingLabel("Herrerías", "smithy.png");
        add(smithLabel, gbc);
        gbc.gridy++;
        towerLabel = createBuildingLabel("Torres Mágicas", "magicTower.png");
        add(towerLabel, gbc);
        gbc.gridy++;
        churchLabel = createBuildingLabel("Iglesias", "church.png");
        add(churchLabel, gbc);

        upgradeDefenseBtn.addActionListener(e -> upgradeTech(true));
        upgradeAttackBtn.addActionListener(e -> upgradeTech(false));
    }

    private JLabel createResourceLabel(String name, String iconFile) {
        JLabel label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setIcon(gui.loadIcon(iconFile, 32, 32));
        label.setText(name + ": 0");
        label.setIconTextGap(10);
        return label;
    }

    private JLabel createBuildingLabel(String name, String iconFile) {
        JLabel label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setIcon(gui.loadIcon(iconFile, 24, 24));
        label.setText(name + ": 0");
        label.setIconTextGap(10);
        return label;
    }

    private JLabel createSectionTitle(String title) {
        JLabel label = new JLabel(title);
        label.setForeground(Color.CYAN);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        return label;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(Color.DARK_GRAY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    private void upgradeTech(boolean isDefense) {
        try {
            Civilization civ = gui.getCivilization();
            if (isDefense) {
                civ.upgradeTechnologyDefense();
                gui.appendLog("Tecnología de defensa mejorada a nivel " + civ.getTechnologyDefense() + "\n");
            } else {
                civ.upgradeTechnologyAttack();
                gui.appendLog("Tecnología de ataque mejorada a nivel " + civ.getTechnologyAttack() + "\n");
            }
            gui.refreshAll();
        } catch (ResourceException ex) {
            JOptionPane.showMessageDialog(gui, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
        Civilization civ = gui.getCivilization();
        foodLabel.setText("Comida: " + civ.getFood());
        woodLabel.setText("Madera: " + civ.getWood());
        ironLabel.setText("Hierro: " + civ.getIron());
        manaLabel.setText("Maná: " + civ.getMana());
        techDefLabel.setText("Defensa: nivel " + civ.getTechnologyDefense());
        techAtkLabel.setText("Ataque: nivel " + civ.getTechnologyAttack());
        farmLabel.setText("Granjas: " + civ.getFarm());
        carpLabel.setText("Carpinterías: " + civ.getCarpentry());
        smithLabel.setText("Herrerías: " + civ.getSmithy());
        towerLabel.setText("Torres Mágicas: " + civ.getMagicTower());
        churchLabel.setText("Iglesias: " + civ.getChurch());

        // Actualizar costes de mejora
        int defCost = Variables.UPGRADE_BASE_DEFENSE_TECHNOLOGY_IRON_COST + civ.getTechnologyDefense() * Variables.UPGRADE_PLUS_DEFENSE_TECHNOLOGY_IRON_COST;
        int atkCost = Variables.UPGRADE_BASE_ATTACK_TECHNOLOGY_IRON_COST + civ.getTechnologyAttack() * Variables.UPGRADE_PLUS_ATTACK_TECHNOLOGY_IRON_COST;
        upgradeDefenseCostLabel.setText(String.valueOf(defCost));
        upgradeAttackCostLabel.setText(String.valueOf(atkCost));
    }
}