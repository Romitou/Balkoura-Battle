package fr.romitou.balkourabattle.tasks;

//import com.google.api.client.util.ArrayMap;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.utils.API_Matchs;
import fr.romitou.balkourabattle.utils.ChatUtils;
import fr.romitou.balkourabattle.utils.Json.Match;
import fr.romitou.balkourabattle.utils.JsonRequest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

public class GetMatches extends BukkitRunnable {
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        //JSONArray matches = JsonRequest.getJsonRequest("/matches", true);
        try {
            ArrayList<Match> matches = new API_Matchs().getMatchs();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        assert matches != null;
        matches.forEach(match -> {
            ArrayMap<?, ?> m = (ArrayMap<?, ?>) ((ArrayMap<?, ?>) match).get("match");
            Integer playerId1 = ((BigDecimal) m.get("player1_id")).intValueExact();
            Integer playerId2 = ((BigDecimal) m.get("player2_id")).intValueExact();
            Player player1 = BattleHandler.getPlayer(playerId1);
            Player player2 = BattleHandler.getPlayer(playerId2);
            assert player1 != null && player2 != null;
            ChatUtils.sendMessage(player1, "Votre prochain match sera contre §e" + player2.getDisplayName() + "§f, préparez-vous !");
            ChatUtils.sendMessage(player2, "Votre prochain match sera contre §e" + player1.getDisplayName() + "§f, préparez-vous !");
        });
        */
    }
}
