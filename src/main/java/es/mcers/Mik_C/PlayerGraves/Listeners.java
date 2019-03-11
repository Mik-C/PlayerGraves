package es.mcers.Mik_C.PlayerGraves;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.LinkedList;

public class Listeners implements Listener {

    //Save the inventory contents in a Grave object
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDie(PlayerDeathEvent event){
        Player p = event.getEntity();
        Grave grave = new Grave(p);
        if(!grave.isEmpty() && grave.isAllowed()){
            LinkedList<Grave> playerGraves = PlayerGraves.graves.get(grave.getOwner());
            if(playerGraves == null){
                playerGraves = new LinkedList<Grave>();
                PlayerGraves.graves.put(grave.getOwner(), playerGraves);
            }
            playerGraves.add(grave);
            //If the inventory is saved in the grave do not drop it!
            event.getDrops().clear();
        }
    }

    //Inform the user of the Grave Location
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event){
        Player p = event.getPlayer();
        Location l = p.getLocation();
        LinkedList<Grave> playerGraves = PlayerGraves.graves.get(p.getUniqueId().toString());
        if(playerGraves == null || playerGraves.isEmpty()) return;
        p.sendRawMessage("¡Has muerto! Ubicación de tus tumbas:");
        for(Grave grave : playerGraves){
            p.sendRawMessage(grave.getLocation().toString());
        }
    }

    //Show the grave if the distance is less than 100 (player.getClientViewDistance​() does not work)
    //player.sendBlockChange(loc, blockdata);
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event){
        //check player permissions
        //add possibility to see all graves (then this structure for saving graves is bad)
        Player p = event.getPlayer();
        Location l = p.getLocation();
        LinkedList<Grave> playerGraves = PlayerGraves.graves.get(p.getUniqueId().toString());
        if(playerGraves == null || playerGraves.isEmpty()) return;
        BlockData d = Bukkit.createBlockData(Material.CHEST);
        for(Grave grave : playerGraves){
            if(l.distance(grave.getLocation()) < 100) {
                event.getPlayer().sendBlockChange(grave.getLocation(), d);
            }
        }
    }

    //Open the death inv
    //player.openInventory​(Inventory inventory)
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event){
        //add possibility yo open all graves by permissions
        Player p = event.getPlayer();
        Location l = event.getClickedBlock().getLocation();
        LinkedList<Grave> playerGraves = PlayerGraves.graves.get(p.getUniqueId().toString());
        if(playerGraves == null || playerGraves.isEmpty()) return;
        for(Grave grave : playerGraves){
            Location gl = grave.getLocation();
            if(l.getBlockX() == gl.getBlockX() && l.getBlockY() == gl.getBlockY() && l.getBlockZ() == gl.getBlockZ()) {
                event.setCancelled(true);
                p.openInventory(grave.getInventory());
            }
        }
    }
}
