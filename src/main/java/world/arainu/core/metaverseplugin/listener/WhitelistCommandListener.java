package world.arainu.core.metaverseplugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.List;
import java.util.UUID;


/**
 * Bukkitに元々あるホワリスのコマンドが実行されたらデータベース側でも処理を行うリスナ＝
 *
 * @author JolTheGreat
 */

public class WhitelistCommandListener implements Listener {

    /**
     * コマンドをサーバー側が検知したときに初めて実行される関数
     * /whitelist remove, /whitelist add と /whitelist list　をデータベースから引っ張ってきて実現する
     *
     * @param event イベント
     */

    @EventHandler
    public void onCommandExecute(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        String command = args[0];
        Player caller = event.getPlayer();

        if (command.equals("/whitelist") && args.length == 3) {
            String operator = args[1];
            UUID target = Bukkit.getOfflinePlayer(args[2]).getUniqueId();

            if (!Bukkit.hasWhitelist()) {
                ChatUtil.warning(caller, "ホワイトリストはオフになっていますが、データベースに情報を追加します。");
            }

            switch (operator) {
                case "add" -> {
                    sqlUtil.addWhitelist(target);
                    ChatUtil.success(caller, "データベースにプレイヤーを追加しました。");
                }

                case "remove" -> {
                    sqlUtil.removeWhitelist(target);
                    ChatUtil.success(caller, "データベースからプレイヤーを取り除きました。");
                }

                case "list" -> {
                    ChatUtil.success(caller, "データベース上のホワリスにいるプレイヤーです：");
                    List<UUID> whitelist = sqlUtil.getWhitelist();

                    assert whitelist != null;
                    for (UUID uuid : whitelist) {
                        //プレイヤーを表示する際にうるさくならないようにplaysoundをfalseに
                        ChatUtil.success(caller, uuid.toString() + ", ", false);
                    }
                }
            }
        }
    }
}
