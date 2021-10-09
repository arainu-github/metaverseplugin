package world.arainu.core.metaverseplugin.scheduler;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.store.BankStore;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * 銀行システムに関するループスケジュール
 *
 * @author kumitatepazuru
 */
public class MoneyScheduler extends BukkitRunnable {
    @Override
    public void run() {
        Economy econ = MetaversePlugin.getEcon();
        HashMap<UUID, Long> login_money_map = BankStore.getLogin_money_map();
        for (UUID i : login_money_map.keySet()) {
            if (System.currentTimeMillis() / 1000 - login_money_map.get(i) > 600) {
                Player p = Bukkit.getPlayer(i);
                int login_money = MetaversePlugin.getConfiguration().getInt("econ.login_money");
                econ.depositPlayer(p,login_money);
                ChatUtil.success(Objects.requireNonNull(p),"ログイン時間ボーナスにより、口座金額が"+econ.format(login_money)+"増えました！\n残高:"+econ.format(econ.getBalance(p)));
                login_money_map.replace(p.getUniqueId(),System.currentTimeMillis() / 1000);
            }
        }
        BankStore.setLogin_money_map(login_money_map);
    }
}
