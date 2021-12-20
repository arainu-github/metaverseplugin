package world.arainu.core.metaverseplugin.scheduler;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.utils.Vector3D;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

import java.util.Objects;

public class DrillingScheduler extends BukkitRunnable {
    private final Block block;
    private final Player player;
    private final Vector3D maxVector3D;
    public int ended = 0;

    public DrillingScheduler(Block block, Player player, Vector3D maxVector3D) {
        this.block = block;
        this.maxVector3D = maxVector3D;
        this.player = player;
    }

    @Override
    public synchronized void cancel(){
        super.cancel();
        ended = 3;
    }

    @Override
    public void run() {
        Vector3D vector3D = Objects.requireNonNull((Vector3D) block.getMetadata("metaverse-drilling__vector2").get(0).value());
        if (vector3D.y < maxVector3D.y) {
            final Location location = block.getLocation();
            location.add(vector3D.x + 1, vector3D.y, vector3D.z);

            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();

            if (query.testState(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_BREAK)) {
                final Block breakBlock = block.getWorld().getBlockAt(location);
                if (breakBlock.getType() != Material.BEDROCK) {
                    breakBlock.setType(Material.AIR);
                }
            } else {
                ChatUtil.warning(player,"保護区域のため、X:"+location.getBlockX()+",Y:"+location.getBlockY()+",Z:"+location.getBlockZ()+"の採掘ができませんでした。");
            }
            vector3D.x++;
            block.setMetadata("metaverse-drilling__vector2", new FixedMetadataValue(MetaversePlugin.getInstance(), vector3D));
            ended = 1;
        } else {
            ended = 2;
        }
    }
}
