package fr.romitou.balkourabattle;

import fr.romitou.balkourabattle.tasks.ParticipantMatchCheckTask;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.ParticipantMatchCheckType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    private static final BalkouraBattle INSTANCE = BalkouraBattle.getInstance();

    @EventHandler
    public void playerConnect(PlayerJoinEvent event) {
        ChatUtils.broadcastConnection(event.getPlayer());
        if (BattleHandler.players.containsValue(event.getPlayer().getName()))
            new ParticipantMatchCheckTask(event.getPlayer(), ParticipantMatchCheckType.CONNECTED).runTaskAsynchronously(INSTANCE);
    }

    @EventHandler
    public void playerDisconnect(PlayerQuitEvent event) {
        ChatUtils.broadcastDisconnection(event.getPlayer());
        if (BattleHandler.players.containsValue(event.getPlayer().getName()))
            new ParticipantMatchCheckTask(event.getPlayer(), ParticipantMatchCheckType.DISCONNECTED).runTaskAsynchronously(INSTANCE);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
        System.out.println("death");
        if (BattleHandler.players.containsValue(event.getEntity().getName()))
            System.out.println("death ok");
        new ParticipantMatchCheckTask(event.getEntity(), ParticipantMatchCheckType.DEATH).runTaskAsynchronously(INSTANCE);
    }

}
