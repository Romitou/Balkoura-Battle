package fr.romitou.balkourabattle.tasks;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.query.ParticipantQuery;
import fr.romitou.balkourabattle.ChallongeManager;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticipantUpdateCurrentMatchTask extends BukkitRunnable {

    private final Participant participant;
    private final Match match;

    public ParticipantUpdateCurrentMatchTask(Participant participant, Match match) {
        this.participant = participant;
        this.match = match;
    }

    @Override
    public void run() {
        ParticipantQuery participantQuery = ParticipantQuery.builder()
                .misc(match.getId().toString())
                .build();
        try {
            ChallongeManager.getChallonge().updateParticipant(participant, participantQuery);
        } catch (DataAccessException e) {
            e.printStackTrace();
            ChatUtils.modAlert(e.getMessage());
        }
    }
}
