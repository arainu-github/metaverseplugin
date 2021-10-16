package world.arainu.core.metaverseplugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


/**
 * プレイヤーの参加を待つクラス
 *
 * @author JolTheGreat
 */

public class WhitelistPlayerJoinListener implements Listener {


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!Bukkit.getWhitelistedPlayers().contains(player)) {
            player.kickPlayer("貴方はホワイトリストに入っていません！入りたい場合は、Discordの方でスタッフにメッセージを送ってください。");
        }
    }
}
