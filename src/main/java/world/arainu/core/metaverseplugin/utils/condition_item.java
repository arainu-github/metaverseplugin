package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.gui.MenuItem;

import java.util.function.Function;

/**
 * menuItemをiphoneに表示させるかを設定する情報を格納するクラス
 */
public class condition_item {
    @Getter private final MenuItem menuItem;
    @Getter private final Function<Player, Boolean> condition;

    /**
     * 初期化
     * @param menuItem menuItem
     * @param condition 表示させるかのFunction
     */
    public condition_item(MenuItem menuItem, Function<Player, Boolean> condition) {
        this.menuItem = menuItem;
        this.condition = condition;
    }
}
