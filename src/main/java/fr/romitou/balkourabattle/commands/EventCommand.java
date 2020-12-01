package fr.romitou.balkourabattle.commands;

import fr.romitou.balkourabattle.BalkouraBattle;
import fr.romitou.balkourabattle.BattleHandler;
import fr.romitou.balkourabattle.tasks.*;
import fr.romitou.balkourabattle.utils.ArenaUtils;
import fr.romitou.balkourabattle.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class EventCommand implements TabExecutor {

    private final static BalkouraBattle INSTANCE = BalkouraBattle.getInstance();

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, String[] args) {
        if (args.length < 1 || args[0].equals("help")) {
            ChatUtils.sendMessage(sender, "Commandes disponibles: §e/battle init§f.");
            return false;
        }
        switch (args[0]) {
            case "init":
                new RegisterParticipants(sender).runTaskAsynchronously(INSTANCE);
                ChatUtils.sendMessage(sender, "Début de l'enregistrement des joueurs. Cela peut prendre un moment ...");
                break;
            case "start":
                new StartTournament().runTaskAsynchronously(INSTANCE);
                ChatUtils.sendMessage(sender, "Le tournois a été ouvert et ne peut plus être modifié.");
                break;
            case "matchmaking":
                ChatUtils.sendMessage(sender, "Récupération des équipes depuis Challonge ...");
                new GetMatches().runTaskAsynchronously(INSTANCE);
                break;
            case "reset":
                new ResetTournament().runTaskAsynchronously(INSTANCE);
                ChatUtils.sendMessage(sender, "Le tournois a été réinitialisé.");
                break;
            case "finalize":
                new FinalizeTournament().runTaskAsynchronously(INSTANCE);
                ChatUtils.sendMessage(sender, "Le tournois a été marqué comme terminé.");
                break;
            case "round":
                if (args[1] == null) {
                    ChatUtils.sendMessage(sender, "Veuillez préciser un nombre valide.");
                    break;
                }
                int round = Integer.parseInt(args[1]);
                if (BattleHandler.getRound() <= round || round < 0) {
                    ChatUtils.sendMessage(sender, "La manche doit être supérieure à celle actuelle et à 0.");
                    break;
                }
                BattleHandler.setRound(round);
                ChatUtils.sendMessage(sender, "La manche a bien été définie à " + round + ".");
                break;
            case "loc":
                ArenaUtils.setLocation(
                        Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]),
                        ((Player) sender).getLocation()
                );
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
