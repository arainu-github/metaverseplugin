package world.arainu.core.metaverseplugin.iphone;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;

import java.util.List;

public class Municipal extends iPhoneBase {
    @Override
    public void executeGui(MenuItem menuItem) {
        Gui.getInstance().openMenu(menuItem.getClicker(),"自治体メニュー", List.of(
                new MenuItem("自分で自治体を作る", this::createMunicipal, true, Material.SLIME_BALL)
        ));
    }

    public void createMunicipal(MenuItem menuItem) {
        menuItem.getClicker().sendMessage(Component.text("まずはじめに、自治体の区域を設定しましょう。\n")
                .append(Component.text("ここをクリック").decorate(TextDecoration.UNDERLINED).decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/__createmunicipal create")))
                .append(Component.text("して区域の始点を設定しましょう。"))
        );
    }
}
