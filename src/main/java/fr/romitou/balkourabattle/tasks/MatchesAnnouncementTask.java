package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import fr.romitou.balkourabattle.BattleManager;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchesAnnouncementTask extends BukkitRunnable {
    @Override
    public void run() {
        try {
            ChallongeManager.getChallonge()
                    .getMatches(ChallongeManager.getTournament())
                    .forEach(match -> BattleManager.getPlayers(match).forEach(BattleManager::sendMatchInfo));
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
    }
}
