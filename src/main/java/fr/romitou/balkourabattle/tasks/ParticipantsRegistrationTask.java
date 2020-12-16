package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.query.ParticipantQuery;
import fr.romitou.balkourabattle.BattleManager;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ParticipantsRegistrationTask extends BukkitRunnable {

    private final Player player;

    public ParticipantsRegistrationTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        List<Participant> registeredParticipants = new LinkedList<>();
        try {
            registeredParticipants.addAll(ChallongeManager.getChallonge().getParticipants(ChallongeManager.getTournament()));
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
        BattleManager.getAvailablePlayers().forEach(player -> {
            if (!BattleManager.containsName(player.getName())) {
                if (registeredParticipants.stream().anyMatch(elt -> elt.getName().equals(player.getName()))) {
                    Optional<Participant> eltParticipant = registeredParticipants.stream()
                            .filter(elt -> elt.getName().equals(player.getName()))
                            .findFirst();
                    if (eltParticipant.isPresent()) {
                        BattleManager.registeredParticipants.put(eltParticipant.get(), player);
                        ChatUtils.sendMessage(player, "Vous avez été inscrit pour ce tournois !");
                    }
                } else {
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
            }
        });
        ChatUtils.sendMessage(player, "Participants enregistrés.");
    }
}
