package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.model.Match;
import fr.romitou.balkourabattle.BalkouraBattle;
import fr.romitou.balkourabattle.BattleManager;
import fr.romitou.balkourabattle.elements.Arena;
import fr.romitou.balkourabattle.elements.ArenaStatus;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class MatchStartingTask extends BukkitRunnable {

    private final Match match;
    private final Arena arena;
    private final BalkouraBattle instance = BalkouraBattle.getInstance();

    public MatchStartingTask(Match match, Arena arena) {
        this.match = match;
        this.arena = arena;
    }

    @Override
    public void run() {

        BattleManager.arenas.remove(arena);
        arena.setArenaStatus(ArenaStatus.BUSY);
        BattleManager.arenas.put(arena, match);

        // Fetch players of this match.
        List<OfflinePlayer> offlinePlayers = BattleManager.getPlayers(match);

        // Run a sync tasks as Bukkit isn't async safe.
        new MatchTeleportingTask(match, arena).runTask(instance);
        new MatchTimerTask(match, 30).runTaskTimer(instance, 0, 20);

        // Run an async task as this task can be asynchronous executed.
        new MatchMarkingAsUnderway(match).runTaskAsynchronously(instance);

        BattleManager.initPlayers(offlinePlayers);

    }

}
