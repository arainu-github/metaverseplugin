package world.arainu.core.metaverseplugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

/**
 * UI システムのメインクラス。
 * @author kumitatepazuru
 */
public class Gui implements Listener {
    /**
     * インスタンスを取得します。
     * @return インスタンス
     */
    public static Gui getInstance() {
        return instance == null ? (instance = new Gui()) : instance;
    }

    /**
     * 内部的に使用するものです。
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * メニューを開きます。
     * @param player メニューを開くプレイヤー
     * @param title メニューのタイトル
     * @param items メニューのアイテム
     */
    public void openMenu(Player player, String title, MenuItem... items) {
        openMenu(player, title, List.of(items));
    }

    /**
     * メニューを開きます。
     * @param player メニューを開くプレイヤー
     * @param title メニューのタイトル
     * @param items メニューのアイテム
     */
    public void openMenu(Player player, String title, Collection<MenuItem> items) {
        openMenuJavaImpl(player, title, items.toArray(MenuItem[]::new));
    }

    /**
     * エラーをプレイヤーに表示します。
     * @param p エラーを表示させるプレイヤー
     * @param message エラー内容
     */
    public void error(Player p, String message) {
        p.sendMessage(message);
        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 0.5f);
    }

    /**
     * JavaでインベントリをメニューUIとして使うため、そのハンドリングを行います。
     * @param e ハンドリングに使用するイベント
    */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        final Inventory inv = e.getInventory();
        final HumanEntity p = e.getWhoClicked();

        // 管理インベントリでなければ無視
        if (!invMap.containsKey(inv)) return;
        e.setCancelled(true);

        final MenuItem[] menuItems = invMap.get(inv);
        final int id = e.getRawSlot();

        if (menuItems.length <= id) return;
        else if (id < 0) return;
        p.closeInventory();
        final MenuItem clickedMenuItem = menuItems[id];
        clickedMenuItem.setClicker((Player) p);
        final Consumer<MenuItem> handler = clickedMenuItem.getOnClick();
        if (handler != null) handler.accept(clickedMenuItem);
    }


    /**
     * JavaでインベントリをメニューUIとして使うため、そのハンドリングを行います。
     * @param e ハンドリングに使用するイベント
    */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        final Inventory inv = e.getInventory();

        // 管理インベントリでなければ無視
        if (!invMap.containsKey(inv)) return;

        // GC
        invMap.remove(inv);
    }

    private void openMenuJavaImpl(Player player, String title, MenuItem[] items) {
        final Inventory inv = Bukkit.createInventory(null, (1 + items.length / 9) * 9, title);

        Arrays.stream(items).map(i -> {
            final ItemStack item = i.getIcon();
            if (i.isShiny()) {
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            }
            final ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(i.getName());
            item.setItemMeta(meta);

            return item;
        }).forEach(inv::addItem);

        invMap.put(inv, items);
        player.openInventory(inv);
    }

    private final HashMap<Inventory, MenuItem[]> invMap = new HashMap<>();
    private static Gui instance;
}
