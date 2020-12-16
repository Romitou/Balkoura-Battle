package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.model.Match;
import fr.romitou.balkourabattle.BattleManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class MatchTimerTask extends BukkitRunnable {

    private final Match match;
    private final List<OfflinePlayer> offlinePlayers;
    private int time;

    public MatchTimerTask(Match match, int time) {
        this.match = match;
        this.offlinePlayers = BattleManager.getPlayers(match);
        this.time = time;
    }

    @Override
    public void run() {
        if (time <= 0) {
            // TODO
            this.cancel();
        }
        offlinePlayers.stream()
                .filter(player -> player.getPlayer() != null)
                .forEach(player -> player.getPlayer().sendActionBar(ChatUtils.getFormattedMessage(
                        "§cDeath match §fdans " + time + " seconde" + (time > 1 ? "s" : "")) + "."));
        time--;
    }

}
