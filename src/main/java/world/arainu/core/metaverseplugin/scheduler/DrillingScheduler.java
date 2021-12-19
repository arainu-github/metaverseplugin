package world.arainu.core.metaverseplugin.scheduler;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.utils.Vector3D;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.util.Objects;

public class DrillingScheduler extends BukkitRunnable {
    private final Block block;
    @Getter
    private final Vector3D vector3D;
    private final Vector3D maxVector3D;
    public int ended = 0;

    public DrillingScheduler(Block block, Vector3D vector3D, Vector3D maxVector3D){
        this.block = block;
        this.vector3D = vector3D;
        this.maxVector3D = maxVector3D;
    }

    @Override
    public void run() {
        if(vector3D.y<maxVector3D.y){
            if(vector3D.z<maxVector3D.z){
                if (!(vector3D.x < maxVector3D.x)) {
                    vector3D.x = 0;
                    vector3D.z++;
                }
            } else {
                vector3D.z = 0;
                vector3D.y++;
            }
            if(vector3D.y<maxVector3D.y){
                final Location location = block.getLocation();
                location.add(vector3D.x+1,vector3D.y,vector3D.z);
                final Block breakBlock = block.getWorld().getBlockAt(location);
                if(breakBlock.getType() != Material.BEDROCK) {
                    breakBlock.setType(Material.AIR);
                }
                vector3D.x++;
                ended = 1;
            }
        } else {
            ended = 2;
        }
    }
}
