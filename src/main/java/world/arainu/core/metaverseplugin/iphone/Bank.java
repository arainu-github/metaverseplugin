package world.arainu.core.metaverseplugin.iphone;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.store.BankStore;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * ネット銀行の関数。
 */
public class Bank extends iPhoneBase {
    public static void addMoneyForPlayer(Player player, int yen){
        int log_money = (int) Math.log(yen);
        if (log_money > 5) {
            log_money = 5;
        }
        for (int i = log_money; i >= 0; i--) {
            ItemStack moneyStack = new ItemStack(Material.EMERALD, (int) (yen / Math.pow(10, i)));
            ItemMeta itemMeta = moneyStack.getItemMeta();
            itemMeta.getPersistentDataContainer().set(BankStore.getKey(), PersistentDataType.INTEGER, (int) Math.pow(10, i));
            itemMeta.lore(Collections.singletonList(Component.text("ゲーム内通貨。").color(NamedTextColor.GREEN)));
            itemMeta.displayName(Component.text("$" + (int) Math.pow(10, i)));
            moneyStack.setItemMeta(itemMeta);
            player.getInventory().addItem(moneyStack);
            yen %= (int) Math.pow(10, i);
        }
    }

    @Override
    public void executeGui(MenuItem menuItem) {
        Economy econ = MetaversePlugin.getEcon();
        AtomicBoolean complete_flag = new AtomicBoolean(false);

        Consumer<MenuItem> withdrawal = (e) -> new AnvilGUI.Builder()
                .onClose(player -> {
                    if (!complete_flag.get()) player.sendMessage(ChatColor.GOLD + "[メタバースプラグイン] お金の引き出しを取りやめました。");
                })
                .onComplete((player, text) -> {
                    try {
                        int withdrawal_yen = Integer.parseInt(text);
                        if (withdrawal_yen < 0) {
                            throw new NumberFormatException();
                        } else if (econ.has(player, withdrawal_yen)) {
                            econ.withdrawPlayer(player, withdrawal_yen);
                            player.sendMessage(ChatColor.GREEN + "[メタバースプラグイン] " + econ.format(withdrawal_yen) + "を正常に引き出しました。");
                            addMoneyForPlayer(player, withdrawal_yen);
                            complete_flag.set(true);
                        } else {
                            Gui.error(player, "あなたはそこまでお金を持っていません！");
                            player.sendMessage(ChatColor.RED + "[メタバースプラグイン][エラー] 残高: " + econ.format(econ.getBalance(player)));
                        }
                    } catch (NumberFormatException err) {
                        Gui.error(player, "数字以外のものが含まれているか無効な数字です！");
                    }
                    return AnvilGUI.Response.close();
                })
                .title("引き出す金額を入力")
                .text("半角数字で!!!")
                .plugin(MetaversePlugin.getInstance())
                .open(menuItem.getClicker());

        Consumer<MenuItem> payment = (e) -> new AnvilGUI.Builder()
                .onClose(player -> {
                    if (!complete_flag.get()) player.sendMessage(ChatColor.GOLD + "[メタバースプラグイン] お金の入金を取りやめました。");
                })
                .onComplete((player, text) -> {
                    try {
                        int payment_yen = Integer.parseInt(text);
                        if (payment_yen < 0) {
                            throw new NumberFormatException();
                        } else {
                            complete_flag.set(true);
                            Inventory inv = Bukkit.createInventory(null, 9, Component.text("入金したいお金を入れてください。"));
                            player.openInventory(inv);
                            HashMap<UUID, Integer> gui_hashmap = BankStore.getGui_hashmap();
                            gui_hashmap.put(player.getUniqueId(),payment_yen);
                            BankStore.setGui_hashmap(gui_hashmap);
                        }
                    } catch (NumberFormatException err) {
                        Gui.error(player, "数字以外のものが含まれているか無効な数字です！");
                    }
                    return AnvilGUI.Response.close();
                })
                .title("引き出す金額を入力")
                .text("半角数字で!!!")
                .plugin(MetaversePlugin.getInstance())
                .open(menuItem.getClicker());

        Consumer<MenuItem> remittance = (e) -> new AnvilGUI.Builder()
                .onClose(player -> {
                    if (!complete_flag.get()) player.sendMessage(ChatColor.GOLD + "[メタバースプラグイン] お金の送金を取りやめました。");
                })
                .onComplete((player, text) -> {
                    try {
                        int remittance_yen = Integer.parseInt(text);
                        if (remittance_yen < 0) {
                            throw new NumberFormatException();
                        } else if (econ.has(player, remittance_yen)) {
                            AtomicBoolean complete_flag_ = new AtomicBoolean(false);
                            new AnvilGUI.Builder()
                                    .onClose(p -> {
                                        if (!complete_flag_.get()) p.sendMessage(ChatColor.GOLD + "[メタバースプラグイン] お金の送金を取りやめました。");
                                    })
                                    .onComplete((p, t) -> {
                                        Player player_ = Bukkit.getPlayer(t);
                                        if (player_ != null){
                                            econ.withdrawPlayer(p, remittance_yen);
                                            econ.depositPlayer(player_, remittance_yen);
                                            player.sendMessage(ChatColor.GREEN + "[メタバースプラグイン] " + econ.format(remittance_yen) + "を"+ player_.getDisplayName() +"に送金しました。");
                                        } else {
                                            Gui.error(p, "そのようなプレイヤーはいません："+t);
                                        }
                                        return AnvilGUI.Response.close();
                                    }).title("送金するプレイヤーを入力")
                                    .plugin(MetaversePlugin.getInstance())
                                    .text("プレイヤー名")
                                    .open(player);
                            complete_flag.set(true);
                        } else {
                            Gui.error(player, "あなたはそこまでお金を持っていません！");
                            player.sendMessage(ChatColor.RED + "[メタバースプラグイン][エラー] 残高: " + econ.format(econ.getBalance(player)));
                        }
                    } catch (NumberFormatException err) {
                        Gui.error(player, "数字以外のものが含まれているか無効な数字です！");
                    }
                    return AnvilGUI.Response.close();
                })
                .title("送金する金額を入力")
                .text("半角数字で!!!")
                .plugin(MetaversePlugin.getInstance())
                .open(menuItem.getClicker());

        Gui.getInstance().openMenu(menuItem.getClicker(),
                ChatColor.DARK_GREEN + "銀行",
                Arrays.asList(
                        new MenuItem(ChatColor.LIGHT_PURPLE + "残高: " + econ.format(econ.getBalance(menuItem.getClicker())), null, false, Material.EMERALD),
                        new MenuItem(ChatColor.GOLD + "引き出し", withdrawal, true, Material.REDSTONE),
                        new MenuItem(ChatColor.RED + "入金", payment, true, Material.GOLD_INGOT),
                        new MenuItem(ChatColor.YELLOW + "入金", remittance, true, Material.DIAMOND)
                )
        );
    }
}
