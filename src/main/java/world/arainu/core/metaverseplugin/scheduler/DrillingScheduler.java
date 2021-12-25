package world.arainu.core.metaverseplugin.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.ItemUtil;

import java.util.Objects;

public class DrillingScheduler extends BukkitRunnable {
    private final Block block;
    private final ItemStack useTool;
    public int ended = 0;

    public DrillingScheduler(Block block, ItemStack useTool) {
        this.block = block;
        this.useTool = useTool;
    }

    @Override
    public synchronized void cancel() {
        super.cancel();
        ended = 3;
    }

    @Override
    public void run() {
        Vector vector3D = Objects.requireNonNull((Vector) block.getMetadata("metaverse-drilling__vector2").get(0).value());
        boolean isItem = block.getMetadata("metaverse-drilling__item").get(0).asBoolean();
        vector3D.add(new Vector(1, 0, 0));
        final Location location = block.getLocation();
        location.add(vector3D);
        final Block breakBlock = location.getBlock();
        if (breakBlock.getType() != Material.BEDROCK) {
            Bukkit.getLogger().info(String.valueOf(isItem));
            if(isItem){
                Location chestLocation = block.getLocation();
                chestLocation.add(0,1,0);
                if(chestLocation.getBlock().getType() == Material.CHEST){
                    breakBlock.getDrops(useTool).forEach(e -> ItemUtil.addItem(e,((Chest)chestLocation.getBlock().getState()).getInventory(),chestLocation));
                } else {
                    breakBlock.getDrops(useTool).forEach(e -> chestLocation.getWorld().dropItemNaturally(chestLocation, e));
                }
            }
            breakBlock.setType(Material.AIR);
        }
        ended = 1;
    }
}
