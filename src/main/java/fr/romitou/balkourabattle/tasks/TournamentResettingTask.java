package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class TournamentResettingTask extends BukkitRunnable {

    @Override
    public void run() {
        try {
            BattleHandler.PARTICIPANTS.clear();
            ChallongeManager.getChallonge().resetTournament(ChallongeManager.getTournament());
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
    }

}
