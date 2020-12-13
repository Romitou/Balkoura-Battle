package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.model.Match;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchScore;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchTimerTask extends BukkitRunnable {

    private final Match match;
    private final OfflinePlayer player1;
    private final OfflinePlayer player2;
    private int time;

    public MatchTimerTask(Match match, OfflinePlayer player1, OfflinePlayer player2, int time) {
        this.match = match;
        this.player1 = player1;
        this.player2 = player2;
        this.time = time;
    }

    @Override
    public void run() {
        if (time <= 0) {
            BattleHandler.applyDeathMatch(player1, player2);
            this.cancel();
        }
        if (player1.getPlayer() != null)
            player1.getPlayer().sendActionBar(ChatUtils.getFormattedMessage("§cDeath match §fdans " + time + " seconde" + (time > 1 ? "s" : "")) + ".");
        if (player2.getPlayer() != null)
            player2.getPlayer().sendActionBar(ChatUtils.getFormattedMessage("§cDeath match §fdans " + time + " seconde" + (time > 1 ? "s" : "")) + ".");
        time--;
    }

}
