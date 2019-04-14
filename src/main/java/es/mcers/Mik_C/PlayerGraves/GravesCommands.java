package es.mcers.Mik_C.PlayerGraves;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GravesCommands implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(label.equals("graves")){
            if(args.length == 1 && sender.hasPermission("playerGraves.other")){
                String uuid = args[0];
                if(PlayerGraves.hasGraves(uuid)){
                    sender.sendMessage(ChatColor.RED + "Graves" + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + "Mostrando tumbas del jugador " + PlayerGraves.getName(uuid) + "...");
                    sender.sendMessage(PlayerGraves.getGravesStrings(uuid));
                }else{
                    sender.sendMessage(ChatColor.RED + "Graves" + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + "Este jugador no ha muerto recientemente o la uuid es incorrecta.");
                }
                return true;
                
            }else if(sender instanceof Player && sender.hasPermission("playerGraves.own")){
                String uuid = ((Player) sender).getUniqueId().toString();
                if(PlayerGraves.hasGraves(uuid)){
                    sender.sendMessage(ChatColor.RED + "Graves" + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + "Mostrando tumbas:");
                    sender.sendMessage(PlayerGraves.getGravesStrings(uuid));
                }else{
                    sender.sendMessage(ChatColor.RED + "Graves" + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + "No tienes ninguna tumba reciente.");
                }
            }
        }else{
            return false;
        }

        return true;
    }

}
