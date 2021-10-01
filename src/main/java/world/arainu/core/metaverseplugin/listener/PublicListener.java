package world.arainu.core.metaverseplugin.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.store.TrapTowerStore;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 公共施設に関するイベントリスナーのクラス
 *
 * @author kumitatepazuru
 */
public class PublicListener implements Listener {
    /**
     * プレイヤーがログインしたときに公共施設を利用していたら強制送還させる
     *
     * @param e イベント
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (p.getWorld().getName().equals(MetaversePlugin.getConfiguration().getString("world.traptower"))){
            p.teleport(Objects.requireNonNull(sqlUtil.getplayerpos(uuid)));
            sqlUtil.deleteplayerpos(uuid);
            if(TrapTowerStore.getUsing_player_list().contains(uuid)){
                List<UUID> using_player_list = TrapTowerStore.getUsing_player_list();
                int i = 0;
                while (!using_player_list.get(i).equals(uuid)) {
                    i++;
                }
                using_player_list.set(i, null);
                TrapTowerStore.setUsing_player_list(using_player_list);
            }
            Gui.warning(p,"公共施設使用時にサーバーから退出したため、サバイバルサーバーへ強制送還しました。");
        }
    }
}
