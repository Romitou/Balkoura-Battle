package fr.romitou.balkourabattle;

import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.enumeration.MatchState;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.romitou.balkourabattle.elements.Arena;
import fr.romitou.balkourabattle.elements.ArenaStatus;
import fr.romitou.balkourabattle.elements.ArenaType;
import fr.romitou.balkourabattle.elements.MatchScore;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BattleManager {

    public static List<Match> waitingMatches = new ArrayList<>();
    public static BiMap<Arena, Match> arenas = HashBiMap.create();
    public static BiMap<Participant, OfflinePlayer> registeredParticipants = HashBiMap.create();
    public static int round;

    public static void registerArenasFromConfig() {
        ConfigurationSection config = BalkouraBattle.getConfigFile().getConfigurationSection("arenas");
        assert config != null;
        config.getKeys(false).forEach(key -> arenas.put(new Arena(
                config.getInt(key + ".id"),
                new Location[]{
                        config.getLocation(key + ".firstLocation"),
                        config.getLocation(key + ".secondLocation")
                },
                ArenaStatus.FREE,
                config.getBoolean(key + ".isFinalArena") ? ArenaType.FINAL : ArenaType.CLASSIC
        ), null));
    }

    public static List<Match> getApprovalWaitingMatchs() {
        return arenas.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getArenaStatus() == ArenaStatus.VALIDATING)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }


    public static Arena getArenaById(int arenaId) {
        return arenas.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getId() == arenaId)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static Arena getArenaByMatchId(long matchId) {
        return arenas.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getId().equals(matchId))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static Arena getCurrentArenaByPlayerId(long playerId) {
        return arenas.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getPlayer1Id().equals(playerId)
                        || entry.getValue().getPlayer2Id().equals(playerId))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static Match getCurrentMatchByPlayerId(long playerId) {
        return arenas.values()
                .stream()
                .filter(match -> match.getPlayer1Id().equals(playerId)
                        || match.getPlayer2Id().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    public static List<Arena> getAvailableArenas() {
        return arenas.keySet()
                .stream()
                .filter(match -> match.getArenaStatus() == ArenaStatus.FREE)
                .collect(Collectors.toList());
    }

    public static Arena getAvailableArena() {
        List<Arena> arenas = getAvailableArenas();
        if (arenas.size() == 0) return null;
        return arenas.get(0);
    }

    public static Boolean containsName(String name) {
        return registeredParticipants.keySet()
                .stream()
                .anyMatch(participant -> participant.getName().equals(name));
    }

    public static Match getMatch(long matchId) {
        return arenas.values()
                .stream()
                .filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElse(null);
    }

    public static OfflinePlayer getPlayer(String name) {
        return registeredParticipants.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getName().equals(name))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public static OfflinePlayer getPlayer(long id) {
        return registeredParticipants.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getId().equals(id))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public static List<OfflinePlayer> getPlayers(Match match) {
        return List.of(
                getPlayer(match.getPlayer1Id()),
                getPlayer(match.getPlayer2Id())
        );
    }

    public static String getDisplayName(OfflinePlayer player) {
        if (player.getPlayer() != null) return player.getPlayer().getName();
        return "Inconnu";
    }

    public static void initPlayers(List<OfflinePlayer> offlinePlayers) {
        offlinePlayers.stream()
                .filter(player -> player.getPlayer() != null)
                .forEach(player -> {
                    player.getPlayer().getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                    player.getPlayer().getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
                    player.getPlayer().updateInventory();
                    player.getPlayer().setHealth(10);
                    player.getPlayer().setFoodLevel(10);
                });
    }

    public static List<Player> getOnlineModerators() {
        return Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.hasPermission("modo.event"))
                .collect(Collectors.toList());
    }

    public static List<Player> getAvailablePlayers() {
        return Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.getGameMode() == GameMode.ADVENTURE)
                .collect(Collectors.toList());
    }

    public static void sendMatchInfo(OfflinePlayer offlinePlayer) {
        if (offlinePlayer.getPlayer() == null) return;
        Player player = offlinePlayer.getPlayer();
        if (!BattleManager.containsName(player.getName())) {
            ChatUtils.sendMessage(player, "Vous n'êtes pas enregistré à ce tournois.");
            return;
        }
        Participant participant = BattleManager.registeredParticipants.inverse().get(player);
        Match match = BattleManager.getCurrentMatchByPlayerId(participant.getId());
        matchInfo(match);
        ChatUtils.sendBeautifulMessage(player, matchInfo(match).toArray(new String[0]));
    }


    public static void sendMatchInfo(OfflinePlayer offlinePlayer, int matchId) {
        if (offlinePlayer.getPlayer() == null) return;
        Player player = offlinePlayer.getPlayer();
        Match match = BattleManager.getMatch(matchId);
        if (match == null) {
            ChatUtils.sendMessage(player, "Aucun match n'a été trouvé avec cet identifiant.");
        }
        ChatUtils.sendBeautifulMessage(player, matchInfo(match).toArray(new String[0]));
    }

    public static List<String> matchInfo(Match match) {
        List<String> stringList = new LinkedList<>();
        stringList.add("   §f§l» §eVotre prochain match :");
        if (match == null) {
            stringList.add("");
            stringList.add("   §fAucun match prévu.");
        } else {
            MatchScore scores = new MatchScore(match.getScoresCsv());
            stringList.add("");
            stringList.add("   §e● §fMatch " + match.getIdentifier() + " :");
            stringList.add("      §e› §7Set : " + scores.getRound()); // TODO
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
            List<OfflinePlayer> offlinePlayers = BattleManager.getPlayers(match);
            stringList.add("      §e› §7Joueur 1 : " + BattleManager.getDisplayName(offlinePlayers.get(0)) + " - "
                    + player1wins + " set" + (player1wins > 1 ? "s" : "") + " gagné" + (player1wins > 1 ? "s" : ""));
            stringList.add("      §e› §7Joueur 2 : " + BattleManager.getDisplayName(offlinePlayers.get(1)) + " - "
                    + player2wins + " set" + (player2wins > 1 ? "s" : "") + " gagné" + (player2wins > 1 ? "s" : ""));
        }
        return stringList;
    }
}
