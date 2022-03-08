package world.arainu.core.metaverseplugin.commands;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.iphone.Bank;
import world.arainu.core.metaverseplugin.utils.ChatUtil;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.*;

/**
 * プラグイン特有のmobをスポーンさせるコマンドを定義している関数
 *
 * @author kumitatepazuru
 */
public class CommandSpawn extends CommandPlayerOnlyBase {
    @Getter
    private static final Map<String, String> METAZON_VILLAGER = Map.of("mason-villager", "鉱石店員","stone-villager", "石工店員","mason-villager-shop","お金店員(鉱石)", "stone-villager-shop","お金店員(石系)", "mob-villager-shop","モブ店員", "sandstone-villager","砂岩砂利店員", "sandstone-villager-shop","お金店員(砂岩系&砂利)");

    /**
     * 村人との取引（買いとり）のレシピを取得する関数
     *
     * @param yen             お金の金額
     * @param quantity        枚数
     * @param ingredientItems 買取のアイテム
     * @return レシピ
     */
    public static MerchantRecipe createRecipe(int yen, int quantity, ItemStack... ingredientItems) {
        MerchantRecipe recipe = new MerchantRecipe(Bank.getPluginMoneyEmerald(yen, quantity), 0, 2147483647, false);
        for (ItemStack ingredientItem : ingredientItems) {
            recipe.addIngredient(ingredientItem);
        }
        return recipe;
    }

    /**
     * 村人との取引（売る）のレシピを取得する関数
     *
     * @param yen            お金の金額
     * @param quantity       枚数
     * @param ingredientItem 売るもののアイテム
     * @return レシピ
     */
    public static MerchantRecipe createRecipe2(int yen, int quantity, ItemStack ingredientItem) {
        MerchantRecipe recipe = new MerchantRecipe(ingredientItem, 0, 2147483647, false);
        recipe.addIngredient(Bank.getPluginMoneyEmerald(yen, quantity));
        return recipe;
    }

