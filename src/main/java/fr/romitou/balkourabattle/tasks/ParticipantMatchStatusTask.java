package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.enumeration.MatchState;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchScore;
import fr.romitou.balkourabattle.utils.ParticipantUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;

public class ParticipantMatchStatusTask extends BukkitRunnable {

    private final Player player;

    public ParticipantMatchStatusTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!BattleHandler.containsName(player.getName())) {
            ChatUtils.sendMessage(player, "Vous n'êtes pas enregistré à ce tournois.");
            return;
        }
        List<String> stringList = new LinkedList<>();
        stringList.add("   §f§l» §eVotre prochain match :");
        try {
            Participant participant = ChallongeManager.getChallonge().getParticipant(
                    ChallongeManager.getTournament(),
                    BattleHandler.getParticipant(player.getName()).getId()
            );
            Match match = ParticipantUtils.getCurrentMatch(participant);
            if (match == null) {
                stringList.add("");
                stringList.add("   §fAucun match prévu.");
            } else {
                MatchScore scores = new MatchScore(match.getScoresCsv());
                stringList.add("");
                stringList.add("   §e● §fMatch " + match.getIdentifier() + " :");
                stringList.add("      §e› §7Set : " + BattleHandler.getRound(match.getId()));
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
            }
            ChatUtils.sendBeautifulMessage(
                    player,
                    stringList.toArray(new String[0])
            );
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
    }
}
