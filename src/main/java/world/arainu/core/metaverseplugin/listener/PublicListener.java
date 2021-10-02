package world.arainu.core.metaverseplugin.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.iphone.MoveSurvival;
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
        MoveSurvival.Move(e.getPlayer(),"公共施設使用時にサーバーから退出したため、サバイバルサーバーへ強制送還しました。");
    }
}
