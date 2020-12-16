package fr.romitou.balkourabattle.commands;

import fr.romitou.balkourabattle.BalkouraBattle;
import fr.romitou.balkourabattle.BattleManager;
import fr.romitou.balkourabattle.tasks.*;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class EventCommand implements TabExecutor {

    private final static BalkouraBattle INSTANCE = BalkouraBattle.getInstance();

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, String[] args) {
        if (args.length < 1) {
            ChatUtils.sendMessage(sender, "Commande invalide.");
            return false;
        }
        switch (args[0]) {
            case "register":
            case "init":
                ChatUtils.sendMessage(sender, "Début de l'enregistrement des joueurs. Cela peut prendre un moment ...");
                new ParticipantsRegistrationTask((Player) sender).runTaskAsynchronously(INSTANCE);
                break;
            case "start":
                new TournamentStartingTask().runTaskAsynchronously(INSTANCE);
                ChatUtils.sendMessage(sender, "Le tournois a été ouvert et ne peut plus être modifié.");
                break;
            case "announce":
                ChatUtils.sendMessage(sender, "Annonce des matchs aux joueurs.");
                new MatchesAnnouncementTask().runTaskAsynchronously(INSTANCE);
                break;
            case "reset":
                new TournamentResettingTask().runTaskAsynchronously(INSTANCE);
                ChatUtils.sendMessage(sender, "Le tournois a été réinitialisé.");
                break;
            case "finalize":
                new TournamentFinalizationTask().runTaskAsynchronously(INSTANCE);
                ChatUtils.sendMessage(sender, "Le tournois a été marqué comme terminé.");
                break;
            case "round":
                if (args[1] == null) {
                    ChatUtils.sendMessage(sender, "Veuillez préciser un nombre valide.");
                    break;
                }
                int round = Integer.parseInt(args[1]);
                BattleManager.round = round;
                ChatUtils.sendMessage(sender, "La manche a bien été définie à " + round + ".");
                break;
            case "info":
            case "status":
                if (args.length == 1) {
                    // TODO: new ParticipantMatchStatusTask((Player) sender).runTaskAsynchronously(INSTANCE);
                    break;
                }
                int matchId = Integer.parseInt(args[1]);
                BattleManager.sendMatchInfo((OfflinePlayer) sender, matchId);
                break;
            case "accept":
                if (args.length >= 2 && args[1] == null)
                    break;
                System.out.println(Arrays.toString(args));
                long id = Integer.parseInt(args[1]);
                new MatchRequestCallbackTask((Player) sender, id).runTaskAsynchronously(INSTANCE);
                break;
            default:
                ChatUtils.sendMessage(sender, "Commande inconnue.");
                return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        return null;
    }
}
