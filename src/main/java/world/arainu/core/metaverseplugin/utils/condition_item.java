package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.entity.Player;
import world.arainu.core.metaverseplugin.gui.MenuItem;

import java.util.function.Function;

public class condition_item {
    @Getter
    MenuItem menuItem;
    @Getter
    Function<Player, Boolean> condition;

    public condition_item(MenuItem menuItem, Function<Player, Boolean> condition) {
        this.menuItem = menuItem;
        this.condition = condition;
    }
}
