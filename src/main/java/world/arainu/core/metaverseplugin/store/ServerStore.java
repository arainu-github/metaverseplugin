package world.arainu.core.metaverseplugin.store;

import lombok.Getter;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.Bukkit;

/**
 * サーバーのデータを格納するクラス
 * @author kumitatepazuru
 */
public class ServerStore {
    @Getter private static String serverName = "";
    @Getter private static final BidiMap<String, String> serverDisplayMap = new DualHashBidiMap<>(){{put("lobby","ロビー"); put("survival","サバイバル"); put("creative","クリエイティブ");}};

    /**
     * bungeeのサーバー名を設定する
     * @param ServerName サーバー名
     */
    public static void setServerName(String ServerName) {
        Bukkit.getLogger().info("BungeecordのServer Name: "+ServerName);
        serverName = ServerName;
    }

    /**
     * サーバーのbungeeの表示名を取得する
     * @return 表示名
     */
    public static String getServerDisplayName(){
        return serverDisplayMap.get(serverName);
    }
}
