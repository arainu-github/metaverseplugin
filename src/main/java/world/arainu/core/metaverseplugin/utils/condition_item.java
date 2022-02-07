package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.gui.MenuItem;

import java.util.function.Function;

/**
 * menuItemをiphoneに表示させるかを設定する情報を格納するクラス
 */
public record condition_item(@Getter MenuItem menuItem,
                             @Getter Function<Player, Boolean> condition) {
}
