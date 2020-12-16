import fr.romitou.balkourabattle.elements.MatchScore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MatchScoresTests {

    @Test
    @DisplayName("Match Scores")
    void matchScore() {
        MatchScore matchScore = new MatchScore("0-0,0-0");
        Assertions.assertEquals(matchScore.getRound(), 2);
        matchScore.setWinnerSet(0, true);
        Assertions.assertEquals(matchScore.getScoreCsv(1), "1-0");
        matchScore.setWinnerSet(1, false);
        Assertions.assertEquals(matchScore.getSet(0).getScore(0), 1);
        Assertions.assertEquals(matchScore.getScoreCsv(2), "1-0,0-1");
        matchScore = new MatchScore(matchScore.getScoreCsv(matchScore.getRound() + 1));
        Assertions.assertEquals(matchScore.getScoreCsv(3), "1-0,0-1,0-0");
        matchScore.setWinnerSet(2, true);
        Assertions.assertEquals(matchScore.getSet(2).getScore(0), 1);
        Assertions.assertEquals(matchScore.getWinSets(true), 2);
    }

}
