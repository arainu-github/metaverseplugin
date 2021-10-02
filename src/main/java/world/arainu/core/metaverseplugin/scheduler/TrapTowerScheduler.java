package world.arainu.core.metaverseplugin.scheduler;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.store.TrapTowerStore;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TrapTowerScheduler extends BukkitRunnable {
    public TrapTowerScheduler(Player p){
        this.p = p;
    }

    @Override
    public void run() {
        if(TrapTowerStore.getUsing_player_list().contains(p.getUniqueId())) {
            Economy econ = MetaversePlugin.getEcon();
            int money = MetaversePlugin.getConfiguration().getInt("traptower.money");
            if (p.isOnline()) {
                if (econ.has(p, money)) {
                    econ.withdrawPlayer(p, money);
                    p.sendMessage(ChatColor.GREEN + "[メタバースプラグイン] 公共施設の使用料" + econ.format(money) + "を正常に徴収しました。");
                    p.sendMessage(ChatColor.GREEN + "[メタバースプラグイン] 残高: " + econ.format(econ.getBalance(p)));
                } else {
                    UUID uuid = p.getUniqueId();
                    p.teleport(Objects.requireNonNull(sqlUtil.getplayerpos(uuid)));
                    sqlUtil.deleteplayerpos(uuid);
                    List<UUID> using_player_list = TrapTowerStore.getUsing_player_list();
                    int i = 0;
                    while (!using_player_list.get(i).equals(uuid)) {
                        i++;
                    }
                    using_player_list.set(i, null);
                    TrapTowerStore.setUsing_player_list(using_player_list);
                    Gui.error(p, "公共施設の使用量" + econ.format(money) + "が銀行から支払えないためサバイバルサーバーへ強制送還しました。");
                    cancel();
                }
            }
        } else {
            cancel();
        }
    }
    Player p;
}
