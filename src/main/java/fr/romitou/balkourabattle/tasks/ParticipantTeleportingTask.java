package fr.romitou.balkourabattle.tasks;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticipantTeleportingTask extends BukkitRunnable {

    private final OfflinePlayer player;
    private final Location location;

    public ParticipantTeleportingTask(OfflinePlayer player, Location location) {
        this.player = player;
        this.location = location;
    }

    @Override
    public void run() {
        System.out.println("teleporting task");
        System.out.println("player:" + player);
        System.out.println("location:" + location);
        if (player.isOnline()) player.getPlayer().teleportAsync(location);
    }
}
