package world.arainu.core.metaverseplugin.gui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * メニューのアイテム。
 * @author kumitatepazuru
 */
public class MenuItem {
    /**
     * メニューのアイテム。
     * @param name アイテム名
     */
    public MenuItem(String name) {
        this(name, null);
    }

    /**
     * メニューのアイテム。
     * @param name アイテム名
     * @param onClick クリック時のイベント
     */
    public MenuItem(String name, Consumer<MenuItem> onClick) {
        this(name, onClick, true, Material.STONE);
    }

    /**
     * メニューのアイテム。
     * @param name アイテム名
     * @param onClick クリック時のイベント
     * @param close クリックしたときにインベントリを閉じるかどうか
     */
    public MenuItem(String name, Consumer<MenuItem> onClick,Boolean close) {
        this(name, onClick,close, Material.STONE);
    }

    /**
     * メニューのアイテム。
     * @param name アイテム名
     * @param onClick クリック時のイベント
     * @param close クリック時にGUIを閉じるか
     * @param icon アイテムのブロック
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, Material icon) {
       this(name, onClick, close, icon, null,false);
    }

    /**
     * メニューのアイテム。
     * @param name アイテム名
     * @param onClick クリック時のイベント
     * @param close クリック時にGUIを閉じるか
     * @param icon アイテムのブロック
     * @param customData Itemにつける任意のデータ
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, Material icon, Object customData) {
        this(name, onClick, close, icon, customData, 1);
    }

    /**
     * メニューのアイテム。
     * @param name アイテム名
     * @param onClick クリック時のイベント
     * @param close クリック時にGUIを閉じるか
     * @param icon アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param shiny ブロックをキラキラさせるか
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, Material icon, Object customData, boolean shiny) {
        this(name, onClick, close, icon, customData, 1, shiny);
    }

    /**
     * メニューのアイテム。
     * @param name アイテム名
     * @param onClick クリック時のイベント
     * @param close クリック時にGUIを閉じるか
     * @param icon アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param count アイテムの個数
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, Material icon, Object customData, int count) {
        this(name, onClick, close, icon, customData, count, false);
    }

    /**
     * メニューのアイテム。
     * @param name アイテム名
     * @param onClick クリック時のイベント
     * @param close クリック時にGUIを閉じるか
     * @param icon アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param count アイテムの個数
     * @param shiny ブロックをキラキラさせるか
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, Material icon, Object customData, int count, boolean shiny) {
        this(name, onClick, close, new ItemStack(icon, count), customData, shiny);
    }

    /**
     * メニューのアイテム。
     * @param name アイテム名
     * @param onClick クリック時のイベント
     * @param close クリック時にGUIを閉じるか
     * @param icon アイテムのブロック
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, ItemStack icon) {
        this(name, onClick, close, icon, null);
    }

    /**
     * メニューのアイテム。
     * @param name アイテム名
     * @param onClick クリック時のイベント
     * @param close クリック時にGUIを閉じるか
     * @param icon アイテムのブロック
     * @param customData Itemにつける任意のデータ
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, ItemStack icon, Object customData) {
        this(name, onClick, close, icon, customData, false);
    }

    /**
     * メニューのアイテム。
     * @param name アイテム名
     * @param onClick クリック時のイベント
     * @param close クリック時にGUIを閉じるか
     * @param icon アイテムのブロック
     * @param customData Itemにつける任意のデータ
     * @param shiny ブロックをキラキラさせるか
     */
    public MenuItem(String name, Consumer<MenuItem> onClick, Boolean close, ItemStack icon, Object customData, boolean shiny) {
        this.name = name;
        this.onClick = onClick;
        this.icon = icon;
        this.customData = customData;
        this.shiny = shiny;
        this.close = close;
    }

    @Getter private final String name;
    @Getter private final ItemStack icon;
    @Getter private final Consumer<MenuItem> onClick;
    @Getter private final Object customData;
    @Getter private final boolean shiny;
    @Getter private final boolean close;
    @Getter @Setter private Player clicker;
}
