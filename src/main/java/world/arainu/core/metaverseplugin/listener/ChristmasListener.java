package world.arainu.core.metaverseplugin.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;

public class ChristmasListener implements Listener {
    private Component generateChristmasText(String text){
        Component christmasText = Component.empty();
        for(int i=0;i<text.length();i++){
            if(i%2 == 0){
                christmasText = christmasText.append(Component.text(text.charAt(i)).color(NamedTextColor.RED));
            } else {
                christmasText = christmasText.append(Component.text(text.charAt(i)).color(NamedTextColor.GREEN));
            }
        }
        return christmasText;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        final boolean alreadyGot = Objects.requireNonNull(sqlUtil.getChristmas()).contains(e.getPlayer().getUniqueId());
        Component merryChristmas = generateChristmasText("Merry Christmas!").decorate(TextDecoration.ITALIC).decorate(TextDecoration.UNDERLINED);
        e.getPlayer().showTitle(Title.title(merryChristmas,Component.empty(), Title.Times.of(Duration.ofMillis(500),Duration.ofMillis(5000),Duration.ofMillis(500))));
        merryChristmas = Component.empty().append(merryChristmas);
        merryChristmas = merryChristmas.append(Component.text("\nメリークリスマス！").color(TextColor.color(NamedTextColor.RED)));
        if(alreadyGot){
            merryChristmas = merryChristmas.append(Component.text("""


                皆さん、クリスマスですよクリスマス！
                ということで、いよいよあらいぬサーバーの閉鎖当日になってしまいました。
                しかし、最終日も楽しんでもらいたい！ということで運営からささやかなプレゼントを用意いたしました！
                あ、でもあなたもうすでにもらってますやん。じゃああげな〜い！
                
                """));
        } else {
            merryChristmas = merryChristmas.append(Component.text("""


                    皆さん、クリスマスですよクリスマス！
                    ということで、いよいよあらいぬサーバーの閉鎖当日になってしまいました。
                    しかし、最終日も楽しんでもらいたい！ということで運営からささやかなプレゼントを用意いたしました！
                    インベントリを見てみましょう。見慣れないシュルカーボックスがあるでしょ？
                    中になにか入っているかもしれませんよ？
                    お役に立てることができたら幸いです♪
                                    
                    """));
        }
        merryChristmas = merryChristmas.append(Component.text("↓特設ページだヨ★↓\n").color(NamedTextColor.RED));
        merryChristmas = merryChristmas.append(Component.text("https://www.arainu.world/archives/499").color(NamedTextColor.RED).decorate(TextDecoration.UNDERLINED));
        e.getPlayer().sendMessage(merryChristmas);
        e.joinMessage(generateChristmasText("[クリスマスイベ開催中] "+e.getPlayer().getName()+"がゲームに参加しました"));
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"execute at "+e.getPlayer().getName()+" run summon firework_rocket ~ ~ ~ {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Explosions:[{Type:1,Trail:1,Colors:[I;11743532],FadeColors:[I;4312372]}],Flight:2}}}}");

        if(!alreadyGot) {
            sqlUtil.addChristmas(e.getPlayer().getUniqueId());
            ItemStack item = new ItemStack(Material.LIME_SHULKER_BOX);
            BlockStateMeta bsm = (BlockStateMeta) item.getItemMeta();
            ShulkerBox shulkerBox = (ShulkerBox) bsm.getBlockState();
            ItemStack pickaxe = new ItemStack(Material.GOLDEN_PICKAXE);
            ItemMeta pickaxeMeta = pickaxe.getItemMeta();
            pickaxeMeta.displayName(Component.text("ぼくがかんがえたさいきょうのつるはし"));
            pickaxeMeta.lore(Collections.singletonList(Component.text("あらいぬ鯖クリスマスイベント2021")));
            pickaxe.setItemMeta(pickaxeMeta);
            pickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10000);
            shulkerBox.getInventory().setItem(13, pickaxe);
            bsm.setBlockState(shulkerBox);
            item.setItemMeta(bsm);
            e.getPlayer().getInventory().addItem(item);
        }
    }
}
