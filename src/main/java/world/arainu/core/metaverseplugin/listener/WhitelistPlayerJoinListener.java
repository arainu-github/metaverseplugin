package world.arainu.core.metaverseplugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

/**
 * プレイヤーの参加を待つクラス
 * @author JolTheGreat
 */

public class WhitelistPlayerJoinListener implements Listener {


    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPlayedBefore()) {
            sqlUtil.addWhiteList(player.getUniqueId());
            Bukkit.getServer().dispatchCommand(MetaversePlugin.getPlugin(MetaversePlugin.class).getServer().getConsoleSender(), "whitelist add " + player.getName());
        }

    }
}
