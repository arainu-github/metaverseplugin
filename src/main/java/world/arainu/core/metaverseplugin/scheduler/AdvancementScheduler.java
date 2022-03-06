package world.arainu.core.metaverseplugin.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.listener.AdvancementListener;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static world.arainu.core.metaverseplugin.listener.AdvancementListener.addPlayerAdvancement;
import static world.arainu.core.metaverseplugin.listener.AdvancementListener.removeQueue;

public class AdvancementScheduler extends BukkitRunnable {
    @Override
    public void run() {
        List<Player> queue = AdvancementListener.getSyncQueue();
        if(queue.size() > 0) {
            Player p = queue.get(0);
            if (p.isOnline()) {
                MetaversePlugin.logger().info("syncing advancement data... size:" + queue.size());
                sqlUtil.removePlayerAdvancement(p.getUniqueId());
                AtomicInteger i = new AtomicInteger();
                AtomicInteger j = new AtomicInteger();
                Bukkit.advancementIterator().forEachRemaining(advancement -> {
                    Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(),() -> addPlayerAdvancement(advancement, p),j.get());
                    i.getAndIncrement();
                    if(j.get() == 50){
                        j.getAndIncrement();
                    }
                });
                MetaversePlugin.logger().info("synced");
            }
            removeQueue();
        }
    }
}
