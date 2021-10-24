package world.arainu.core.metaverseplugin.iphone;

import org.bukkit.World;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

import java.util.Objects;

public class EnderDragon extends iPhoneBase {

    public static boolean ALIVE;

    public static void reviveDragon(Player player) {
        World world = player.getWorld();
        if (world.getEnvironment() == World.Environment.THE_END) {
            world.getLivingEntities().forEach((livingEntity -> {
                ALIVE = livingEntity instanceof org.bukkit.entity.EnderDragon;
            }));

            if (ALIVE) {
                Objects.requireNonNull(world.getEnderDragonBattle()).initiateRespawn();
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
