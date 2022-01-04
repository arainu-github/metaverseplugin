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

    public static List<ItemStack> getAll() {
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer("inutanuking"));
        head.setItemMeta(meta);
        ItemStack[] stacks = {birchWood, apple, bread, iron, gold, diamond, netherite, dragonHead, head};
        return Arrays.stream(stacks).toList();
    }

    public static ItemStack getRandom() {
        return getAll().get(ThreadLocalRandom.current().nextInt(0, 9));
    }

    public static class SlotListeners {

        private final List<Consumer<StopMethod>> slotFinishListeners = new ArrayList<>();

        public void addSlotFinishListener(Consumer<StopMethod> consumer) {
            slotFinishListeners.add(consumer);
        }

        public void slotFinishTrigger(StopMethod stopMethod) {
            slotFinishListeners.forEach((listener) -> listener.accept(stopMethod));
        }

        public void clearSlotFinishListeners() {
            slotFinishListeners.clear();
        }

        public enum StopMethod {
            INDIVIDUAL, ALL
        }
    }

    public static class SlotResult {
        private int prize;
        private Material material;
        private Patterns patterns;
        private SlotListeners.StopMethod stopMethod;

        public int getPrize() {
            return prize;
        }

        public void setPrize(int prize) {
            this.prize = prize;
        }

        public Material getMaterial() {
            return material;
        }

        public void setMaterial(Material material) {
            this.material = material;
        }

        public Patterns getPatterns() {
            return patterns;
        }

        public void setPatterns(Patterns patterns) {
            this.patterns = patterns;
        }

        public SlotListeners.StopMethod getStopMethod() {
            return stopMethod;
        }

        public void setStopMethod(SlotListeners.StopMethod stopMethod) {
            this.stopMethod = stopMethod;
        }

        public enum Patterns {
            DIAGONAL, X, MIDDLE_HORIZONTAL, ELSE_HORIZONTAL, ALL
        }
    }
}
