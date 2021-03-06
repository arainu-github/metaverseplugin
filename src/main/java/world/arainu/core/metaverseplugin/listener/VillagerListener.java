package world.arainu.core.metaverseplugin.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.gui.casino.SlotMachine;
import world.arainu.core.metaverseplugin.iphone.Bank;
import world.arainu.core.metaverseplugin.store.BankStore;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.ItemUtil;
import world.arainu.core.metaverseplugin.utils.SoundUtil;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 村人取引時の独自UIのリスナー
 *
 * @author kumitatepazuru
 */
public class VillagerListener implements Listener {
    private final HashMap<Inventory, GuiData> invMap = new HashMap<>();

    /**
     * インベントリ内のお金を取得する関数
     *
     * @param inv 対象のインベントリ
     * @return お金の情報
     */
    public static ReturnMoney getTotalmoney(Inventory inv) {
        final List<ItemStack> money_list = new ArrayList<>(inv.all(Material.EMERALD).values());
        int total_money = 0;
        for (ItemStack i : money_list) {
            if (Bank.isMoney(i)) {
                final PersistentDataContainer persistentDataContainer = i.getItemMeta().getPersistentDataContainer();
                total_money += Objects.requireNonNull(persistentDataContainer.get(BankStore.getKey(), PersistentDataType.INTEGER)) * i.getAmount();
            }
        }
        return new ReturnMoney(money_list, total_money);
    }

