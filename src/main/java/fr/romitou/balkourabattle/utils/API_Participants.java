package fr.romitou.balkourabattle.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.romitou.balkourabattle.utils.Json.Parser;
import fr.romitou.balkourabattle.utils.Json.Participant;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class API_Participants {
    public ArrayList<Participant> getParticipants() throws IOException {
        HttpURLConnection connection = new API().getApi("/participants.json");
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == 200) {
            ArrayList<Participant> participants = new ArrayList<>();
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            for (JSONObject object : Parser.getJsonArray(Parser.getBody(connection))) {
                participants.add(gson.fromJson(object.toString(),Participant.class));
            }
            return participants;
        }
        return null;
    }

    public Participant getParticipant(double id) throws IOException {
        HttpURLConnection connection = new API().getApi("/participants/" + id + ".json");
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == 200) {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            return gson.fromJson(Parser.getBody(connection).getJSONObject("participant").toString(), Participant.class);
        }
        return null;
    }
}
