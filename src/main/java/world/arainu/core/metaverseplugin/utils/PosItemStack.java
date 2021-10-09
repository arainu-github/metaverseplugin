package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class PosItemStack {
    public PosItemStack(ItemStack item, int index) {
        this.item = item;
        this.index = index;
    }

    @Getter private final ItemStack item;
    @Getter private final int index;
}
