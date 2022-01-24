package world.arainu.core.metaverseplugin.listener;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.List;

/**
 * 進捗データを操作する内部関数群をまとめたクラス。
 * @author kumitatepazuru
 */
public class AdvancementListener implements Listener {
    private void addPlayerAdvancement(Advancement advancement,Player p){
        if(advancement.getDisplay() != null) {
            String id = advancement.getKey().getNamespace() + ":" + advancement.getKey().getKey();
            AdvancementProgress advancementProgress = p.getAdvancementProgress(advancement);
            List<String> awarded = advancementProgress.getAwardedCriteria().stream().toList();
            List<String> remaining = advancementProgress.getRemainingCriteria().stream().toList();
            sqlUtil.addPlayerAdvancement(p.getUniqueId(), id, awarded, remaining, advancementProgress.isDone());
        }
    }

    /**
     * プレイヤー入室時にSQL上にプレイヤーの進捗データを保存し、同期する関数。
     * @param e イベント
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Bukkit.getScheduler().runTaskAsynchronously(MetaversePlugin.getInstance(),() -> {
            Player p = e.getPlayer();
            MetaversePlugin.logger().info("syncing advancement data");
            sqlUtil.removePlayerAdvancement(p.getUniqueId());
            Bukkit.advancementIterator().forEachRemaining(advancement -> addPlayerAdvancement(advancement,p));
            MetaversePlugin.logger().info("synced");
        });
    }

    /**
     * プレイヤーが進捗を達成したときにSQL上のプレイヤーの進捗データを更新する関数。
     * @param e イベント
     */
    @EventHandler
    public void onPlayerAdvancementCriterionGrant(PlayerAdvancementCriterionGrantEvent e){
        addPlayerAdvancement(e.getAdvancement(),e.getPlayer());
    }
}
