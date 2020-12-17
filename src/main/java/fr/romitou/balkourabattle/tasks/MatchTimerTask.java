package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.model.Match;
import fr.romitou.balkourabattle.BattleManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MatchTimerTask extends BukkitRunnable {

    private final Match match;
    private final List<OfflinePlayer> offlinePlayers;
    private int fightTime, idleTime;

    public MatchTimerTask(Match match, int fightTime) {
        this.match = match;
        this.offlinePlayers = BattleManager.getPlayers(match);
        this.fightTime = fightTime;
        this.idleTime = 10;
    }

    @Override
    public void run() {
        if (idleTime <= 0) {
            if (fightTime <= 0) {
                BattleManager.deathMatch(offlinePlayers
                        .stream()
                        .map(OfflinePlayer::getPlayer)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
                this.cancel();
            }
            offlinePlayers.stream()
                    .filter(player -> player.getPlayer() != null)
                    .forEach(player -> player.getPlayer().sendActionBar(ChatUtils.getFormattedMessage(
                            "§cDeath match §fdans " + fightTime + " seconde" + (fightTime > 1 ? "s" : "")) + ".")
                    );
            fightTime--;
        } else {
            offlinePlayers.stream()
                    .filter(player -> player.getPlayer() != null)
                    .forEach(player -> player.getPlayer().sendActionBar(ChatUtils.getFormattedMessage(
                            "Début du combat dans " + idleTime + " seconde" + (fightTime > 1 ? "s" : "")) + ".")
                    );
            idleTime--;
            if (idleTime <= 0)
                BattleManager.freeze.removeAll(offlinePlayers);
        }
    }

}
