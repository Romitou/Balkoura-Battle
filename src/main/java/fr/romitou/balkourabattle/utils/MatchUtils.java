package fr.romitou.balkourabattle.utils;

import at.stefangeyer.challonge.model.Match;

public class MatchUtils {

    /**
     * This method is useful to retrieve scores of a match.
     *
     * @param match The JSONObject of the match.
     * @return An Integer array.
     */
    public static Integer[] getScores(Match match) {
        String csvScores = match.getScoresCsv();
        if (csvScores.equals("")) csvScores = "0-0-0-0-0-0";
        String[] scores = csvScores.split("-");
        return new Integer[]{
                Integer.parseInt(scores[0]),
                Integer.parseInt(scores[1])
        };
    }


}
