package world.arainu.core.metaverseplugin.store;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * サーバーのデータを格納するクラス
 *
 * @author kumitatepazuru
 */
public class ServerStore {
    @Getter
    private static String serverName = "";
    @Getter
    private static final BidiMap<String, String> serverDisplayMap = new DualHashBidiMap<>() {{
        put("lobby", "ロビー");
        put("survival", "サバイバル");
        put("creative", "クリエイティブ");
    }};
    @Getter
    @Setter
    private static HashMap<Player, ArrayList<Location>> MarkerData = new HashMap<>();

    @Getter private static final NamespacedKey MunicipalBookKey = new NamespacedKey(MetaversePlugin.getInstance(), "metaverse-municipal");

    /**
     * bungeeのサーバー名を設定する
     *
     * @param ServerName サーバー名
     */
    public static void setServerName(String ServerName) {
        Bukkit.getLogger().info("BungeecordのServer Name: " + ServerName);
        serverName = ServerName;
    }

    /**
     * サーバーのbungeeの表示名を取得する
     *
     * @return 表示名
     */
    public static String getServerDisplayName() {
        return serverDisplayMap.get(serverName);
    }
}
