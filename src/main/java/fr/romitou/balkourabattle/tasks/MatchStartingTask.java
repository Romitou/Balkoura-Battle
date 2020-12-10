package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import fr.romitou.balkourabattle.BalkouraBattle;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ArenaUtils;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchStartingTask extends BukkitRunnable {

    private final Match match;
    private final BalkouraBattle INSTANCE = BalkouraBattle.getInstance();

    public MatchStartingTask(Match match) {
        this.match = match;
    }

    @Override
    public void run() {
        // Fetch players of this match.
        OfflinePlayer player1 = BattleHandler.getPlayer(match.getPlayer1Id());
        OfflinePlayer player2 = BattleHandler.getPlayer(match.getPlayer2Id());
        assert player1 != null && player2 != null;

        // Fetch a random arena and get locations of it.
        int arena = ArenaUtils.getRandomAvailableArena();
        BattleHandler.ARENAS.put(arena, match.getId());
        Location[] locations = ArenaUtils.getArenaLocations(arena);

        // Run a sync tasks as Bukkit isn't async safe.
        new ParticipantTeleportingTask(player1, locations[0]).runTask(INSTANCE);
        new ParticipantTeleportingTask(player2, locations[1]).runTask(INSTANCE);
        if (match.getRound() == 3)
            new DeathMatchApplicationTask(player1, player2).runTask(INSTANCE);
        new MatchTimerTask(match, player1, player2, 30).runTaskTimer(INSTANCE, 0, 20);

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
            ChatUtils.modAlert(e.getMessage());
        }
    }
}
