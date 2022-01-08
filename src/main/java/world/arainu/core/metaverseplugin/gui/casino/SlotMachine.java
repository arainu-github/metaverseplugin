package world.arainu.core.metaverseplugin.gui.casino;

import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
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

public class SlotMachine implements Listener {

    final private static ArrayList<BukkitTask> tasks = new ArrayList<>();
    final private static SlotUtil.SlotListeners listeners = new SlotUtil.SlotListeners();
    static Inventory inventory = Bukkit.createInventory(null, 54, Component.text(ChatColor.GOLD + "Slot Machine"));

    public SlotMachine() {
        MetaversePlugin.getInstance().getServer().getPluginManager().registerEvents(this, MetaversePlugin.getInstance());
    }

    /**
     * とりあえずリスナーとスロットのコードを同じクラスにまとめました。
     *
     * @param event イベント
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            tasks.forEach(BukkitTask::cancel);
            SlotUtil.isSlotStarted = false;
        }
    }

    /**
     * スロットマシーンを開く関数
     *
     * @param player スロットのguiを表示させたいプレイヤー
     */
    public void start(Player player) {
        if (!Gui.isBedrock(player)) {
            new AnvilGUI.Builder()
                    .title("賭ける金額を入力")
                    .onClose(p -> ChatUtil.warning(p, "賭ける金額の入力を取りやめました。"))
                    .onComplete(slotMechanic())
                    .itemLeft(new ItemStack(Material.PAPER))
                    .plugin(MetaversePlugin.getInstance())
                    .text("半角数字で!:残高=" + MetaversePlugin.getEcon().getBalance(player) + "円")
                    .open(player);
        }
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
                        startButtonMeta.displayName(Component.text(ChatColor.GREEN + "スロットを回す"));
                        startButton.setItemMeta(startButtonMeta);
                        inventory.setItem(24, startButton);


                        final int[] stopButtonPattern = {46, 47, 48};

                        for (int j : stopButtonPattern) {
                            ItemStack stopButton = new ItemStack(Material.WARPED_BUTTON);
                            ItemMeta stopButtonMeta = stopButton.getItemMeta();
                            assert stopButtonMeta != null;
                            stopButtonMeta.displayName(Component.text(ChatColor.RED + ((j - 45) + "番目のスロットを止める")));
                            stopButton.setItemMeta(stopButtonMeta);
                            inventory.setItem(j, stopButton);
                        }
                        player.openInventory(inventory);

                        listeners.addSlotFinishListener((stopMethod) -> {
                            SlotUtil.SlotResult result = getWinMoney(getPattern(), stopMethod, bet);
                            System.out.println(result);
                            if (result.getPrize() != 0) {
                                String itemNameJapanese = "";
                                String method = "";
                                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 1F, 0.5F);

                                switch (result.getMaterial()) {
                                    case BIRCH_WOOD -> itemNameJapanese = "シラカバの木";
                                    case APPLE -> itemNameJapanese = "リンゴ";
                                    case BREAD -> itemNameJapanese = "パン";
                                    case IRON_INGOT -> itemNameJapanese = "銀インゴット";
                                    case GOLD_INGOT -> itemNameJapanese = "金インゴット";
                                    case DIAMOND -> itemNameJapanese = "ダイヤモンド";
                                    case NETHERITE_INGOT -> itemNameJapanese = "ネザライトインゴット";
                                    case DRAGON_HEAD -> itemNameJapanese = "ドラゴンの頭";
                                    case PLAYER_HEAD -> itemNameJapanese = "いぬたぬきの生首";
                                }
                                ChatUtil.success(player, "おめでとうございます！" + itemNameJapanese + "が");

                                switch (result.getPatterns()) {
                                    case ALL -> ChatUtil.success(player, "\nXXX\nXXX\nXXX");
                                    case X -> ChatUtil.success(player, "\nX-X\n-X-\nX-X");
                                    case DIAGONAL -> ChatUtil.success(player, "\nX--\n-X-\n--X\n又は\n--X\n-X-\nX--");
                                    case ELSE_HORIZONTAL -> ChatUtil.success(player, "\nXXX\n---\n---\n又は\n---\n---\nXXX");
                                    case MIDDLE_HORIZONTAL -> ChatUtil.success(player, "\n---\nXXX\n---");
                                }

                                switch (result.getStopMethod()) {
                                    case ALL -> method = "同時に";
                                    case INDIVIDUAL -> method = "一つずつ";
                                }

                                ChatUtil.success(player, "のような模様で揃い、スロットが" + method + "止められたので、" + result.getPrize() + "円ゲットです！");
                            } else {
                                player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1F, 0.5F);
                                ChatUtil.error(player, "残念！あなたは" + bet + "円負けました。ご臨終様です。");
                            }

                            /*
                             * @todo result.getPrize()にあるだけプレイヤーにお金を口座に直接入金するコードを書く
                             */

                            final ItemStack againButton = new ItemStack(Material.SPECTRAL_ARROW);
                            final ItemMeta againButtonMeta = againButton.getItemMeta();
                            final List<Component> againButtonLore = new ArrayList<>();

                            againButtonMeta.displayName(Component.text(ChatColor.GOLD + "もう一度!"));
                            againButtonLore.add(Component.text("残高: " + (int) MetaversePlugin.getEcon().getBalance(player) + "円"));
                            againButtonMeta.lore(againButtonLore);

                            againButton.setItemMeta(againButtonMeta);
                            inventory.setItem(24, againButton);
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
     * 賭ける金額と止まったスロットの状態から賞金を計算する関数
     *
     * @param pattern スロットの状態
     * @param method  　スロットが止まった方法（１列ずつ止めたか３列同時に止めたか）
     * @param bet     　賭ける金額
     * @return 賞金
     */
    public static SlotUtil.SlotResult getWinMoney(List<Material> pattern, SlotUtil.SlotListeners.StopMethod method, int bet) {
        /*
        賞金にかける倍数をconfig.ymlに保存
         */
        SlotUtil.SlotResult slotResult = new SlotUtil.SlotResult();
        slotResult.setStopMethod(method);
        double winMoney = 0;
        final FileConfiguration configuration = MetaversePlugin.getInstance().getConfig();

        if ((pattern.get(0) == pattern.get(4) && pattern.get(0) == pattern.get(8)) || (pattern.get(2) == pattern.get(4) && pattern.get(2) == pattern.get(6))) {
            /*
            X00
            0X0
            00X
            又は
            00X
            0X0
            X00
            だった場合の処理
             */
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

            slotResult.setPatterns(SlotUtil.SlotResult.Patterns.DIAGONAL);
            slotResult.setMaterial(pattern.get(4));
            /*
             * X0X
             * 0X0
             * X0X
             * のような模様だったら(X)、賞金二倍
             */
            if ((pattern.get(0) == pattern.get(4) && pattern.get(0) == pattern.get(8)) && (pattern.get(2) == pattern.get(4) && pattern.get(2) == pattern.get(6))) {
                winMoney = winMoney * 2;
                slotResult.setPatterns(SlotUtil.SlotResult.Patterns.X);
                slotResult.setMaterial(pattern.get(0));
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

            slotResult.setPatterns(SlotUtil.SlotResult.Patterns.MIDDLE_HORIZONTAL);
            slotResult.setMaterial(pattern.get(4));
        } else if (pattern.stream().distinct().limit(2).count() <= 1) {
            /*
            スロット内のアイテムがすべて同じだったら、賞金二十倍
            多分無いと思うけど
             */
            winMoney = winMoney * 20;

            slotResult.setPatterns(SlotUtil.SlotResult.Patterns.ALL);
            slotResult.setMaterial(pattern.get(0));
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

            slotResult.setPatterns(SlotUtil.SlotResult.Patterns.ELSE_HORIZONTAL);
            slotResult.setMaterial(pattern.get(4));
        }


        /*
         * ３列同時に止められたならば、賞金は4倍
         */
        if (method == SlotUtil.SlotListeners.StopMethod.ALL) {
            winMoney = winMoney * 4;
        }

        slotResult.setPrize((int) Math.round(winMoney));

        //かける金額と賞金が同じなら賞金は０
        return slotResult;
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        ItemStack eventStack = event.getCurrentItem();
        if (event.getInventory() == inventory && eventStack != null) {
            switch (Objects.requireNonNull(eventStack).getType()) {
                case WARPED_BUTTON -> {
                    if (SlotUtil.isSlotStarted) {
                        inventory.remove(Material.RED_STAINED_GLASS_PANE);
                        int type = event.getRawSlot() - 45;
                        tasks.get(type - 1).cancel();
                        inventory.clear(type + 45);
                        inventory.clear(33);
                        if (tasks.get(0).isCancelled() && tasks.get(1).isCancelled() && tasks.get(2).isCancelled()) {
                            listeners.slotFinishTrigger(SlotUtil.SlotListeners.StopMethod.INDIVIDUAL);
                            SlotUtil.isSlotStarted = false;
                        }
                    }
                }
                case GREEN_STAINED_GLASS_PANE -> {
                    SlotUtil.isSlotStarted = true;
                    tasks.add(Bukkit.getScheduler().runTaskTimer(MetaversePlugin.getInstance(), () -> {
                        ItemStack newSlot = SlotUtil.getRandom();
                        inventory.setItem(28, inventory.getItem(19));
                        inventory.setItem(19, inventory.getItem(10));
                        inventory.setItem(10, newSlot);
                    }, 0, 4));

                    tasks.add(Bukkit.getScheduler().runTaskTimer(MetaversePlugin.getInstance(), () -> {
                        ItemStack newSlot = SlotUtil.getRandom();
                        inventory.setItem(29, inventory.getItem(20));
                        inventory.setItem(20, inventory.getItem(11));
                        inventory.setItem(11, newSlot);
                    }, 0, 4));
                    tasks.add(Bukkit.getScheduler().runTaskTimer(MetaversePlugin.getInstance(), () -> {
                        ItemStack newSlot = SlotUtil.getRandom();
                        inventory.setItem(30, inventory.getItem(21));
                        inventory.setItem(21, inventory.getItem(12));
                        inventory.setItem(12, newSlot);
                    }, 0, 3));

                    final ItemStack stopAllButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    final ItemMeta stopAllButtonMeta = stopAllButton.getItemMeta();
                    assert stopAllButtonMeta != null;
                    stopAllButtonMeta.displayName(Component.text(ChatColor.RED + "同時に止める"));
                    stopAllButton.setItemMeta(stopAllButtonMeta);
                    inventory.setItem(24, stopAllButton);
                }
                case RED_STAINED_GLASS_PANE -> {
                    if (SlotUtil.isSlotStarted) {
                        tasks.forEach(BukkitTask::cancel);
                        listeners.slotFinishTrigger(SlotUtil.SlotListeners.StopMethod.ALL);
                        inventory.remove(Material.RED_STAINED_GLASS_PANE);

                        SlotUtil.isSlotStarted = false;
                        inventory.clear(46);
                        inventory.clear(47);
                        inventory.clear(48);
                    }
                }
                case SPECTRAL_ARROW -> {
                    HumanEntity entity = event.getWhoClicked();
                    entity.closeInventory();
                    start((Player) entity);
                }
            }
            event.setCancelled(true);
        }
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
