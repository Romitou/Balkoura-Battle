package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.enumeration.MatchState;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchScore;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;

public class SendMatchInfoTask extends BukkitRunnable {

    private final Player player;
    private final Integer matchId;

    public SendMatchInfoTask(Player player, Integer matchId) {
        this.player = player;
        this.matchId = matchId;
    }

    @Override
    public void run() {
        Match match = null;
        try {
            match = ChallongeManager.getChallonge().getMatch(
                    ChallongeManager.getTournament(),
                    matchId
            );
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        if (match == null) {
            ChatUtils.sendMessage(player, "Match inconnu.");
            return;
        }
        List<String> stringList = new LinkedList<>();
        MatchScore scores = new MatchScore(match.getScoresCsv());
        stringList.add("   §f§l» §eInformations du match " + match.getIdentifier() + " :");
        stringList.add("      §e› §7Set : " + match.getRound());
        stringList.add("      §e› §7Status : " + (
                (match.getState() == MatchState.COMPLETE)
                        ? "§aTerminé"
                        : (match.getState() == MatchState.OPEN
                        || match.getState() == MatchState.PENDING)
                        ? (match.getUnderwayAt() == null)
                        ? "§eEn attente"
                        : "§6En cours"
                        : "§3Attente d'informations"
        ));
        long player1wins = scores.getWinSets(true);
        long player2wins = scores.getWinSets(false);
        stringList.add("      §e› §7Joueur 1 : " + BattleHandler.getName(match.getPlayer1Id()) + " - " + player1wins + " set" + (player1wins > 1 ? "s" : "") + " gagné" + (player1wins > 1 ? "s" : ""));
        stringList.add("      §e› §7Joueur 2 : " + BattleHandler.getName(match.getPlayer2Id()) + " - " + player2wins + " set" + (player2wins > 1 ? "s" : "") + " gagné" + (player2wins > 1 ? "s" : ""));
        ChatUtils.sendBeautifulMessage(player, stringList.toArray(new String[0]));
    }
}
