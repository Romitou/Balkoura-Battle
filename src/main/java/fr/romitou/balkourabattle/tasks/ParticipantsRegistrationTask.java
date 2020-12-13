package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.query.ParticipantQuery;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ParticipantsRegistrationTask extends BukkitRunnable {

    @Override
    public void run() {
        List<Participant> registeredParticipants = new LinkedList<>();
        try {
            registeredParticipants.addAll(ChallongeManager.getChallonge().getParticipants(ChallongeManager.getTournament()));
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if (!BattleHandler.containsName(player.getName())) {
                if (registeredParticipants.stream().anyMatch(elt -> elt.getName().equals(player.getName()))) {
                    Optional<Participant> eltParticipant = registeredParticipants.stream()
                            .filter(elt -> elt.getName().equals(player.getName()))
                            .findFirst();
                    if (eltParticipant.isPresent()) {
                        BattleHandler.PARTICIPANTS.add(eltParticipant.get());
                        ChatUtils.sendMessage(player, "Vous êtes inscrit pour ce tournois. Préparez-vous !");
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
                            BattleHandler.PARTICIPANTS.add(participant);
                            ChatUtils.sendMessage(player, "Vous êtes inscrit pour ce tournois. Préparez-vous !");
                        }
                        Thread.sleep(1000); // We wait one second in order to not surcharge Challonge's API.
                    } catch (InterruptedException | DataAccessException e) {
                        e.printStackTrace();
                        ChatUtils.modAlert(e.getMessage());
                    }
                }
            }
        });
        ChatUtils.broadcast("Les participants suivant sont inscrits pour ce tournois :");
        ChatUtils.broadcast(StringUtils.join(
                BattleHandler.PARTICIPANTS.stream().map(Participant::getName).toArray(),
                ", "
        ));
        ChatUtils.broadcast("Que le meilleur gagne !");
    }
}
