package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchesAnnouncementTask extends BukkitRunnable {
    @Override
    public void run() {
        try {
            ChallongeManager.getChallonge().getMatches(ChallongeManager.getTournament()).forEach(match -> {
                OfflinePlayer player1 = BattleHandler.getPlayer(match.getPlayer1Id());
                OfflinePlayer player2 = BattleHandler.getPlayer(match.getPlayer2Id());
                assert player1 != null && player2 != null;
                if (player1.getPlayer() != null)
                    ChatUtils.sendMessage(
                            player1.getPlayer(),
                            "Votre prochain match sera contre §e" + player2.getName() + "§f, préparez-vous !"
                    );
                if (player2.getPlayer() != null)
                    ChatUtils.sendMessage(
                            player2.getPlayer(),
                            "Votre prochain match sera contre §e" + player1.getName() + "§f, préparez-vous !"
                    );
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
    }
}
