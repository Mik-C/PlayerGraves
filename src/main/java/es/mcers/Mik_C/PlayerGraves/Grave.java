package es.mcers.Mik_C.PlayerGraves;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

public class Grave {
    public String owner;
    public String ownerName;
    public long createdAt;
    public long lastSave;
    public long expires;
    public Location location;
    public Inventory inventory;

    public static long now(){
        Date now = new Date();
        return now.getTime();
    }

    public Grave(Player player){
        owner = player.getUniqueId().toString();
        ownerName = player.getDisplayName();
        createdAt = now();
        lastSave = -1;
        expires = createdAt + (1800000);    //1000*60*30
        location = player.getLocation();
        inventory = PlayerGraves.server.createInventory(null, 54, ChatColor.DARK_RED+"Tumba de "+ownerName);
        inventory.setContents(player.getInventory().getContents());

        //do something with location (be on ground, avoid lava)
    }

    public Grave(ConfigurationSection section){
        owner = section.getString("owner");
        ownerName = section.getString("ownerName");
        createdAt = section.getLong("createdAt");
        lastSave = section.getLong("lastSave");
        expires = section.getLong("expires");

        //Add extra time for reset restarting
        expires += (now() - lastSave);

        int x = section.getInt("x");
        int y = section.getInt("y");
        int z = section.getInt("z");
        String w = section.getString("world");
        World world = PlayerGraves.server.getWorld(w);

        location = new Location(world, x, y, z);
        ItemStack[] contents = new ItemStack[54];
        int count = 0;
        for(String i : section.getConfigurationSection("inventory").getKeys(false)){
            contents[count] = section.getItemStack("inventory."+i);
            count++;
        }
        inventory = PlayerGraves.server.createInventory(null, 54, ChatColor.DARK_RED+"Tumba de "+ownerName);
        inventory.setContents(contents);
    }

    public ConfigurationSection save(ConfigurationSection section){
        lastSave = now();

        section.set("owner", owner);
        section.set("ownerName", ownerName);
        section.set("createdAt", createdAt);
        section.set("lastSave", lastSave);
        section.set("expires", expires);
        section.set("x", location.getBlockX());
        section.set("y", location.getBlockY());
        section.set("z", location.getBlockZ());
        section.set("world", location.getWorld().getName());
        ItemStack[] contents = inventory.getContents();
        for(int i=0; i<contents.length; i++){
            section.set("inventory."+i, contents[i]);
        }

        return section;
    }

    public boolean isAllowed(){
        //check world and location with config options

        return true;
    }

    public boolean hasTime(){
        return remainingTime() > 0;
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
