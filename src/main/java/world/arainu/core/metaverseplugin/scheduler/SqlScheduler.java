package world.arainu.core.metaverseplugin.scheduler;

import org.bukkit.scheduler.BukkitRunnable;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

public class SqlScheduler extends BukkitRunnable {
    @Override
    public void run() {
        sqlUtil.ping();
    }
}
