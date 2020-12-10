package fr.romitou.balkourabattle.tasks;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticipantTeleportingTask extends BukkitRunnable {

    private final Player player;
    private final Location location;

    public ParticipantTeleportingTask(OfflinePlayer player, Location location) {
        this.player = player.getPlayer();
        this.location = location;
    }

    @Override
    public void run() {
        assert player != null;
        player.teleportAsync(location);
    }
}
