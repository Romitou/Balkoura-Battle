package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.enumeration.MatchState;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ArenaUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChallongeSyncTask extends BukkitRunnable {

    @Override
    public void run() {
        System.out.println(ArenaUtils.getAvailableArenas());
        if (BattleHandler.round < 1 || ArenaUtils.getAvailableArenas().size() == 0) return; // Don't continue if the tournament is not started.
        try {
            System.out.println("Debug: " + Arrays.toString(ChallongeManager.getChallonge().getMatches(ChallongeManager.getTournament()).toArray()));
            List<Match> matchesToAccept = ChallongeManager.getChallonge()
                    .getMatches(ChallongeManager.getTournament())
                    .stream()
                    .filter(match -> (match.getState() == MatchState.OPEN) && (match.getUnderwayAt() == null))
                    .collect(Collectors.toList());
            if (matchesToAccept.size() != 0)
                BattleHandler.matchRequest(matchesToAccept);
            } catch (DataAccessException dataAccessException) {
                dataAccessException.printStackTrace();
        }
    }
}
