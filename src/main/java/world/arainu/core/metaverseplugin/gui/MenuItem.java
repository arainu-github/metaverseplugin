package world.arainu.core.metaverseplugin.gui;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

/**
 * メニューのアイテム。
 *
 * @author kumitatepazuru
 */
public class MenuItem {
    /**
     * メニューのアイテム。
     *
     * @param name アイテム名
     */
    public MenuItem(String name) {
        this(name, null);
    }

    /**
     * メニューのアイテム。
     *
     * @param name    アイテム名
     * @param onClick クリック時のイベント
     */
    public MenuItem(String name, Consumer<MenuItem> onClick) {
        this(name, onClick, true, Material.STONE);
    }

    /**
     * メニューのアイテム。
     *
     * @param name    アイテム名
     * @param onClick クリック時のイベント
     * @param close   クリックしたときにインベントリを閉じるかどうか
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close) {
        this(name, onClick, close, Material.STONE);
    }

    /**
     * メニューのアイテム。
     *
     * @param name    アイテム名
     * @param onClick クリック時のイベント
     * @param close   クリック時にGUIを閉じるか
     * @param icon    アイテムのブロック
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, Material icon) {
        this(name, onClick, close, icon, null, false);
    }

    /**
     * メニューのアイテム。
     *
     * @param name       アイテム名
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, Material icon, Object customData) {
        this(name, onClick, close, icon, customData, 1);
    }

    /**
     * メニューのアイテム。
     *
     * @param name       アイテム名
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param shiny      ブロックをキラキラさせるか
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, Material icon, Object customData, boolean shiny) {
        this(name, onClick, close, icon, customData, 1, shiny, -1, -1);
    }

    /**
     * メニューのアイテム。
     *
     * @param name       アイテム名
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param count      アイテムの個数
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, Material icon, Object customData, int count) {
        this(name, onClick, close, icon, customData, count, false, -1, -1);
    }

    /**
     * メニューのアイテム。
     *
     * @param name       アイテム名
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param x          アイテムの場所(x軸)。左上が0
     * @param y          アイテムの場所(y軸)。左上が0
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, Material icon, Object customData, int x, int y) {
        this(name, onClick, close, icon, customData, 1, false, x, y);
    }

    /**
     * メニューのアイテム。
     *
     * @param name       アイテム名
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param count      アイテムの個数
     * @param shiny      ブロックをキラキラさせるか
     * @param x          アイテムの場所(x軸)。左上が0
     * @param y          アイテムの場所(y軸)。左上が0
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, Material icon, Object customData, int count, boolean shiny, int x, int y) {
        this(Component.text(name), onClick, close, new ItemStack(icon, count), customData, shiny, x, y);
    }

    /**
     * メニューのアイテム。
     *
     * @param name    アイテム名
     * @param onClick クリック時のイベント
     * @param close   クリック時にGUIを閉じるか
     * @param icon    アイテムのブロック
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, ItemStack icon) {
        this(name, onClick, close, icon, null);
    }

    /**
     * メニューのアイテム。
     *
     * @param name       アイテム名
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, ItemStack icon, Object customData) {
        this(Component.text(name), onClick, close, icon, customData, false, -1, -1);
    }

    /**
     * メニューのアイテム。
     *
     * @param name       アイテム名
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param shiny      ブロックをキラキラさせるか
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, ItemStack icon, Object customData, Boolean shiny) {
        this(Component.text(name), onClick, close, icon, customData, shiny, -1, -1);
    }

    /**
     * メニューのアイテム。
     *
     * @param name       アイテム名
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param x          アイテムの場所(x軸)。左上が0
     * @param y          アイテムの場所(y軸)。左上が0
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, ItemStack icon, Object customData, int x, int y) {
        this(Component.text(name), onClick, close, icon, customData, false, x, y);
    }

    /**
     * メニューのアイテム。
     *
     * @param name       アイテム名
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param shiny      ブロックをキラキラさせるか
     * @param x          アイテムの場所(x軸)。左上が0
     * @param y          アイテムの場所(y軸)。左上が0
     */
    public MenuItem(Component name, Consumer<MenuItem> onClick, Boolean close, ItemStack icon, Object customData, boolean shiny, int x, int y) {
        ItemMeta meta = icon.getItemMeta();
        meta.displayName(name);
        icon.setItemMeta(meta);
        this.onClick = onClick;
        this.icon = icon;
        this.customData = customData;
        this.shiny = shiny;
        this.close = close;
        this.x = x;
        this.y = y;
    }

    /**
     * メニューのアイテム。
     *
     * @param onClick    クリック時のイベント
     * @param close      クリック時にGUIを閉じるか
     * @param icon       アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param shiny      ブロックをキラキラさせるか
     * @param x          アイテムの場所(x軸)。左上が0
     * @param y          アイテムの場所(y軸)。左上が0
     */
    public MenuItem(Consumer<MenuItem> onClick, Boolean close, ItemStack icon, Object customData, boolean shiny, int x, int y) {
        this.onClick = onClick;
        this.icon = icon;
        this.customData = customData;
        this.shiny = shiny;
        this.close = close;
        this.x = x;
        this.y = y;
    }

    @Getter
    private final ItemStack icon;
    @Getter
    private final Consumer<MenuItem> onClick;
    @Getter
    private final Object customData;
    @Getter
    private final boolean shiny;
    @Getter
    private final boolean close;
    @Getter
    private final int x;
    @Getter
    private final int y;
    @Getter
    @Setter
    private Player clicker;
}
