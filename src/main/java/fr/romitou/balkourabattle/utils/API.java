package fr.romitou.balkourabattle.utils;

import fr.romitou.balkourabattle.BalkouraBattle;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class API {
    private static final String CHALLONGE_URL = BalkouraBattle.getInstance().getConfigFile().getString("challonge.default_endpoint");
    private static final String CHALLONGE_KEY = BalkouraBattle.getInstance().getConfigFile().getString("challonge.key");

    public HttpURLConnection getApi(String url) throws IOException {
        assert CHALLONGE_URL != null;
        HttpURLConnection connection = (HttpURLConnection) new URL(CHALLONGE_URL + url + "?api_key=" + CHALLONGE_KEY).openConnection();
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }

    /**
     * Ajout rapide de paramètres à la requête
     */
    public static void setHttpParams(HttpURLConnection connection, HashMap<String, String> parameters) {
        try {
            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(getParamsString(parameters));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Get from https://www.baeldung.com/java-http-request#adding-request-parameters
     */
    private static String getParamsString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }
        String resultString = result.toString();
        return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
    }
}
