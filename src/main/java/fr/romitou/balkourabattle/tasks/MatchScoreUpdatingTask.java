package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.query.MatchQuery;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchScore;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchScoreUpdatingTask extends BukkitRunnable {

    private final Match match;
    private final MatchScore scores;

    public MatchScoreUpdatingTask(Match match, MatchScore scores) {
        this.match = match;
        this.scores = scores;
    }

    @Override
    public void run() {
        try {
            ChallongeManager.getChallonge().updateMatch(
                    match,
                    MatchQuery.builder().scoresCsv(scores.getScoreCsv(match.getRound())).build()
            );
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
    }
}
