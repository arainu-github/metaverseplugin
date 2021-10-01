package world.arainu.core.metaverseplugin.iphone;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.store.BankStore;
import world.arainu.core.metaverseplugin.utils.BankNotice;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * ネット銀行の関数。
 *
 * @author kumitatepazuru
 */
public class Bank extends iPhoneBase {
    /**
     * プラグインのゲーム内通貨を取得するプログラム
     * @param yen お金の金額
     * @param quantity 枚数
     * @return ItemStack
     */
    public static ItemStack getPluginMoneyEmerald(int yen, int quantity){
        ItemStack moneyStack = new ItemStack(Material.EMERALD, quantity);
        ItemMeta itemMeta = moneyStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(BankStore.getKey(), PersistentDataType.INTEGER, yen);
        itemMeta.lore(Collections.singletonList(Component.text("ゲーム内通貨。").color(NamedTextColor.GREEN)));
        Component component = Component.text(yen+"円").decoration(TextDecoration.ITALIC,false);
        itemMeta.displayName(component);
        moneyStack.setItemMeta(itemMeta);
        return moneyStack;
    }

    /**
     * 口座のお金を現金に換金する関数。
     *
     * @param player 対象のプレイヤー
     * @param yen    換金する額
     */
    public static void addMoneyForPlayer(Player player, int yen) {
        int log_money = (int) Math.log(yen);
        if (log_money > 5) {
            log_money = 5;
        }
        for (int i = log_money; i >= 0; i--) {
            ItemStack moneyStack = getPluginMoneyEmerald((int) Math.pow(10, i),(int) (yen / Math.pow(10, i)));
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
                    if (!complete_flag.get()) Gui.warning(player,"お金の引き出しを取りやめました。");
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
                    if (!complete_flag.get()) Gui.warning(player,"お金の入金を取りやめました。");
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
                            gui_hashmap.put(player.getUniqueId(), payment_yen);
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
                    if (!complete_flag.get()) Gui.warning(player,"お金の送金を取りやめました。");
                })
                .onComplete((player, text) -> {
                    try {
                        int remittance_yen = Integer.parseInt(text);
                        if (remittance_yen < 0) {
                            throw new NumberFormatException();
                        } else if (econ.has(player, remittance_yen)) {
                            complete_flag.set(true);
                            AtomicBoolean complete_flag_ = new AtomicBoolean(false);
                            new AnvilGUI.Builder()
                                    .onClose(p -> {
                                        if (!complete_flag_.get())
                                            Gui.warning(p,"お金の送金を取りやめました。");
                                    })
                                    .onComplete((p, t) -> {
                                        // TODO: getDisplayNameをDisplayNameに変更する
                                        OfflinePlayer player_ = Bukkit.getOfflinePlayer(t);
                                        if (econ.hasAccount(player_)) {
                                            econ.withdrawPlayer(p, remittance_yen);
                                            econ.depositPlayer(player_, remittance_yen);
                                            player.sendMessage(ChatColor.GREEN + "[メタバースプラグイン] " + econ.format(remittance_yen) + "を" + player_.getName() + "に送金しました。");
                                            if (player_.isOnline()) {
                                                final Player player_online = (Player) player_;
                                                player_online.sendMessage(Component.text(ChatColor.GREEN+"[メタバースプラグイン] ").append(p.displayName()).append(Component.text("があなたへ"+econ.format(remittance_yen) + "送金しました。")));
                                                player_online.sendMessage(ChatColor.GREEN + "[メタバースプラグイン] 所持金は" + econ.format(econ.getBalance(player_)) + "です。");
                                            } else {
                                                Gui.warning(player,"送金先のプレイヤーはオフラインです。プレイヤーが入室してきたときに送金の趣旨を通知します。");
                                                HashMap<UUID, List<BankNotice>> remittance_map = BankStore.getRemittance_map();
                                                if (remittance_map.containsKey(player_.getUniqueId())) {
                                                    List<BankNotice> old_list = new ArrayList<>(remittance_map.get(player_.getUniqueId()));
                                                    old_list.add(new BankNotice(p,remittance_yen));
                                                    remittance_map.replace(player_.getUniqueId(),old_list);
                                                } else {
                                                    remittance_map.put(player_.getUniqueId(), List.of(new BankNotice(p,remittance_yen)));
                                                }
                                                BankStore.setRemittance_map(remittance_map);
                                            }
                                            complete_flag_.set(true);
                                        } else {
                                            Gui.error(p, "そのようなプレイヤーはいません：" + t);
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
                        new MenuItem(ChatColor.YELLOW + "送金", remittance, true, Material.DIAMOND)
                )
        );
    }
}
