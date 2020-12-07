package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.MatchUtils;
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
        long playerId = BattleHandler.players.inverse().get(player.getName());
        System.out.println("playerid:" + playerId);
        Participant participant;
        Match match = null;
        OfflinePlayer[] players = null;
        try {
            participant = ChallongeManager.getChallonge().getParticipant(
                    ChallongeManager.getTournament(),
                    playerId
            );
            match = ChallongeManager.getChallonge().getMatch(
                    ChallongeManager.getTournament(),
                    Long.parseLong(participant.getMisc())
            );
            players = MatchUtils.getPlayers(match);
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
        assert players != null;
        switch (checkType) {
            case DISCONNECTED:
                if (Objects.equals(players[0].getName(), player.getName()))
                    BattleHandler.handleDisconnect(match, players[0], players[1]);
                BattleHandler.handleDisconnect(match, players[1], players[0]);
                break;
            case CONNECTED:
                BattleHandler.handleConnect(match, players[0], players[1]);
                break;
            case DEATH:
                BattleHandler.handleDeath(match, players[0], players[1], player);
                break;
            default:
                ChatUtils.modAlert("Invalid check type for player" + player.getName() + ": " + checkType.toString());
        }
    }
}
