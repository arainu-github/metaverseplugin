package world.arainu.core.metaverseplugin.utils;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * Guiで主に使用するItemStackと場所(index)を紐付けるクラス
 */
public class PosItemStack {
    /**
     * 初期化
     * @param item ItemStack
     * @param index 場所(index)
     */
    public PosItemStack(ItemStack item, int index) {
        this.item = item;
        this.index = index;
    }

    @Getter private final ItemStack item;
    @Getter private final int index;
}
