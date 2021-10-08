package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class PosItemStack {
    public PosItemStack(ItemStack item, int x, int y) {
        this.item = item;
        this.x = x;
        this.y = y;
    }

    @Getter private final ItemStack item;
    @Getter private final int x;
    @Getter private final int y;
}
