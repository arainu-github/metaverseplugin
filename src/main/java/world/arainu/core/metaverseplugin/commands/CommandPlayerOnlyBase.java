package world.arainu.core.metaverseplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * プレイヤーだけが実行できるコマンドを実装するクラス。
 *
 * @author kumitatepazuru
 */
public abstract class CommandPlayerOnlyBase extends CommandBase {
    /**
     * プレイヤーがコマンドが呼び出されたときに動く関数
     *
     * @param player コマンドを実行したプレイヤーの情報が入っている
     * @param args   子コマンド(引数)が入っている
     * @return 正常に実行された場合はtrue、そうでない場合はfalse
     */
    public abstract boolean execute(Player player, String[] args);

    @Override
    public final boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "[ERR] プレイヤーが実行してください");
            return true;
        }
        return execute((Player) sender, args);
    }
}
