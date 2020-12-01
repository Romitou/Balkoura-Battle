package fr.romitou.balkourabattle.utils.Json;

import com.google.gson.annotations.SerializedName;

public class Match {
    @SerializedName("id")
    private double id;
    @SerializedName("identifier")
    private String identifier;
    @SerializedName("loser_id")
    private double loser_id;
    @SerializedName("player1_id")
    private double player1_id;
    @SerializedName("player1_is_prereq_match_loser")
    private boolean player1_is_prereq_match_loser;
    @SerializedName("player2_id")
    private double player2_id;
    @SerializedName("player2_is_prereq_match_loser")
    private boolean player2_is_prereq_match_loser;
    @SerializedName("round")
    private int round;
    @SerializedName("started_at")
    private String started_at;
    @SerializedName("state")
    private String state;
    @SerializedName("tournament_id")
    private double tournament_id;
    @SerializedName("underway_at")
    private String underway_at;
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("winner_id")
    private double winner_id;
    @SerializedName("prerequisite_match_ids_csv")
    private String prerequisite_match_ids_csv;
    @SerializedName("scores_csv")
    private String scores_csv;

    public Match(double id, String identifier, double loser_id, double player1_id, boolean player1_is_prereq_match_loser, double player2_id, boolean player2_is_prereq_match_loser, int round, String started_at, String state, double tournament_id, String underway_at, String updated_at, double winner_id, String prerequisite_match_ids_csv, String scores_csv) {
        this.id = id;
        this.identifier = identifier;
        this.loser_id = loser_id;
        this.player1_id = player1_id;
        this.player1_is_prereq_match_loser = player1_is_prereq_match_loser;
        this.player2_id = player2_id;
        this.player2_is_prereq_match_loser = player2_is_prereq_match_loser;
        this.round = round;
        this.started_at = started_at;
        this.state = state;
        this.tournament_id = tournament_id;
        this.underway_at = underway_at;
        this.updated_at = updated_at;
        this.winner_id = winner_id;
        this.prerequisite_match_ids_csv = prerequisite_match_ids_csv;
        this.scores_csv = scores_csv;
    }

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public double getLoser_id() {
        return loser_id;
    }

    public void setLoser_id(double loser_id) {
        this.loser_id = loser_id;
    }

    public double getPlayer1_id() {
        return player1_id;
    }

    public void setPlayer1_id(double player1_id) {
        this.player1_id = player1_id;
    }

    public boolean isPlayer1_is_prereq_match_loser() {
        return player1_is_prereq_match_loser;
    }

    public void setPlayer1_is_prereq_match_loser(boolean player1_is_prereq_match_loser) {
        this.player1_is_prereq_match_loser = player1_is_prereq_match_loser;
    }

    public double getPlayer2_id() {
        return player2_id;
    }

    public void setPlayer2_id(double player2_id) {
        this.player2_id = player2_id;
    }

    public boolean isPlayer2_is_prereq_match_loser() {
        return player2_is_prereq_match_loser;
    }

    public void setPlayer2_is_prereq_match_loser(boolean player2_is_prereq_match_loser) {
        this.player2_is_prereq_match_loser = player2_is_prereq_match_loser;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getTournament_id() {
        return tournament_id;
    }

    public void setTournament_id(double tournament_id) {
        this.tournament_id = tournament_id;
    }

    public String getUnderway_at() {
        return underway_at;
    }

    public void setUnderway_at(String underway_at) {
        this.underway_at = underway_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public double getWinner_id() {
        return winner_id;
    }

    public void setWinner_id(double winner_id) {
        this.winner_id = winner_id;
    }

    public String getPrerequisite_match_ids_csv() {
        return prerequisite_match_ids_csv;
    }

    public void setPrerequisite_match_ids_csv(String prerequisite_match_ids_csv) {
        this.prerequisite_match_ids_csv = prerequisite_match_ids_csv;
    }

    public String getScores_csv() {
        return scores_csv;
    }

    public void setScores_csv(String scores_csv) {
        this.scores_csv = scores_csv;
    }
}
