package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.model.Match;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchTimerTask extends BukkitRunnable {

    private final Match match;
    private final OfflinePlayer player1;
    private final OfflinePlayer player2;
    private final int round;
    private int time;

    public MatchTimerTask(Match match, OfflinePlayer player1, OfflinePlayer player2, int time) {
        this.match = match;
        this.player1 = player1;
        this.player2 = player2;
        this.time = time;
        this.round = match.getRound();
    }

    @Override
    public void run() {
        if (time <= 0) {
            BattleHandler.handleEndMatch(match, round);
            this.cancel();
        }
        if (player1.getPlayer() != null)
            player1.getPlayer().sendActionBar(ChatUtils.getFormattedMessage(time + " seconde" + (time > 1 ? "s" : "")));
        if (player2.getPlayer() != null)
            player2.getPlayer().sendActionBar(ChatUtils.getFormattedMessage(time + " seconde" + (time > 1 ? "s" : "")));
        time--;
    }

}
