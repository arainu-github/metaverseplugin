package world.arainu.core.metaverseplugin.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import world.arainu.core.metaverseplugin.MetaversePlugin;

/**
 * サーバーの動作に関するイベントリスナーのクラス
 *
 * @author kumitatepazuru
 */
public class ServerListener implements Listener {
    /**
     * プレイヤーがログインしたときにそのプレイヤーを使用してBungeeCordの名前を取得する
     *
     * @param e イベント
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        player.sendPluginMessage(MetaversePlugin.getInstance(), "BungeeCord", out.toByteArray());
    }
}
