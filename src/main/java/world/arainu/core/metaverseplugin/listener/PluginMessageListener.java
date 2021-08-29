package world.arainu.core.metaverseplugin.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.store.ServerStore;

public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {
    static PluginMessageListener Instance;

    public PluginMessageListener(){
        Instance = this;
    }

    public static PluginMessageListener getInstance(){
        return Instance;
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
