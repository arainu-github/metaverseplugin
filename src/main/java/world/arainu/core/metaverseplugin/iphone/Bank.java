package world.arainu.core.metaverseplugin.iphone;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.commands.CommandiPhone;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.listener.VillagerListener;
import world.arainu.core.metaverseplugin.store.BankStore;
import world.arainu.core.metaverseplugin.utils.BankNotice;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.ItemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
     *
     * @param yen      お金の金額
     * @param quantity 枚数
     * @return ItemStack
     */
    public static ItemStack getPluginMoneyEmerald(int yen, int quantity) {
        ItemStack moneyStack = new ItemStack(Material.EMERALD, quantity);
        ItemMeta itemMeta = moneyStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(BankStore.getKey(), PersistentDataType.INTEGER, yen);
        itemMeta.lore(Collections.singletonList(Component.text("ゲーム内通貨。").color(NamedTextColor.GREEN)));
        Component component = Component.text(yen + "円").decoration(TextDecoration.ITALIC, false);
        itemMeta.displayName(component);
        moneyStack.setItemMeta(itemMeta);
        return moneyStack;
    }

    /**
     * アイテムがお金かどうか確認する関数
     *
     * @param item 対象のアイテム
     * @return お金かどうか
     */
    public static boolean isMoney(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(BankStore.getKey(), PersistentDataType.INTEGER);
    }

    /**
     * 口座のお金を現金に換金する関数。
     *
     * @param player 対象のプレイヤー
     * @param yen    換金する額
     */
    public static void addMoneyForPlayer(Player player, int yen) {
        addMoneyForInventory(player.getInventory(), yen,player);
    }

    /**
     * 口座のお金を現金に換金する関数。
     *
     * @param inv 対象のインベントリ
     * @param yen 換金する額
     */
    public static void addMoneyForInventory(Inventory inv, int yen,Player p) {
        int log_money = (int) Math.log(yen);
        if (log_money > 5) {
            log_money = 5;
        }
        for (int i = log_money; i >= 0; i--) {
            ItemStack moneyStack = getPluginMoneyEmerald((int) Math.pow(10, i), (int) (yen / Math.pow(10, i)));
            ItemUtil.addItem(moneyStack,inv,p);
            yen %= (int) Math.pow(10, i);
        }
    }

    private void withdraw_Complete(Player player, String text, Economy econ, AtomicBoolean complete_flag) {
        try {
            int withdrawal_yen = Integer.parseInt(text);
            if (withdrawal_yen < 0) {
                throw new NumberFormatException();
            } else if (econ.has(player, withdrawal_yen)) {
                econ.withdrawPlayer(player, withdrawal_yen);
                ChatUtil.success(player, econ.format(withdrawal_yen) + "を正常に引き出しました。");
                addMoneyForPlayer(player, withdrawal_yen);
                complete_flag.set(true);
            } else {
                ChatUtil.error(player, "あなたはそこまでお金を持っていません！\n残高: " + econ.format(econ.getBalance(player)));
            }
        } catch (NumberFormatException err) {
            ChatUtil.error(player, "数字以外のものが含まれているか無効な数字です！");
        }
    }

    private void payment_Complete(Player player, String text, AtomicBoolean complete_flag) {
        try {
            if(Objects.equals(text, "#")){
                final Inventory player_inv = player.getInventory();
                final VillagerListener.ReturnMoney returnMoney = VillagerListener.getTotalmoney(player_inv);
                text = String.valueOf(returnMoney.total_money());
            }
            int payment_yen = Integer.parseInt(text);
            if (payment_yen < 0) {
                throw new NumberFormatException();
            } else {
                complete_flag.set(true);
                Inventory inv = Bukkit.createInventory(null, 27, Component.text("入金したいお金を入れてください。"));
                final ItemStack partition = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta itemMeta = partition.getItemMeta();
                itemMeta.displayName(Component.text(""));
                partition.setItemMeta(itemMeta);
                for (int i = 0; i < 9; i++) {
                    inv.setItem(i, partition);
                }
                final ItemStack price_item = new ItemStack(Material.EMERALD);
                itemMeta = price_item.getItemMeta();
                itemMeta.displayName(Component.text("必要な金額:" + payment_yen));
                itemMeta.lore(Collections.singletonList(Component.text("クリックして入金").color(NamedTextColor.GRAY)));
                price_item.setItemMeta(itemMeta);
                inv.setItem(4, price_item);
                final ItemStack move_all_item = new ItemStack(Material.GOLD_INGOT);
                itemMeta = move_all_item.getItemMeta();
                itemMeta.displayName(Component.text("全額入金ボックスに移動させる").color(NamedTextColor.GOLD));
                move_all_item.setItemMeta(itemMeta);
                inv.setItem(8, move_all_item);
                player.openInventory(inv);
                HashMap<UUID, Integer> gui_hashmap = BankStore.getGui_hashmap();
                gui_hashmap.put(player.getUniqueId(), payment_yen);
                BankStore.setGui_hashmap(gui_hashmap);
            }
        } catch (NumberFormatException err) {
            ChatUtil.error(player, "数字以外のものが含まれているか無効な数字です！");
        }
    }

    private void remittance_Complete(Player player, String text, Economy econ, AtomicBoolean complete_flag) {
        try {
            int remittance_yen = Integer.parseInt(text);
            if (remittance_yen < 0) {
                throw new NumberFormatException();
            } else if (econ.has(player, remittance_yen)) {
                complete_flag.set(true);
                AtomicBoolean complete_flag_ = new AtomicBoolean(false);
                if (Gui.isBedrock(player)) {
                    CustomForm.Builder builder = CustomForm.builder()
                            .title("お金の送金")
                            .input("送金するプレイヤーを入力", "プレイヤー名")
                            .responseHandler((form, responseData) -> {
                                CustomFormResponse response = form.parseResponse(responseData);
                                if (!response.isCorrect()) ChatUtil.warning(player, "お金の送金を取りやめました。");
                                else
                                    remittance_Complete2(player, econ, remittance_yen, response.getInput(0), complete_flag_);
                            });
                    final FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
                    fPlayer.sendForm(builder);
                } else {
                    new AnvilGUI.Builder()
                            .onClose(p -> {
                                if (!complete_flag_.get())
                                    ChatUtil.warning(p, "お金の送金を取りやめました。");
                            })
                            .onComplete((p, t) -> {
                                remittance_Complete2(player, econ, remittance_yen, t, complete_flag_);
                                return AnvilGUI.Response.close();
                            }).title("送金するプレイヤーを入力")
                            .plugin(MetaversePlugin.getInstance())
                            .text("プレイヤー名")
                            .open(player);
                }
                complete_flag.set(true);
            } else {
                ChatUtil.error(player, "あなたはそこまでお金を持っていません！ 残高: " + econ.format(econ.getBalance(player)));
            }
        } catch (NumberFormatException err) {
            ChatUtil.error(player, "数字以外のものが含まれているか無効な数字です！");
        }
    }

    private void remittance_Complete2(Player player, Economy econ, int remittance_yen, String t, AtomicBoolean complete_flag_) {
        OfflinePlayer player_ = Bukkit.getOfflinePlayer(t);
        if (econ.hasAccount(player_)) {
            econ.withdrawPlayer(player, remittance_yen);
            econ.depositPlayer(player_, remittance_yen);
            ChatUtil.success(player, econ.format(remittance_yen) + "を" + player_.getName() + "に送金しました。");
            if (player_.isOnline()) {
                final Player player_online = (Player) player_;
                ChatUtil.success(player_online, (TextComponent) player.displayName()
                        .append(Component.text("があなたへ" + econ.format(remittance_yen) + "送金しました。"))
                        .append(Component.text("\n所持金は" + econ.format(econ.getBalance(player_)) + "です。")));
            } else {
                ChatUtil.warning(player, "送金先のプレイヤーはオフラインです。プレイヤーが入室してきたときに送金の趣旨を通知します。");
                HashMap<UUID, List<BankNotice>> remittance_map = BankStore.getRemittance_map();
                if (remittance_map.containsKey(player_.getUniqueId())) {
                    List<BankNotice> old_list = new ArrayList<>(remittance_map.get(player_.getUniqueId()));
                    old_list.add(new BankNotice(player, remittance_yen));
                    remittance_map.replace(player_.getUniqueId(), old_list);
                } else {
                    remittance_map.put(player_.getUniqueId(), List.of(new BankNotice(player, remittance_yen)));
                }
                BankStore.setRemittance_map(remittance_map);
            }
            complete_flag_.set(true);
        } else {
            ChatUtil.error(player, "そのようなプレイヤーはいません：" + t);
        }
    }

    @Override
    public void executeGui(MenuItem menuItem) {
        Economy econ = MetaversePlugin.getEcon();
        AtomicBoolean complete_flag = new AtomicBoolean(false);
        Consumer<MenuItem> withdrawal;
        Consumer<MenuItem> payment;
        Consumer<MenuItem> remittance;
        if (Gui.isBedrock(menuItem.getClicker())) {
            withdrawal = (e) -> {
                Player player = menuItem.getClicker();
                CustomForm.Builder builder = CustomForm.builder()
                        .title("お金の引き出し")
                        .input("引き出す金額を入力", "半角数字で!!!!!")
                        .responseHandler((form, responseData) -> {
                            CustomFormResponse response = form.parseResponse(responseData);
                            if (!response.isCorrect()) ChatUtil.warning(player, "お金の引き出しを取りやめました。");
                            else withdraw_Complete(player, response.getInput(0), econ, complete_flag);
                        });
                final FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
                fPlayer.sendForm(builder);
            };

            payment = (e) -> {
                Player player = menuItem.getClicker();
                CustomForm.Builder builder = CustomForm.builder()
                        .title("お金の入金")
                        .input("入金する金額を入力", "全額入金は#と入力")
                        .responseHandler((form, responseData) -> {
                            CustomFormResponse response = form.parseResponse(responseData);
                            if (!response.isCorrect()) ChatUtil.warning(player, "お金の入金を取りやめました。");
                            else payment_Complete(player, response.getInput(0), complete_flag);
                        });
                final FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
                fPlayer.sendForm(builder);
            };

            remittance = (e) -> {
                Player player = menuItem.getClicker();
                CustomForm.Builder builder = CustomForm.builder()
                        .title("お金の送金")
                        .input("送金する金額を入力", "半角数字で!!!!!")
                        .responseHandler((form, responseData) -> {
                            CustomFormResponse response = form.parseResponse(responseData);
                            if (!response.isCorrect()) ChatUtil.warning(player, "お金の送金を取りやめました。");
                            else remittance_Complete(player, response.getInput(0), econ, complete_flag);
                        });
                final FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
                fPlayer.sendForm(builder);
            };
        } else {
            withdrawal = (e) -> new AnvilGUI.Builder()
                    .onClose(player -> {
                        if (!complete_flag.get()) ChatUtil.warning(player, "お金の引き出しを取りやめました。");
                    })
                    .onComplete((player, text) -> {
                        withdraw_Complete(player, text, econ, complete_flag);
                        return AnvilGUI.Response.close();
                    })
                    .title("引き出す金額を入力")
                    .text("半角数字で!!!")
                    .plugin(MetaversePlugin.getInstance())
                    .open(menuItem.getClicker());

            payment = (e) -> new AnvilGUI.Builder()
                    .onClose(player -> {
                        if (!complete_flag.get()) ChatUtil.warning(player, "お金の入金を取りやめました。");
                    })
                    .onComplete((player, text) -> {
                        payment_Complete(player, text, complete_flag);
                        return AnvilGUI.Response.close();
                    })
                    .title("入金する金額を入力")
                    .text("全額入金は#と入力")
                    .plugin(MetaversePlugin.getInstance())
                    .open(menuItem.getClicker());

            remittance = (e) -> new AnvilGUI.Builder()
                    .onClose(player -> {
                        if (!complete_flag.get()) ChatUtil.warning(player, "お金の送金を取りやめました。");
                    })
                    .onComplete((player, text) -> {
                        remittance_Complete(player, text, econ, complete_flag);
                        return AnvilGUI.Response.close();
                    })
                    .title("送金する金額を入力")
                    .text("半角数字で!!!")
                    .plugin(MetaversePlugin.getInstance())
                    .open(menuItem.getClicker());
        }

        Gui.getInstance().openMenu(menuItem.getClicker(),
                ChatColor.DARK_GREEN + "銀行",
                Arrays.asList(
                        new MenuItem(ChatColor.LIGHT_PURPLE + "残高: " + econ.format(econ.getBalance(menuItem.getClicker())), null, false, Material.EMERALD),
                        new MenuItem("", null, false, Material.LIME_STAINED_GLASS_PANE),
                        new MenuItem("引き出し", withdrawal, true, Material.REDSTONE),
                        new MenuItem("入金", payment, true, Material.GOLD_INGOT),
                        new MenuItem("送金", remittance, true, Material.DIAMOND),
                        new MenuItem("戻る", CommandiPhone::run, true, Material.ARROW, null, 8, 0)
                )
        );
    }
}
