package es.mcers.Mik_C.PlayerGraves;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PlayerGraves extends JavaPlugin {
    public static Server server = null;
    public static Map<String, LinkedList<Grave>> graves = null;

    @Override
    public void onEnable() {
        server = getServer();

        loadGraves();
        server.getPluginManager().registerEvents(new Listeners(), this);
    }

    @Override
    public void onDisable() {
        //Save all graves to file
    }

    private void loadGraves(){
        graves = new HashMap<String, LinkedList<Grave>>();

        //Load all graves from file
    }

    public static void printGraves(){
        System.out.println("Showing graves list");
        for(Map.Entry<String, LinkedList<Grave>> pair : graves.entrySet()){
            System.out.println(pair.getKey() + " :");
            for(Grave grave : pair.getValue()){
                System.out.println(grave.toString());
            }
            System.out.println("-");
        }
    }
}
