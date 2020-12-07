package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.query.MatchQuery;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ArenaUtils;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchEndingTask extends BukkitRunnable {

    private final Match match;
    private final long winnerId;

    public MatchEndingTask(Match match, long winnerId) {
        this.match = match;
        this.winnerId = winnerId;
    }

    @Override
    public void run() {
        try {
            Integer[] scores = MatchUtils.getScores(match);
            MatchQuery matchQuery = MatchQuery.builder()
                    .winnerId(winnerId)
                    .scoresCsv(scores[0] + "-" + scores[1])
                    .build();
            ChallongeManager.getChallonge().updateMatch(match, matchQuery);
            BattleHandler.arenas.remove(ArenaUtils.getArenaIdByMatchId(match.getId()));
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
    }
}
