package world.arainu.core.metaverseplugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import world.arainu.core.metaverseplugin.gui.casino.SlotMachine;
import world.arainu.core.metaverseplugin.iphone.MoveSurvival;

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
//        SlotMachine.start(e.getPlayer());
        MoveSurvival.Move(e.getPlayer(),"公共施設使用時にサーバーから退出したため、サバイバルサーバーへ強制送還しました。");
    }
}
