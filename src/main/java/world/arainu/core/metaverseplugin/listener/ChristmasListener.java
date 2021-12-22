package world.arainu.core.metaverseplugin.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;

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
        Component merryChristmas = generateChristmasText("Merry Christmas!").decorate(TextDecoration.ITALIC).decorate(TextDecoration.UNDERLINED);
        e.getPlayer().showTitle(Title.title(merryChristmas,Component.empty(), Title.Times.of(Duration.ofMillis(500),Duration.ofMillis(5000),Duration.ofMillis(500))));
        merryChristmas = Component.empty().append(merryChristmas);
        merryChristmas = merryChristmas.append(Component.text("\nメリークリスマス！").color(TextColor.color(NamedTextColor.RED)));
        merryChristmas = merryChristmas.append(Component.text("""


                皆さん、クリスマスですよクリスマス！
                ということで、いよいよあらいぬサーバーの閉鎖当日になってしまいました。
                しかし、最終日も楽しんでもらいたい！ということで運営からささやかなプレゼントを用意いたしました！
                お役に立てることができたら幸いです♪
                
                """));
        merryChristmas = merryChristmas.append(Component.text("↓特設ページだヨ★↓\n").color(NamedTextColor.RED));
        merryChristmas = merryChristmas.append(Component.text("https://www.arainu.world/archives/499").color(NamedTextColor.RED).decorate(TextDecoration.UNDERLINED));
        e.getPlayer().sendMessage(merryChristmas);
        e.joinMessage(generateChristmasText("[クリスマスイベ開催中] "+e.getPlayer().getName()+"がゲームに参加しました"));
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"execute at "+e.getPlayer().getName()+" run summon firework_rocket ~ ~ ~ {LifeTime:40,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Explosions:[{Type:1,Trail:1,Colors:[I;11743532],FadeColors:[I;4312372]}],Flight:2}}}}");
    }
}
