package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * sql関係の便利関数を集めているクラス
 *
 * @author kumitatepazuru
 */
public class sqlUtil {
    /** sql関係の便利関数を集めているクラス */
    public sqlUtil(){
        Instance = this;
    }

    private void create_uuidtype_table(Connection conn){
        try {
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `uuidtype` ( `uuid` VARCHAR(36) NOT NULL , `type` VARCHAR(20) NOT NULL , PRIMARY KEY (`uuid`)) ");
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 何かしらのタイプとUUIDを紐付ける関数。
     *
     * @param uuid UUID
     * @param type type
     */
    public void setuuidtype(UUID uuid, String type){
        try {
            Connection conn = DriverManager.getConnection(url_connection, user, pass);
            create_uuidtype_table(conn);
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `uuidtype` (`uuid`, `type`) VALUES('" + uuid + "', '" + type + "')");
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 何かしらのタイプとUUIDを紐付ける関数。
     *
     * @param type type
     * @return UUID
     */
    public List<UUID> getuuidsbytype(String type){
        try {
            Connection conn = DriverManager.getConnection(url_connection, user, pass);
            create_uuidtype_table(conn);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `uuidtype` WHERE `type` LIKE '"+type+"'");
            List<UUID> uuidList = new ArrayList<>();
            while(rs.next()){
                uuidList.add(UUID.fromString(rs.getString("uuid")));
            }
            rs.close();
            stmt.close();
            conn.close();
            return uuidList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Getter private final static String db_name = MetaversePlugin.getConfiguration().getString("mysql.db_name");
    @Getter private final static String user = MetaversePlugin.getConfiguration().getString("mysql.user");
    @Getter private final static String pass = MetaversePlugin.getConfiguration().getString("mysql.pass");
    @Getter private final static int port = MetaversePlugin.getConfiguration().getInt("mysql.port");
    @Getter private final static String url = MetaversePlugin.getConfiguration().getString("mysql.url");
    private final static String url_connection = "jdbc:mysql://"+url+":"+port+"/"+db_name;
    @Getter private static sqlUtil Instance;
}
