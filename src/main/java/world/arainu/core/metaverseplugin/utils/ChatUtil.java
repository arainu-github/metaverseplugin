package world.arainu.core.metaverseplugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class ChatUtil {
    /**
     * エラーをプレイヤーに表示します。
     *
     * @param p       エラーを表示させるプレイヤー
     * @param message エラー内容
     */
    public static void error(Player p, String message) {
        Bukkit.getLogger().warning(
                Component.text("[").append(p.displayName()).append(Component.text("] エラー>> " + message)).content());
        for (String msg : message.split("\n"))
            p.sendMessage(ChatColor.RED + "[メタバースプラグイン][エラー] " + msg);
        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 0.5f);
    }

    /**
     * 警告をプレイヤーに表示します。
     *
     * @param p       警告を表示させるプレイヤー
     * @param message 警告内容
     */
    public static void warning(Player p, String message) {
        Bukkit.getLogger().info(
                Component.text("[").append(p.displayName()).append(Component.text("] 警告>> " + message)).content());
        p.sendMessage(ChatColor.GOLD + "[メタバースプラグイン] " + message);
    }

    /**
     * 操作の成功をプレイヤーに表示します。
     *
     * @param p       成功を表示させるプレイヤー
     * @param message 成功内容
     */
    public static void success(Player p, String message) {
        success(p, Component.text(message), true);
    }

    /**
     * 操作の成功をプレイヤーに表示します。
     *
     * @param p       成功を表示させるプレイヤー
     * @param message 成功内容
     */
    public static void success(Player p, TextComponent message) {
        success(p, message, true);
    }

    /**
     * 操作の成功をプレイヤーに表示します。
     *
     * @param p         成功を表示させるプレイヤー
     * @param message   成功内容
     * @param playsound 音を再生するか
     */
    public static void success(Player p, String message, Boolean playsound) {
        success(p, Component.text(message), playsound);
    }

    /**
     * 操作の成功をプレイヤーに表示します。
     *
     * @param p         成功を表示させるプレイヤー
     * @param message   成功内容
     * @param playsound 音を再生するか
     */
    public static void success(Player p, TextComponent message, Boolean playsound) {
        Bukkit.getLogger().info(
                Component.text("[").append(p.displayName()).append(Component.text("] 成功>> " + message)).content());
        p.sendMessage(Component.text("[メタバースプラグイン] ").append(message).color(NamedTextColor.GREEN));
        if (playsound) {
            p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 1, 1f);
        }
    }
}
