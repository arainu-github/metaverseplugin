package world.arainu.core.metaverseplugin.iphone;

import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.store.TrapTowerStore;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 公共施設からサバイバルサーバーに戻るボタンを実現するクラス
 */
public class MoveSurvival extends iPhoneBase {
    /**
     * プレイヤーをサバイバルサーバーに戻す関数
     * @param p 対象のプレイヤー
     * @param msg 戻すときのメッセージ（nullの場合は送らない）
     */
    public static void Move(Player p, String msg) {
        UUID uuid = p.getUniqueId();
        if (p.getWorld().getName().equals(MetaversePlugin.getConfiguration().getString("world.traptower"))) {
            try {
                p.teleport(Objects.requireNonNull(sqlUtil.getplayerpos(uuid)));
                sqlUtil.deleteplayerpos(uuid);
                if (TrapTowerStore.getUsing_player_list().contains(uuid)) {
                    List<UUID> using_player_list = TrapTowerStore.getUsing_player_list();
                    int i = 0;
                    while (!using_player_list.get(i).equals(uuid)) {
                        i++;
                    }
                    using_player_list.set(i, null);
                    TrapTowerStore.setUsing_player_list(using_player_list);
                }
                if (msg != null) Gui.warning(p, msg);
            } catch (NullPointerException err) {
                p.sendMessage("NullPointerException");
            }
        }
    }

    @Override
    public void executeGui(MenuItem menuItem) {
        Player p = Objects.requireNonNull(menuItem.getClicker());
        Move(p, null);
    }
}
