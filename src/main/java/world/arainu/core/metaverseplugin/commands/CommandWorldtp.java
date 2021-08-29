package world.arainu.core.metaverseplugin.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.store.ServerStore;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class CommandWorldtp extends CommandPlayerOnlyBase {
    /**
     * worldtpコマンドが実行されたときに動く関数
     * @param player コマンドを実行したプレイヤーの情報が入っている
     * @param command 実行されたコマンドに関する情報が入っている
     * @param label 親コマンドが入っている
     * @param args 子コマンド(引数)が入っている
     * @return 正常に実行できたか
     */
    @Override
    public boolean execute(Player player, Command command, String label, String[] args) {
        Consumer<MenuItem> TeleportPlayer = (m) -> {
            if (!Objects.equals(ServerStore.getInstance().getServerDisplayName(), m.getName())) {
                ByteArrayDataOutput _out = ByteStreams.newDataOutput();

                _out.writeUTF("ConnectOther");
                _out.writeUTF(player.getName());
                _out.writeUTF(ServerStore.getServerDisplayMap().getKey(m.getName()));

                Objects.requireNonNull(player).sendPluginMessage(MetaversePlugin.getInstance(), "BungeeCord", _out.toByteArray());
                Bukkit.getServer().getLogger().info(player.getName()+"("+player.getUniqueId()+")を"+m.getName()+"に転送しました");
            } else {
                Gui.getInstance().error(player,ChatColor.RED+"[エラー] 既にそのサーバーにいます");
            }
        };

        Gui.getInstance().openMenu(player, "title", Arrays.asList(new MenuItem("ロビー", TeleportPlayer),new MenuItem("サバイバル", TeleportPlayer),new MenuItem("クリエイティブ", TeleportPlayer)));
        return true;
    }

    /**
     * TAB補完
     * @param commandSender コマンドを入力中のプレイヤーかコンソールの情報が入っている
     * @param command 実行されたコマンドに関する情報が入っている
     * @param label コマンド名が入っている
     * @param args 引数
     * @return 引数のリスト
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                                      String[] args) {
        return COMPLETE_LIST_EMPTY;
    }
}
