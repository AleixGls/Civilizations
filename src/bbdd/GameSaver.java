package bbdd;

import civilizations.Civilization;
import civilizations.Battle;
import java.util.List;

public class GameSaver {

    private Database db;

    public GameSaver(Database db) {
        this.db = db;
    }

    public void saveGame(Civilization civilization, List<Battle> battleHistory) {
        SaveData data = new SaveData(civilization, battleHistory);
        saveCivilization(data.getCivilization());
        saveBattleHistory(data.getBattleHistory());
    }

    private void saveCivilization(Civilization civilization) {
        GlobalContext.civilizationStatsTable = new CivilizationStatsTable(db, civilization);

        if (GlobalContext.civilization_id == 0) {
            int generatedId = GlobalContext.civilizationStatsTable.insertRow();
            GlobalContext.civilization_id = generatedId;
            System.out.println("New civilization saved. ID: " + GlobalContext.civilization_id);
        } else {
            GlobalContext.civilizationStatsTable.updateAttributes();
            System.out.println("Civilization updated. ID: " + GlobalContext.civilization_id);
        }
    }

    private void saveBattleHistory(List<Battle> battleHistory) {
        if (battleHistory == null || battleHistory.isEmpty()) {
            System.out.println("No battle history to save.");
            return;
        }
        
        System.out.println("Saving battle history (" + battleHistory.size() + " battles)...");
        
        for (Battle battle : battleHistory) {
            saveBattle(battle);
        }
        
        System.out.println("Battle history saved successfully.");
    }

    private void saveBattle(Battle battle) {
        System.out.println("Saving battle #" + battle.getBattleNumber() + "...");

        GlobalContext.battleStatsTable = new BattleStatsTable(
            db, GlobalContext.civilization_id, battle
        );
        GlobalContext.battleStatsTable.insertRow();

        GlobalContext.battleLogTable = new BattleLogTable(
            db, GlobalContext.civilization_id, battle
        );
        GlobalContext.battleLogTable.insertRow();

        GlobalContext.civilizationArmyTable = new CivilizationArmyTable(
            db, GlobalContext.civilization_id, battle
        );
        GlobalContext.civilizationArmyTable.insertRow();

        GlobalContext.enemyArmyTable = new EnemyArmyTable(
            db, GlobalContext.civilization_id, battle
        );
        GlobalContext.enemyArmyTable.insertRow();

        System.out.println("Battle #" + battle.getBattleNumber() + " saved.");
    }
}