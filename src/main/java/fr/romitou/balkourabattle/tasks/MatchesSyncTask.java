package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.enumeration.MatchState;
import fr.romitou.balkourabattle.BattleManager;
import fr.romitou.balkourabattle.ChallongeManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

public class MatchesSyncTask extends BukkitRunnable {

    // Get all future matches to sync them with local datas.
    @Override
    public void run() {
        try {
            BattleManager.waitingMatches.addAll(ChallongeManager.getChallonge()
                    .getMatches(ChallongeManager.getTournament())
                    .stream()
                    .filter(match -> match.getUnderwayAt() == null
                            && match.getState() != MatchState.COMPLETE
                            && !BattleManager.arenas.containsValue(match)
                            && !BattleManager.waitingMatches.contains(match))
                    .collect(Collectors.toList()));
        } catch (DataAccessException e) {
            e.printStackTrace();
            // Don't force retry this task as it's running periodically.
        }
    }

}
