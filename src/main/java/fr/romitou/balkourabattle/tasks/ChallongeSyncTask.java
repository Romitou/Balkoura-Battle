package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.enumeration.MatchState;
import fr.romitou.balkourabattle.BalkouraBattle;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ArenaUtils;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class ChallongeSyncTask extends BukkitRunnable {

    @Override
    public void run() {
        if (BattleHandler.round < 1) return; // Don't continue if the tournament is not started.
        try {
            ChallongeManager.getChallonge().getMatches(ChallongeManager.getTournament()).forEach(match -> {

                if (!(match.getRound() == 1))
                    return; // The match is already handled as his round isn't default.
                if (match.getState() != MatchState.OPEN)
                    return; // The match is already started, pending or finished.
                if (match.getUnderwayAt() != null)
                    return; // The match is already started as it was marked as underway.
                if (ArenaUtils.getAvailableArenas().size() == 0)
                    return; // Don't continue if there is no arenas available.

                new MatchStartingTask(match).runTaskAsynchronously(BalkouraBattle.getInstance());
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
    }

}
