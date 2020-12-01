package fr.romitou.balkourabattle.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.romitou.balkourabattle.utils.Json.Parser;
import fr.romitou.balkourabattle.utils.Json.Match;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class API_Matchs {
    public ArrayList<Match> getMatchs() throws IOException {
        HttpURLConnection connection = new API().getApi("/matches.json");
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == 200) {
            ArrayList<Match> matches = new ArrayList<>();
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            for (JSONObject object : Parser.getJsonArray(Parser.getBody(connection))) {
                matches.add(gson.fromJson(object.toString(), Match.class));
            }
            return matches;
        }
        return null;
    }

    public Match getMatch(double id) throws IOException {
        HttpURLConnection connection = new API().getApi("/matches/" + id + ".json");
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == 200) {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            return gson.fromJson(Parser.getBody(connection).getJSONObject("match").toString(), Match.class);
        }
        return null;
    }

    public Match updateMatch(double id, HashMap<String, String> params) throws IOException {
        HttpURLConnection connection = new API().getApi("/matches/" + id + ".json");
        connection.setRequestMethod("PUT");
        API.setHttpParams(connection, params);
        if (connection.getResponseCode() == 200) {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            return gson.fromJson(Parser.getBody(connection).getJSONObject("match").toString(), Match.class);
        }
        return null;
    }
}
