package es.mcers.Mik_C.PlayerGraves;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;

public class DeleteGraveTask extends BukkitRunnable {
    private Grave toDelete = null;

    DeleteGraveTask(Grave grave){
        this.toDelete = grave;
    }

    public void run() {
        toDelete.drop();

        //Delete grave
        LinkedList<Grave> playerGraves = PlayerGraves.getGraves(toDelete.owner);
        PlayerGraves.deleteGrave(toDelete);

        this.cancel();
    }
}
