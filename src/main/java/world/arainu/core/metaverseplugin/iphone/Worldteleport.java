package world.arainu.core.metaverseplugin.iphone;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.store.ServerStore;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * bungeecordのワールド間テレポートツール。
 * Mod＆Admin専用
 *
 * @author kumitatepazuru
 */
public class Worldteleport extends iPhoneBase {
    @Override
    public boolean execute(Player player, Command command, String label, String[] args) {
        Run(player);
        return true;
    }

    @Override
    public void executeGui(MenuItem menuItem) {
        Run(menuItem.getClicker());
    }

    /**
     * 主要関数
     * @param player プレイヤー
     */
    private void Run(Player player){
        Consumer<MenuItem> TeleportPlayer = (m) -> {
            if (!Objects.equals(ServerStore.getServerDisplayName(), m.getName())) {
                ByteArrayDataOutput _out = ByteStreams.newDataOutput();

                _out.writeUTF("ConnectOther");
                _out.writeUTF(player.getName());
                _out.writeUTF(ServerStore.getServerDisplayMap().getKey(m.getName()));

                Objects.requireNonNull(player).sendPluginMessage(MetaversePlugin.getInstance(), "BungeeCord", _out.toByteArray());
                Bukkit.getServer().getLogger().info(player.getName()+"("+player.getUniqueId()+")を"+m.getName()+"に転送しました");
            } else {
                Gui.error(player, "既にそのサーバーにいます");
            }
        };

        Gui.getInstance().openMenu(player, "WorldTeleportGUI/MOD ONLY", Arrays.asList(new MenuItem("ロビー", TeleportPlayer),new MenuItem("サバイバル", TeleportPlayer),new MenuItem("クリエイティブ", TeleportPlayer)));
    }
}