    /**
     * 村人を作成(?)する関数。
     *
     * @param name   店員の名前
     * @param type   店員の役職
     * @param player プレイヤー（ここの座標に作成する）
     * @return 村人
     */
    public static Villager createVillager(String name, Villager.Profession type, Player player,String[] args) {
        Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        villager.setProfession(type);
        villager.setVillagerLevel(5);
        villager.setCustomName(name);
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        if(args.length == 2) {
            if (Objects.equals(args[1], "invisible")) {
                villager.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999, 255, false, false));
            }
        }
        sqlUtil.setuuidtype(villager.getUniqueId(), args[0]);
        return villager;
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 1 || args.length == 2) {
            switch (args[0]) {
                case "casino-villager" -> createVillager("スロットマン", Villager.Profession.MASON, player,args);
                case "mason-villager" -> {
                    Villager villager = createVillager(METAZON_VILLAGER.get(args[0]), Villager.Profession.MASON, player,args);
                    List<MerchantRecipe> recipes = new ArrayList<>();
                    recipes.add(createRecipe(1, 50, new ItemStack(Material.COAL)));
                    recipes.add(createRecipe(5, 40, new ItemStack(Material.RAW_COPPER)));
                    recipes.add(createRecipe(5, 40, new ItemStack(Material.RAW_IRON)));
                    recipes.add(createRecipe(10, 40, new ItemStack(Material.RAW_GOLD)));
                    recipes.add(createRecipe(5, 20, new ItemStack(Material.REDSTONE)));
                    recipes.add(createRecipe(50, 20, new ItemStack(Material.DIAMOND)));
                    recipes.add(createRecipe(5, 30, new ItemStack(Material.LAPIS_LAZULI)));
                    villager.setRecipes(recipes);
                }
                case "mason-villager-shop" -> {
                    Villager villager = createVillager(METAZON_VILLAGER.get(args[0]), Villager.Profession.MASON, player,args);
                    List<MerchantRecipe> recipes = new ArrayList<>();
                    recipes.add(createRecipe2(1, 50, new ItemStack(Material.COAL)));
                    recipes.add(createRecipe2(5, 40, new ItemStack(Material.RAW_COPPER)));
                    recipes.add(createRecipe2(5, 40, new ItemStack(Material.RAW_IRON)));
                    recipes.add(createRecipe2(10, 40, new ItemStack(Material.RAW_GOLD)));
                    recipes.add(createRecipe2(5, 20, new ItemStack(Material.REDSTONE)));
                    recipes.add(createRecipe2(50, 20, new ItemStack(Material.DIAMOND)));
                    recipes.add(createRecipe2(5, 30, new ItemStack(Material.LAPIS_LAZULI)));
                    villager.setRecipes(recipes);
                }
                case "stone-villager" -> {
                    Villager villager = createVillager(METAZON_VILLAGER.get(args[0]), Villager.Profession.MASON, player,args);
                    List<MerchantRecipe> recipes = new ArrayList<>();
                    recipes.add(createRecipe(1, 3, new ItemStack(Material.COBBLESTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.MOSSY_COBBLESTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.STONE)));
                    recipes.add(createRecipe(1, 8, new ItemStack(Material.SMOOTH_STONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.STONE_BRICKS)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CRACKED_STONE_BRICKS)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CHISELED_STONE_BRICKS)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.GRANITE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.POLISHED_GRANITE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.DIORITE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.POLISHED_DIORITE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.ANDESITE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.POLISHED_ANDESITE)));
                    recipes.add(createRecipe(1, 40, new ItemStack(Material.END_STONE)));
                    recipes.add(createRecipe(1, 40, new ItemStack(Material.END_STONE_BRICKS)));
                    recipes.add(createRecipe(1, 50, new ItemStack(Material.OBSIDIAN)));
                    recipes.add(createRecipe(1, 60, new ItemStack(Material.CRYING_OBSIDIAN)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.NETHERRACK)));
                    recipes.add(createRecipe(1, 20, new ItemStack(Material.GLOWSTONE)));
                    recipes.add(createRecipe(1, 30, new ItemStack(Material.QUARTZ_BLOCK)));
                    recipes.add(createRecipe(1, 30, new ItemStack(Material.CHISELED_QUARTZ_BLOCK)));
                    recipes.add(createRecipe(1, 30, new ItemStack(Material.QUARTZ_PILLAR)));
                    recipes.add(createRecipe(1, 30, new ItemStack(Material.SMOOTH_QUARTZ)));
                    recipes.add(createRecipe(1, 30, new ItemStack(Material.QUARTZ_BRICKS)));
                    recipes.add(createRecipe(1, 50, new ItemStack(Material.PRISMARINE)));
                    recipes.add(createRecipe(1, 50, new ItemStack(Material.PRISMARINE_BRICKS)));
                    recipes.add(createRecipe(1, 50, new ItemStack(Material.DARK_PRISMARINE)));
                    recipes.add(createRecipe(1, 15, new ItemStack(Material.BASALT)));
                    recipes.add(createRecipe(1, 15, new ItemStack(Material.POLISHED_BASALT)));
                    recipes.add(createRecipe(1, 15, new ItemStack(Material.SMOOTH_BASALT)));
                    recipes.add(createRecipe(1, 15, new ItemStack(Material.BLACKSTONE)));
                    recipes.add(createRecipe(1, 15, new ItemStack(Material.POLISHED_BLACKSTONE)));
                    recipes.add(createRecipe(1, 15, new ItemStack(Material.CHISELED_POLISHED_BLACKSTONE)));
                    recipes.add(createRecipe(1, 15, new ItemStack(Material.POLISHED_BLACKSTONE_BRICKS)));
                    recipes.add(createRecipe(1, 15, new ItemStack(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS)));
                    recipes.add(createRecipe(1, 15, new ItemStack(Material.DEEPSLATE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.POLISHED_DEEPSLATE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.COBBLED_DEEPSLATE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.DEEPSLATE_BRICKS)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.CRACKED_DEEPSLATE_BRICKS)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.DEEPSLATE_TILES)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.CRACKED_DEEPSLATE_TILES)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.CHISELED_DEEPSLATE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.TUFF)));
                    recipes.add(createRecipe(1, 30, new ItemStack(Material.CALCITE)));
                    recipes.add(createRecipe(1, 15, new ItemStack(Material.DRIPSTONE_BLOCK)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.POINTED_DRIPSTONE)));
                    villager.setRecipes(recipes);
                }
                case "stone-villager-shop" -> {
                    Villager villager = createVillager(METAZON_VILLAGER.get(args[0]), Villager.Profession.MASON, player,args);
                    List<MerchantRecipe> recipes = new ArrayList<>();
                    recipes.add(createRecipe2(1, 3, new ItemStack(Material.COBBLESTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.MOSSY_COBBLESTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.STONE)));
                    recipes.add(createRecipe2(1, 8, new ItemStack(Material.SMOOTH_STONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.STONE_BRICKS)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CRACKED_STONE_BRICKS)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CHISELED_STONE_BRICKS)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.GRANITE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.POLISHED_GRANITE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.DIORITE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.POLISHED_DIORITE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.ANDESITE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.POLISHED_ANDESITE)));
                    recipes.add(createRecipe2(1, 40, new ItemStack(Material.END_STONE)));
                    recipes.add(createRecipe2(1, 40, new ItemStack(Material.END_STONE_BRICKS)));
                    recipes.add(createRecipe2(1, 50, new ItemStack(Material.OBSIDIAN)));
                    recipes.add(createRecipe2(1, 60, new ItemStack(Material.CRYING_OBSIDIAN)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.NETHERRACK)));
                    recipes.add(createRecipe2(1, 20, new ItemStack(Material.GLOWSTONE)));
                    recipes.add(createRecipe2(1, 30, new ItemStack(Material.QUARTZ_BLOCK)));
                    recipes.add(createRecipe2(1, 30, new ItemStack(Material.CHISELED_QUARTZ_BLOCK)));
                    recipes.add(createRecipe2(1, 30, new ItemStack(Material.QUARTZ_PILLAR)));
                    recipes.add(createRecipe2(1, 30, new ItemStack(Material.SMOOTH_QUARTZ)));
                    recipes.add(createRecipe2(1, 30, new ItemStack(Material.QUARTZ_BRICKS)));
                    recipes.add(createRecipe2(1, 50, new ItemStack(Material.PRISMARINE)));
                    recipes.add(createRecipe2(1, 50, new ItemStack(Material.PRISMARINE_BRICKS)));
                    recipes.add(createRecipe2(1, 50, new ItemStack(Material.DARK_PRISMARINE)));
                    recipes.add(createRecipe2(1, 15, new ItemStack(Material.BASALT)));
                    recipes.add(createRecipe2(1, 15, new ItemStack(Material.POLISHED_BASALT)));
                    recipes.add(createRecipe2(1, 15, new ItemStack(Material.SMOOTH_BASALT)));
                    recipes.add(createRecipe2(1, 15, new ItemStack(Material.BLACKSTONE)));
                    recipes.add(createRecipe2(1, 15, new ItemStack(Material.POLISHED_BLACKSTONE)));
                    recipes.add(createRecipe2(1, 15, new ItemStack(Material.CHISELED_POLISHED_BLACKSTONE)));
                    recipes.add(createRecipe2(1, 15, new ItemStack(Material.POLISHED_BLACKSTONE_BRICKS)));
                    recipes.add(createRecipe2(1, 15, new ItemStack(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS)));
                    recipes.add(createRecipe2(1, 15, new ItemStack(Material.DEEPSLATE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.POLISHED_DEEPSLATE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.COBBLED_DEEPSLATE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.DEEPSLATE_BRICKS)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.CRACKED_DEEPSLATE_BRICKS)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.DEEPSLATE_TILES)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.CRACKED_DEEPSLATE_TILES)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.CHISELED_DEEPSLATE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.TUFF)));
                    recipes.add(createRecipe2(1, 30, new ItemStack(Material.CALCITE)));
                    recipes.add(createRecipe2(1, 15, new ItemStack(Material.DRIPSTONE_BLOCK)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.POINTED_DRIPSTONE)));
                    villager.setRecipes(recipes);
                }
                case "mob-villager-shop" -> {
                    Villager villager = createVillager(METAZON_VILLAGER.get(args[0]), Villager.Profession.FARMER, player,args);
                    List<MerchantRecipe> recipes = new ArrayList<>();
                    recipes.add(createRecipe2(1000, 5, new ItemStack(Material.AXOLOTL_SPAWN_EGG)));
                    recipes.add(createRecipe2(500, 5, new ItemStack(Material.BEE_SPAWN_EGG)));
                    recipes.add(createRecipe2(500, 5, new ItemStack(Material.CAT_SPAWN_EGG)));
                    recipes.add(createRecipe2(150, 5, new ItemStack(Material.CHICKEN_SPAWN_EGG)));
                    recipes.add(createRecipe2(150, 5, new ItemStack(Material.COD_SPAWN_EGG)));
                    recipes.add(createRecipe2(150, 5, new ItemStack(Material.COW_SPAWN_EGG)));
                    recipes.add(createRecipe2(100000, 5, new ItemStack(Material.DOLPHIN_SPAWN_EGG)));
                    recipes.add(createRecipe2(1000, 5, new ItemStack(Material.DONKEY_SPAWN_EGG)));
                    recipes.add(createRecipe2(500, 5, new ItemStack(Material.FOX_SPAWN_EGG)));
                    recipes.add(createRecipe2(1000, 5, new ItemStack(Material.HORSE_SPAWN_EGG)));
                    recipes.add(createRecipe2(1000, 5, new ItemStack(Material.LLAMA_SPAWN_EGG)));
                    recipes.add(createRecipe2(500, 5, new ItemStack(Material.OCELOT_SPAWN_EGG)));
                    recipes.add(createRecipe2(1000, 5, new ItemStack(Material.PARROT_SPAWN_EGG)));
                    recipes.add(createRecipe2(500, 5, new ItemStack(Material.PANDA_SPAWN_EGG)));
                    recipes.add(createRecipe2(150, 5, new ItemStack(Material.PIG_SPAWN_EGG)));
                    recipes.add(createRecipe2(500, 5, new ItemStack(Material.POLAR_BEAR_SPAWN_EGG)));
                    recipes.add(createRecipe2(200, 5, new ItemStack(Material.PUFFERFISH_SPAWN_EGG)));
                    recipes.add(createRecipe2(500, 5, new ItemStack(Material.RABBIT_SPAWN_EGG)));
                    recipes.add(createRecipe2(150, 5, new ItemStack(Material.SALMON_SPAWN_EGG)));
                    recipes.add(createRecipe2(150, 5, new ItemStack(Material.SHEEP_SPAWN_EGG)));
                    recipes.add(createRecipe2(1000, 5, new ItemStack(Material.SKELETON_HORSE_SPAWN_EGG)));
                    recipes.add(createRecipe2(150, 5, new ItemStack(Material.SQUID_SPAWN_EGG)));
                    recipes.add(createRecipe2(1000, 5, new ItemStack(Material.STRIDER_SPAWN_EGG)));
                    recipes.add(createRecipe2(200, 5, new ItemStack(Material.TROPICAL_FISH_SPAWN_EGG)));
                    recipes.add(createRecipe2(300, 5, new ItemStack(Material.TURTLE_SPAWN_EGG)));
                    recipes.add(createRecipe2(500, 5, new ItemStack(Material.WOLF_SPAWN_EGG)));
                    recipes.add(createRecipe2(1000, 5, new ItemStack(Material.ZOMBIE_HORSE_SPAWN_EGG)));
                    villager.setRecipes(recipes);

                }
                case "sandstone-villager" -> {
                    Villager villager = createVillager(METAZON_VILLAGER.get(args[0]), Villager.Profession.MASON, player,args);
                    List<MerchantRecipe> recipes = new ArrayList<>();
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.GRAVEL)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CUT_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CHISELED_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.SMOOTH_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.RED_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CUT_RED_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CHISELED_RED_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.SMOOTH_RED_SANDSTONE)));
                    villager.setRecipes(recipes);
                }
                case "sandstone-villager-shop" -> {
                    Villager villager = createVillager(METAZON_VILLAGER.get(args[0]), Villager.Profession.MASON, player,args);
                    List<MerchantRecipe> recipes = new ArrayList<>();
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.GRAVEL)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CUT_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CHISELED_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.SMOOTH_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.RED_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CUT_RED_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CHISELED_RED_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.SMOOTH_RED_SANDSTONE)));
                    villager.setRecipes(recipes);
                }
                default -> {
                    ChatUtil.error(player, "そのような独自Mobは存在しません！");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                                      String[] args) {
        List<String> r = new ArrayList<>(METAZON_VILLAGER.keySet());
        r.add("casino-villager");
        return r;
    }
}
