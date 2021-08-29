package world.arainu.core.metaverseplugin.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import world.arainu.core.metaverseplugin.MetaversePlugin;

public class PlayerListener implements Listener {

    static PlayerListener Instance;

    public PlayerListener() {
        Instance = this;
    }

    public static PlayerListener getInstance() {
        return Instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        player.sendPluginMessage(MetaversePlugin.getInstance(), "BungeeCord", out.toByteArray());
    }
}
