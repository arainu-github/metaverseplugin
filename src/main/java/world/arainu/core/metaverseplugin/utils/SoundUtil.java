package world.arainu.core.metaverseplugin.utils;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

/**
 * 音に関するユーティリティ
 *
 * @author kumitatepazuru
 */
public class SoundUtil {
    /**
     * ボタンを押したときの、カチという音を再生する関数。
     *
     * @param p 再生するプレイヤー
     */
    public static void playClickSound(Player p) {
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1, 1f);
    }
}
