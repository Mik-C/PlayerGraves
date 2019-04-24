package es.mcers.Mik_C.PlayerGraves;

import org.bukkit.scheduler.BukkitRunnable;

public class SaveGravesTask extends BukkitRunnable{
    SaveGravesTask(){
    }

    public void run() {
        PlayerGraves.saveGraves();
    }
}
