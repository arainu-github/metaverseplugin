package world.arainu.core.metaverseplugin.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.*;

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

            if(!new ArrayList<>(shit_location_map.values()).contains(blockLocation)) {
                double stairsX = blockLocation.getBlockX() + 0.5;
                double stairsY = blockLocation.getBlockY() - 1.2;
                double stairsZ = blockLocation.getBlockZ() + 0.5;

                Location loc = new Location(block.getWorld(), stairsX, stairsY, stairsZ);
                shit_location_map.put(player.getUniqueId(), loc);

                Entity armorStand = loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
                armorStand.addPassenger(player);
                armorStand.setInvulnerable(true);
                armorStand.setGravity(false);
                ArmorStand as = (ArmorStand) armorStand;
                as.setVisible(false);
            }
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
        Block block = Objects.requireNonNull(e.getClickedBlock());
        Player player = e.getPlayer();

        if (player.getInventory().getItemInMainHand().getType().isAir() && stair_matrials.contains(block.getType())) {
            if (action == Action.RIGHT_CLICK_BLOCK) {
                Stairs stairs = (Stairs) block.getBlockData();
                if (stairs.getHalf().equals(Bisected.Half.BOTTOM)) {
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
        if (e.getDismounted().getType() == EntityType.ARMOR_STAND) {
            Entity entity = e.getEntity();
            ArmorStand armorStand = (ArmorStand) e.getDismounted();
            armorStand.remove();
            Location loc = entity.getLocation();
            loc.setY(loc.getY() + 0.6);
            entity.teleport(loc);
            shit_location_map.remove(e.getEntity().getUniqueId());
        }
    }

    private final HashMap<UUID,Location> shit_location_map = new HashMap<>();
    private final List<Material> stair_matrials = Arrays.asList(
            Material.CUT_COPPER_STAIRS,
            Material.EXPOSED_CUT_COPPER_STAIRS,
            Material.WEATHERED_CUT_COPPER_STAIRS,
            Material.OXIDIZED_CUT_COPPER_STAIRS,
            Material.WAXED_CUT_COPPER_STAIRS,
            Material.WAXED_EXPOSED_CUT_COPPER_STAIRS,
            Material.WAXED_WEATHERED_CUT_COPPER_STAIRS,
            Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS,
            Material.PURPUR_STAIRS,
            Material.OAK_STAIRS,
            Material.COBBLESTONE_STAIRS,
            Material.BRICK_STAIRS,
            Material.STONE_BRICK_STAIRS,
            Material.NETHER_BRICK_STAIRS,
            Material.SANDSTONE_STAIRS,
            Material.SPRUCE_STAIRS,
            Material.BIRCH_STAIRS,
            Material.JUNGLE_STAIRS,
            Material.CRIMSON_STAIRS,
            Material.WARPED_STAIRS,
            Material.QUARTZ_STAIRS,
            Material.ACACIA_STAIRS,
            Material.DARK_OAK_STAIRS,
            Material.PRISMARINE_STAIRS,
            Material.PRISMARINE_BRICK_STAIRS,
            Material.DARK_PRISMARINE_STAIRS,
            Material.RED_SANDSTONE_STAIRS,
            Material.POLISHED_GRANITE_STAIRS,
            Material.SMOOTH_RED_SANDSTONE_STAIRS,
            Material.MOSSY_STONE_BRICK_STAIRS,
            Material.POLISHED_DIORITE_STAIRS,
            Material.MOSSY_COBBLESTONE_STAIRS,
            Material.END_STONE_BRICK_STAIRS,
            Material.STONE_STAIRS,
            Material.SMOOTH_SANDSTONE_STAIRS,
            Material.SMOOTH_QUARTZ_STAIRS,
            Material.GRANITE_STAIRS,
            Material.ANDESITE_STAIRS,
            Material.RED_NETHER_BRICK_STAIRS,
            Material.POLISHED_ANDESITE_STAIRS,
            Material.DIORITE_STAIRS,
            Material.COBBLED_DEEPSLATE_STAIRS,
            Material.POLISHED_DEEPSLATE_STAIRS,
            Material.DEEPSLATE_BRICK_STAIRS,
            Material.DEEPSLATE_TILE_STAIRS,
            Material.BLACKSTONE_STAIRS,
            Material.POLISHED_BLACKSTONE_STAIRS,
            Material.POLISHED_BLACKSTONE_BRICK_STAIRS
    );
}
