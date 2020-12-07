package fr.romitou.balkourabattle;

import at.stefangeyer.challonge.model.Match;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.romitou.balkourabattle.tasks.MatchEndingTask;
import fr.romitou.balkourabattle.tasks.MatchScoreUpdatingTask;
import fr.romitou.balkourabattle.tasks.ParticipantTeleportingTask;
import fr.romitou.balkourabattle.utils.ArenaUtils;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class BattleHandler {

    public static final BiMap<Long, String> players = HashBiMap.create();
    public static final HashMap<Integer, Long> arenas = new HashMap<>();
    public static Integer round = 0;

    @SuppressWarnings("deprecation")
    public static OfflinePlayer getPlayer(Long id) {
        if (!players.containsKey(id))
            return null;
        String player = players.get(id);
        OfflinePlayer cache = Bukkit.getOfflinePlayerIfCached(player);
        if (cache != null) return cache;
        return Bukkit.getOfflinePlayer(player);
    }

    public static void handleDeath(Match match, OfflinePlayer player1, OfflinePlayer player2, OfflinePlayer attacker) {
        Integer[] scores = MatchUtils.getScores(match);
        System.out.println("scores:" + Arrays.toString(scores));
        if (Objects.equals(player1.getName(), attacker.getName())) {
            scores[0]++;
        } else {
            scores[1]++;
        }
        new MatchScoreUpdatingTask(
                match,
                scores[0],
                scores[1]
        ).runTaskAsynchronously(BalkouraBattle.getInstance());
        System.out.println("death renew match");
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

        ChatUtils.sendMessage(opponent, "Votre adversaire s'est déconnecté. Il peut revenir jusqu'à la fin du match ou vous serez désigné comme vainqueur.");
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

        ChatUtils.sendMessage(player1, "Votre adversaire s'est reconnecté, au combat !");
        ChatUtils.sendMessage(player2, "Vous avez été téléporté à votre arène. Le combat continue !");
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
    public static void handleEndMatch(Match match) {
        Integer[] scores = MatchUtils.getScores(match);
        if (scores[0] > scores[1])
            new MatchEndingTask(match, match.getPlayer1Id()).runTaskAsynchronously(BalkouraBattle.getInstance());
        else
            new MatchEndingTask(match, match.getPlayer2Id()).runTaskAsynchronously(BalkouraBattle.getInstance());
    }
}
