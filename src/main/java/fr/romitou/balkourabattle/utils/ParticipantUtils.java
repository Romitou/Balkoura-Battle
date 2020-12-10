package fr.romitou.balkourabattle.utils;

import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import fr.romitou.balkourabattle.ChallongeManager;

public class ParticipantUtils {

    public static Long getCurrentMatchId(Participant participant) {
        return Long.valueOf(participant.getMisc());
    }

    public static Match getCurrentMatch(Participant participant) throws DataAccessException {
        return ChallongeManager.getChallonge().getMatch(
                ChallongeManager.getTournament(),
                getCurrentMatchId(participant)
        );
    }

}
