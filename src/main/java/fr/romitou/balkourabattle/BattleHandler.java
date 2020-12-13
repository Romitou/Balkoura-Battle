package fr.romitou.balkourabattle;

import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import fr.romitou.balkourabattle.tasks.*;
import fr.romitou.balkourabattle.utils.ArenaUtils;
import fr.romitou.balkourabattle.utils.BalkouraUtils;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchScore;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class BattleHandler {

    public static final List<Participant> PARTICIPANTS = new LinkedList<>();
    public static final HashMap<Integer, Long> ARENAS = new HashMap<>();
    public static final HashMap<Long, Integer> TASKS = new HashMap<>();
    public static final HashMap<Long, Integer> MATCHES_ROUND = new HashMap<>();
    public static final HashMap<Long, Long>
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
        if (player.isEmpty()) return null;
        OfflinePlayer cache = Bukkit.getOfflinePlayerIfCached(player.get().getName());
        if (cache != null) return cache;
        return Bukkit.getOfflinePlayer(player.get().getName());
    }

    public static String getName(Long id) {
        Optional<Participant> player = PARTICIPANTS.stream()
                .filter(participant -> participant.getId().equals(id))
                .findFirst();
        if (player.isEmpty()) return "Inconnu";
        return player.get().getName();
    }

    public static Boolean containsName(String name) {
        return PARTICIPANTS.stream().anyMatch(participant -> participant.getName().equals(name));
    }

    public static void setRound(long matchId, int round) {
        BattleHandler.MATCHES_ROUND.put(matchId, round);
    }

    public static int getRound(long matchId) {
        return Optional.ofNullable(BattleHandler.MATCHES_ROUND.get(matchId)).orElse(0);
    }

    public static void applyDeathMatch(OfflinePlayer player1, OfflinePlayer player2) {
        if (player1.getPlayer() != null)
            new PotionEffect(PotionEffectType.WITHER, 20, 1, true, false).apply(player1.getPlayer());
        if (player2.getPlayer() != null)
            new PotionEffect(PotionEffectType.WITHER, 20, 1, true, false).apply(player1.getPlayer());
    }

    public static void stopTimer(long matchId) {
        Integer taskId = TASKS.get(matchId);
        if (taskId == null) {
            ChatUtils.modAlert("Le chronomètre du match " + matchId + " n'a pas pu être arrêté.");
            return;
        }
        BukkitScheduler scheduler = Bukkit.getScheduler();
        if (scheduler.isCurrentlyRunning(taskId))
            scheduler.cancelTask(taskId);
    }

    public static void handleDeath(Match match, OfflinePlayer player1, OfflinePlayer player2, OfflinePlayer attacker) {
        System.out.println("Handle death of player of match: " + match.getId());
        MatchScore matchScore = new MatchScore(match.getScoresCsv());
        matchScore.setWinnerSet(BattleHandler.getRound(match.getId()), Objects.equals(player1.getName(), attacker.getName()));
        new MatchScoreUpdatingTask(match, matchScore).runTaskAsynchronously(BalkouraBattle.getInstance());
        match.setScoresCsv(matchScore.getScoreCsv(BattleHandler.getRound(match.getId())));
        handleEndMatch(match);
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
        System.out.println("Handle connection of player of match: " + match.getId());
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
        System.out.println("Renewing match: " + match.getId());
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
        System.out.println("Handle ending match: " + match.getId());
        BattleHandler.stopTimer(match.getId());
        BattleHandler.ARENAS.remove(ArenaUtils.getArenaIdByMatchId(match.getId()));
        MatchScore matchScore = new MatchScore(match.getScoresCsv());
        int round = BattleHandler.getRound(match.getId());
        if (round >= 2) {
            System.out.println("Check for definitely ending match.");
            if (!(matchScore.getWinSets(true) == matchScore.getWinSets(false))) {
                System.out.println("Stopping match...");
                new MatchEndingTask(match, matchScore).runTaskAsynchronously(BalkouraBattle.getInstance());
                return;
            }
            System.out.println("Nope.");
        }
        BattleHandler.setRound(match.getId(), round + 1);
        new MatchRenewingTask(match).runTaskAsynchronously(BalkouraBattle.getInstance());
    }

    public static void matchRequest(List<Match> match) {
        System.out.println("Handle match request for matches: " + match);
        List<TextComponent> textComponents = new LinkedList<>();
        textComponents.add(new TextComponent("   §f§l» §eMatchs en attente :"));
        textComponents.add(new TextComponent(""));
        match.forEach(m -> {
            TextComponent base = new TextComponent("   §e● §fMatch " + m.getIdentifier() + " §7| ");
            TextComponent info = new TextComponent("§6[+ d'infos] ");
            info.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/battle info " + m.getId()));
            base.addExtra(info);
            TextComponent accept = new TextComponent("§a[Accepter]");
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/battle accept " + m.getId()));
            base.addExtra(accept);
            textComponents.add(base);
        });
        BalkouraUtils.getOnlineModerators().forEach(player -> ChatUtils.sendBeautifulMessage(player, textComponents));
    }

    public static void initPlayer(OfflinePlayer player) {
        if (player.getPlayer() != null) {
            player.getPlayer().getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            player.getPlayer().getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
            player.getPlayer().updateInventory();
            player.getPlayer().setHealth(10);
            player.getPlayer().setFoodLevel(10);
        }
    }

}
