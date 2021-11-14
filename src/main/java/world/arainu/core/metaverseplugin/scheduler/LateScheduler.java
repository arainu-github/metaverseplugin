package world.arainu.core.metaverseplugin.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.scheduler.BukkitRunnable;
import world.arainu.core.metaverseplugin.utils.sqlUtil;
import world.arainu.core.metaverseplugin.commands.CommandSpawn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 村人市場の取引に関するものをまとめているクラス
 */
public class LateScheduler extends BukkitRunnable {
    @Override
    public void run() {
        if (Bukkit.getWorlds().get(0).getTime() < old_time) {
            update("mason-villager", Arrays.asList(
                    Arrays.asList(1, 50, 10, Material.COAL),
                    Arrays.asList(5, 40, 10, Material.RAW_COPPER),
                    Arrays.asList(5, 40, 10, Material.RAW_IRON),
                    Arrays.asList(10, 40, 10, Material.RAW_GOLD),
                    Arrays.asList(5, 20, 10, Material.REDSTONE),
                    Arrays.asList(50, 20, 6, Material.DIAMOND),
                    Arrays.asList(5, 30, 10, Material.LAPIS_LAZULI)
            ));
            //noinspection RedundantTypeArguments (explicit type arguments speedup compilation and analysis time)
            update("stone-villager", Arrays.<List<Object>>asList(
                    Arrays.asList(1, 3, 0, Material.COBBLESTONE),
                    Arrays.asList(1, 5, 2, Material.MOSSY_COBBLESTONE),
                    Arrays.asList(1, 5, 2, Material.STONE),
                    Arrays.asList(1, 8, 3, Material.SMOOTH_STONE),
                    Arrays.asList(1, 5, 2, Material.STONE_BRICKS),
                    Arrays.asList(1, 5, 2, Material.CRACKED_STONE_BRICKS),
                    Arrays.asList(1, 5, 2, Material.CHISELED_STONE_BRICKS),
                    Arrays.asList(1, 5, 2, Material.GRAVEL),
                    Arrays.asList(1, 10, 5, Material.GRANITE),
                    Arrays.asList(1, 10, 5, Material.POLISHED_GRANITE),
                    Arrays.asList(1, 10, 5, Material.DIORITE),
                    Arrays.asList(1, 10, 5, Material.POLISHED_DIORITE),
                    Arrays.asList(1, 10, 5, Material.ANDESITE),
                    Arrays.asList(1, 10, 5, Material.POLISHED_ANDESITE),
                    Arrays.asList(1, 5, 2, Material.SANDSTONE),
                    Arrays.asList(1, 5, 2, Material.CUT_SANDSTONE),
                    Arrays.asList(1, 5, 2, Material.CHISELED_SANDSTONE),
                    Arrays.asList(1, 5, 2, Material.SMOOTH_SANDSTONE),
                    Arrays.asList(1, 5, 2, Material.RED_SANDSTONE),
                    Arrays.asList(1, 5, 2, Material.CUT_RED_SANDSTONE),
                    Arrays.asList(1, 5, 2, Material.CHISELED_RED_SANDSTONE),
                    Arrays.asList(1, 5, 2, Material.SMOOTH_RED_SANDSTONE),
                    Arrays.asList(1, 40, 5, Material.END_STONE),
                    Arrays.asList(1, 40, 5, Material.END_STONE_BRICKS),
                    Arrays.asList(1, 50, 5, Material.OBSIDIAN),
                    Arrays.asList(1, 60, 5, Material.CRYING_OBSIDIAN),
                    Arrays.asList(1, 5, 2, Material.NETHERRACK),
                    Arrays.asList(1, 20, 5, Material.GLOWSTONE),
                    Arrays.asList(1, 30, 5, Material.QUARTZ_BLOCK),
                    Arrays.asList(1, 30, 5, Material.CHISELED_QUARTZ_BLOCK),
                    Arrays.asList(1, 30, 5, Material.QUARTZ_PILLAR),
                    Arrays.asList(1, 30, 5, Material.SMOOTH_QUARTZ),
                    Arrays.asList(1, 30, 5, Material.QUARTZ_BRICKS),
                    Arrays.asList(1, 50, 5, Material.PRISMARINE),
                    Arrays.asList(1, 50, 5, Material.PRISMARINE_BRICKS),
                    Arrays.asList(1, 50, 5, Material.DARK_PRISMARINE),
                    Arrays.asList(1, 15, 5, Material.BASALT),
                    Arrays.asList(1, 15, 5, Material.POLISHED_BASALT),
                    Arrays.asList(1, 15, 5, Material.SMOOTH_BASALT),
                    Arrays.asList(1, 15, 5, Material.BLACKSTONE),
                    Arrays.asList(1, 15, 5, Material.POLISHED_BLACKSTONE),
                    Arrays.asList(1, 15, 5, Material.CHISELED_POLISHED_BLACKSTONE),
                    Arrays.asList(1, 15, 5, Material.POLISHED_BLACKSTONE_BRICKS),
                    Arrays.asList(1, 15, 5, Material.CRACKED_POLISHED_BLACKSTONE_BRICKS),
                    Arrays.asList(1, 15, 5, Material.DEEPSLATE),
                    Arrays.asList(1, 10, 5, Material.POLISHED_DEEPSLATE),
                    Arrays.asList(1, 10, 5, Material.COBBLED_DEEPSLATE),
                    Arrays.asList(1, 10, 5, Material.DEEPSLATE_BRICKS),
                    Arrays.asList(1, 10, 5, Material.CRACKED_DEEPSLATE_BRICKS),
                    Arrays.asList(1, 10, 5, Material.DEEPSLATE_TILES),
                    Arrays.asList(1, 10, 5, Material.CRACKED_DEEPSLATE_TILES),
                    Arrays.asList(1, 10, 5, Material.CHISELED_DEEPSLATE),
                    Arrays.asList(1, 10, 5, Material.TUFF),
                    Arrays.asList(1, 30, 5, Material.CALCITE),
                    Arrays.asList(1, 15, 5, Material.DRIPSTONE_BLOCK),
                    Arrays.asList(1, 5, 2, Material.POINTED_DRIPSTONE)
            ));
            update("mob-villager", Arrays.asList(
                    Arrays.asList(1000, 5, 3, Material.AXOLOTL_SPAWN_EGG),
                    Arrays.asList(500, 5, 3, Material.BEE_SPAWN_EGG),
                    Arrays.asList(500, 5, 3, Material.CAT_SPAWN_EGG),
                    Arrays.asList(150, 5, 3, Material.CHICKEN_SPAWN_EGG),
                    Arrays.asList(150, 5, 3, Material.COD_SPAWN_EGG),
                    Arrays.asList(150, 5, 3, Material.COW_SPAWN_EGG),
                    Arrays.asList(1000, 5, 3, Material.DOLPHIN_SPAWN_EGG),
                    Arrays.asList(1000, 5, 3, Material.DONKEY_SPAWN_EGG),
                    Arrays.asList(500, 5, 3, Material.FOX_SPAWN_EGG),
                    Arrays.asList(1000, 5, 3, Material.HORSE_SPAWN_EGG),
                    Arrays.asList(1000, 5, 3, Material.LLAMA_SPAWN_EGG),
                    Arrays.asList(500, 5, 3, Material.OCELOT_SPAWN_EGG),
                    Arrays.asList(1000, 5, 3, Material.PARROT_SPAWN_EGG),
                    Arrays.asList(500, 5, 3, Material.PANDA_SPAWN_EGG),
                    Arrays.asList(150, 5, 3, Material.PIG_SPAWN_EGG),
                    Arrays.asList(500, 5, 3, Material.POLAR_BEAR_SPAWN_EGG),
                    Arrays.asList(200, 5, 3, Material.PUFFERFISH_SPAWN_EGG),
                    Arrays.asList(500, 5, 3, Material.RABBIT_SPAWN_EGG),
                    Arrays.asList(150, 5, 3, Material.SALMON_SPAWN_EGG),
                    Arrays.asList(150, 5, 3, Material.SHEEP_SPAWN_EGG),
                    Arrays.asList(1000, 5, 3, Material.SKELETON_HORSE_SPAWN_EGG),
                    Arrays.asList(150, 5, 3, Material.SQUID_SPAWN_EGG),
                    Arrays.asList(1000, 5, 3, Material.STRIDER_SPAWN_EGG),
                    Arrays.asList(200, 5, 3, Material.TROPICAL_FISH_SPAWN_EGG),
                    Arrays.asList(300, 5, 3, Material.TURTLE_SPAWN_EGG),
                    Arrays.asList(500, 5, 3, Material.WOLF_SPAWN_EGG),
                    Arrays.asList(1000, 5, 3, Material.ZOMBIE_HORSE_SPAWN_EGG)
            ));
        }
        old_time = Bukkit.getWorlds().get(0).getTime();
    }

