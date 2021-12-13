package world.arainu.core.metaverseplugin.utils;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundUtil {
    public static void playClickSound(Player p) {
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1, 1f);
    }
}
