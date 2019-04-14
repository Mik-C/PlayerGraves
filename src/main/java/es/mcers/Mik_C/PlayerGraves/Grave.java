package es.mcers.Mik_C.PlayerGraves;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

public class Grave {
    public String owner;
    public String ownerName;
    public Date createdAt;
    public long expires;
    public Location location;
    public Inventory inventory;

    public Grave(Player player){
        owner = player.getUniqueId().toString();
        ownerName = player.getDisplayName();
        createdAt = new Date();
        expires = createdAt.getTime() + (1000*60*30);
        location = player.getLocation();
        inventory = PlayerGraves.server.createInventory(null, 54, ChatColor.DARK_RED+"Tumba de "+player.getDisplayName());
        inventory.setContents(player.getInventory().getContents());

        //do something with location (be on ground, avoid lava)
    }

    public boolean isAllowed(){
        //check world and location with config options

        return true;
    }

    public long remainingTime(){
        Date now = new Date();
        return expires - now.getTime();
    }

    public String remainingTimeString(){
        long diff = remainingTime();

        long seconds = diff / 1000;

        return "" + seconds/60 + ":" + seconds%60 + " minutos";
    }

    public String locationString(){
        return "( " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + " )";
    }

    public boolean isEmpty(){
        return PlayerGraves.isEmpty(inventory);
    }

    public boolean isThisInventory(Inventory i){
        return inventory.hashCode() == i.hashCode();
    }

    public String toString(){
        return locationString() + " === " + remainingTimeString();
    }

    public void drop(){
        Chunk chunk = location.getChunk();
        boolean isLoaded = chunk.isLoaded();

        //Load grave chunk
        if(!isLoaded){
            chunk.load();
        }

        //Spawn items in world
        for(ItemStack is : inventory.getContents()){
            location.getWorld().dropItemNaturally(location, is);
        }
        inventory.clear();

        //Unload chunk (if no player near!)
        if(!isLoaded){
            chunk.unload(true);
        }
    }

    public void openInventory(Player p){
        p.openInventory(inventory);
    }
}
