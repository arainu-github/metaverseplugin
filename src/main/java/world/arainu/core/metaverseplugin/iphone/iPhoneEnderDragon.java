package world.arainu.core.metaverseplugin.iphone;

import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

import java.util.Objects;

public class iPhoneEnderDragon extends iPhoneBase {

    public static boolean ALIVE = false;

    public static void reviveDragon(Player player) {
        World world = player.getWorld();
        if (world.getEnvironment() == World.Environment.THE_END) {

            for (LivingEntity entity : world.getLivingEntities()) {
                if (entity instanceof EnderDragon) {
                    ALIVE = true;
                    break;
                }
            }

            if (ALIVE) {
                Objects.requireNonNull(world.getEnderDragonBattle()).initiateRespawn();
                ChatUtil.success(player, "エンダードラゴンを復活させました！");
            } else {
                ChatUtil.warning(player, "まだエンドラは生きています");
            }

        } else {
            ChatUtil.warning(player, "あなたはエンドにいません！");
        }
    }

    @Override
    public void executeGui(MenuItem menuItem) {
        reviveDragon(Objects.requireNonNull(menuItem.getClicker()));
    }
}
