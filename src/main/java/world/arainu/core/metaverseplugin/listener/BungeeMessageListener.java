package world.arainu.core.metaverseplugin.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.store.ServerStore;

/**
 * Bungeecordとの通信に使用するクラス
 * @author kumitatepazuru
 */
public class BungeeMessageListener implements PluginMessageListener {
    @Getter static private BungeeMessageListener Instance;

    /**
     * Bungeecordとの通信に使用するクラス
     */
    public BungeeMessageListener(){
        Instance = this;
    }

    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if(subchannel.equals("GetServer")){
            String name = in.readUTF();
            ServerStore.setServerName(name);
        }
    }
}
