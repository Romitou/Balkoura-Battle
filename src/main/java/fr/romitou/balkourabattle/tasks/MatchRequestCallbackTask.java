package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import fr.romitou.balkourabattle.BalkouraBattle;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchRequestCallbackTask extends BukkitRunnable {

    private final Player player;
    private final Long id;

    public MatchRequestCallbackTask(Player player, long id) {
        this.player = player;
        this.id = id;
    }

    @Override
    public void run() {
        Match match = null;
        try {
            match = ChallongeManager.getChallonge().getMatch(
                    ChallongeManager.getTournament(),
                    id
            );
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        assert match != null;
        if (match.getUnderwayAt() != null) {
            ChatUtils.sendMessage(player, "Ce match a déjà été validé par un autre modérateur.");
            return;
        }
        BattleHandler.setRound(match.getId(), 1);
        new MatchStartingTask(match).runTaskAsynchronously(BalkouraBattle.getInstance());
    }
}
