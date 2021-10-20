package world.arainu.core.metaverseplugin.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.iphone.Bank;
import world.arainu.core.metaverseplugin.store.BankStore;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class VillagerListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Villager) {
            if (Boolean.TRUE.equals(sqlUtil.hasuuid(e.getRightClicked().getUniqueId()))) {
                e.setCancelled(true);
                Villager villager = (Villager) e.getRightClicked();
                List<MenuItem> tradeitems = villager.getRecipes().stream()
                        .map((recipe) -> new MenuItem(
                                this::onClick,
                                true,
                                Bank.isMoney(recipe.getResult()) ? recipe.getIngredients().get(0) : recipe.getResult(),
                                recipe,
                                false,
                                -1,
                                -1))
                        .collect(Collectors.toList());

                Gui.getInstance().openMenu(e.getPlayer(), villager.getName(), tradeitems);
            }
        }
    }

    /*
    TODO: Util化
     */
    private void playClickSound(Player p){
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1, 1f);
    }

    private void updatePrice(Inventory inv,ItemStack item){
        ItemStack priceItem = Objects.requireNonNull(inv.getItem(6));
        ItemMeta itemMeta = priceItem.getItemMeta();
        final int price = invMap.get(inv).price*item.getAmount();
        if (invMap.get(inv).isPurchase) {
            itemMeta.displayName(Component.text("入手できる金額:" + price));
        } else {
            itemMeta.displayName(Component.text("必要な金額:" + price));
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

        // 管理インベントリでなければ無視
        if (!invMap.containsKey(inv)) return;
        final int id = e.getRawSlot();
        if (id < 18 && id > 0) {
            e.setCancelled(true);
            final ItemStack item = Objects.requireNonNull(inv.getItem(2));
            switch (id) {
                case 1 -> {
                    playClickSound((Player) p);
                    if (item.getAmount() != 1) {
                        item.setAmount(item.getAmount() - 1);
                    }
                    updatePrice(inv,item);
                }
                case 3 -> {
                    playClickSound((Player) p);
                    if (item.getAmount() != 64) {
                        item.setAmount(item.getAmount() + 1);
                    }
                    updatePrice(inv,item);
                }
                case 6 -> {
                    playClickSound((Player) p);
                    GuiData guiData = invMap.get(inv);
                    if(guiData.isPurchase) {
                        final HashMap<Integer, ? extends ItemStack> item_list = inv.all(item.getType());
                        int total = 0;
                        for(Map.Entry<Integer, ? extends ItemStack> i: item_list.entrySet()){
                            if(i.getKey() != 2){
                                total += i.getValue().getAmount();
                            }
                        }
                        final int required_item = item.getAmount();
                        if (required_item <= total) {
                            Bank.addMoneyForPlayer((Player) p, guiData.price*item.getAmount());

                            total = 0;
                            for(Map.Entry<Integer, ? extends ItemStack> i: item_list.entrySet()){
                                final ItemStack pay_item = i.getValue();
                                final int index = i.getKey();
                                if(index != 2){
                                    if(total+pay_item.getAmount() < required_item) {
                                        inv.setItem(index,new ItemStack(Material.AIR));
                                        total += pay_item.getAmount();
                                    } else {
                                        pay_item.setAmount(pay_item.getAmount()-(required_item-total));
                                        inv.setItem(index,pay_item);
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        final List<ItemStack> money_list = new ArrayList<>(inv.all(Material.EMERALD).values());
                        int total_money = 0;
                        for (ItemStack i : money_list) {
                            final PersistentDataContainer persistentDataContainer = i.getItemMeta().getPersistentDataContainer();
                            if (persistentDataContainer.has(BankStore.getKey(), PersistentDataType.INTEGER)) {
                                total_money += persistentDataContainer.get(BankStore.getKey(), PersistentDataType.INTEGER) * i.getAmount();
                            }
                        }
                        final int required_money = guiData.price * Objects.requireNonNull(inv.getItem(2)).getAmount();
                        if (required_money <= total_money) {
                            for (ItemStack i : money_list) {
                                final PersistentDataContainer persistentDataContainer = i.getItemMeta().getPersistentDataContainer();
                                if (persistentDataContainer.has(BankStore.getKey(), PersistentDataType.INTEGER)) {
                                    inv.remove(i);
                                }
                            }
                            Bank.addMoneyForPlayer((Player) p, total_money - required_money);
                            p.getInventory().addItem(Objects.requireNonNull(inv.getItem(2)));
                        }
                    }
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
        invMap.remove(inv);
    }

    private void onClick(MenuItem e) {
        MerchantRecipe recipe = (MerchantRecipe) e.getCustomData();
        boolean isPurchase = Bank.isMoney(recipe.getResult());
        ItemStack money = Bank.isMoney(recipe.getResult()) ? recipe.getResult() : recipe.getIngredients().get(0);
        int price = money.getAmount() * money.getItemMeta().getPersistentDataContainer().get(BankStore.getKey(), PersistentDataType.INTEGER);
        final Inventory inv = Bukkit.createInventory(null, 27, e.getIcon().displayName().append(Component.text(" を購入")));

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
        } else {
            itemMeta.displayName(Component.text("必要な金額:" + price));
        }
        itemMeta.lore(Collections.singletonList(Component.text("クリックして購入").color(NamedTextColor.GREEN)));
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

        invMap.put(inv,new GuiData(price,isPurchase));
        e.getClicker().openInventory(inv);
    }

    private final HashMap<Inventory,GuiData> invMap = new HashMap<>();

    private static class GuiData {
        GuiData(int price, boolean isPurchase){
            this.price = price;
            this.isPurchase = isPurchase;
        }
        private final int price;
        private final boolean isPurchase;
    }
}
