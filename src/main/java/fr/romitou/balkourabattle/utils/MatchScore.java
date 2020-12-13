package fr.romitou.balkourabattle.utils;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.MalformedParametersException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MatchScore {

    private final Integer[][] scores;

    public MatchScore(String scoreCsv) {
        if (scoreCsv.equals("")) scoreCsv = "0-0";
        String[] splitScore = scoreCsv.split(",");
        String[] firstSet = splitScore[0].split("-");
        String[] secondSet = new String[0];
        String[] thirdSet = new String[0];
        if (splitScore.length >= 2)
            secondSet = splitScore[1].split("-");
        if (splitScore.length >= 3)
            thirdSet = splitScore[2].split("-");
        this.scores = new Integer[splitScore.length][2];
        this.scores[0] = new Integer[]{
                Integer.parseInt(firstSet[0]),
                Integer.parseInt(firstSet[1])
        };
        if (secondSet.length != 0)
            this.scores[1] = new Integer[]{
                    Integer.parseInt(secondSet[0]),
                    Integer.parseInt(secondSet[1]),
            };
        if (thirdSet.length != 0)
            this.scores[2] = new Integer[]{
                    Integer.parseInt(thirdSet[0]),
                    Integer.parseInt(thirdSet[1]),
            };
    }

    public void setWinnerSet(int set, boolean isPlayer1) {
        scores[set - 1][isPlayer1 ? 0 : 1] = 1;
    }

    public MatchSet getSet(int set) {
        if (set - 1 > scores.length)
            throw new MalformedParametersException();
        return new MatchSet(scores[set - 1]);
    }

    public long getWinSets(boolean isPlayer1) {
        System.out.println(Arrays.deepToString(scores));
        return Arrays.stream(scores).filter(set -> set[isPlayer1 ? 0 : 1] == 1).count();
    }

    public String getScoreCsv(int maxSet) {
        List<String> list = new LinkedList<>();
        for (int i = 0; i < maxSet; i++) list.add(getSet(i).getScore(1) + "-" + getSet(i).getScore(2));
        return StringUtils.join(list, ",");
    }

}

class MatchSet {

    private final Integer[] scores;

    MatchSet(Integer[] scores) {
        this.scores = scores;
    }

    int getScore(int score) {
        if (score - 1 > scores.length)
            throw new MalformedParametersException();
        return scores[score - 1];
    }

}
