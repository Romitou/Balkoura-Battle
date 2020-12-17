package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.query.ParticipantQuery;
import fr.romitou.balkourabattle.BattleManager;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticipantsRegistrationTask extends BukkitRunnable {

    private final Player player;

    public ParticipantsRegistrationTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            ChallongeManager.getChallonge().getParticipants(ChallongeManager.getTournament())
                    .stream()
                    .filter(participant -> !BattleManager.registeredParticipants.containsKey(participant))
                    .forEach(
                    participant -> BattleManager.registeredParticipants.put(
                            participant,
                            BattleManager.getBukkitOfflinePlayer(participant.getName())
                    )
            );
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
        BattleManager.getAvailablePlayers().forEach(player -> {
            if (!BattleManager.containsName(player.getName())) {
                ParticipantQuery participantQuery = ParticipantQuery.builder()
                        .name(player.getName())
                        .build();
                try {
                    Participant participant = ChallongeManager.getChallonge().addParticipant(
                            ChallongeManager.getTournament(),
                            participantQuery
                    );
                    if (participant != null) {
                        BattleManager.registeredParticipants.put(participant, player);
                        ChatUtils.sendMessage(player, "Vous avez été inscrit pour ce tournois !");
                    }
                    Thread.sleep(1000); // We wait one second in order to not surcharge Challonge's API.
                } catch (InterruptedException | DataAccessException e) {
                    e.printStackTrace();
                    ChatUtils.modAlert(e.getMessage());
                }
            }
        });
        ChatUtils.sendMessage(player, "Participants enregistrés.");
    }
}
