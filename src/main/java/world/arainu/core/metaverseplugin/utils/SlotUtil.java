package world.arainu.core.metaverseplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * スロットマシーンを支える関数や変数などを保管するクラス
 *
 * @author JolTheGreat
 */
public class SlotUtil {
    private static final ItemStack birchWood = new ItemStack(Material.BIRCH_WOOD);
    private static final ItemStack apple = new ItemStack(Material.APPLE);
    private static final ItemStack bread = new ItemStack(Material.BREAD);
    private static final ItemStack iron = new ItemStack(Material.IRON_INGOT);
    private static final ItemStack gold = new ItemStack(Material.GOLD_INGOT);
    private static final ItemStack diamond = new ItemStack(Material.DIAMOND);
    private static final ItemStack netherite = new ItemStack(Material.NETHERITE_INGOT);
    private static final ItemStack dragonHead = new ItemStack(Material.DRAGON_HEAD);
    private static final ItemStack head = new ItemStack(Material.PLAYER_HEAD);

    /**
     * スロットが始まっているか確かめる変数
     */
    public static boolean isSlotStarted = false;

    /**
     * スロットで使えるアイテムを取得する関数
     *
     * @return 使えるアイテムが入っているリスト
     */
    public static List<ItemStack> getAll() {
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer("inutanuking"));
        head.setItemMeta(meta);
        ItemStack[] stacks = {birchWood, apple, bread, iron, gold, diamond, netherite, dragonHead, head};
        return Arrays.stream(stacks).toList();
    }

    /**
     * ランダムでアイテムを取得する関数
     *
     * @return ランダムなアイテム
     */
    public static ItemStack getRandom() {
        return getAll().get(ThreadLocalRandom.current().nextInt(0, 9));
    }

    /**
     * スロット内のイベントに反応する専用リスナー
     */
    public static class SlotListeners {

        private final List<Consumer<StopMethod>> slotFinishListeners = new ArrayList<>();

        /**
         * スロットが止まったときに反応するリスナーを登録する関数
         *
         * @param consumer 反応する関数
         */
        public void addSlotFinishListener(Consumer<StopMethod> consumer) {
            slotFinishListeners.add(consumer);
        }

        /**
         * スロットが止まったときに使われる関数
         * 登録されたリスナー全部を実行させる
         *
         * @param stopMethod 　スロットが止められた方法
         */
        public void slotFinishTrigger(StopMethod stopMethod) {
            slotFinishListeners.forEach((listener) -> listener.accept(stopMethod));
        }

        /**
         * 登録されたリスナーを全て消す関数
         */

        public void clearSlotFinishListeners() {
            slotFinishListeners.clear();
        }

        /**
         * スロットがどのように止められたかを管理するenum
         */
        public enum StopMethod {
            /**
             * スロットが一つずつ止められたならINDIVIDUAL
             */
            INDIVIDUAL,
            /**
             * 同時に止められたならALL
             */
            ALL
        }
    }

    /**
     * スロットの結果をまとめるクラス
     */
    public static class SlotResult {
        /**
         * 賞金
         */
        private int prize;
        /**
         * スロットが止まった時の模様の素材（語彙力
         */
        private Material material;
        /**
         * 登録されている模様の種類
         */
        private Patterns patterns;
        /**
         * スロットがどのように止められたか
         */
        private SlotListeners.StopMethod stopMethod;

        /**
         * 賞金を取得する関数
         *
         * @return 賞金
         */
        public int getPrize() {
            return prize;
        }

        /**
         * 賞金を設定する関数
         *
         * @param prize 賞金
         */
        public void setPrize(int prize) {
            this.prize = prize;
        }

        /**
         * 模様に入っていた素材を取得する関数
         *
         * @return 素材
         */
        public Material getMaterial() {
            return material;
        }

        /**
         * 模様に入っていた素材を設定する関数
         *
         * @param material 　素材
         */
        public void setMaterial(Material material) {
            this.material = material;
        }

        /**
         * スロットが止まった時の模様を取得する関数
         *
         * @return 模様
         */
        public Patterns getPatterns() {
            return patterns;
        }

        /**
         * スロットが止まった時の模様を設定する関数
         *
         * @param patterns 模様
         */
        public void setPatterns(Patterns patterns) {
            this.patterns = patterns;
        }

        /**
         * スロットが止められた方法を取得する関数
         *
         * @return 方法
         */
        public SlotListeners.StopMethod getStopMethod() {
            return stopMethod;
        }

        /**
         * スロットが止められた方法を設定する関数
         *
         * @param stopMethod 方法
         */
        public void setStopMethod(SlotListeners.StopMethod stopMethod) {
            this.stopMethod = stopMethod;
        }

        /**
         * スロットが止まった時の模様
         */
        public enum Patterns {
            /**
             * 斜め
             */
            DIAGONAL,
            /**
             * Xのような模様
             */
            X,
            /**
             * 真ん中を通って横へ進む模様
             * ---
             * XXX
             * ---
             */
            MIDDLE_HORIZONTAL,
            /**
             * 一番上か一番したを通って横へ進む模様
             * XXX
             * ---
             * ---
             * 又は
             * ---
             * ---
             * XXX
             */
            ELSE_HORIZONTAL,
            /**
             * 全てのアイテムが同じだった場合
             */
            ALL
        }
    }
}
