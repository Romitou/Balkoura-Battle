package fr.romitou.balkourabattle.utils;

import java.lang.reflect.MalformedParametersException;
import java.util.Arrays;

public class MatchScore {

    private final Integer[][] scores;

    public MatchScore(String scoreCsv) {
        if (scoreCsv.equals("")) scoreCsv = "0-0,0-0,0-0";
        String[] splitScore = scoreCsv.split("-");
        this.scores = new Integer[][]{
                new Integer[]{
                        Integer.parseInt(splitScore[0]),
                        Integer.parseInt(splitScore[1]),
                },
                new Integer[]{
                        Integer.parseInt(splitScore[2]),
                        Integer.parseInt(splitScore[3]),
                },
                new Integer[]{
                        Integer.parseInt(splitScore[4]),
                        Integer.parseInt(splitScore[5]),
                },
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
        return Arrays.stream(scores).filter(match -> match[isPlayer1 ? 0 : 1] == 1).count();
    }

    public String getScoreCsv(boolean finalSet) {
        return getSet(1).getScore(1) + "-" + getSet(1).getScore(2) + ","
                + getSet(2).getScore(1) + "-" + getSet(2).getScore(2) + ","
                + ((finalSet)
                ? getSet(3).getScore(1) + "-" + getSet(3).getScore(2)
                : "");
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
