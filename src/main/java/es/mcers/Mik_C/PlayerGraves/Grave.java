package es.mcers.Mik_C.PlayerGraves;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Date;
import java.util.UUID;

public class Grave {
    private String owner;
    private Date createdAt;
    private Location location;
    private Inventory inventory;

    public Grave(Player player){
        owner = player.getUniqueId().toString();
        createdAt = new Date();
        location = player.getLocation();
        inventory = PlayerGraves.server.createInventory(null, 54, ChatColor.DARK_RED+"Tumba de "+player.getDisplayName());
        inventory.setContents(player.getInventory().getContents());

        //do something with location (be on ground, avoid lava)
    }

    public boolean isAllowed(){
        //check world and location with config options

        return true;
    }

    public String getOwner(){
        return owner;
    }

    public Location getLocation(){
        return location;
    }

    public Inventory getInventory(){
        return inventory;
    }

    public boolean isEmpty(){
        return inventory.getContents().length == 0;
    }

    public String toString(){
        String s = "";
        s += location.toString();

        return s;
    }
}
