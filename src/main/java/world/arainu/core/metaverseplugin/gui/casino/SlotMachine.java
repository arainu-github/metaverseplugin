package world.arainu.core.metaverseplugin.gui.casino;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.SlotUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * @author JolTheGreat
 * スロットマシーンのクラス
 * SlotMachine.start(player)で開始
 */

public class SlotMachine implements Listener {

    final private static ArrayList<BukkitTask> tasks = new ArrayList<>();
    final private static SlotUtil.SlotListeners listeners = new SlotUtil.SlotListeners();
    static Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Slot Machine");

    /**
     * とりあえずリスナーとスロットのコードを同じクラスにまとめました。
     *
     * @param event イベント
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Slot Machine")) {
            tasks.forEach(BukkitTask::cancel);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        ItemStack eventStack = event.getCurrentItem();
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Slot Machine") && eventStack != null) {
            switch (Objects.requireNonNull(eventStack).getType()) {
                case WARPED_BUTTON -> {
                    final String displayName = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();
                    int type = Integer.parseInt(displayName.split("番")[0].replace("§c", ""));
                    tasks.get(type - 1).cancel();
                    inventory.clear(type + 45);
                    inventory.clear(33);
                    if (tasks.get(0).isCancelled() && tasks.get(1).isCancelled() && tasks.get(2).isCancelled()) {
                        listeners.slotFinishTrigger(SlotUtil.SlotListeners.StopMethod.INDIVIDUAL);
                    }
                }
                case GREEN_STAINED_GLASS_PANE -> {
                    tasks.add(Bukkit.getScheduler().runTaskTimer(MetaversePlugin.getInstance(), () -> {
                        ItemStack newSlot = SlotUtil.getRandom();
                        inventory.setItem(28, inventory.getItem(19));
                        inventory.setItem(19, inventory.getItem(10));
                        inventory.setItem(10, newSlot);
                    }, 0, 5));

                    tasks.add(Bukkit.getScheduler().runTaskTimer(MetaversePlugin.getInstance(), () -> {
                        ItemStack newSlot = SlotUtil.getRandom();
                        inventory.setItem(29, inventory.getItem(20));
                        inventory.setItem(20, inventory.getItem(11));
                        inventory.setItem(11, newSlot);
                    }, 0, 5));
                    tasks.add(Bukkit.getScheduler().runTaskTimer(MetaversePlugin.getInstance(), () -> {
                        ItemStack newSlot = SlotUtil.getRandom();
                        inventory.setItem(30, inventory.getItem(21));
                        inventory.setItem(21, inventory.getItem(12));
                        inventory.setItem(12, newSlot);
                    }, 0, 5));

                    final ItemStack stopAllButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    final ItemMeta stopAllButtonMeta = stopAllButton.getItemMeta();
                    assert stopAllButtonMeta != null;
                    stopAllButtonMeta.setDisplayName(ChatColor.RED + "Stop");
                    stopAllButton.setItemMeta(stopAllButtonMeta);
                    inventory.setItem(24, stopAllButton);
                }
                case RED_STAINED_GLASS_PANE -> {
                    tasks.forEach(BukkitTask::cancel);
                    listeners.slotFinishTrigger(SlotUtil.SlotListeners.StopMethod.ALL);
                    inventory.remove(Material.RED_STAINED_GLASS_PANE);
                }
            }
            event.setCancelled(true);
        } else {
            ChatUtil.warning(event.getWhoClicked(), "エラーが発生しました。");
        }

    }

    /**
     * スロットマシーンを開く関数
     *
     * @param player スロットのguiを表示させたいプレイヤー
     */
    public static void start(Player player) {
        if (!Gui.isBedrock(player)) {
            new AnvilGUI.Builder()
                    .title("賭ける金額を入力")
                    .onClose(p -> ChatUtil.warning(p, "賭ける金額の入力を取りやめました。"))
                    .onComplete(slotMechanic())
                    .itemLeft(new ItemStack(Material.PAPER))
                    .plugin(MetaversePlugin.getInstance())
                    .text("半角数字で!!!")
                    .open(player);
        }
    }