    private void update(String type, List<List<Object>> recipeData) {
        List<Integer> villager_use = new ArrayList<>(Collections.nCopies(recipeData.size(), 0));
        final List<UUID> villager_list = new ArrayList<>();
        villager_list.addAll(Objects.requireNonNull(sqlUtil.getuuidsbytype(type)));
        villager_list.addAll(Objects.requireNonNull(sqlUtil.getuuidsbytype(type + "-shop")));
        for (UUID uuid : villager_list) {
            Bukkit.getLogger().info("villager uuid:" + uuid);
            Villager villager = (Villager) Bukkit.getEntity(uuid);
            if (villager != null) {
                List<MerchantRecipe> recipes = villager.getRecipes();
                for (int i = 0; i < recipes.size(); i++) {
                    villager_use.set(i, villager_use.get(i) + recipes.get(i).getUses());
                }
            }
        }

        HashMap<String, List<Integer>> quantity_t_minus2 = new HashMap<>(quantity_t_minus1);
        quantity_t_minus1.remove(type);
        quantity_t_minus1.put(type, villager_use);
        if (quantity_t_minus2.get(type) != null) {
            List<Integer> p = quantity_p.get(type);
            int minus2;
            int minus1;
            for (int i = 0; i < quantity_t_minus1.get(type).size(); i++) {
                minus2 = quantity_t_minus2.get(type).get(i);
                minus1 = quantity_t_minus1.get(type).get(i);
                if (0 < minus2 - minus1) {
                    p.set(i, p.get(i) - 1);
                } else if (minus2 - minus1 < 0) {
                    p.set(i, p.get(i) + 1);
                }
            }
            quantity_p.replace(type, p);

            List<MerchantRecipe> recipes = new ArrayList<>();
            List<MerchantRecipe> recipes2 = new ArrayList<>();
            for (int i = 0; i < recipeData.size(); i++) {
                List<Object> data = recipeData.get(i);
                recipes.add(CommandSpawn.createRecipe((int) data.get(0), (int) Math.round((int) data.get(1) + ((int) data.get(2) * Math.tanh(p.get(i) / 5f))), new ItemStack((Material) data.get(3))));
                recipes2.add(CommandSpawn.createRecipe2((int) data.get(0), (int) Math.round((int) data.get(1) + ((int) data.get(2) * Math.tanh(p.get(i) / 5f))), new ItemStack((Material) data.get(3))));
            }

            for (UUID uuid : Objects.requireNonNull(sqlUtil.getuuidsbytype(type))) {
                Villager villager = (Villager) Bukkit.getEntity(uuid);
                Objects.requireNonNull(villager).setRecipes(recipes);
            }
            for (UUID uuid : Objects.requireNonNull(sqlUtil.getuuidsbytype(type + "-shop"))) {
                Villager villager = (Villager) Bukkit.getEntity(uuid);
                Objects.requireNonNull(villager).setRecipes(recipes2);
            }
            Bukkit.getLogger().info("すべての" + type + "及び" + type + "-shopの価格を変更しました。");
        }
    }

    private final HashMap<String, List<Integer>> quantity_t_minus1 = new HashMap<>();
    private static final HashMap<String, List<Integer>> quantity_p = new HashMap<>() {
        {
            put("mason-villager", new ArrayList<>(Collections.nCopies(7, 0)));
            put("stone-villager", new ArrayList<>(Collections.nCopies(56, 0)));
        }
    };
    private long old_time;
}
