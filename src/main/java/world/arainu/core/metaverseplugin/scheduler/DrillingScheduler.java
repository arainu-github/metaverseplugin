package world.arainu.core.metaverseplugin.scheduler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import world.arainu.core.metaverseplugin.utils.ItemUtil;

import java.util.Objects;

/**
 * 採掘をするときに使用するスケジューラー。
 * 一定時間経過後に採掘される。
 * @author kumitatepazuru
 */
public class DrillingScheduler extends BukkitRunnable {
    private final Block block;
    private final ItemStack useTool;
    /**
     * 採掘が終了したかどうかの判定に使用するフラグ。
     * 1: 正常終了
     * 3: 一時停止（このスケジューラーがキャンセルされた）
     */
    public int ended = 0;

    /**
     * 初期化
     * @param block 採掘マシーンのブロックデータ
     * @param useTool 使用するツール
     */
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
