package fr.romitou.balkourabattle.utils.Json;

import com.google.gson.annotations.SerializedName;

public class Participant {
    @SerializedName("active")
    private boolean active;
    @SerializedName("id")
    private double id;
    @SerializedName("name")
    private String name;
    @SerializedName("seed")
    private double seed;
    @SerializedName("tournament_id")
    private double tournament_id;
    @SerializedName("updated_at")
    private String updated_at;

    public Participant(boolean active, double id, String name, double seed, double tournament_id, String updated_at) {
        this.active = active;
        this.id = id;
        this.name = name;
        this.seed = seed;
        this.tournament_id = tournament_id;
        this.updated_at = updated_at;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSeed() {
        return seed;
    }

    public void setSeed(double seed) {
        this.seed = seed;
    }

    public double getTournament_id() {
        return tournament_id;
    }

    public void setTournament_id(double tournament_id) {
        this.tournament_id = tournament_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
