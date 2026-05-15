package bbdd;

import civilizations.Civilization;
import civilizations.Battle;
import java.io.Serializable;
import java.util.List;

public class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Civilization civilization;
    private List<Battle> battleHistory;

    public SaveData(Civilization civilization, List<Battle> battleHistory) {
        this.civilization = civilization;
        this.battleHistory = battleHistory;
    }

    public Civilization getCivilization() {
        return civilization;
    }

    public List<Battle> getBattleHistory() {
        return battleHistory;
    }
}