    /**
     * プレイヤーが右クリックしたときに特定の村人の場合は独自UIを表示させるリスナー
     *
     * @param e イベント
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Villager) {
            if (Boolean.TRUE.equals(sqlUtil.hasuuid(e.getRightClicked().getUniqueId()))) {
                e.setCancelled(true);
                if (Objects.requireNonNull(sqlUtil.getuuidsbytype("casino-villager")).contains(e.getRightClicked().getUniqueId())) {
                    SlotMachine obj = new SlotMachine();
                    obj.start(e.getPlayer());
                } else {
                    Villager villager = (Villager) e.getRightClicked();
                    AtomicInteger i = new AtomicInteger(-1);
                    List<MenuItem> tradeitems = villager.getRecipes().stream().map((recipe) -> {
                                i.getAndIncrement();
                                return new Mapdata(recipe, i.get(), villager);
                            })
                            .map((recipe) -> new MenuItem(
                                    this::onClick,
                                    true,
                                    Bank.isMoney(recipe.recipe.getResult()) ? recipe.recipe.getIngredients().get(0) : recipe.recipe.getResult(),
                                    recipe,
                                    false,
                                    -1,
                                    -1))
                            .collect(Collectors.toList());

                    Gui.getInstance().openMenu(e.getPlayer(), villager.getName(), tradeitems);
                }
            }
        }
    }

    private void updatePrice(Inventory inv, ItemStack item) {
        Economy econ = MetaversePlugin.getEcon();
        ItemStack priceItem = Objects.requireNonNull(inv.getItem(6));
        ItemMeta itemMeta = priceItem.getItemMeta();
        final int price = invMap.get(inv).price * item.getAmount();
        if (invMap.get(inv).isPurchase) {
            itemMeta.displayName(Component.text("入手できる金額:" + econ.format(price)));
        } else {
            itemMeta.displayName(Component.text("必要な金額:" + econ.format(price)));
        }
        priceItem.setItemMeta(itemMeta);
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
        GuiData guiData = invMap.get(inv);
        if (guiData != null) {
            final ItemStack item = Objects.requireNonNull(inv.getItem(2));

            // 管理インベントリでなければ無視
            if (!invMap.containsKey(inv)) return;
            final int id = e.getRawSlot();
            if (id < 18 && id >= 0) {
                e.setCancelled(true);
                switch (id) {
                    case 1 -> {
                        SoundUtil.playClickSound((Player) p);
                        if (item.getAmount() != 1) {
                            item.setAmount(item.getAmount() - 1);
                        }
                        updatePrice(inv, item);
                    }
                    case 3 -> {
                        SoundUtil.playClickSound((Player) p);
                        if (item.getAmount() != 64) {
                            item.setAmount(item.getAmount() + 1);
                        }
                        updatePrice(inv, item);
                    }
                    case 6 -> {
                        SoundUtil.playClickSound((Player) p);
                        boolean okay = false;
                        if (guiData.isPurchase) {
                            final HashMap<Integer, ? extends ItemStack> item_list = inv.all(item.getType());
                            int total = 0;
                            for (Map.Entry<Integer, ? extends ItemStack> i : item_list.entrySet()) {
                                if (i.getKey() != 2) {
                                    total += i.getValue().getAmount();
                                }
                            }
                            final int required_item = item.getAmount();
                            if (required_item <= total) {
                                Bank.addMoneyForPlayer((Player) p, guiData.price * item.getAmount());

                                total = 0;
                                okay = true;
                                for (Map.Entry<Integer, ? extends ItemStack> i : item_list.entrySet()) {
                                    final ItemStack pay_item = i.getValue();
                                    final int index = i.getKey();
                                    if (index != 2) {
                                        if (total + pay_item.getAmount() < required_item) {
                                            inv.setItem(index, new ItemStack(Material.AIR));
                                            total += pay_item.getAmount();
                                        } else {
                                            pay_item.setAmount(pay_item.getAmount() - (required_item - total));
                                            inv.setItem(index, pay_item);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            final ReturnMoney returnMoney = getTotalmoney(inv);
                            final int required_money = guiData.price * item.getAmount();
                            if (required_money <= returnMoney.total_money()) {
                                okay = true;
                                for (ItemStack i : returnMoney.money_list()) {
                                    if (Bank.isMoney(i)) {
                                        inv.remove(i);
                                    }
                                }
                                Bank.addMoneyForPlayer((Player) p, returnMoney.total_money() - required_money);
                                final ItemStack addItem = new ItemStack(item.getType());
                                addItem.setAmount(item.getAmount());
                                ItemUtil.addItem(addItem, e.getInventory(), (Player) p);
                            }
                        }
                        if (okay) {
                            MerchantRecipe recipe = guiData.villager.getRecipe(guiData.index);
                            recipe.setUses(recipe.getUses() + 1);
                            guiData.villager.setRecipe(guiData.index, recipe);
                        }
                    }
                }
            }
            Bukkit.getScheduler().runTaskLater(MetaversePlugin.getInstance(), () -> {
                int total = 0;
                final int required;
                if (guiData.isPurchase) {
                    final HashMap<Integer, ? extends ItemStack> item_list = inv.all(item.getType());
                    for (Map.Entry<Integer, ? extends ItemStack> i : item_list.entrySet()) {
                        if (i.getKey() != 2) {
                            total += i.getValue().getAmount();
                        }
                    }
                    required = item.getAmount();
                } else {
                    ReturnMoney money = getTotalmoney(inv);
                    total = money.total_money();
                    required = guiData.price * Objects.requireNonNull(inv.getItem(2)).getAmount();
                }

                ItemStack priceItem = Objects.requireNonNull(inv.getItem(6));
                ItemMeta itemMeta = priceItem.getItemMeta();
                if (required > total) {
                    if (guiData.isPurchase) {
                        itemMeta.lore(Arrays.asList(
                                Component.text("クリックして買取").color(NamedTextColor.GRAY),
                                Component.text(required - total + "個不足しています").color(NamedTextColor.RED)
                        ));
                    } else {
                        itemMeta.lore(Arrays.asList(
                                Component.text("クリックして購入").color(NamedTextColor.GRAY),
                                Component.text(MetaversePlugin.getEcon().format(required - total) + "不足しています").color(NamedTextColor.RED)
                        ));
                    }
                } else {
                    if (guiData.isPurchase) {
                        itemMeta.lore(List.of(Component.text("クリックして買取").color(NamedTextColor.GREEN)));
                    } else {
                        itemMeta.lore(List.of(Component.text("クリックして購入").color(NamedTextColor.GREEN)));
                    }
                }
                priceItem.setItemMeta(itemMeta);
            }, 1);
        } else {
            if (inv.getType() == InventoryType.MERCHANT) {
                HashMap<Integer, ? extends ItemStack> items = inv.all(Material.EMERALD);
                List<Boolean> isMoney = items.values().stream()
                        .map(itemStack -> itemStack.getItemMeta().getPersistentDataContainer().has(BankStore.getKey(), PersistentDataType.INTEGER))
                        .collect(Collectors.toList());
                MerchantRecipe recipe = ((MerchantInventory) inv).getSelectedRecipe();
                if (recipe != null && isMoney.contains(true)) {
                    recipe.setUses(recipe.getUses() - 1);
                    ChatUtil.error(p, "ゲーム内通貨で通常の村人と貿易することはできません！");
                    e.setCancelled(true);
                }
            }
        }
    }

    /**
     * JavaでインベントリをメニューUIとして使うため、そのハンドリングを行います。
     *
     * @param e ハンドリングに使用するイベント
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        final Inventory inv = e.getInventory();
        if (!invMap.containsKey(inv)) return;
        for (int i = 18; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            ItemUtil.addItem(item, e.getPlayer().getInventory(), (Player) e.getPlayer());
        }
        invMap.remove(inv);
    }

    private void onClick(MenuItem e) {
        MerchantRecipe recipe = ((Mapdata) e.getCustomData()).recipe;
        int index = ((Mapdata) e.getCustomData()).index;
        Villager villager = ((Mapdata) e.getCustomData()).villager;
        boolean isPurchase = Bank.isMoney(recipe.getResult());
        ItemStack money = Bank.isMoney(recipe.getResult()) ? recipe.getResult() : recipe.getIngredients().get(0);
        int price = money.getAmount() * Objects.requireNonNull(money.getItemMeta().getPersistentDataContainer().get(BankStore.getKey(), PersistentDataType.INTEGER));
        final Inventory inv;
        if (isPurchase) {
            inv = Bukkit.createInventory(null, 27, e.getIcon().displayName().append(Component.text(" を買取")).color(NamedTextColor.BLACK));
        } else {
            inv = Bukkit.createInventory(null, 27, e.getIcon().displayName().append(Component.text(" を購入")).color(NamedTextColor.BLACK));
        }

        final ItemStack down_button = new ItemStack(Material.RED_WOOL);
        ItemMeta itemMeta = down_button.getItemMeta();
        if (isPurchase) {
            itemMeta.displayName(Component.text("買取個数を減らす").color(NamedTextColor.RED));
        } else {
            itemMeta.displayName(Component.text("購入個数を減らす").color(NamedTextColor.RED));
        }
        down_button.setItemMeta(itemMeta);

        final ItemStack Buy_item = e.getIcon();
        itemMeta = Buy_item.getItemMeta();
        if (isPurchase) {
            itemMeta.lore(Collections.singletonList(Component.text("買い取るもの").color(NamedTextColor.GRAY)));
        } else {
            itemMeta.lore(Collections.singletonList(Component.text("購入するもの").color(NamedTextColor.GRAY)));
        }
        Buy_item.setItemMeta(itemMeta);

        final ItemStack up_button = new ItemStack(Material.LIME_WOOL);
        itemMeta = up_button.getItemMeta();
        if (isPurchase) {
            itemMeta.displayName(Component.text("買取個数を増やす").color(NamedTextColor.GREEN));
        } else {
            itemMeta.displayName(Component.text("購入個数を増やす").color(NamedTextColor.GREEN));
        }
        up_button.setItemMeta(itemMeta);

        final ItemStack price_item = new ItemStack(Material.EMERALD);
        itemMeta = price_item.getItemMeta();
        if (isPurchase) {
            itemMeta.displayName(Component.text("入手できる金額:" + price));
            itemMeta.lore(Collections.singletonList(Component.text("クリックして買取").color(NamedTextColor.GRAY)));
        } else {
            itemMeta.displayName(Component.text("必要な金額:" + price));
            itemMeta.lore(Collections.singletonList(Component.text("クリックして購入").color(NamedTextColor.GRAY)));
        }
        price_item.setItemMeta(itemMeta);

        final ItemStack partition = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        itemMeta = partition.getItemMeta();
        itemMeta.displayName(Component.text(""));
        partition.setItemMeta(itemMeta);

        inv.setItem(0, partition);
        inv.setItem(1, down_button);
        inv.setItem(2, Buy_item);
        inv.setItem(3, up_button);
        inv.setItem(4, partition);
        inv.setItem(5, partition);
        inv.setItem(6, price_item);
        inv.setItem(7, partition);
        inv.setItem(8, partition);
        for (int i = 9; i < 18; i++) {
            inv.setItem(i, partition);
        }

        invMap.put(inv, new GuiData(price, index, isPurchase, villager));
        e.getClicker().openInventory(inv);
    }

    /**
     * Mapのreturnに使うやつ
     */
    record Mapdata(MerchantRecipe recipe, int index, Villager villager) {
    }

    /**
     * お金に関するクラス
     */
    public record ReturnMoney(List<ItemStack> money_list, int total_money) {
    }

    private record GuiData(int price, int index, boolean isPurchase, Villager villager) {
    }
}
