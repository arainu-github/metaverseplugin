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
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.iphone.Bank;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * プラグイン特有のmobをスポーンさせるコマンドを定義している関数
 *
 * @author kumitatepazuru
 */
public class CommandSpawn extends CommandPlayerOnlyBase {
    @Getter
    private final CommandSpawn Instance;

    /**
     * プラグイン特有のmobをスポーンさせるコマンドを定義している関数
     */
    public CommandSpawn() {
        this.Instance = this;
    }

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
     * @param name ニキの名前
     * @param type ニキの役職
     * @param player プレイヤー（ここの座標に作成する）
     * @return 村人
     */
    public static Villager createVillager(String name,Villager.Profession type,Player player){
        Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        villager.setProfession(type);
        villager.setVillagerLevel(5);
        villager.setCustomName(name);
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        return villager;
    }

    @Override
    public boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "mason-villager" -> {
                    Villager villager = createVillager("鉱石ニキ", Villager.Profession.MASON, player);
                    sqlUtil.setuuidtype(villager.getUniqueId(), args[0]);
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
                    Villager villager = createVillager("お金ニキ(鉱石)", Villager.Profession.MASON, player);
                    sqlUtil.setuuidtype(villager.getUniqueId(), args[0]);
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
                    Villager villager = createVillager("石工ニキ", Villager.Profession.MASON, player);
                    sqlUtil.setuuidtype(villager.getUniqueId(), args[0]);
                    List<MerchantRecipe> recipes = new ArrayList<>();
                    recipes.add(createRecipe(1, 3, new ItemStack(Material.COBBLESTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.MOSSY_COBBLESTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.STONE)));
                    recipes.add(createRecipe(1, 8, new ItemStack(Material.SMOOTH_STONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.STONE_BRICKS)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CRACKED_STONE_BRICKS)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CHISELED_STONE_BRICKS)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.GRAVEL)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.GRANITE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.POLISHED_GRANITE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.DIORITE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.POLISHED_DIORITE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.ANDESITE)));
                    recipes.add(createRecipe(1, 10, new ItemStack(Material.POLISHED_ANDESITE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CUT_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CHISELED_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.SMOOTH_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.RED_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CUT_RED_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.CHISELED_RED_SANDSTONE)));
                    recipes.add(createRecipe(1, 5, new ItemStack(Material.SMOOTH_RED_SANDSTONE)));
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
                    Villager villager = createVillager("お金ニキ(石系)", Villager.Profession.MASON, player);
                    sqlUtil.setuuidtype(villager.getUniqueId(), args[0]);
                    List<MerchantRecipe> recipes = new ArrayList<>();
                    recipes.add(createRecipe2(1, 3, new ItemStack(Material.COBBLESTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.MOSSY_COBBLESTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.STONE)));
                    recipes.add(createRecipe2(1, 8, new ItemStack(Material.SMOOTH_STONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.STONE_BRICKS)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CRACKED_STONE_BRICKS)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CHISELED_STONE_BRICKS)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.GRAVEL)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.GRANITE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.POLISHED_GRANITE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.DIORITE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.POLISHED_DIORITE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.ANDESITE)));
                    recipes.add(createRecipe2(1, 10, new ItemStack(Material.POLISHED_ANDESITE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CUT_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CHISELED_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.SMOOTH_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.RED_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CUT_RED_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.CHISELED_RED_SANDSTONE)));
                    recipes.add(createRecipe2(1, 5, new ItemStack(Material.SMOOTH_RED_SANDSTONE)));
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
                default -> {
                    Gui.error(player, "そのような独自Mobは存在しません！");
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
        return List.of("mason-villager", "stone-villager", "mason-villager-shop", "stone-villager-shop");
    }
}
