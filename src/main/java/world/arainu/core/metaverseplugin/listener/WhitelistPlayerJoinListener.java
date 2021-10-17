package world.arainu.core.metaverseplugin.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    /**
     * プレイヤーの参加を待つリスナー
     *
     * @param e イベント
     */

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!Bukkit.hasWhitelist()) {
            Player player = e.getPlayer();
            if (!Bukkit.getWhitelistedPlayers().contains(player)) {
                player.kick(Component.text("貴方はホワイトリストに入っていません！入りたい場合は、Discordの方でスタッフにメッセージを送ってください。").color(NamedTextColor.RED));
            }
        }
    }
}
