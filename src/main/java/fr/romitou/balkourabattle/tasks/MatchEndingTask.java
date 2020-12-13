package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.query.MatchQuery;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ArenaUtils;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchScore;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchEndingTask extends BukkitRunnable {

    private final Match match;
    private final MatchScore matchScore;

    public MatchEndingTask(Match match, MatchScore matchScore) {
        this.match = match;
        this.matchScore = matchScore;
    }

    @Override
    public void run() {
        try {
            MatchQuery matchQuery = MatchQuery.builder()
                    .winnerId(matchScore.getWinSets(true) > matchScore.getWinSets(false)
                            ? match.getPlayer1Id()
                            : match.getPlayer2Id()
                    ).scoresCsv(matchScore.getScoreCsv(BattleHandler.getRound(match.getId())))
                    .build();
            ChallongeManager.getChallonge().updateMatch(match, matchQuery);
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
    }
}
