package es.mcers.Mik_C.PlayerGraves;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class PlayerGraves extends JavaPlugin {
    public static Server server = null;
    public static PlayerGraves instance = null;
    public static Map<String, LinkedList<Grave>> graves = null;

    public static File gravesFile = null;
    public static YamlConfiguration gravesYaml = null;

    @Override
    public void onEnable() {
        server = getServer();
        instance = this;

        loadGraves();
        server.getPluginManager().registerEvents(new Listeners(), this);
        this.getCommand("graves").setExecutor(new GravesCommands());

        //Schedule periodic graves saving to a file every 10min (12000 is 10min: 20tick/s * 60s/min * 10min)
        new SaveGravesTask().runTaskTimer(instance, 12000, 12000);
    }

    @Override
    public void onDisable() {
        //Save all graves to file
        saveGraves();
    }

    public void openFile(){
        gravesFile = new File(getDataFolder(), "playerGraves.yml");
        if(!gravesFile.exists()){
            gravesFile.getParentFile().mkdirs();
        }
        gravesYaml = new YamlConfiguration();
        try{
            gravesYaml.load(gravesFile);
        }catch(Exception e){
            System.err.println("PlayerGraves: No se ha podido cargar el fichero de tumbas guardadas por...");
            e.printStackTrace();
        }
    }

    /*
        Yaml format:

        uuid1:
            1:
                #grave fields
            2:
                #grave fields
        uuid2:
            1:
                #grave fields
        etc.

     */

    public static void loadGraves(){
        graves = new HashMap<String, LinkedList<Grave>>();

        //Load all graves from file
        for(String uuid : gravesYaml.getKeys(false)){
            LinkedList<Grave> playerGraves = new LinkedList<Grave>();
            for(String i : gravesYaml.getConfigurationSection(uuid).getKeys(false)){
                Grave g = new Grave(gravesYaml.getConfigurationSection(uuid+"."+i));
                if(g.remainingTime() > 0){
                    playerGraves.add(g);
                }else{
                    g.drop();
                }
            }
            graves.put(uuid, playerGraves);
        }
    }

    public static void saveGraves(){
        YamlConfiguration newYaml = new YamlConfiguration();
        Set<String> uuids = graves.keySet();
        for(String uuid : uuids){
            LinkedList<Grave> playerGraves = graves.get(uuid);
            int count = 0;
            for(Grave g : playerGraves){
                g.save(newYaml.createSection(uuid+"."+count));
                count++;
            }
        }
        gravesYaml = newYaml;
        try {
            gravesYaml.save(gravesFile);
        }catch (Exception e){
            System.err.println("PlayerGraves: No se ha podido guardar el fichero de tumbas guardadas por...");
            e.printStackTrace();
            System.out.println("Log de tumbas:");
            System.out.println(gravesYaml.saveToString());
        }
    }

    public static LinkedList<Grave> getGraves(String uuid){
        return PlayerGraves.graves.get(uuid);
    }

    public static boolean hasGraves(String uuid){
        LinkedList<Grave> playerGraves = getGraves(uuid);
        return playerGraves != null && !playerGraves.isEmpty();
    }

    public static String getName(String uuid){
        LinkedList<Grave> playerGraves = getGraves(uuid);
        if(hasGraves(uuid)) return playerGraves.getFirst().ownerName;
        else return "";
    }

    public static void createGrave(Grave g, Player p){
        LinkedList<Grave> playerGraves = PlayerGraves.graves.get(g.owner);
        if(playerGraves == null){
            playerGraves = new LinkedList<Grave>();
            PlayerGraves.graves.put(g.owner, playerGraves);
        }
        playerGraves.add(g);

        //Create task to delete grave (36000 is 30min: 20tick/s * 60s/min * 30min)
        new DeleteGraveTask(g).runTaskLater(instance, 36000);
    }

    public static void deleteGrave(Grave g){
        if(hasGraves(g.owner)){
            LinkedList<Grave> playerGraves = PlayerGraves.getGraves(g.owner);
            playerGraves.remove(g);
        }
    }

    public static Grave getFromLocation(String uuid, Location l){
        if(hasGraves(uuid)){
            LinkedList<Grave> playerGraves = PlayerGraves.getGraves(uuid);
            for(Grave g : playerGraves){
                Location gl = g.location;
                if(l.getBlockX() == gl.getBlockX() && l.getBlockY() == gl.getBlockY() && l.getBlockZ() == gl.getBlockZ()) {
                    return g;
                }
            }
        }
        return null;
    }

    public static Grave getFromInventory(String uuid, Inventory i){
        if(hasGraves(uuid)){
            LinkedList<Grave> playerGraves = PlayerGraves.getGraves(uuid);
            for(Grave g : playerGraves){
                if(g.isThisInventory(i)){
                    return g;
                }
            }
        }
        return null;
    }

    public static void deleteEmptyFromPlayer(String uuid){
        if(hasGraves(uuid)){
            LinkedList<Grave> playerGraves = PlayerGraves.getGraves(uuid);
            for(Grave g : playerGraves){
                if(g.isEmpty()){
                    deleteGrave(g);
                }
            }
        }
    }

    public static String[] getGravesStrings(String uuid){
        LinkedList<Grave> playerGraves = getGraves(uuid);

        String[] graveStrings = new String[playerGraves.size()];
        for(int i=0; i<graveStrings.length; i++){
            graveStrings[i] = playerGraves.get(i).toString();
        }

        return graveStrings;
    }

    public static boolean isEmpty(Inventory i){
        for(ItemStack is : i.getContents()){
            if(is != null) return false;
        }
        return true;
    }
}
