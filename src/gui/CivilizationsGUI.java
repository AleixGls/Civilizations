package gui;

import civilizations.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CivilizationsGUI extends JFrame implements Variables {
    private Civilization civilization;
    private ArrayList<MilitaryUnit> currentEnemyArmy;
    private boolean enemyPending;
    private Timer gameTimer;
    private JTextArea logArea;
    private LeftPanel leftPanel;
    private MiddlePanel middlePanel;
    private RightPanel rightPanel;
    private BottomPanel bottomPanel;

    public CivilizationsGUI() {
        civilization = new Civilization();
        currentEnemyArmy = null;
        enemyPending = false;
        gameTimer = new Timer();

        // Recursos iniciales para empezar con algo
        civilization.setFood(50000);
        civilization.setWood(50000);
        civilization.setIron(50000);
        civilization.setMana(0);

        initUI();
        startTimers();
        setTitle("Civilizations - Gestión de tu Imperio");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        refreshAll();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        leftPanel = new LeftPanel();
        middlePanel = new MiddlePanel();
        rightPanel = new RightPanel();
        bottomPanel = new BottomPanel();

        add(leftPanel, BorderLayout.WEST);
        add(middlePanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void startTimers() {
        // Generación de recursos cada 60 segundos
        TimerTask resourceTask = new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> generateResources());
            }
        };
        gameTimer.schedule(resourceTask, 60000, 60000);

        // Creación de enemigo cada 180 segundos (3 minutos)
        TimerTask enemyTask = new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (!enemyPending) {
                        currentEnemyArmy = createEnemyArmy();
                        enemyPending = true;
                        appendLog("*** ¡Nueva amenaza enemiga! Usa 'Ver Amenaza' para detalles. ***\n");
                        refreshThreatIndicator(true);
                        refreshAll();
                    }
                });
            }
        };
        gameTimer.schedule(enemyTask, 180000, 180000);
    }

    private void generateResources() {
        int foodGen = CIVILIZATION_FOOD_GENERATED + civilization.getFarm() * CIVILIZATION_FOOD_GENERATED_PER_FARM;
        int woodGen = CIVILIZATION_WOOD_GENERATED + civilization.getCarpentry() * CIVILIZATION_WOOD_GENERATED_PER_CARPENTRY;
        int ironGen = CIVILIZATION_IRON_GENERATED + civilization.getSmithy() * CIVILIZATION_IRON_GENERATED_PER_SMITHY;
        int manaGen = civilization.getMagicTower() * CIVILIZATION_MANA_GENERATED_PER_MAGIC_TOWER;
        civilization.setFood(civilization.getFood() + foodGen);
        civilization.setWood(civilization.getWood() + woodGen);
        civilization.setIron(civilization.getIron() + ironGen);
        civilization.setMana(civilization.getMana() + manaGen);
        appendLog(String.format("Recursos generados: +%d comida, +%d madera, +%d hierro, +%d maná\n",
                foodGen, woodGen, ironGen, manaGen));
        refreshAll();
    }

    private ArrayList<MilitaryUnit> createEnemyArmy() {
        int battles = civilization.getBattles();
        int increment = battles * ENEMY_FLEET_INCREASE / 100;
        int availableIron = IRON_BASE_ENEMY_ARMY * (100 + increment) / 100;
        int availableWood = WOOD_BASE_ENEMY_ARMY * (100 + increment) / 100;
        int availableFood = FOOD_BASE_ENEMY_ARMY * (100 + increment) / 100;
        ArrayList<MilitaryUnit> army = new ArrayList<>();
        java.util.Random rand = new java.util.Random();
        int[] probs = {35, 25, 20, 20}; // Swordsman, Spearman, Crossbow, Cannon
        while (true) {
            int type = selectByProb(probs, rand);
            MilitaryUnit unit = null;
            int foodCost = 0, woodCost = 0, ironCost = 0;
            switch (type) {
                case 0: unit = new Swordsman(); foodCost = FOOD_COST_SWORDSMAN; woodCost = WOOD_COST_SWORDSMAN; ironCost = IRON_COST_SWORDSMAN; break;
                case 1: unit = new Spearman(); foodCost = FOOD_COST_SPEARMAN; woodCost = WOOD_COST_SPEARMAN; ironCost = IRON_COST_SPEARMAN; break;
                case 2: unit = new Crossbow(); foodCost = FOOD_COST_CROSSBOW; woodCost = WOOD_COST_CROSSBOW; ironCost = IRON_COST_CROSSBOW; break;
                case 3: unit = new Cannon(); foodCost = FOOD_COST_CANNON; woodCost = WOOD_COST_CANNON; ironCost = IRON_COST_CANNON; break;
            }
            if (availableFood >= foodCost && availableWood >= woodCost && availableIron >= ironCost) {
                army.add(unit);
                availableFood -= foodCost;
                availableWood -= woodCost;
                availableIron -= ironCost;
            } else {
                // Intentar con la unidad más barata (Swordsman)
                if (availableFood >= FOOD_COST_SWORDSMAN && availableWood >= WOOD_COST_SWORDSMAN && availableIron >= IRON_COST_SWORDSMAN) {
                    army.add(new Swordsman());
                    availableFood -= FOOD_COST_SWORDSMAN;
                    availableWood -= WOOD_COST_SWORDSMAN;
                    availableIron -= IRON_COST_SWORDSMAN;
                } else {
                    break;
                }
            }
        }
        return army;
    }

    private int selectByProb(int[] probs, java.util.Random rand) {
        int total = 0;
        for (int p : probs) total += p;
        int r = rand.nextInt(total) + 1;
        int acum = 0;
        for (int i = 0; i < probs.length; i++) {
            acum += probs[i];
            if (acum >= r) return i;
        }
        return 0;
    }

    private void viewThreat() {
        if (!enemyPending || currentEnemyArmy == null) {
            JOptionPane.showMessageDialog(this, "No hay ninguna amenaza enemiga en este momento.", "Amenaza", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new ThreatFrame(currentEnemyArmy);
    }

    private void startBattle() {
        if (!enemyPending || currentEnemyArmy == null) {
            JOptionPane.showMessageDialog(this, "No hay enemigo para batallar. Espera a que llegue uno.", "Batalla", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Battle battle = new Battle(civilization.getArmy(), currentEnemyArmy);
        battle.startBattle();
        civilization.setBattles(civilization.getBattles() + 1);
        if (battle.isCivilizationWinner()) {
            int[] waste = battle.getWaste();
            civilization.setWood(civilization.getWood() + waste[0]);
            civilization.setIron(civilization.getIron() + waste[1]);
            appendLog("¡Victoria! Has obtenido " + waste[0] + " madera y " + waste[1] + " hierro de escombros.\n");
            JOptionPane.showMessageDialog(this, "¡Has ganado la batalla!\nRecursos obtenidos: +" + waste[0] + " madera, +" + waste[1] + " hierro.", "Victoria", JOptionPane.INFORMATION_MESSAGE);
        } else {
            appendLog("Derrota... La civilización ha sufrido pérdidas.\n");
            JOptionPane.showMessageDialog(this, "Has perdido la batalla.", "Derrota", JOptionPane.ERROR_MESSAGE);
        }
        currentEnemyArmy = null;
        enemyPending = false;
        refreshThreatIndicator(false);
        refreshAll();
    }

    private void showBattleReports() {
        JOptionPane.showMessageDialog(this, "Funcionalidad de informes de batalla pendiente.\nCada batalla se guarda en el historial.", "Informes", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshAll() {
        leftPanel.refresh();
        middlePanel.refresh();
        rightPanel.refresh();
        bottomPanel.refresh();
    }

    private void refreshThreatIndicator(boolean threatActive) {
        middlePanel.setThreatActive(threatActive);
    }

    private void appendLog(String msg) {
        middlePanel.appendLog(msg);
    }

    private int getManaCost(int unitType) {
        switch (unitType) {
            case 7: return MANA_COST_MAGICIAN;
            case 8: return MANA_COST_PRIEST;
            default: return 0;
        }
    }

    // ==================== PANELES INTERNOS ====================

    class LeftPanel extends JPanel {
        private JLabel foodLabel, woodLabel, ironLabel, manaLabel;
        private JLabel techDefLabel, techAtkLabel;
        private JLabel farmLabel, carpLabel, smithLabel, towerLabel, churchLabel;
        private JButton upgradeDefenseBtn, upgradeAttackBtn;

        LeftPanel() {
            setLayout(new GridBagLayout());
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Gestión", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.WHITE));
            setPreferredSize(new Dimension(250, 0));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;

            // Título Recursos
            add(createSectionTitle("RECURSOS"), gbc);
            gbc.gridy++;
            foodLabel = createResourceLabel("Comida", "./M3-Programacion/GUI/images/wheat.png");
            add(foodLabel, gbc);
            gbc.gridy++;
            woodLabel = createResourceLabel("Madera", "./M3-Programacion/GUI/images/oak_log.png");
            add(woodLabel, gbc);
            gbc.gridy++;
            ironLabel = createResourceLabel("Hierro", "./M3-Programacion/GUI/images/iron_ingot.png");
            add(ironLabel, gbc);
            gbc.gridy++;
            manaLabel = createResourceLabel("Maná", "./M3-Programacion/GUI/images/redstone.png");
            add(manaLabel, gbc);

            // Tecnología
            gbc.gridy++;
            add(createSectionTitle("TECNOLOGÍA"), gbc);
            gbc.gridy++;
            techDefLabel = new JLabel("Defensa: nivel 0");
            techDefLabel.setForeground(Color.WHITE);
            add(techDefLabel, gbc);
            gbc.gridy++;
            upgradeDefenseBtn = new JButton("Mejorar Defensa");
            styleButton(upgradeDefenseBtn);
            add(upgradeDefenseBtn, gbc);
            gbc.gridy++;
            techAtkLabel = new JLabel("Ataque: nivel 0");
            techAtkLabel.setForeground(Color.WHITE);
            add(techAtkLabel, gbc);
            gbc.gridy++;
            upgradeAttackBtn = new JButton("Mejorar Ataque");
            styleButton(upgradeAttackBtn);
            add(upgradeAttackBtn, gbc);

            // Edificios
            gbc.gridy++;
            add(createSectionTitle("EDIFICIOS"), gbc);
            gbc.gridy++;
            farmLabel = createBuildingLabel("Granjas", "./M3-Programacion/GUI/images/farm.png");
            add(farmLabel, gbc);
            gbc.gridy++;
            carpLabel = createBuildingLabel("Carpinterías", "./M3-Programacion/GUI/images/carpentry.png");
            add(carpLabel, gbc);
            gbc.gridy++;
            smithLabel = createBuildingLabel("Herrerías", "./M3-Programacion/GUI/images/smithy.png");
            add(smithLabel, gbc);
            gbc.gridy++;
            towerLabel = createBuildingLabel("Torres Mágicas", "./M3-Programacion/GUI/images/magic_tower.png");
            add(towerLabel, gbc);
            gbc.gridy++;
            churchLabel = createBuildingLabel("Iglesias", "./M3-Programacion/GUI/images/church.png");
            add(churchLabel, gbc);

            upgradeDefenseBtn.addActionListener(e -> upgradeTech(true));
            upgradeAttackBtn.addActionListener(e -> upgradeTech(false));
        }

        private JLabel createResourceLabel(String name, String iconPath) {
            JLabel label = new JLabel();
            label.setForeground(Color.WHITE);
            label.setIcon(loadIcon(iconPath, 32, 32));
            label.setText(name + ": 0");
            label.setIconTextGap(10);
            return label;
        }

        private JLabel createBuildingLabel(String name, String iconPath) {
            JLabel label = new JLabel();
            label.setForeground(Color.WHITE);
            label.setIcon(loadIcon(iconPath, 24, 24));
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
                if (isDefense) {
                    civilization.upgradeTechnologyDefense();
                    appendLog("Tecnología de defensa mejorada a nivel " + civilization.getTechnologyDefense() + "\n");
                } else {
                    civilization.upgradeTechnologyAttack();
                    appendLog("Tecnología de ataque mejorada a nivel " + civilization.getTechnologyAttack() + "\n");
                }
                refresh();
            } catch (ResourceException ex) {
                JOptionPane.showMessageDialog(CivilizationsGUI.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        void refresh() {
            foodLabel.setText("Comida: " + civilization.getFood());
            woodLabel.setText("Madera: " + civilization.getWood());
            ironLabel.setText("Hierro: " + civilization.getIron());
            manaLabel.setText("Maná: " + civilization.getMana());
            techDefLabel.setText("Defensa: nivel " + civilization.getTechnologyDefense());
            techAtkLabel.setText("Ataque: nivel " + civilization.getTechnologyAttack());
            farmLabel.setText("Granjas: " + civilization.getFarm());
            carpLabel.setText("Carpinterías: " + civilization.getCarpentry());
            smithLabel.setText("Herrerías: " + civilization.getSmithy());
            towerLabel.setText("Torres Mágicas: " + civilization.getMagicTower());
            churchLabel.setText("Iglesias: " + civilization.getChurch());
        }
    }

    class MiddlePanel extends JPanel {
        private JTextArea log;
        private JPanel threatPanel;
        private JLabel threatIcon;
        private JLabel threatLabel;

        MiddlePanel() {
            setLayout(new BorderLayout());
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Eventos y Estado", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.WHITE));

            log = new JTextArea();
            log.setEditable(false);
            log.setBackground(Color.DARK_GRAY);
            log.setForeground(Color.WHITE);
            log.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scroll = new JScrollPane(log);
            scroll.setBorder(null);

            threatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            threatPanel.setBackground(Color.BLACK);
            threatIcon = new JLabel(loadIcon("./M3-Programacion/GUI/images/alert.png", 32, 32));
            threatLabel = new JLabel("No hay amenaza");
            threatLabel.setForeground(Color.GREEN);
            threatPanel.add(threatIcon);
            threatPanel.add(threatLabel);

            add(threatPanel, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
        }

        void appendLog(String msg) {
            log.append(msg);
            log.setCaretPosition(log.getDocument().getLength());
        }

        void refresh() {
            // No es necesario actualizar nada más
        }

        void setThreatActive(boolean active) {
            if (active) {
                threatLabel.setText("¡AMENAZA PRESENTE!");
                threatLabel.setForeground(Color.RED);
            } else {
                threatLabel.setText("Sin amenaza");
                threatLabel.setForeground(Color.GREEN);
            }
        }
    }

    class RightPanel extends JPanel {
        private JButton[] buildingButtons;
        private JButton viewThreatBtn, battleBtn, reportsBtn, exitBtn;

        RightPanel() {
            setLayout(new GridBagLayout());
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Acciones", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.WHITE));
            setPreferredSize(new Dimension(200, 0));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;

            String[] buildNames = {"Granja", "Carpintería", "Herrería", "Torre Mágica", "Iglesia"};
            buildingButtons = new JButton[5];
            for (int i = 0; i < buildNames.length; i++) {
                buildingButtons[i] = new JButton("Construir " + buildNames[i]);
                styleButton(buildingButtons[i]);
                final int type = i;
                buildingButtons[i].addActionListener(e -> buildBuilding(type));
                add(buildingButtons[i], gbc);
                gbc.gridy++;
            }

            add(Box.createVerticalStrut(20), gbc);
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

            viewThreatBtn.addActionListener(e -> viewThreat());
            battleBtn.addActionListener(e -> startBattle());
            reportsBtn.addActionListener(e -> showBattleReports());
            exitBtn.addActionListener(e -> System.exit(0));
        }

        private void styleButton(JButton btn) {
            btn.setBackground(Color.DARK_GRAY);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
        }

        private void buildBuilding(int type) {
            try {
                switch (type) {
                    case 0: civilization.newFarm(); appendLog("Granja construida.\n"); break;
                    case 1: civilization.newCarpentry(); appendLog("Carpintería construida.\n"); break;
                    case 2: civilization.newSmithy(); appendLog("Herrería construida.\n"); break;
                    case 3: civilization.newMagicTower(); appendLog("Torre Mágica construida.\n"); break;
                    case 4: civilization.newChurch(); appendLog("Iglesia construida.\n"); break;
                }
                refreshAll();
            } catch (ResourceException ex) {
                JOptionPane.showMessageDialog(CivilizationsGUI.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        void refresh() {
            // No necesita actualización dinámica
        }
    }

    class BottomPanel extends JPanel {
        private JButton[] unitButtons;
        private String[] unitNames = {"Espadachín", "Lancero", "Ballesta", "Cañón", "Torre flecha", "Catapulta", "Lanzacohetes", "Mago", "Sacerdote"};
        private String[] unitIcons = {"swordsman.png", "spearman.png", "crossbow.png", "cannon.png", "arrow_tower.png", "catapult.png", "rocket_launcher.png", "magician.png", "priest.png"};

        BottomPanel() {
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
                // Icono
                JLabel iconLabel = new JLabel(loadIcon("./M3-Programacion/GUI/images/" + unitIcons[i], 50, 50));
                // Texto
                JLabel textLabel = new JLabel(unitNames[type] + " (0)");
                textLabel.setForeground(Color.WHITE);
                textLabel.setHorizontalAlignment(SwingConstants.CENTER);
                // Costes
                JPanel costPanel = new JPanel(new FlowLayout());
                costPanel.setBackground(Color.DARK_GRAY);
                int metalCost = IRON_COST_UNITS[type];
                int manaCost = getManaCost(type);
                costPanel.add(new JLabel(loadIcon("./M3-Programacion/GUI/images/iron_ingot.png", 16, 16)));
                costPanel.add(new JLabel(String.valueOf(metalCost)));
                if (manaCost > 0) {
                    costPanel.add(new JLabel(loadIcon("./M3-Programacion/GUI/images/redstone.png", 16, 16)));
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
            String input = JOptionPane.showInputDialog(CivilizationsGUI.this, "Cantidad de " + unitNames[type] + ":");
            if (input != null) {
                try {
                    int n = Integer.parseInt(input);
                    if (n <= 0) throw new NumberFormatException();
                    switch (type) {
                        case 0: civilization.newSwordsman(n); break;
                        case 1: civilization.newSpearman(n); break;
                        case 2: civilization.newCrossbow(n); break;
                        case 3: civilization.newCannon(n); break;
                        case 4: civilization.newArrowTower(n); break;
                        case 5: civilization.newCatapult(n); break;
                        case 6: civilization.newRocketLauncher(n); break;
                        case 7: civilization.newMagician(n); break;
                        case 8: civilization.newPriest(n); break;
                    }
                    appendLog("Creadas " + n + " unidades de tipo " + unitNames[type] + "\n");
                    refreshAll();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CivilizationsGUI.this, "Cantidad inválida", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceException | BuildingException e) {
                    JOptionPane.showMessageDialog(CivilizationsGUI.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    refreshAll();
                }
            }
        }

        void refresh() {
            for (int i = 0; i < unitButtons.length; i++) {
                JButton btn = unitButtons[i];
                JLabel textLabel = (JLabel) btn.getComponent(1);
                int count = civilization.getArmy()[i].size();
                textLabel.setText(unitNames[i] + " (" + count + ")");
            }
        }
    }

    // ==================== THREAT FRAME ====================
    class ThreatFrame extends JFrame {
        ThreatFrame(ArrayList<MilitaryUnit> enemyArmy) {
            setTitle("Amenaza Enemiga");
            setSize(500, 250);
            setLocationRelativeTo(CivilizationsGUI.this);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new FlowLayout());
            getContentPane().setBackground(Color.BLACK);

            int[] counts = new int[4];
            for (MilitaryUnit u : enemyArmy) {
                if (u instanceof Swordsman) counts[0]++;
                else if (u instanceof Spearman) counts[1]++;
                else if (u instanceof Crossbow) counts[2]++;
                else if (u instanceof Cannon) counts[3]++;
            }
            String[] names = {"Espadachín", "Lancero", "Ballesta", "Cañón"};
            String[] icons = {"swordsman.png", "spearman.png", "crossbow.png", "cannon.png"};
            for (int i = 0; i < 4; i++) {
                if (counts[i] > 0) {
                    JPanel panel = new JPanel();
                    panel.setBackground(Color.DARK_GRAY);
                    panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    panel.add(new JLabel(loadIcon("./M3-Programacion/GUI/images/" + icons[i], 64, 64)));
                    JLabel text = new JLabel(names[i] + " x" + counts[i]);
                    text.setForeground(Color.WHITE);
                    panel.add(text);
                    add(panel);
                }
            }
            setVisible(true);
        }
    }

    // ==================== UTILIDADES ====================
    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return new ImageIcon();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CivilizationsGUI());
    }
}