package es.mcers.Mik_C.PlayerGraves;

import org.bukkit.scheduler.BukkitRunnable;

public class DeleteGraveTask extends BukkitRunnable {
    private Grave toDelete = null;

    DeleteGraveTask(Grave grave){
        this.toDelete = grave;
    }

    public void run() {
        toDelete.drop();

        //Delete grave
        PlayerGraves.deleteGrave(toDelete);

        this.cancel();
    }
}
