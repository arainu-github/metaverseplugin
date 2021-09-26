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

/**
 * 村人市場の取引に関するものをまとめているクラス
 */
public class LateScheduler extends BukkitRunnable {
    @Override
    public void run() {
        if(Bukkit.getWorlds().get(0).getTime() < old_time) {
            update("mason-villager",Arrays.asList(
                    Arrays.asList(1,50,10,Material.COAL),
                    Arrays.asList(5,40,10,Material.RAW_COPPER),
                    Arrays.asList(5,40,10,Material.RAW_IRON),
                    Arrays.asList(10,40,10,Material.RAW_GOLD),
                    Arrays.asList(5,20,10,Material.REDSTONE),
                    Arrays.asList(50,20,6,Material.DIAMOND),
                    Arrays.asList(5,30,10,Material.LAPIS_LAZULI)
            ));
        }
        old_time = Bukkit.getWorlds().get(0).getTime();
    }

    private void update(String type, List<List<Object>> recipeData){
        List<Integer> villager_use = Arrays.asList(0, 0, 0, 0, 0, 0, 0);
        for (UUID uuid : Objects.requireNonNull(sqlUtil.getInstance().getuuidsbytype(type))) {
            Villager villager = (Villager) Bukkit.getEntity(uuid);
            List<MerchantRecipe> recipes = Objects.requireNonNull(villager).getRecipes();
            for (int i = 0; i < recipes.size(); i++) {
                villager_use.set(i, villager_use.get(i) + recipes.get(i).getUses());
            }
        }

        HashMap<String, List<Integer>> quantity_t_minus2 = quantity_t_minus1;
        quantity_t_minus1 = new HashMap<>();
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
                Bukkit.getLogger().info(type+"_p:" + p.get(i));
            }
            quantity_p.replace(type, p);

            List<MerchantRecipe> recipes = new ArrayList<>();
            for (List<Object> data: recipeData){
                recipes.add(CommandSpawn.createRecipe((int) data.get(0),(int) Math.round((int) data.get(1) + ((int) data.get(2) * Math.tanh(p.get(0) / 5f))),new ItemStack((Material) data.get(3))));
            }

            for (UUID uuid : Objects.requireNonNull(sqlUtil.getInstance().getuuidsbytype(type))) {
                Villager villager = (Villager) Bukkit.getEntity(uuid);
                Objects.requireNonNull(villager).setRecipes(recipes);
            }
            Bukkit.getLogger().info("すべての"+type+"の価格を変更しました。");
        }
    }

    private HashMap<String,List<Integer>> quantity_t_minus1 = new HashMap<>();
    private static final HashMap<String,List<Integer>> quantity_p = new HashMap<>(){
        {
            put("mason-villager",Arrays.asList(0,0,0,0,0,0,0));
        }
    };
    private long old_time;
}
