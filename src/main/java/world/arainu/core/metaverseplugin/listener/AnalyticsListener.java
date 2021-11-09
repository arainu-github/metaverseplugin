//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package world.arainu.core.metaverseplugin.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

public class AnalyticsListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("count", Bukkit.getOnlinePlayers().size());
        sqlUtil.adddata(sqlUtil.DataType.PLAYER_JOIN, root);
    }

    @EventHandler
    public void onPlayerQuit(PlayerJoinEvent e) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("count", Bukkit.getOnlinePlayers().size()-1);
        sqlUtil.adddata(sqlUtil.DataType.PLAYER_QUIT, root);
    }
}
