package world.arainu.core.metaverseplugin.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.scheduler.BukkitRunnable;
import world.arainu.core.metaverseplugin.utils.sqlUtil;
import world.arainu.core.metaverseplugin.commands.CommandSpawn;

import java.util.*;


public class LateScheduler extends BukkitRunnable {
    @Override
    public void run() {
        if(Bukkit.getWorlds().get(0).getTime() < old_time) {
            List<Integer> mason_villager_use = Arrays.asList(0, 0, 0, 0, 0, 0, 0);
            for (UUID uuid : Objects.requireNonNull(sqlUtil.getInstance().getuuidsbytype("mason-villager"))) {
                Villager villager = (Villager) Bukkit.getEntity(uuid);
                List<MerchantRecipe> recipes = Objects.requireNonNull(villager).getRecipes();
                for (int i = 0; i < recipes.size(); i++) {
                    mason_villager_use.set(i, mason_villager_use.get(i) + recipes.get(i).getUses());
                }
            }

            HashMap<String, List<Integer>> quantity_t_minus2 = quantity_t_minus1;
            quantity_t_minus1 = new HashMap<>();
            quantity_t_minus1.put("mason-villager", mason_villager_use);

            if (quantity_t_minus2.get("mason-villager") != null) {
                List<Integer> mason_villager_p = quantity_p.get("mason-villager");
                int minus2;
                int minus1;
                for (int i = 0; i < quantity_t_minus1.get("mason-villager").size(); i++) {
                    minus2 = quantity_t_minus2.get("mason-villager").get(i);
                    minus1 = quantity_t_minus1.get("mason-villager").get(i);
                    if (0 < minus2 - minus1) {
                        mason_villager_p.set(i, mason_villager_p.get(i) - 1);
                    } else if (minus2 - minus1 < 0) {
                        mason_villager_p.set(i, mason_villager_p.get(i) + 1);
                    }
                    Bukkit.getLogger().info("mason_villager_p:" + mason_villager_p.get(i));
                }
                quantity_p.replace("mason-villager", mason_villager_p);

                List<MerchantRecipe> recipes = new ArrayList<>();
                recipes.add(CommandSpawn.createRecipe(1, (int) Math.round(50 + (10 * Math.tanh(mason_villager_p.get(0) / 5f))), new ItemStack(Material.COAL)));
                recipes.add(CommandSpawn.createRecipe(5, (int) Math.round(40 + (10 * Math.tanh(mason_villager_p.get(1) / 5f))), new ItemStack(Material.RAW_COPPER)));
                recipes.add(CommandSpawn.createRecipe(5, (int) Math.round(40 + (10 * Math.tanh(mason_villager_p.get(2) / 5f))), new ItemStack(Material.RAW_IRON)));
                recipes.add(CommandSpawn.createRecipe(10, (int) Math.round(40 + (10 * Math.tanh(mason_villager_p.get(3) / 5f))), new ItemStack(Material.RAW_GOLD)));
                recipes.add(CommandSpawn.createRecipe(5, (int) Math.round(20 + (10 * Math.tanh(mason_villager_p.get(4) / 5f))), new ItemStack(Material.REDSTONE)));
                recipes.add(CommandSpawn.createRecipe(50, (int) Math.round(20 + (6 * Math.tanh(mason_villager_p.get(5) / 5f))), new ItemStack(Material.DIAMOND)));
                recipes.add(CommandSpawn.createRecipe(5, (int) Math.round(30 + (10 * Math.tanh(mason_villager_p.get(6) / 5f))), new ItemStack(Material.LAPIS_LAZULI)));

                for (UUID uuid : Objects.requireNonNull(sqlUtil.getInstance().getuuidsbytype("mason-villager"))) {
                    Villager villager = (Villager) Bukkit.getEntity(uuid);
                    Objects.requireNonNull(villager).setRecipes(recipes);
                }
                Bukkit.getLogger().info("すべてのmason-villagerの価格を変更しました。");
            }
        }
        old_time = Bukkit.getWorlds().get(0).getTime();
    }

    private HashMap<String,List<Integer>> quantity_t_minus1 = new HashMap<>();
    private static final HashMap<String,List<Integer>> quantity_p = new HashMap<>(){
        {
            put("mason-villager",Arrays.asList(0,0,0,0,0,0,0));
        }
    };
    private long old_time;
}
