package world.arainu.core.metaverseplugin.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 30秒ごとに、データベースからホワリスを取ってくるクラス
 * ついでにBukkitのホワリスとデータべースのホワリスを同じにする
 *
 * @author JolTheGreat
 */

public class WhitelistSyncScheduler extends BukkitRunnable {
    @Override
    public void run() {
        List<OfflinePlayer> originalWhitelist = new ArrayList<>(Bukkit.getWhitelistedPlayers());
        List<OfflinePlayer> newWhitelist = new ArrayList<>();

        for (UUID uuid : Objects.requireNonNull(sqlUtil.getWhitelist())) {
            newWhitelist.add(Bukkit.getServer().getOfflinePlayer(uuid));
        }

        for (OfflinePlayer player : originalWhitelist) {
            if (!newWhitelist.contains(player)) {
                originalWhitelist.remove(player);
                player.setWhitelisted(false);
            }
        }

        for (OfflinePlayer player : newWhitelist) {
            if (!originalWhitelist.contains(player)) {
                originalWhitelist.add(player);
                player.setWhitelisted(true);
            }
        }

        Bukkit.reloadWhitelist();
    }
}