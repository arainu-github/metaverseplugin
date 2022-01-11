package world.arainu.core.metaverseplugin.iphone;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

public class Spawn extends iPhoneBase {
    @Override
    public void executeGui(MenuItem menuItem) {
        Player player = menuItem.getClicker();
        Economy econ = MetaversePlugin.getEcon();
        if (econ.has(player, 500)) {
            econ.withdrawPlayer(player, 500);
            player.teleport(player.getWorld().getSpawnLocation());
            ChatUtil.success(player, econ.format(500) + "を支払い初期スポーン地点にテレポートしました。");
        } else {
            ChatUtil.error(player, "あなたはそこまでお金を持っていません！\n残高: " + econ.format(econ.getBalance(player)));
        }
    }
}
