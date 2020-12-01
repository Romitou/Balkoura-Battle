package fr.romitou.balkourabattle.utils.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Parser {

    public static JSONObject getBody(HttpURLConnection connection) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) {
            sb.append((char) cp);
        }
        reader.close();
        return new JSONObject(sb.toString());
    }

    public static ArrayList<JSONObject> getJsonArray(Object object) throws JSONException {
        ArrayList<JSONObject> array = new ArrayList<>();
        JSONArray jsonArray = (JSONArray) object;
        for (int i=0; i < jsonArray.length() ;i++) {
            array.add((JSONObject) ((JSONArray) object).get(i));
        }
        return array;
    }
}
