package world.arainu.core.metaverseplugin.scheduler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class DrillingScheduler extends BukkitRunnable {
    private final Block block;
    public int ended = 0;

    public DrillingScheduler(Block block) {
        this.block = block;
    }

    @Override
    public synchronized void cancel() {
        super.cancel();
        ended = 3;
    }

    @Override
    public void run() {
        Vector vector3D = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
        vector3D.add(new Vector(1, 0, 0));
        final Location location = block.getLocation();
        location.add(vector3D);
        final Block breakBlock = block.getWorld().getBlockAt(location);
        if (breakBlock.getType() != Material.BEDROCK) {
            breakBlock.setType(Material.AIR);
        }
        ended = 1;
    }
}
