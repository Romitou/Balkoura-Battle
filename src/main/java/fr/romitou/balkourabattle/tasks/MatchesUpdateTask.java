package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.enumeration.MatchState;
import fr.romitou.balkourabattle.BattleManager;
import fr.romitou.balkourabattle.elements.Arena;
import fr.romitou.balkourabattle.elements.ArenaStatus;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class MatchesUpdateTask extends BukkitRunnable {

    // Get all matches which are open and assignable to an arena.
    @Override
    public void run() {
        List<Arena> arenas = BattleManager.getAvailableArenas();
        if (arenas.size() == 0) return;
        List<Match> matches = BattleManager.arenas.values()
                .stream()
                .filter(match -> match.getUnderwayAt() == null
                        && match.getState() == MatchState.OPEN
                        && !BattleManager.arenas.containsValue(match))
                .collect(Collectors.toList());

        // Stop the assignation when there's no arenas or matches left.
        for (int i = 0; i < arenas.size() && i < matches.size(); i++) {
            Arena arena = arenas.get(i);
            Match match = matches.get(i);

            // Modify the status of the arena.
            BattleManager.arenas.remove(arena);
            arena.setArenaStatus(ArenaStatus.VALIDATING);
            BattleManager.arenas.put(arena, match);

            // Remove this match from the waiting list.
            BattleManager.waitingMatches.remove(match);
        }
    }

}