    /**
     * 賭ける金額と止まったスロットの状態から賞金を計算する関数
     *
     * @param pattern スロットの状態
     * @param method  　スロットが止まった方法（１列ずつ止めたか３列同時に止めたか）
     * @param bet     　賭ける金額
     * @return 賞金
     */
    public static double getWinMoney(List<Material> pattern, SlotUtil.SlotListeners.StopMethod method, int bet) {
        /*
        賞金にかける倍数をconfig.ymlに保存
         */
        double winMoney = bet;
        final FileConfiguration configuration = MetaversePlugin.getInstance().getConfig();

        if ((pattern.get(0) == pattern.get(4) && pattern.get(0) == pattern.get(8))) {

            switch (pattern.get(4)) {
                case BIRCH_WOOD -> winMoney = bet * configuration.getDouble("casino.diagonal.birchWood");
                case APPLE -> winMoney = bet * configuration.getDouble("casino.diagonal.apple");
                case BREAD -> winMoney = bet * configuration.getDouble("casino.diagonal.bread");
                case IRON_INGOT -> winMoney = bet * configuration.getDouble("casino.diagonal.iron");
                case GOLD_INGOT -> winMoney = bet * configuration.getDouble("casino.diagonal.gold");
                case DIAMOND -> winMoney = bet * configuration.getDouble("casino.diagonal.diamond");
                case NETHERITE_INGOT -> winMoney = bet * configuration.getDouble("casino.diagonal.netherite");
                case DRAGON_HEAD -> winMoney = bet * configuration.getDouble("casino.diagonal.dragonHead");
                case PLAYER_HEAD -> winMoney = bet * configuration.getDouble("casino.diagonal.head");
            }

            /*
             * X0X
             * 0X0
             * X0X
             * のような模様だったら(X)、賞金二倍
             */
            if ((pattern.get(0) == pattern.get(4) && pattern.get(0) == pattern.get(8)) && (pattern.get(2) == pattern.get(4) && pattern.get(2) == pattern.get(6))) {
                winMoney = winMoney * 2;
            }
        } else if ((pattern.get(3) == pattern.get(4)) && (pattern.get(3) == pattern.get(5))) {
            /*
            000
            XXX
            000
            だった場合の処理
             */
            switch (pattern.get(4)) {
                case BIRCH_WOOD -> winMoney = bet * configuration.getDouble("casino.horizontal.middle.birchWood");
                case APPLE -> winMoney = bet * configuration.getDouble("casino.horizontal.middle.apple");
                case BREAD -> winMoney = bet * configuration.getDouble("casino.horizontal.middle.bread");
                case IRON_INGOT -> winMoney = bet * configuration.getDouble("casino.horizontal.middle.iron");
                case GOLD_INGOT -> winMoney = bet * configuration.getDouble("casino.horizontal.middle.gold");
                case DIAMOND -> winMoney = bet * configuration.getDouble("casino.horizontal.middle.diamond");
                case NETHERITE_INGOT -> winMoney = bet * configuration.getDouble("casino.horizontal.middle.netherite");
                case DRAGON_HEAD -> winMoney = bet * configuration.getDouble("casino.horizontal.middle.dragonHead");
                case PLAYER_HEAD -> winMoney = bet * configuration.getDouble("casino.horizontal.middle.head");
            }
        } else if (pattern.stream().distinct().limit(2).count() <= 1) {
            /*
            スロット内のアイテムがすべて同じだったら、賞金二十倍
            多分無いと思うけど
             */
            winMoney = winMoney * 20;
        } else if ((pattern.get(0) == pattern.get(1) && pattern.get(0) == pattern.get(2)) || (pattern.get(6) == pattern.get(7) && pattern.get(6) == pattern.get(8))) {
            /*
            XXX
            000
            000
            又は
            000
            000
            XXX
            だった場合の処理
             */
            switch (pattern.get(4)) {
                case BIRCH_WOOD -> winMoney = bet * configuration.getDouble("casino.horizontal.else.birchWood");
                case APPLE -> winMoney = bet * configuration.getDouble("casino.horizontal.else.apple");
                case BREAD -> winMoney = bet * configuration.getDouble("casino.horizontal.else.bread");
                case IRON_INGOT -> winMoney = bet * configuration.getDouble("casino.horizontal.else.iron");
                case GOLD_INGOT -> winMoney = bet * configuration.getDouble("casino.horizontal.else.gold");
                case DIAMOND -> winMoney = bet * configuration.getDouble("casino.horizontal.else.diamond");
                case NETHERITE_INGOT -> winMoney = bet * configuration.getDouble("casino.horizontal.else.netherite");
                case DRAGON_HEAD -> winMoney = bet * configuration.getDouble("casino.horizontal.else.dragonHead");
                case PLAYER_HEAD -> winMoney = bet * configuration.getDouble("casino.horizontal.else.head");
            }
        }


        /*
         * ３列同時に止められたならば、賞金は二倍
         */
        if (method == SlotUtil.SlotListeners.StopMethod.ALL) {
            winMoney = winMoney * 2;
        }

        //かける金額と賞金が同じなら賞金は０
        return bet == winMoney ? 0 : winMoney;
    }

