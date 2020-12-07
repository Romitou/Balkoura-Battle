package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.enumeration.MatchState;
import fr.romitou.balkourabattle.BalkouraBattle;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ArenaUtils;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class ChallongeSyncTask extends BukkitRunnable {

    private static final BalkouraBattle INSTANCE = BalkouraBattle.getInstance();

    @Override
    public void run() {
        if (BattleHandler.round < 1) return; // Don't continue if the tournament is not started.
        try {
            ChallongeManager.getChallonge().getMatches(ChallongeManager.getTournament()).forEach(match -> {

                if (!match.getRound().equals(BattleHandler.round))
                    return; // The round doesn't match with actual.
                if (match.getState() != MatchState.OPEN)
                    return; // The match is already started, pending or finished.
                if (match.getUnderwayAt() != null)
                    return; // The match is already started as it was marked as underway.
                if (ArenaUtils.getAvailableArenas().size() == 0)
                    return; // Don't continue if there is no arenas available.

                // Fetch players of this match.
                OfflinePlayer[] players = MatchUtils.getPlayers(match);
                assert players[0] != null && players[1] != null;

                // Fetch a random arena and get locations of it.
                int arena = ArenaUtils.getRandomAvailableArena();
                BattleHandler.arenas.put(arena, match.getId());
                Location[] locations = ArenaUtils.getArenaLocations(arena);

                // Run a sync tasks as Bukkit isn't async safe.
                new ParticipantTeleportingTask(players[0], locations[0]).runTask(INSTANCE);
                new ParticipantTeleportingTask(players[1], locations[1]).runTask(INSTANCE);
                new MatchTimerTask(match, players[0], players[1], 30).runTaskTimer(INSTANCE, 0, 20);

                // Run an async task as this task can be asynchronous executed.
                new MarkMatchAsUnderwayTask(match).runTaskAsynchronously(INSTANCE);
                try {
                    new ParticipantUpdateCurrentMatchTask(
                            ChallongeManager.getChallonge().getParticipant(ChallongeManager.getTournament(), match.getPlayer1Id()),
                            match
                    ).runTaskAsynchronously(INSTANCE);
                    new ParticipantUpdateCurrentMatchTask(
                            ChallongeManager.getChallonge().getParticipant(ChallongeManager.getTournament(), match.getPlayer2Id()),
                            match
                    ).runTaskAsynchronously(INSTANCE);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
    }

}
