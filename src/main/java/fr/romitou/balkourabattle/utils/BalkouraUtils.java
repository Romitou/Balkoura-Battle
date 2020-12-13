package fr.romitou.balkourabattle.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class BalkouraUtils {

    public static List<Player> getOnlineModerators() {
        return Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.hasPermission("modo.event.pvp"))
                .collect(Collectors.toList());
    }

    public static List<Player> getAvailablePlayers() {
        return Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.getGameMode() == GameMode.SURVIVAL
                        || player.getGameMode() == GameMode.ADVENTURE)
                .collect(Collectors.toList());
    }

}
