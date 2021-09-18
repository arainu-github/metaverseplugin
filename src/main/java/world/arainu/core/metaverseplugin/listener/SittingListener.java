package world.arainu.core.metaverseplugin.listener;

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
import java.util.List;

/**
 * 階段ブロックに座るときに使うイベントリスナーのクラス
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
     * @param e イベント
     */
    @EventHandler
    public void Sitting(PlayerInteractEvent e) {
        Action action = e.getAction();
        Block block = e.getClickedBlock();
        Player player = e.getPlayer();
        File stairsYml = new File(MetaversePlugin.plugin.getDataFolder() + File.separator + "stairs.yml");
        FileConfiguration stairsConfig = YamlConfiguration.loadConfiguration(stairsYml);
        List<String> list = stairsConfig.getStringList("stairs");

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (player.getInventory().getItemInMainHand().getType().isAir()) {
                for (String key : list) {
                    if (block.getType() == Material.matchMaterial(key)) {
                        Stairs stairs = (Stairs) block.getBlockData();
                        if (stairs.getHalf() == Bisected.Half.BOTTOM) {
                            if (stairs.getFacing() == BlockFace.NORTH) {
                                double yaw = 0;
                                this.spawnArmorStand(block, player, yaw);
                            } else if (stairs.getFacing() == BlockFace.EAST) {
                                double yaw = 90;
                                this.spawnArmorStand(block,player, yaw);
                            } else if (stairs.getFacing() == BlockFace.SOUTH) {
                                double yaw = 180;
                                this.spawnArmorStand(block, player, yaw);
                            } else if (stairs.getFacing() == BlockFace.WEST) {
                                double yaw = 270;
                                this.spawnArmorStand(block, player, yaw);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * プレイヤーがアーマースタンドから降りたら、アーマースタンドを消去する
     * 降りた際に階段ブロックに埋まるバグは未だに修正出来ていません /(ㄒoㄒ)/~~
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
