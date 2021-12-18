package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.apache.commons.lang3.SerializationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.dynmap.utils.Vector3D;
import world.arainu.core.metaverseplugin.MetaversePlugin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * sql関係の便利関数を集めているクラス
 *
 * @author kumitatepazuru
 */
public class sqlUtil {
    /**
     * SQLに接続する
     */
    public static void connect(){
        try {
            conn = DriverManager.getConnection(url_connection, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * SQLから切断する
     */
    public static void disconnect(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * SQLからconnectionを切断されないようにpingを送る関数
     */
    public static void ping(){
        try {
            PreparedStatement ps = conn.prepareStatement("/* ping */ SELECT 1");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void create_uuidtype_table(){
        try {
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `uuidtype` ( `uuid` VARCHAR(36) NOT NULL , `type` VARCHAR(64) NOT NULL , PRIMARY KEY (`uuid`)) ");
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void create_playerpos_table(){
        try {
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `playerpos` ( `uuid` VARCHAR(36) NOT NULL ,  `world` VARCHAR(36) NOT NULL , `x` INT NOT NULL , `y` SMALLINT NOT NULL ,`z` INT NOT NULL ,PRIMARY KEY (`uuid`)) ");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void create_drilling_table(){
        try {
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `drilling` ( `uuid` VARCHAR(36) NOT NULL , `x` INT NOT NULL, `y` INT NOT NULL, `z` INT NOT NULL, `blockX` INT NOT NULL, `blockY` INT NOT NULL, `blockZ` INT NOT NULL, PRIMARY KEY (`uuid`)) ");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void create_kickcount_table() {
        try {
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `kickcount` ( `uuid` VARCHAR(36) NOT NULL ,  `count` INT NOT NULL ,PRIMARY KEY (`uuid`)) ");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void create_whitelist_table() {
        try {
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS whitelist (uuid VARCHAR(36) NOT NULL ,PRIMARY KEY (`uuid`)) ");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 何かしらのタイプとUUIDを紐付ける関数。
     *
     * @param uuid UUID
     * @param type type
     */
    public static void setuuidtype(UUID uuid, String type){
        try {
            create_uuidtype_table();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `uuidtype` VALUES('" + uuid + "', '" + type + "')");
            ps.executeUpdate();
            ps.close();
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
    public static List<UUID> getuuidsbytype(String type){
        try {
            create_uuidtype_table();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `uuidtype` WHERE `type` LIKE '"+type+"'");
            List<UUID> uuidList = new ArrayList<>();
            while(rs.next()){
                uuidList.add(UUID.fromString(rs.getString("uuid")));
            }
            rs.close();
            stmt.close();
            return uuidList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * UUIDタイプが存在するか確認する関数
     *
     * @param uuid UUID
     * @return 存在するか
     */
    public static Boolean hasuuid(UUID uuid){
        try {
            create_uuidtype_table();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) AS cnt FROM `uuidtype` WHERE `uuid` LIKE '"+uuid+"'");
            rs.next();
            final Boolean ret = rs.getInt("cnt") != 0;
            rs.close();
            stmt.close();
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * プレイヤーの座標をSQLに保存する関数
     *
     * @param uuid UUID
     * @param location 座標
     */
    public static void setplayerpos(UUID uuid, Location location){
        try {
            create_playerpos_table();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `playerpos` VALUES('" + uuid + "', '" + location.getWorld().getUID() + "',"+(int) location.getX()+","+(int) location.getY()+","+(int) location.getZ()+")");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * プレイヤーの座標をSQLから取得する関数
     *
     * @param uuid UUID
     * @return UUID
     */
    public static Location getplayerpos(UUID uuid){
        try {
            create_playerpos_table();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `playerpos` WHERE `uuid` LIKE '"+uuid+"'");
            rs.next();
            Location loc = new Location(Bukkit.getWorld(UUID.fromString(rs.getString("world"))),rs.getInt("x"),rs.getInt("y"),rs.getInt("z"));
            rs.close();
            stmt.close();
            return loc;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * SQLに保存されたプレイヤーの座標を削除する関数
     *
     * @param uuid UUID
     */
    public static void deleteplayerpos(UUID uuid){
        try {
            create_playerpos_table();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM `playerpos` WHERE `uuid` LIKE '"+uuid+"'");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * kickcountを増やす関数
     * @param uuid UUID
     */
    public static void addkickcount(UUID uuid){
        try {
            create_kickcount_table();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `kickcount` (`uuid`, `count`) VALUES('" + uuid + "', 1) ON DUPLICATE KEY UPDATE count = count+1");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * kickcountを取得する関数
     * @param uuid UUID
     * @return kick count
     */
    public static Integer getkickcount(UUID uuid){
        try {
            create_kickcount_table();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `kickcount` WHERE `uuid` LIKE '"+uuid+"'");
            rs.next();
            int count = rs.getInt("count");
            rs.close();
            stmt.close();
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * データベース上のホワリスにプレイヤーを追加する関数
     *
     * @param uuid 文字通りUUID
     */

    public static void addWhitelist(UUID uuid) {
        try {
            create_whitelist_table();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO whitelist VALUES('" + uuid + "')");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * ホワイトリストをSQLから削除する
     * @param uuid UUID
     */
    public static void removeWhitelist(UUID uuid) {
        try {
            create_whitelist_table();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM whitelist WHERE uuid LIKE '" + uuid + "'");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * ホワリスを取得する関数
     *
     * @return ホワリスに入っているプレイヤーのUUIDをリストで返します。
     */

    public static List<UUID> getWhitelist() {
        try {
            create_whitelist_table();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM whitelist");
            List<UUID> uuidList = new ArrayList<>();
            while (rs.next()) {
                uuidList.add(UUID.fromString(rs.getString("uuid")));
            }
            rs.close();
            stmt.close();
            return uuidList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static returnDrilling getDrillingBlock(UUID uuid){
        try {
            create_drilling_table();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `drilling` WHERE `uuid` LIKE '"+uuid+"'");
            rs.first();
            InputStream is = rs.getBinaryStream( 5 );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bs = new byte[1024];
            int size;
            while( ( size = is.read( bs ) ) != -1 ){
                baos.write( bs, 0, size );
            }

            Vector3D vector3D = new Vector3D(rs.getInt(2),rs.getInt(3),rs.getInt(4));
            Block block = SerializationUtils.deserialize(baos.toByteArray());
            rs.close();
            stmt.close();
            return new returnDrilling(vector3D,block);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    record returnDrilling(Vector3D vector3D, Block block){
    }

    public static void addDrillingBlock(UUID uuid, Vector3D vector3D, Block block){
        try {
            create_drilling_table();
            Location loc = block.getLocation();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO drilling VALUES(?, ?, ?, ?,?)");
            ps.setString(1, String.valueOf(uuid));
            ps.setInt(2, (int) vector3D.x);
            ps.setInt(3, (int) vector3D.y);
            ps.setInt(4, (int) vector3D.z);
            ps.setBytes(5, SerializationUtils.serialize((Serializable) block));
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeDrillingBlock(UUID uuid) {
        try {
            create_drilling_table();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM drilling WHERE uuid LIKE '" + uuid + "'");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Getter
    private final static String db_name = MetaversePlugin.getConfiguration().getString("mysql.db_name");
    @Getter
    private final static String db_public = MetaversePlugin.getConfiguration().getString("mysql.db_public");
    @Getter
    private final static String user = MetaversePlugin.getConfiguration().getString("mysql.user");
    @Getter
    private final static String pass = MetaversePlugin.getConfiguration().getString("mysql.pass");
    @Getter
    private final static int port = MetaversePlugin.getConfiguration().getInt("mysql.port");
    @Getter
    private final static String url = MetaversePlugin.getConfiguration().getString("mysql.url");
    private final static String url_connection = "jdbc:mysql://" + url + ":" + port + "/" + db_name + "?autoReconnect=true&maxReconnects=3&useSSL=false";
    private static Connection conn;
}
