package world.arainu.core.metaverseplugin.iphone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

import java.util.Objects;

/**
 * iPhoneでエンドラを復活するクラス
 *
 * @author JolTheGreat
 */

public class iPhoneEnderDragon extends iPhoneBase {
    /**
     * エンドラを復活させる関数
     * @param player プレイヤー
     */
    public static void reviveDragon(Player player) {
        World world = player.getWorld();
        DragonBattle db = Objects.requireNonNull(world.getEnderDragonBattle());
        final Location portalLocation = Objects.requireNonNull(db.getEndPortalLocation());
        world.spawnEntity(new Location(world, 0.5,portalLocation.getBlockY()+1,3.5), EntityType.ENDER_CRYSTAL);
        world.spawnEntity(new Location(world, 0.5,portalLocation.getBlockY()+1,-2.5), EntityType.ENDER_CRYSTAL);
        world.spawnEntity(new Location(world, 3.5,portalLocation.getBlockY()+1,0.5), EntityType.ENDER_CRYSTAL);
        world.spawnEntity(new Location(world, -2.5,portalLocation.getBlockY()+1,0.5), EntityType.ENDER_CRYSTAL);
        db.initiateRespawn();
        ChatUtil.success(player, "エンダードラゴンを復活させました！");
    }

    @Override
    public void executeGui(MenuItem menuItem) {
        reviveDragon(Objects.requireNonNull(menuItem.getClicker()));
    }
}
