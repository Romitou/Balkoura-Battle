package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.enumeration.MatchState;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipantMatchStatusTask extends BukkitRunnable {

    private final OfflinePlayer player;

    public ParticipantMatchStatusTask(OfflinePlayer player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!BattleHandler.players.containsValue(player.getName())) {
            ChatUtils.sendMessage(player, "Vous n'êtes pas enregistré à ce tournois.");
            return;
        }
        List<String> stringList = new LinkedList<>();
        stringList.add("   §f§l» §eVos prochains matchs :");
        try {
            Participant participant = ChallongeManager.getChallonge().getParticipant(
                    ChallongeManager.getTournament(),
                    BattleHandler.players.inverse().get(player.getName())
            );
            List<Match> matches = ChallongeManager.getChallonge()
                    .getMatches(ChallongeManager.getTournament())
                    .stream()
                    .filter(m -> m.getPlayer1Id().equals(participant.getId())
                            || m.getPlayer2Id().equals(participant.getId()))
                    .collect(Collectors.toList());
            if (matches.size() == 0) {
                stringList.add("");
                stringList.add("   §fAucun match prévu.");
            } else {
                matches.forEach(m -> {
                    OfflinePlayer[] players = MatchUtils.getPlayers(m);
                    Integer[] scores = MatchUtils.getScores(m);
                    stringList.add("");
                    stringList.add("   §e● §fMatch " + m.getId() + " :");
                    stringList.add("      §e› §7Manche : " + m.getRound());
                    stringList.add("      §e› §7Status : " + (
                            (m.getState() == MatchState.COMPLETE)
                                    ? "§aTerminé"
                                    : (m.getState() == MatchState.OPEN || m.getState() == MatchState.PENDING)
                                    ? (m.getUnderwayAt() == null)
                                    ? "§eEn attente"
                                    : "§6En cours"
                                    : "§3Attente d'informations"
                    ));
                    stringList.add("      §e› §7Joueur 1 : " + players[0].getName() + " - " + scores[0] + " set" + (scores[0] > 1 ? "s" : "") + " gagné" + (scores[0] > 1 ? "s" : ""));
                    stringList.add("      §e› §7Joueur 2 : " + players[1].getName() + " - " + scores[1] + " set" + (scores[0] > 1 ? "s" : "") + " gagné" + (scores[0] > 1 ? "s" : ""));

                });
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
