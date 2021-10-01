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
 * @author kumitatepazuru
 */
public class CommandSpawn extends CommandPlayerOnlyBase {
    @Getter private final CommandSpawn Instance;

    /**
     * プラグイン特有のmobをスポーンさせるコマンドを定義している関数
     */
    public CommandSpawn(){
        this.Instance = this;
    }

    /**
     * 村人との取引（買いとり）のレシピを取得する関数
     * @param yen お金の金額
     * @param quantity 枚数
     * @param ingredientItems 買取のアイテム
     * @return レシピ
     */
    public static MerchantRecipe createRecipe(int yen, int quantity, ItemStack... ingredientItems){
        MerchantRecipe recipe = new MerchantRecipe(Bank.getPluginMoneyEmerald(yen,quantity),0,2147483647,false);
        for(ItemStack ingredientItem : ingredientItems) {
            recipe.addIngredient(ingredientItem);
        }
        return recipe;
    }

    @Override
    public boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 1){
            switch (args[0]){
                case "mason-villager" -> {
                    Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
                    villager.setProfession(Villager.Profession.MASON);
                    villager.setVillagerLevel(5);
                    villager.setCustomName("鉱石ニキ");
                    villager.setCustomNameVisible(true);
                    villager.setAI(false);
                    villager.setInvulnerable(true);
                    sqlUtil.setuuidtype(villager.getUniqueId(), args[0]);
                    List<MerchantRecipe> recipes = new ArrayList<>();
                    recipes.add(createRecipe(1,50,new ItemStack(Material.COAL)));
                    recipes.add(createRecipe(5,40,new ItemStack(Material.RAW_COPPER)));
                    recipes.add(createRecipe(5,40,new ItemStack(Material.RAW_IRON)));
                    recipes.add(createRecipe(10,40,new ItemStack(Material.RAW_GOLD)));
                    recipes.add(createRecipe(5,20,new ItemStack(Material.REDSTONE)));
                    recipes.add(createRecipe(50,20,new ItemStack(Material.DIAMOND)));
                    recipes.add(createRecipe(5,30,new ItemStack(Material.LAPIS_LAZULI)));
                    villager.setRecipes(recipes);
                }
                default -> {
                    Gui.error(player,"そのような独自Mobは存在しません！");
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
        return List.of("mason-villager");
    }
}
