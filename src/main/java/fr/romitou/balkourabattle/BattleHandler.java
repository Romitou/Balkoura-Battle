package fr.romitou.balkourabattle;

import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.enumeration.MatchState;
import fr.romitou.balkourabattle.tasks.MatchEndingTask;
import fr.romitou.balkourabattle.tasks.MatchScoreUpdatingTask;
import fr.romitou.balkourabattle.tasks.ParticipantTeleportingTask;
import fr.romitou.balkourabattle.tasks.UnmarkMatchAsUnderwayTask;
import fr.romitou.balkourabattle.utils.ArenaUtils;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchScore;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class BattleHandler {

    public static final Set<Participant> PARTICIPANTS = new HashSet<>();
    public static final HashMap<Integer, Long> ARENAS = new HashMap<>();
    public static Integer round = 0;

    public static Participant getParticipant(String name) {
        return PARTICIPANTS.stream()
                .filter(participant -> participant.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("deprecation")
    public static OfflinePlayer getPlayer(Long id) {
        Optional<Participant> player = PARTICIPANTS.stream()
                .filter(participant -> participant.getId().equals(id))
                .findFirst();
        if (!player.isPresent()) return null;
        OfflinePlayer cache = Bukkit.getOfflinePlayerIfCached(player.get().getName());
        if (cache != null) return cache;
        return Bukkit.getOfflinePlayer(player.get().getName());
    }

    public static String getName(Long id) {
        Optional<Participant> player = PARTICIPANTS.stream()
                .filter(participant -> participant.getId().equals(id))
                .findFirst();
        if (!player.isPresent()) return "Inconnu";
        return player.get().getName();
    }

    public static Boolean containsName(String name) {
        return PARTICIPANTS.stream().anyMatch(participant -> participant.getName().equals(name));
    }


    public static void handleDeath(Match match, OfflinePlayer player1, OfflinePlayer player2, OfflinePlayer attacker) {
        MatchScore matchScore = new MatchScore(match.getScoresCsv());
        matchScore.setWinnerSet(match.getRound(), Objects.equals(player1.getName(), attacker.getName()));
        new MatchScoreUpdatingTask(match, matchScore).runTaskAsynchronously(BalkouraBattle.getInstance());
        renewMatch(match, player1, player2);
    }

    /**
     * This method handle the disconnection of a player who is in a match.
     *
     * @param match        The JSONObject of the match.
     * @param disconnected The player who have disconnecting when fighting.
     * @param opponent     The opponent of the player who disconnecting.
     */
    public static void handleDisconnect(Match match, OfflinePlayer disconnected, OfflinePlayer opponent) {
        if (match.getUnderwayAt() == null) return;
        if (opponent.getPlayer() == null) return;
        ChatUtils.sendMessage(opponent.getPlayer(), "Votre adversaire s'est déconnecté. Il peut revenir jusqu'à la fin du match ou vous serez désigné comme vainqueur.");
    }

    /**
     * This method handle the (re)connection of a player who was before in a match.
     * It will allow to renew the match by teleporting each other.
     *
     * @param match   The JSONObject of the match.
     * @param player1 The first player of the match.
     * @param player2 The second player of the match.
     */
    public static void handleConnect(Match match, OfflinePlayer player1, OfflinePlayer player2) {
        if (match.getUnderwayAt() == null) return;

        if (player1.getPlayer() != null)
            ChatUtils.sendMessage(player1.getPlayer(), "Votre adversaire s'est reconnecté, au combat !");
        if (player2.getPlayer() != null)
            ChatUtils.sendMessage(player2.getPlayer(), "Vous avez été téléporté à votre arène. Le combat continue !");
        renewMatch(match, player1, player2);
    }

    /**
     * This method allow to reset, renew a match by teleporting the players in the default spawn locations.
     * It would be useful to restart a match equitably, but not the timer.
     *
     * @param match   The match.
     * @param player1 The first player of the match.
     * @param player2 The second player of the match.
     */
    public static void renewMatch(Match match, OfflinePlayer player1, OfflinePlayer player2) {
        Location[] locations = ArenaUtils.getArenaLocations(ArenaUtils.getArenaIdByMatchId(match.getId()));
        new ParticipantTeleportingTask(player1, locations[0]);
        new ParticipantTeleportingTask(player2, locations[1]);
    }

    /**
     * This method handle the end of a match.
     *
     * @param match The identifier of the match.
     */
    public static void handleEndMatch(Match match, int round) {
        MatchScore matchScore = new MatchScore(match.getScoresCsv());
        if (match.getRound() >= 2) {
            if (!(matchScore.getWinSets(true) == matchScore.getWinSets(false))) {
                new MatchEndingTask(match, matchScore).runTaskAsynchronously(BalkouraBattle.getInstance());
                return;
            }
        }
        new UnmarkMatchAsUnderwayTask(match).runTaskAsynchronously(BalkouraBattle.getInstance());
        matchRequest(match);
    }

    public static void matchRequest(Match match) {
        List<TextComponent> stringList = new LinkedList<>();
        stringList.add(new TextComponent("   §f§l» §eNouvelle demande de match :"));
        MatchScore scores = new MatchScore(match.getScoresCsv());
        stringList.add(new TextComponent(""));
        stringList.add(new TextComponent("   §e● §fMatch " + match.getIdentifier() + " :"));
        stringList.add(new TextComponent("      §e› §7Set : " + match.getRound()));
        stringList.add(new TextComponent("      §e› §7Status : " + (
                (match.getState() == MatchState.COMPLETE)
                        ? "§aTerminé"
                        : (match.getState() == MatchState.OPEN
                        || match.getState() == MatchState.PENDING)
                        ? (match.getUnderwayAt() == null)
                        ? "§eEn attente"
                        : "§6En cours"
                        : "§3Attente d'informations"
        )));
        TextComponent action = new TextComponent("§a[Arbitrer]");
        action.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "battle accept " + match.getId()));
        stringList.add(action);
        long player1wins = scores.getWinSets(true);
        long player2wins = scores.getWinSets(false);
        stringList.add(new TextComponent("      §e› §7Joueur 1 : " + BattleHandler.getName(match.getPlayer1Id()) + " - " + player1wins + " set" + (player1wins > 1 ? "s" : "") + " gagné" + (player1wins > 1 ? "s" : "")));
        stringList.add(new TextComponent("      §e› §7Joueur 2 : " + BattleHandler.getName(match.getPlayer2Id()) + " - " + player2wins + " set" + (player2wins > 1 ? "s" : "") + " gagné" + (player2wins > 1 ? "s" : "")));
        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(""))
                .forEach(player -> ChatUtils.sendBeautifulMessage(player, stringList.toArray(new TextComponent[0])));
    }

}
