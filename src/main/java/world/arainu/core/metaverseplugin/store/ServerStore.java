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
    /**
     * サーバーのデータを格納するクラス
     */
    public ServerStore(){
        ServerStore.instance = this;
    }

    @Getter
    private static ServerStore instance;
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
    public String getServerDisplayName(){
        return serverDisplayMap.get(serverName);
    }
}
