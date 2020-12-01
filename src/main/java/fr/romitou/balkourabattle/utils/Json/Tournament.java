package fr.romitou.balkourabattle.utils.Json;

import com.google.gson.annotations.SerializedName;

public class Tournament {
    @SerializedName("id")
    private double id;
    @SerializedName("name")
    private String name;
    @SerializedName("participants_count")
    private int participants_count;

    public Tournament(double id, String name, int participants_count) {
        this.id = id;
        this.name = name;
        this.participants_count = participants_count;
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

    public int getParticipants_count() {
        return participants_count;
    }

    public void setParticipants_count(int participants_count) {
        this.participants_count = participants_count;
    }
}
