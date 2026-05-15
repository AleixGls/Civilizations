package bbdd;

public class GlobalContext {
    public static Database database;
    public static int civilization_id;
    public static int num_battle;

    public static CivilizationStatsTable civilizationStatsTable;
    public static BattleStatsTable battleStatsTable;
    public static BattleLogTable battleLogTable;
    public static CivilizationArmyTable civilizationArmyTable;
    public static EnemyArmyTable enemyArmyTable;

    private GlobalContext() {
        // utility class — no instances
    }
}