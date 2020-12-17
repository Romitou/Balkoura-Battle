package fr.romitou.balkourabattle;

import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import fr.romitou.balkourabattle.elements.Arena;
import fr.romitou.balkourabattle.elements.MatchScore;
import fr.romitou.balkourabattle.tasks.MatchScoreUpdatingTask;
import fr.romitou.balkourabattle.tasks.MatchStartingTask;
import fr.romitou.balkourabattle.tasks.MatchStoppingTask;
import org.bukkit.entity.Player;

public class BattleHandler {

    public static void handleDeath(Player player) {
        if (!BattleManager.containsName(player.getName())) return;
        Participant participant = BattleManager.getParticipant(player);
        if (participant == null) return;
        Match match = BattleManager.getCurrentMatchByPlayerId(participant.getId());
        if (match == null) {
            // TODO: alert
            return;
        }
        BattleManager.stopTimer(match);
        MatchScore matchScore = new MatchScore(match.getScoresCsv());

        // Update scores for the current set and the match's scores.
        matchScore.setWinnerSet(matchScore.getCurrentRound(), !(match.getPlayer1Id().equals(participant.getId())));
        matchScore.addRound();
        match.setScoresCsv(matchScore.getScoreCsv());

        System.out.println(matchScore.getCurrentRound() >= 2);
        System.out.println(matchScore.getWinSets(true));
        System.out.println(matchScore.getWinSets(false));
        if ((matchScore.getCurrentRound() >= 2
                && matchScore.getWinSets(true) != matchScore.getWinSets(false))
                || matchScore.getCurrentRound() >= 3) {
            new MatchStoppingTask(match).runTaskAsynchronously(BalkouraBattle.getInstance());
            return;
        }

        // Send them to Challonge for a live update.
        new MatchScoreUpdatingTask(match, false).runTaskAsynchronously(BalkouraBattle.getInstance());

        Arena arena = BattleManager.getArenaByMatchId(match.getId());
        if (arena == null) {
            // TODO
            return;
        }

        // Renewing the match as the score requirements are not meet.
        new MatchStartingTask(match, arena).runTaskAsynchronously(BalkouraBattle.getInstance());
    }

}
