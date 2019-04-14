package es.mcers.Mik_C.PlayerGraves;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;

import java.util.LinkedList;

public class Listeners implements Listener {

    //Save the inventory contents in a Grave object
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDie(PlayerDeathEvent event){
        Player p = event.getEntity();
        Grave g = new Grave(p);
        if(!g.isEmpty() && g.isAllowed()){
            PlayerGraves.createGrave(g, p);
            //If the inventory is saved in the grave do not drop it!
            event.getDrops().clear();
        }
    }

    //Inform the user of the Grave Location
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event){
        Player p = event.getPlayer();
        LinkedList<Grave> playerGraves = PlayerGraves.graves.get(p.getUniqueId().toString());
        if(playerGraves == null || playerGraves.isEmpty()) return;
        Grave g = playerGraves.getLast();
        p.sendRawMessage("¡Has muerto en " + g.locationString() + "! Una tumba guardará tu inventario durante " + g.remainingTimeString() + "minutos.");
    }

    //Show the grave if the distance is less than 100 (player.getClientViewDistance​() does not work)
    //player.sendBlockChange(loc, blockdata);
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event){
        //check player permissions
        //add possibility to see all graves (then this structure for saving graves is bad)
        Player p = event.getPlayer();
        Location l = p.getLocation();

        if(PlayerGraves.hasGraves(p.getUniqueId().toString())){
            LinkedList<Grave> playerGraves = PlayerGraves.getGraves(p.getUniqueId().toString());
            for(Grave g : playerGraves){
                if(l.distance(g.location) < 100){
                    BlockData d = Bukkit.createBlockData(Material.CHEST);
                    p.sendBlockChange(g.location, d);
                }
            }
        }
    }

    //Open the death inv
    //player.openInventory​(Inventory inventory)
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event){
        //add possibility to open all graves by permissions
        Player p = event.getPlayer();
        Location l = event.getClickedBlock().getLocation();
        Grave g = PlayerGraves.getFromLocation(p.getUniqueId().toString(), l);
        if(g != null){
            event.setCancelled(true);
            g.openInventory(p);
        }
    }

    //Delete dead inv if empty
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event){
        //Determine if it is a grave inventory
        HumanEntity e = event.getPlayer();
        if(e instanceof Player){
            Player p = (Player)e;
            Inventory i = event.getInventory();

            //If the inventory is empty it will check for a empty grave inventory of the player
            if(PlayerGraves.isEmpty(i)){
                PlayerGraves.deleteEmptyFromPlayer(p.getUniqueId().toString());
            }

        }

    }
}
