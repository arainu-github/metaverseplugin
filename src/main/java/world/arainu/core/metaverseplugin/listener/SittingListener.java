package world.arainu.core.metaverseplugin.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * 階段ブロックに座るときに使うイベントリスナーのクラス
 *
 * @author AreaEffectCloud
 */
public class SittingListener implements Listener {
    /**
     * アーマースタンドの召喚
     */
    public void spawnArmorStand(Block block, Player player, double yaw) {

        synchronized (this) {
            Location blockLocation = block.getLocation();

            double stairsX = blockLocation.getBlockX() + 0.5;
            double stairsY = blockLocation.getBlockY() - 1.2;
            double stairsZ = blockLocation.getBlockZ() + 0.5;

            Location loc = new Location(block.getWorld(), stairsX, stairsY, stairsZ);
            loc.setYaw((float) yaw);

            Entity armorstand = loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            ArmorStand as = (ArmorStand) armorstand;
            as.setInvisible(true);
            armorstand.addPassenger(player);
            armorstand.setGravity(false);
            armorstand.setInvulnerable(true);

        }
    }

    /**
     * プレイヤーがメインハンドに何も持たずに階段ブロックをクリックしたとき、上記の"spawnArmorStand"を実行する
     * 他プレイヤーが座っている階段ブロックに座ることは出来ない
     *
     * @param e イベント
     */
    @EventHandler
    public void Sitting(PlayerInteractEvent e) {
        Action action = e.getAction();
        Block block = e.getClickedBlock();
        Player player = e.getPlayer();
        File stairsYml = new File(MetaversePlugin.getInstance().getDataFolder() + File.separator + "stairs.yml");
        FileConfiguration stairsConfig = YamlConfiguration.loadConfiguration(stairsYml);
        List<String> list = stairsConfig.getStringList("stairs");

        if (action == Action.RIGHT_CLICK_BLOCK) {
            for (String key : list) {
                if (Objects.requireNonNull(block).getType() == Material.matchMaterial(key)) {
                    if (player.getInventory().getItemInMainHand().getType().isAir()) {
                        Stairs stairs = (Stairs) block.getBlockData();
                        if (stairs.getHalf() == Bisected.Half.BOTTOM) {
                            Collection<Entity> armorstand = block.getWorld().getNearbyEntities(block.getLocation(), 0.5, 1, 0.5, (entity) -> entity.getType() == EntityType.ARMOR_STAND);
                            if (armorstand.isEmpty()) {
                                if (stairs.getFacing() == BlockFace.NORTH) {
                                    double yaw = 0;
                                    this.spawnArmorStand(block, player, yaw);
                                } else if (stairs.getFacing() == BlockFace.EAST) {
                                    double yaw = 90;
                                    this.spawnArmorStand(block, player, yaw);
                                } else if (stairs.getFacing() == BlockFace.SOUTH) {
                                    double yaw = 180;
                                    this.spawnArmorStand(block, player, yaw);
                                } else if (stairs.getFacing() == BlockFace.WEST) {
                                    double yaw = 270;
                                    this.spawnArmorStand(block, player, yaw);
                                }
                            }
                        }
                    } else {
                        player.sendActionBar(Component.text("何も持っていない状態だと座れます"));
                    }
                }
            }
        }
    }

    /**
     * プレイヤーがアーマースタンドから降りたら、アーマースタンドを消去する
     *
     * @param e イベント
     */
    @EventHandler
    public void Remove(EntityDismountEvent e) {
        if (e.getDismounted().getType() == EntityType.ARMOR_STAND) {
            ArmorStand armorStand = (ArmorStand) e.getDismounted();
            armorStand.remove();
            Entity entity = e.getEntity();
            Location loc = entity.getLocation();
            loc.setY(loc.getY() + 0.6);
            entity.teleport(loc);
        }
    }
}
