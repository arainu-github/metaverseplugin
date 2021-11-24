package world.arainu.core.metaverseplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.List;
import java.util.UUID;


/**
 * 独自のホワイトリストシステムに使用するリスナー
 *
 * @author JolTheGreat
 */

public class CommandWhitelist extends CommandBase implements Listener {
    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            final String operator = args[0];

            if (!Bukkit.hasWhitelist()) {
                ChatUtil.warning(sender, "ホワイトリストはオフになっています。");
            }
            boolean error = false;

            switch (operator) {
                case "add" -> {
                    if (args.length == 2) {
                        final OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
                        final UUID target = p.getUniqueId();
                        sqlUtil.addWhitelist(target);
                        p.setWhitelisted(true);
                        ChatUtil.success(sender, "データベースにプレイヤーを追加しました。");
                    } else {
                        error = true;
                    }

                }

                case "remove" -> {
                    if (args.length == 2) {
                        final OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
                        final UUID target = p.getUniqueId();
                        sqlUtil.removeWhitelist(target);
                        p.setWhitelisted(false);
                        ChatUtil.success(sender, "データベースからプレイヤーを取り除きました。");
                    } else {
                        error = true;
                    }
                }

                case "list" -> {
                    if (args.length == 1) {
                        ChatUtil.success(sender, "ホワイトリストに入っているプレイヤー");
                        List<UUID> whitelist = sqlUtil.getWhitelist();

                        assert whitelist != null;
                        for (UUID uuid : whitelist) {
                            //プレイヤーを表示する際にうるさくならないようにplaysoundをfalseに
                            String name = Bukkit.getOfflinePlayer(uuid).getName();
                            if(name == null){
                                name = uuid.toString();
                            }
                            ChatUtil.success(sender, name, false);
                        }
                        ChatUtil.success(sender, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", false);
                    } else {
                        error = true;
                    }
                }

                case "on" -> {
                    if (args.length == 1) {
                        ChatUtil.success(sender, "ホワイトリストをオンにしました");
                        Bukkit.setWhitelist(true);
                    } else {
                        error = true;
                    }
                }

                case "off" -> {
                    if (args.length == 1) {
                        ChatUtil.success(sender, "ホワイトリストをオフにしました");
                        Bukkit.setWhitelist(false);
                    } else {
                        error = true;
                    }
                }

                default -> error = true;
            }

            if (error) {
                ChatUtil.error(sender, "引数が多すぎるか少なすぎるかそもそもコマンドが存在しないかのいずれかの問題が発生しました");
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                                      String[] args) {
        return List.of("on", "off", "add", "remove", "list");
    }
}
