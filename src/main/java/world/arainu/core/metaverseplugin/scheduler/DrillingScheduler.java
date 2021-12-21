package world.arainu.core.metaverseplugin.scheduler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.utils.Vector3D;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.util.Objects;

public class DrillingScheduler extends BukkitRunnable {
    private final Block block;
    private final Vector3D maxVector3D;
    public int ended = 0;

    public DrillingScheduler(Block block, Vector3D maxVector3D) {
        this.block = block;
        this.maxVector3D = maxVector3D;
    }

    @Override
    public synchronized void cancel(){
        super.cancel();
        ended = 3;
    }

    @Override
    public void run() {
        Vector3D vector3D = Objects.requireNonNull((Vector3D) block.getMetadata("metaverse-drilling__vector2").get(0).value());
            final Location location = block.getLocation();
            location.add(vector3D.x + 1, vector3D.y, vector3D.z);
            final Block breakBlock = block.getWorld().getBlockAt(location);
            if (breakBlock.getType() != Material.BEDROCK) {
                breakBlock.setType(Material.AIR);
            }
            vector3D.x++;
            ended = 1;
    }
}
