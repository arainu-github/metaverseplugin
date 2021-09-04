package world.arainu.core.metaverseplugin.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

/**
 * 階段ブロックに座るときに使うイベントリスナーのクラス
 * @author AreaEffectCloud
 */
public class SittingListener implements Listener {
    /**
     * アーマースタンドの召喚
     */
    private void spawnArmorStand(Block block, Player player) {

        synchronized (this) {
            Location blockLocation = block.getLocation();

            double stairsX = blockLocation.getBlockX() + 0.5;
            double stairsY = blockLocation.getBlockY() - 1.2;
            double stairsZ = blockLocation.getBlockZ() + 0.5;

            Location loc = new Location(block.getWorld(), stairsX, stairsY, stairsZ);

            Entity armorStand = loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            armorStand.addPassenger(player);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            ArmorStand as = (ArmorStand) armorStand;
            as.setVisible(false);
        }
    }

    /**
     * プレイヤーがメインハンドに何も持たずに板材系の階段ブロックをクリックしたとき、上記の"spawnArmorStand"を実行する
     * プレイヤーがアーマースタンドから降りたら、アーマースタンドを消去する
     * @param e イベント
     */
    @EventHandler
    public void Sitting(PlayerInteractEvent e) {
        Action action = e.getAction();
        Block block = e.getClickedBlock();
        Player player = e.getPlayer();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (player.getInventory().getItemInMainHand().getType().isAir()) {

                if (block.getType().equals(Material.ACACIA_STAIRS)) {
                    spawnArmorStand(block, player);

                } else if (block.getType().equals(Material.BIRCH_STAIRS)) {
                    spawnArmorStand(block, player);

                } else if (block.getType().equals(Material.DARK_OAK_STAIRS)) {
                    spawnArmorStand(block, player);

                } else if (block.getType().equals(Material.JUNGLE_STAIRS)) {
                    spawnArmorStand(block, player);

                } else if (block.getType().equals(Material.OAK_STAIRS)) {
                    spawnArmorStand(block, player);

                } else if (block.getType().equals(Material.SPRUCE_STAIRS)) {
                    spawnArmorStand(block, player);

                } else if (block.getType().equals(Material.CRIMSON_STAIRS)) {
                    spawnArmorStand(block, player);

                } else if (block.getType().equals(Material.WARPED_STAIRS)) {
                    spawnArmorStand(block, player);

                } else if (block.getType().equals(Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS)) {
                    spawnArmorStand(block, player);

                } else if (block.getType().equals(Material.POLISHED_BLACKSTONE_STAIRS)) {
                    spawnArmorStand(block, player);

                }
            }
        }
    }

    /**
     * プレイヤーがアーマースタンドから降りたら、アーマースタンドを消去する
     * @param e イベント
     */
    @EventHandler
    public void Remove(EntityDismountEvent e) {
        Player player = e.getPlayer();
        if (e.getDismounted().getType() == EntityType.ARMOR_STAND) {
            ArmorStand armorStand = (ArmorStand) e.getDismounted();
            armorStand.remove();
            Location loc = player.getLocation();
            loc.setY(loc.getY() + 1);
            player.teleport(loc);
        }
    }
}
