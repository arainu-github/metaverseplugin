package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * sql関係の便利関数を集めているクラス
 *
 * @author kumitatepazuru
 */
public class sqlUtil {
    public sqlUtil(){
        db_name = MetaversePlugin.getConfiguration().getString("mysql.db_name");
        url = MetaversePlugin.getConfiguration().getString("mysql.url");
        user = MetaversePlugin.getConfiguration().getString("mysql.user");
        pass = MetaversePlugin.getConfiguration().getString("mysql.pass");
        port = MetaversePlugin.getConfiguration().getInt("mysql.port");

        url_connection = "jdbc:mysql://"+url+":"+port+"/"+db_name;
    }

    public void time_update(UUID uuid){
        try {
            Connection conn = DriverManager.getConnection(url_connection, user, pass);
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `logintime` ( `uuid` VARCHAR(36) NOT NULL , `time` INT NOT NULL , PRIMARY KEY (`uuid`)) ");
            ps.executeUpdate();
            ps = conn.prepareStatement("INSERT INTO `logintime` (`uuid`, `time`) VALUES('" + uuid + "', " + System.currentTimeMillis() / 1000 + ") ON DUPLICATE KEY UPDATE time=VALUES(time)");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Getter private static String db_name;
    @Getter private static String user;
    @Getter private static String pass;
    @Getter private static int port;
    @Getter private static String url;
    private static String url_connection;
}
