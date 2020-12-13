package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.model.Match;
import fr.romitou.balkourabattle.BalkouraBattle;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.utils.ArenaUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchRenewingTask extends BukkitRunnable {

    private static final BalkouraBattle INSTANCE = BalkouraBattle.getInstance();
    private final Match match;

    public MatchRenewingTask(Match match) {
        this.match = match;
    }

    @Override
    public void run() {
        System.out.println("renewing match: " + match.getId());

        // Fetch players of this match.
        OfflinePlayer player1 = BattleHandler.getPlayer(match.getPlayer1Id());
        OfflinePlayer player2 = BattleHandler.getPlayer(match.getPlayer2Id());
        assert player1 != null && player2 != null;

        // Fetch a random arena and get locations of it.
        int arena = ArenaUtils.getArenaIdByMatchId(match.getId());
        Location[] locations = ArenaUtils.getArenaLocations(arena);

        // Run a sync tasks as Bukkit isn't async safe.
        new ParticipantTeleportingTask(player1, locations[0]).runTask(INSTANCE);
        new ParticipantTeleportingTask(player2, locations[1]).runTask(INSTANCE);
        new MatchTimerTask(match, player1, player2, 30).runTaskTimer(INSTANCE, 0, 20);

        BattleHandler.initPlayer(player1);
        BattleHandler.initPlayer(player2);
    }

}
