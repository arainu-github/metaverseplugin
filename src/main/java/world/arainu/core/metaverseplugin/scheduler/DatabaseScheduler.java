package world.arainu.core.metaverseplugin.scheduler;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

/**
 * 定期的にデータベースからデータを取ってきて、config.ymlに反映するクラス。
 *
 * @author jolthegreat
 */

public class DatabaseScheduler extends BukkitRunnable {
    @SneakyThrows
    @Override
    public void run() {
        FileConfiguration config = MetaversePlugin.getConfiguration();
        config.loadFromString("mysql:\n" +
                "  db_name: metaverse\n" +
                "  db_name_public: metaverse_public\n" +
                "  port: 3306\n" +
                "  url: mc_mysql\n" +
                "  user: root\n" +
                "  pass: password\n" +
                sqlUtil.getConfig());
    }
}
