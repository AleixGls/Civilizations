package gui;

import civilizations.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CivilizationGUI extends JFrame implements civilizations.Variables {
    private Civilization civilization;
    private ArrayList<MilitaryUnit> currentEnemyArmy;
    private boolean enemyPending;
    private java.util.Timer gameTimer;
    private JTextArea textArea;
    private JLabel resourcesLabel;
    private JLabel techLabel;
    private JLabel buildingsLabel;
    private JLabel armyLabel;
    private JLabel enemyLabel;

    public CivilizationGUI() {
        civilization = new Civilization();
        currentEnemyArmy = null;
        enemyPending = false;
        gameTimer = new java.util.Timer();

        // Recursos iniciales para pruebas
        civilization.setFood(50000);
        civilization.setWood(50000);
        civilization.setIron(50000);
        civilization.setMana(0);

        initUI();
        startTimers();

        setTitle("Civilizations - Gestión de tu Civilización");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        refreshAll();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Panel superior con información resumida
        JPanel topPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Información General"));
        resourcesLabel = new JLabel("Recursos: ");
        techLabel = new JLabel("Tecnología: ");
        buildingsLabel = new JLabel("Edificios: ");
        armyLabel = new JLabel("Ejército: ");
        enemyLabel = new JLabel("Amenaza: Ninguna");
        topPanel.add(resourcesLabel);
        topPanel.add(techLabel);
        topPanel.add(buildingsLabel);
        topPanel.add(armyLabel);
        topPanel.add(enemyLabel);
        add(topPanel, BorderLayout.NORTH);

        // Panel central con área de texto (log y estado detallado)
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Eventos y Estado Detallado"));
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton buildButton = new JButton("Construir Edificio");
        JButton techButton = new JButton("Mejorar Tecnología");
        JButton unitButton = new JButton("Crear Unidades");
        JButton threatButton = new JButton("Ver Amenaza");
        JButton battleButton = new JButton("¡LIBAR BATALLA!");
        JButton reportsButton = new JButton("Informes de Batalla");
        JButton refreshButton = new JButton("Actualizar Estado");
        JButton exitButton = new JButton("Salir");

        buildButton.addActionListener(e -> showBuildDialog());
        techButton.addActionListener(e -> showTechDialog());
        unitButton.addActionListener(e -> showUnitDialog());
        threatButton.addActionListener(e -> showThreat());
        battleButton.addActionListener(e -> startBattle());
        reportsButton.addActionListener(e -> showBattleReports());
        refreshButton.addActionListener(e -> refreshAll());
        exitButton.addActionListener(e -> {
            gameTimer.cancel();
            System.exit(0);
        });

        buttonPanel.add(buildButton);
        buttonPanel.add(techButton);
        buttonPanel.add(unitButton);
        buttonPanel.add(threatButton);
        buttonPanel.add(battleButton);
        buttonPanel.add(reportsButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void startTimers() {
        // Generación de recursos cada minuto
        TimerTask resourceTask = new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> generateResources());
            }
        };
        gameTimer.schedule(resourceTask, 60000, 60000);

        // Creación de enemigo cada 3 minutos
        TimerTask enemyTask = new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (!enemyPending) {
                        currentEnemyArmy = createEnemyArmy();
                        enemyPending = true;
                        appendLog("*** ¡Nueva amenaza enemiga! ***\n");
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

    private void showBuildDialog() {
        String[] options = {"Granja", "Carpintería", "Herrería", "Torre Mágica", "Iglesia"};
        int choice = JOptionPane.showOptionDialog(this, "¿Qué edificio deseas construir?", "Construir Edificio",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice < 0) return;
        try {
            switch (choice) {
                case 0: civilization.newFarm(); break;
                case 1: civilization.newCarpentry(); break;
                case 2: civilization.newSmithy(); break;
                case 3: civilization.newMagicTower(); break;
                case 4: civilization.newChurch(); break;
            }
            appendLog("Edificio construido: " + options[choice] + "\n");
            refreshAll();
        } catch (ResourceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de recursos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTechDialog() {
        String[] options = {"Mejorar Defensa", "Mejorar Ataque"};
        int choice = JOptionPane.showOptionDialog(this, "¿Qué tecnología mejorar?", "Mejorar Tecnología",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice < 0) return;
        try {
            if (choice == 0) civilization.upgradeTechnologyDefense();
            else civilization.upgradeTechnologyAttack();
            appendLog("Tecnología mejorada: " + options[choice] + "\n");
            refreshAll();
        } catch (ResourceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de recursos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUnitDialog() {
        String[] types = {"Espadachín", "Lancero", "Ballesta", "Cañón", "Torre flecha", "Catapulta", "Lanzacohetes", "Mago", "Sacerdote"};
        int type = JOptionPane.showOptionDialog(this, "Selecciona el tipo de unidad:", "Crear Unidades",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
        if (type < 0) return;
        String cantidadStr = JOptionPane.showInputDialog(this, "¿Cuántas unidades deseas crear?");
        if (cantidadStr == null) return;
        int n;
        try {
            n = Integer.parseInt(cantidadStr);
            if (n <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
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
            appendLog("Creadas " + n + " unidades de tipo " + types[type] + "\n");
            refreshAll();
        } catch (ResourceException | BuildingException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            refreshAll(); // Para mostrar las que se pudieron crear (el método ya añade las posibles)
        }
    }

    private void showThreat() {
        if (!enemyPending || currentEnemyArmy == null) {
            JOptionPane.showMessageDialog(this, "No hay ninguna amenaza enemiga en este momento.", "Amenaza", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int[] counts = new int[4];
        for (MilitaryUnit u : currentEnemyArmy) {
            if (u instanceof Swordsman) counts[0]++;
            else if (u instanceof Spearman) counts[1]++;
            else if (u instanceof Crossbow) counts[2]++;
            else if (u instanceof Cannon) counts[3]++;
        }
        String message = String.format("Ejército enemigo:\nEspadachines: %d\nLanceros: %d\nBallestas: %d\nCañones: %d",
                counts[0], counts[1], counts[2], counts[3]);
        JOptionPane.showMessageDialog(this, message, "Amenaza Enemiga", JOptionPane.WARNING_MESSAGE);
    }

    private void startBattle() {
        if (!enemyPending || currentEnemyArmy == null) {
            JOptionPane.showMessageDialog(this, "No hay enemigo para batallar. Espera a que llegue uno.", "Batalla", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Battle battle = new Battle(civilization.getArmy(), currentEnemyArmy);
        battle.startBattle();
        civilization.setBattles(civilization.getBattles() + 1);
        // Actualizar recursos (pérdidas y residuos)
        if (battle.isCivilizationWinner()) {
            int[] waste = battle.getWaste();
            civilization.setWood(civilization.getWood() + waste[0]);
            civilization.setIron(civilization.getIron() + waste[1]);
            appendLog("¡Victoria! Has obtenido " + waste[0] + " madera y " + waste[1] + " hierro de escombros.\n");
        } else {
            appendLog("Derrota... La civilización ha sufrido pérdidas.\n");
        }
        // Guardar batalla en historial (lo haríamos en una lista, pero no está implementada en Civilization)
        // Por simplicidad, no guardamos historial aquí, pero se podría añadir.
        currentEnemyArmy = null;
        enemyPending = false;
        appendLog("Batalla finalizada.\n");
        refreshAll();
    }

    private void showBattleReports() {
        JOptionPane.showMessageDialog(this, "Funcionalidad de informes de batalla pendiente de implementación completa.\n"
                + "Cada batalla se puede almacenar y mostrar aquí.", "Informes", JOptionPane.INFORMATION_MESSAGE);
        // Aquí se podría mostrar un diálogo con la lista de batallas y el detalle.
    }

    private void refreshAll() {
        updateSummaryLabels();
        displayFullState();
    }

    private void updateSummaryLabels() {
        resourcesLabel.setText(String.format("Recursos: 🍗%d  🪵%d  ⚙️%d  ✨%d",
                civilization.getFood(), civilization.getWood(), civilization.getIron(), civilization.getMana()));
        techLabel.setText(String.format("Tecnología: Defensa %d  Ataque %d",
                civilization.getTechnologyDefense(), civilization.getTechnologyAttack()));
        buildingsLabel.setText(String.format("Edificios: 🏠%d  🪚%d  ⚒️%d  🔮%d  ⛪%d",
                civilization.getFarm(), civilization.getCarpentry(), civilization.getSmithy(),
                civilization.getMagicTower(), civilization.getChurch()));
        int totalUnits = 0;
        for (ArrayList<MilitaryUnit> list : civilization.getArmy()) totalUnits += list.size();
        armyLabel.setText("Ejército: " + totalUnits + " unidades");
        enemyLabel.setText(enemyPending ? "⚠️ Amenaza presente" : "✅ Sin amenaza");
    }

    private void displayFullState() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADO COMPLETO DE LA CIVILIZACIÓN ===\n\n");
        sb.append("RECURSOS:\n");
        sb.append("  Comida: ").append(civilization.getFood()).append("\n");
        sb.append("  Madera: ").append(civilization.getWood()).append("\n");
        sb.append("  Hierro: ").append(civilization.getIron()).append("\n");
        sb.append("  Maná:   ").append(civilization.getMana()).append("\n\n");
        sb.append("TECNOLOGÍAS:\n");
        sb.append("  Defensa: ").append(civilization.getTechnologyDefense()).append("\n");
        sb.append("  Ataque:  ").append(civilization.getTechnologyAttack()).append("\n\n");
        sb.append("EDIFICIOS:\n");
        sb.append("  Granjas: ").append(civilization.getFarm()).append("\n");
        sb.append("  Carpinterías: ").append(civilization.getCarpentry()).append("\n");
        sb.append("  Herreries: ").append(civilization.getSmithy()).append("\n");
        sb.append("  Torres Mágicas: ").append(civilization.getMagicTower()).append("\n");
        sb.append("  Iglesias: ").append(civilization.getChurch()).append("\n\n");
        sb.append("EJÉRCITO:\n");
        String[] names = {"Espadachines", "Lanceros", "Ballestas", "Cañones", "Torres flecha", "Catapultas", "Lanzacohetes", "Magos", "Sacerdotes"};
        for (int i = 0; i < names.length; i++) {
            sb.append("  ").append(names[i]).append(": ").append(civilization.getArmy()[i].size()).append("\n");
        }
        sb.append("\nBatallas libradas: ").append(civilization.getBattles()).append("\n");
        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);
    }

    private void appendLog(String msg) {
        textArea.append(msg);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CivilizationGUI());
    }
}