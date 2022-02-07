package world.arainu.core.metaverseplugin.iphone;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.commands.CommandiPhone;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.store.ServerStore;
import world.arainu.core.metaverseplugin.utils.ChatUtil;

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
    public boolean execute(Player player, String[] args) {
        Run(player,true);
        return true;
    }

    @Override
    public void executeGui(MenuItem menuItem) {
        Run(menuItem.getClicker(),false);
    }

    /**
     * 主要関数
     * @param player プレイヤー
     */
    private void Run(Player player,Boolean onCommand){
        Consumer<MenuItem> TeleportPlayer = (m) -> {
            if (!Objects.equals(ServerStore.getServerDisplayName(), m.getCustomData())) {
                ByteArrayDataOutput _out = ByteStreams.newDataOutput();

                _out.writeUTF("ConnectOther");
                _out.writeUTF(player.getName());
                _out.writeUTF(ServerStore.getServerDisplayMap().getKey(m.getCustomData()));

                Objects.requireNonNull(player).sendPluginMessage(MetaversePlugin.getInstance(), "BungeeCord", _out.toByteArray());
                Bukkit.getServer().getLogger().info(player.getName()+"("+player.getUniqueId()+")を"+m.getCustomData()+"に転送しました");
            } else {
                ChatUtil.error(player, "既にそのサーバーにいます");
            }
        };

        if(onCommand) {
            Gui.getInstance().openMenu(player, "WorldTeleportGUI/MOD ONLY", Arrays.asList(
                    new MenuItem("ロビー", TeleportPlayer,true,Material.BLACK_CONCRETE, "ロビー"),
                    new MenuItem("サバイバル", TeleportPlayer, true, Material.GRASS_BLOCK, "サバイバル"),
                    new MenuItem("クリエイティブ", TeleportPlayer, true, Material.COBBLESTONE, "クリエイティブ")
            ));
        } else {
            Gui.getInstance().openMenu(player, "WorldTeleportGUI/MOD ONLY", Arrays.asList(
                    new MenuItem("ロビー", TeleportPlayer,true,Material.BLACK_CONCRETE, "ロビー"),
                    new MenuItem("サバイバル", TeleportPlayer, true, Material.GRASS_BLOCK, "サバイバル"),
                    new MenuItem("クリエイティブ", TeleportPlayer, true, Material.COBBLESTONE, "クリエイティブ"),
                    new MenuItem("戻る", CommandiPhone::run, true, Material.ARROW, null, 8, 0)
            ));
        }
    }
}