    /**
     * @return 賭ける金額のAnvilGuiを閉じたときの処理（カジノの処理でもあるョ）
     */
    public static BiFunction<Player, String, AnvilGUI.Response> slotMechanic() {
        return (player, s) -> {
            listeners.clearSlotFinishListeners();
            tasks.clear();
            //数字かどうか確認
            if (Pattern.compile("-?\\d+(\\.\\d+)?").matcher(s).matches()) {
                final int bet = Integer.parseInt(s);
                final List<ItemStack> possible = SlotUtil.getAll();

                if (MetaversePlugin.getEcon().has(player, bet)) {
                    if (possible.size() == 9) {
                        final ItemStack around = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                        final int[] aroundPattern = {0, 1, 2, 3, 4, 9, 13, 18, 22, 27, 31, 36, 37, 38, 39, 40};
                        for (int i : aroundPattern) {
                            inventory.setItem(i, around);
                        }

                        final int[] possiblePattern = {10, 11, 12, 19, 20, 21, 28, 29, 30};
                        for (int i = 0; i < possible.size(); i++) {
                            inventory.setItem(possiblePattern[i], possible.get(i));
                        }

                        final ItemStack startButton = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
                        final ItemMeta startButtonMeta = startButton.getItemMeta();
                        assert startButtonMeta != null;
                        startButtonMeta.setDisplayName(ChatColor.GREEN + "Start");
                        startButton.setItemMeta(startButtonMeta);
                        inventory.setItem(24, startButton);


                        final int[] stopButtonPattern = {46, 47, 48};

                        for (int j : stopButtonPattern) {
                            ItemStack stopButton = new ItemStack(Material.WARPED_BUTTON);
                            ItemMeta stopButtonMeta = stopButton.getItemMeta();
                            assert stopButtonMeta != null;
                            stopButtonMeta.setDisplayName(ChatColor.RED + ((j - 45) + "番目のスロットを止める"));
                            stopButton.setItemMeta(stopButtonMeta);
                            inventory.setItem(j, stopButton);
                        }
                        player.openInventory(inventory);

                        listeners.addSlotFinishListener((stopMethod) -> {
                            int prize = (int) Math.round(getWinMoney(getPattern(), stopMethod, bet));
                            /*
                             * @todo prize変数にあるだけプレイヤーにお金を口座に直接入金するコードを書く
                             */
                        });
                    } else {
                        throw new Error("スロット内のアイテムは９個でなければいけません。SlotUtilを確認してください。");
                    }
                } else {
                    ChatUtil.warning(player, "あなたはそこまでお金を持っていません");
                }
            } else {
                ChatUtil.error(player, "数字以外のものが含まれているか無効な数字です！");
            }
            return AnvilGUI.Response.close();
        };
    }

    /**
     * スロット内の９つのアイテムを取得する関数
     *
     * @return アイテムの状態
     */
    public static List<Material> getPattern() {
        return Arrays.asList(Objects.requireNonNull(
                        Objects.requireNonNull(inventory.getItem(10)).getType()),
                Objects.requireNonNull(inventory.getItem(11)).getType(),
                Objects.requireNonNull(inventory.getItem(12)).getType(),
                Objects.requireNonNull(inventory.getItem(19)).getType(),
                Objects.requireNonNull(inventory.getItem(20)).getType(),
                Objects.requireNonNull(inventory.getItem(21)).getType(),
                Objects.requireNonNull(inventory.getItem(28)).getType(),
                Objects.requireNonNull(inventory.getItem(29)).getType(),
                Objects.requireNonNull(inventory.getItem(30)).getType()
        );
    }
}
