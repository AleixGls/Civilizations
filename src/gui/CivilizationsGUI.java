package gui;

import civilizations.*;
import bbdd.Database;
import bbdd.GameSaver;
import bbdd.GlobalContext;
import bbdd.SaveData;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CivilizationsGUI extends JFrame implements Variables {
    private Civilization civilization;
    private ArrayList<MilitaryUnit> currentEnemyArmy;
    private boolean enemyPending;
    private Timer gameTimer;
    private Timer countdownTimer;
    private TimerTask countdownTask;
    private List<Battle> battleHistory;
    private LeftPanel leftPanel;
    private MiddlePanel middlePanel;
    private RightPanel rightPanel;
    private BottomPanel bottomPanel;

    // Constructor para nueva partida o carga desde StartScreen
    public CivilizationsGUI(Civilization loadedCiv, List<Battle> loadedHistory) {
        if (loadedCiv != null || loadedHistory != null) {
            this.civilization = loadedCiv;
            this.battleHistory = loadedHistory;
            this.enemyPending = false;
            this.currentEnemyArmy = null;
        } else {
            this.civilization = new Civilization();
            this.battleHistory = new ArrayList<>();
            this.civilization.setFood(500000);
            this.civilization.setWood(500000);
            this.civilization.setIron(500000);
            this.civilization.setMana(500000);
            this.enemyPending = false;
            this.currentEnemyArmy = null;
        }
        this.gameTimer = new Timer();
        initUI();
        startTimers();
        setTitle("Civilizations - Hola, usuario nº "+GlobalContext.civilization_id);
        setSize(1400, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        refreshAll();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);
        leftPanel = new LeftPanel(this);
        middlePanel = new MiddlePanel(this);
        rightPanel = new RightPanel(this);
        bottomPanel = new BottomPanel(this);
        add(leftPanel, BorderLayout.WEST);
        add(middlePanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void startTimers() {
        // Generación de recursos cada 60 segundos
        TimerTask resourceTask = new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> generateResources());
            }
        };
        gameTimer.schedule(resourceTask, 60000, 60000);

        // Creación de enemigo cada 180 segundos (3 minutos)
        TimerTask enemyTask = new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (!enemyPending) {
                        currentEnemyArmy = createEnemyArmy();
                        enemyPending = true;
                        appendLog("*** ¡Nueva amenaza enemiga! Usa 'Ver Amenaza' para detalles. ***\n");
                        refreshThreatIndicator(true);
                        refreshAll();
                        startCountdown();
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
        int[] probs = {35, 25, 20, 20};
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

    public void forceEnemy() {
        if (!enemyPending) {
            currentEnemyArmy = createEnemyArmy();
            enemyPending = true;
            appendLog("*** ¡Has buscado batalla! Amenaza enemiga detectada. ***\n");
            refreshThreatIndicator(true);
            refreshAll();
            startCountdown();
        } else {
            appendLog("Ya hay una amenaza activa. No puedes buscar otra.\n");
            JOptionPane.showMessageDialog(this, "Ya hay una amenaza enemiga activa.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void startCountdown() {
        cancelCountdown();
        final int[] timeLeft = {10};
        countdownTask = new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (enemyPending) {
                        middlePanel.setCountdown(timeLeft[0]);
                        if (timeLeft[0] <= 0) {
                            if (enemyPending && currentEnemyArmy != null) {
                                startBattle();
                            }
                            cancelCountdown();
                        } else {
                            timeLeft[0]--;
                        }
                    } else {
                        cancelCountdown();
                    }
                });
            }
        };
        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(countdownTask, 0, 1000);
    }

    private void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer.purge();
            countdownTimer = null;
        }
        middlePanel.clearCountdown();
    }

    public void viewThreat() {
        if (!enemyPending || currentEnemyArmy == null) {
            JOptionPane.showMessageDialog(this, "No hay ninguna amenaza enemiga en este momento.", "Amenaza", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new ThreatFrame(currentEnemyArmy);
    }

    public void startBattle() {
        cancelCountdown();
        if (!enemyPending || currentEnemyArmy == null) {
            JOptionPane.showMessageDialog(this, "No hay enemigo para batallar. Espera a que llegue uno.", "Batalla", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // Verificar si el jugador tiene unidades
        int totalUnits = 0;
        for (ArrayList<MilitaryUnit> list : civilization.getArmy()) {
            totalUnits += list.size();
        }
        if (totalUnits == 0) {
            JOptionPane.showMessageDialog(this, "No tienes ninguna unidad para combatir. Construye algunas primero.", "Sin ejército", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Battle battle = new Battle(civilization.getArmy(), currentEnemyArmy);
        battle.setCivilization(civilization);
        battle.startBattle();

        // Aplicar pérdidas de recursos
        int foodLoss = battle.getResourcesLooses()[0][0];
        int woodLoss = battle.getResourcesLooses()[0][1];
        int ironLoss = battle.getResourcesLooses()[0][2];
        civilization.setFood(Math.max(0, civilization.getFood() - foodLoss));
        civilization.setWood(Math.max(0, civilization.getWood() - woodLoss));
        civilization.setIron(Math.max(0, civilization.getIron() - ironLoss));
        appendLog(String.format("Pérdidas tras la batalla: -%d comida, -%d madera, -%d hierro\n", foodLoss, woodLoss, ironLoss));

        civilization.setBattles(civilization.getBattles() + 1);
        battleHistory.add(battle);

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

    public void showBattleReports() {
        if (battleHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay batallas registradas.", "Informes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new BattleReportsFrame(battleHistory);
    }

    public void saveGame() {
        SaveData data = new SaveData(civilization, battleHistory);
        try {
            GameSaver gameSaver = new GameSaver(GlobalContext.database);
            gameSaver.saveGame(civilization, battleHistory);
            setTitle("Civilizations - Hola, usuario nº "+GlobalContext.civilization_id);
            appendLog("Partida guardada en base de datos.\n");
            JOptionPane.showMessageDialog(this, "Partida guardada", "Guardado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar en base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshAll() {
        leftPanel.refresh();
        middlePanel.refresh();
        rightPanel.refresh();
        bottomPanel.refresh();
    }

    private void refreshThreatIndicator(boolean threatActive) {
        middlePanel.setThreatActive(threatActive);
        if (!threatActive) cancelCountdown();
    }

    public void appendLog(String msg) {
        middlePanel.appendLog(msg);
    }

    public Civilization getCivilization() {
        return civilization;
    }

    public int getManaCost(int unitType) {
        switch (unitType) {
            case 7: return MANA_COST_MAGICIAN;
            case 8: return MANA_COST_PRIEST;
            default: return 0;
        }
    }

    private String getImagePath(String fileName) {
        return "./src/gui/images/" + fileName;
    }

    public ImageIcon loadIcon(String fileName, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(getImagePath(fileName));
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return new ImageIcon();
        }
    }

    // Punto de entrada: muestra la pantalla de inicio (StartScreen)
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new StartScreen());
    }
}