package world.arainu.core.metaverseplugin.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import world.arainu.core.metaverseplugin.utils.PosItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * UI システムのメインクラス。
 *
 * @author kumitatepazuru
 */
public class Gui implements Listener {
    /**
     * インスタンスを取得します。
     *
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
     *
     * @param player メニューを開くプレイヤー
     * @param title  メニューのタイトル
     * @param items  メニューのアイテム
     */
    public void openMenu(Player player, String title, Collection<MenuItem> items) {
        if (isBedrock(player)) {
            openMenuBedrockImpl(player, title, items.toArray(MenuItem[]::new));
        } else {
            openMenuJavaImpl(player, title, items.toArray(MenuItem[]::new));
        }
    }

    /**
     * JavaでインベントリをメニューUIとして使うため、そのハンドリングを行います。
     *
     * @param e ハンドリングに使用するイベント
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        final Inventory inv = e.getInventory();
        final HumanEntity p = e.getWhoClicked();

        // 管理インベントリでなければ無視
        if (!invMap.containsKey(inv)) return;
        e.setCancelled(true);

        final HashMap<Integer, MenuItem> menuItems = invMap.get(inv);
        final int id = e.getRawSlot();

        if (id < 0) return;
        else if (!menuItems.containsKey(id)) return;
        final MenuItem clickedMenuItem = menuItems.get(id);
        if (clickedMenuItem.isClose()) {
            p.closeInventory();
        }
        clickedMenuItem.setClicker((Player) p);
        ((Player) p).playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1, 1f);
        final Consumer<MenuItem> handler = clickedMenuItem.getOnClick();
        if (handler != null) handler.accept(clickedMenuItem);
    }


    /**
     * JavaでインベントリをメニューUIとして使うため、そのハンドリングを行います。
     *
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
        Integer max = Arrays.stream(items).map(MenuItem::getY).max(Comparator.naturalOrder()).orElse(0);
        final Inventory inv = Bukkit.createInventory(null, Math.max(1 + items.length / 9, max) * 9, Component.text(title));
        final HashMap<Integer, MenuItem> itemmap = new HashMap<>();
        final int[] count = {0};

        Arrays.stream(items).map(i -> {
            final ItemStack item = i.getIcon();
            if (i.isShiny()) {
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            }
            final ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.displayName(Component.text(i.getName()).decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(meta);
            final int index;
            if (i.getX() > -1) {
                index = i.getX() + i.getY() * 9;
            } else {
                while (itemmap.containsKey(count[0])) {
                    count[0]++;
                }
                index = count[0];
            }
            itemmap.put(index, i);

            return new PosItemStack(item, index);
        }).forEach(i -> inv.setItem(i.getIndex(), i.getItem()));

        invMap.put(inv, itemmap);
        player.openInventory(inv);
    }

    private void openMenuBedrockImpl(Player player, String title, MenuItem[] items) {
        final SimpleForm.Builder builder = SimpleForm.builder()
                .title(title);

        for (var item : items) {
            String text = item.getName();
            if (item.isShiny()) {
                text = ChatColor.DARK_GREEN + text;
            }
            if (item.isClose()) {
                builder.button(text);
            } else {
                builder.content(text);
            }
        }

        builder.responseHandler((form, data) -> {
            final SimpleFormResponse res = form.parseResponse(data);
            if (!res.isCorrect()) {
                return;
            }

            final int id = Math.toIntExact(res.getClickedButtonId() + Arrays.stream(items).filter(value -> !value.isClose()).count());
            final Consumer<MenuItem> callback = items[id].getOnClick();
            if (callback != null) {
                items[id].setClicker(player);
                callback.accept(items[id]);
            }
        });

        final FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        fPlayer.sendForm(builder);
    }

    /**
     * プレイヤーがBE勢かを調べる
     *
     * @param player 対象のプレイヤー
     * @return BEの場合はtrue
     */
    public static boolean isBedrock(Player player) {
        return FloodgateApi.getInstance().isFloodgateId(player.getUniqueId());
    }

    private final HashMap<Inventory, HashMap<Integer, MenuItem>> invMap = new HashMap<>();
    private static Gui instance;
}
