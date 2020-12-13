package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.ParticipantMatchCheckType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class ParticipantMatchCheckTask extends BukkitRunnable {

    private final Player player;
    private final ParticipantMatchCheckType checkType;

    public ParticipantMatchCheckTask(Player player, ParticipantMatchCheckType checkType) {
        this.player = player;
        this.checkType = checkType;
    }

    @Override
    public void run() {
        Participant participant = BattleHandler.getParticipant(player.getName());
        Match match = null;
        System.out.println("debug misc: " + participant.getMisc());
        try {
            match = ChallongeManager.getChallonge().getMatch(
                    ChallongeManager.getTournament(),
                    Integer.parseInt(participant.getMisc())
            );
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
        assert match != null;
        OfflinePlayer player1 = BattleHandler.getPlayer(match.getPlayer1Id());
        OfflinePlayer player2 = BattleHandler.getPlayer(match.getPlayer2Id());
        assert player1 != null && player2 != null;
        switch (checkType) {
            case DISCONNECTED:
                if (Objects.equals(player1.getName(), player.getName()))
                    BattleHandler.handleDisconnect(match, player1, player2);
                BattleHandler.handleDisconnect(match, player2, player1);
                break;
            case CONNECTED:
                BattleHandler.handleConnect(match, player1, player2);
                break;
            case DEATH:
                BattleHandler.handleDeath(match, player1, player2, player);
                break;
            default:
                ChatUtils.modAlert("Invalid check type for player" + player.getName() + ": " + checkType.toString());
        }
    }
}
