package fr.romitou.balkourabattle.tasks;

import org.bukkit.OfflinePlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathMatchApplicationTask extends BukkitRunnable {

    private final OfflinePlayer player1, player2;

    public DeathMatchApplicationTask(OfflinePlayer player1, OfflinePlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public void run() {
        if (player1.getPlayer() != null)
            new PotionEffect(PotionEffectType.WITHER, 20, 1, true, false).apply(player1.getPlayer());
        if (player2.getPlayer() != null)
            new PotionEffect(PotionEffectType.WITHER, 20, 1, true, false).apply(player1.getPlayer());
    }
}
