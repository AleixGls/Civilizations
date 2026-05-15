// ==================== Main.java (consola con temporizadores) ====================
package civilizations;

import java.util.*;

public class Main implements Variables {
    private static Civilization civilization;
    private static List<Battle> battleHistory;
    private static Timer timer;
    private static ArrayList<MilitaryUnit> currentEnemyArmy;
    private static boolean enemyPending;

    public static void main(String[] args) {
        civilization = new Civilization();
        battleHistory = new ArrayList<>();
        timer = new Timer();
        currentEnemyArmy = null;
        enemyPending = false;
        // Recursos iniciales para pruebas (según documento, empezar con algo)
        civilization.setFood(50000);
        civilization.setWood(50000);
        civilization.setIron(50000);
        civilization.setMana(0);

        // Tarea de generación de recursos cada minuto
        TimerTask resourceTask = new TimerTask() {
            public void run() {
                generateResources();
            }
        };
        timer.schedule(resourceTask, 60000, 60000);

        // Tarea de creación de enemigo cada 3 minutos
        TimerTask enemyTask = new TimerTask() {
            public void run() {
                if (!enemyPending) {
                    currentEnemyArmy = createEnemyArmy();
                    enemyPending = true;
                    System.out.println("\n*** ¡Nueva amenaza enemiga! ***");
                    viewThreat();
                }
            }
        };
        timer.schedule(enemyTask, 180000, 180000);

        // Menú principal
        Scanner scanner = new Scanner(System.in);
        int option;
        do {
            System.out.println("\n=== CIVILIZATIONS ===");
            System.out.println("1. Ver estado");
            System.out.println("2. Construir edificio");
            System.out.println("3. Mejorar tecnología");
            System.out.println("4. Crear unidades");
            System.out.println("5. Ver amenaza enemiga");
            System.out.println("6. Libar batalla (si hay enemigo)");
            System.out.println("7. Ver informes de batalla");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1: civilization.printStats(); break;
                case 2: buildMenu(scanner); break;
                case 3: techMenu(scanner); break;
                case 4: unitMenu(scanner); break;
                case 5: if (enemyPending) viewThreat(); else System.out.println("No hay amenaza enemiga."); break;
                case 6: if (enemyPending) startBattle(); else System.out.println("No hay enemigo atacando."); break;
                case 7: viewBattleReports(scanner); break;
                case 0: System.out.println("Saliendo..."); timer.cancel(); break;
                default: System.out.println("Opción inválida");
            }
        } while (option != 0);
        scanner.close();
    }

    private static void generateResources() {
        int foodGen = CIVILIZATION_FOOD_GENERATED + civilization.getFarm() * CIVILIZATION_FOOD_GENERATED_PER_FARM;
        int woodGen = CIVILIZATION_WOOD_GENERATED + civilization.getCarpentry() * CIVILIZATION_WOOD_GENERATED_PER_CARPENTRY;
        int ironGen = CIVILIZATION_IRON_GENERATED + civilization.getSmithy() * CIVILIZATION_IRON_GENERATED_PER_SMITHY;
        int manaGen = civilization.getMagicTower() * CIVILIZATION_MANA_GENERATED_PER_MAGIC_TOWER;
        civilization.setFood(civilization.getFood() + foodGen);
        civilization.setWood(civilization.getWood() + woodGen);
        civilization.setIron(civilization.getIron() + ironGen);
        civilization.setMana(civilization.getMana() + manaGen);
        System.out.println("\n[Recursos generados] +Comida:" + foodGen + " +Madera:" + woodGen + " +Hierro:" + ironGen + " +Maná:" + manaGen);
    }

    private static ArrayList<MilitaryUnit> createEnemyArmy() {
        int battles = civilization.getBattles();
        int increment = battles * ENEMY_FLEET_INCREASE / 100;
        int availableIron = IRON_BASE_ENEMY_ARMY * (100 + increment) / 100;
        int availableWood = WOOD_BASE_ENEMY_ARMY * (100 + increment) / 100;
        int availableFood = FOOD_BASE_ENEMY_ARMY * (100 + increment) / 100;
        ArrayList<MilitaryUnit> army = new ArrayList<>();
        Random rand = new Random();
        int[] probs = {35, 25, 20, 20}; // Swordsman, Spearman, Crossbow, Cannon
        while (true) {
            int type = selectByProb(probs, rand);
            MilitaryUnit unit = null;
            int foodCost=0, woodCost=0, ironCost=0;
            switch (type) {
                case 0: unit = new Swordsman(); foodCost=FOOD_COST_SWORDSMAN; woodCost=WOOD_COST_SWORDSMAN; ironCost=IRON_COST_SWORDSMAN; break;
                case 1: unit = new Spearman(); foodCost=FOOD_COST_SPEARMAN; woodCost=WOOD_COST_SPEARMAN; ironCost=IRON_COST_SPEARMAN; break;
                case 2: unit = new Crossbow(); foodCost=FOOD_COST_CROSSBOW; woodCost=WOOD_COST_CROSSBOW; ironCost=IRON_COST_CROSSBOW; break;
                case 3: unit = new Cannon(); foodCost=FOOD_COST_CANNON; woodCost=WOOD_COST_CANNON; ironCost=IRON_COST_CANNON; break;
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

    private static int selectByProb(int[] probs, Random rand) {
        int total = 0;
        for (int p : probs) total += p;
        int r = rand.nextInt(total) + 1;
        int acum = 0;
        for (int i=0; i<probs.length; i++) {
            acum += probs[i];
            if (acum >= r) return i;
        }
        return 0;
    }

    private static void viewThreat() {
        if (currentEnemyArmy == null) return;
        int[] counts = new int[4];
        for (MilitaryUnit u : currentEnemyArmy) {
            if (u instanceof Swordsman) counts[0]++;
            else if (u instanceof Spearman) counts[1]++;
            else if (u instanceof Crossbow) counts[2]++;
            else if (u instanceof Cannon) counts[3]++;
        }
        System.out.println("NEW threat COMING");
        System.out.println("Swordsman " + counts[0]);
        System.out.println("Spearman " + counts[1]);
        System.out.println("Crossbow " + counts[2]);
        System.out.println("Cannon " + counts[3]);
    }

    private static void startBattle() {
        Battle battle = new Battle(civilization.getArmy(), currentEnemyArmy);
        battle.startBattle();
        battleHistory.add(battle);
        civilization.setBattles(civilization.getBattles() + 1);
        // Actualizar recursos: restar pérdidas y añadir residuos si ganó
        if (battle.isCivilizationWinner()) {
            int[] waste = battle.getWaste();
            civilization.setWood(civilization.getWood() + waste[0]);
            civilization.setIron(civilization.getIron() + waste[1]);
            System.out.println("¡Has ganado la batalla! Has obtenido " + waste[0] + " madera y " + waste[1] + " hierro de escombros.");
        } else {
            System.out.println("Has perdido la batalla.");
        }
        // Eliminar unidades muertas (ya lo hizo Battle)
        // Reemplazar el ejército de civilization con las unidades supervivientes
        // Nota: Battle modificó las listas internas, pero no actualiza directamente el array de civilization.
        // Para simplificar, asumimos que civilization.getArmy() se actualiza por referencia.
        // En una implementación completa habría que sincronizar.
        currentEnemyArmy = null;
        enemyPending = false;
        System.out.println("Batalla finalizada.");
    }

    private static void viewBattleReports(Scanner scanner) {
        if (battleHistory.isEmpty()) {
            System.out.println("No hay batallas registradas.");
            return;
        }
        System.out.println("Battle Reports");
        for (int i=0; i<battleHistory.size(); i++) {
            System.out.println((i+1) + ". Batalla " + (i+1));
        }
        System.out.print("Seleccione informe (0 para volver): ");
        int idx = scanner.nextInt();
        if (idx>=1 && idx<=battleHistory.size()) {
            Battle b = battleHistory.get(idx-1);
            System.out.println(b.getBattleReport());
            System.out.print("¿Ver desarrollo? (s/n): ");
            String opt = scanner.next();
            if (opt.equalsIgnoreCase("s")) {
                System.out.println(b.getBattleDevelopment());
            }
        }
    }

    private static void buildMenu(Scanner scanner) {
        System.out.println("Construir: 1.Granja 2.Carpintería 3.Herrería 4.Torre Mágica 5.Iglesia");
        int op = scanner.nextInt();
        try {
            switch(op) {
                case 1: civilization.newFarm(); break;
                case 2: civilization.newCarpentry(); break;
                case 3: civilization.newSmithy(); break;
                case 4: civilization.newMagicTower(); break;
                case 5: civilization.newChurch(); break;
                default: System.out.println("Opción inválida");
            }
            System.out.println("Edificio construido.");
        } catch (ResourceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void techMenu(Scanner scanner) {
        System.out.println("Mejorar: 1.Defensa 2.Ataque");
        int op = scanner.nextInt();
        try {
            if (op == 1) civilization.upgradeTechnologyDefense();
            else if (op == 2) civilization.upgradeTechnologyAttack();
            else System.out.println("Opción inválida");
            System.out.println("Tecnología mejorada.");
        } catch (ResourceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void unitMenu(Scanner scanner) {
        System.out.println("Crear unidades: 1.Espadachín 2.Lancero 3.Ballesta 4.Cañón 5.Torre flecha 6.Catapulta 7.Lanzacohetes 8.Mago 9.Sacerdote");
        int type = scanner.nextInt();
        System.out.print("Cantidad: ");
        int n = scanner.nextInt();
        try {
            switch(type) {
                case 1: civilization.newSwordsman(n); break;
                case 2: civilization.newSpearman(n); break;
                case 3: civilization.newCrossbow(n); break;
                case 4: civilization.newCannon(n); break;
                case 5: civilization.newArrowTower(n); break;
                case 6: civilization.newCatapult(n); break;
                case 7: civilization.newRocketLauncher(n); break;
                case 8: civilization.newMagician(n); break;
                case 9: civilization.newPriest(n); break;
                default: System.out.println("Tipo inválido");
            }
            System.out.println("Unidades añadidas.");
        } catch (ResourceException | BuildingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}