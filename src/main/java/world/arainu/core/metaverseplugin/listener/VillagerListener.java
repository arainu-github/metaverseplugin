package world.arainu.core.metaverseplugin.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.iphone.Bank;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.List;
import java.util.stream.Collectors;

public class VillagerListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Villager) {
            if (Boolean.TRUE.equals(sqlUtil.hasuuid(e.getRightClicked().getUniqueId()))) {
                e.setCancelled(true);
                Villager villager = (Villager) e.getRightClicked();
                List<MenuItem> tradeitems = villager.getRecipes().stream()
                        .map((recipe) -> new MenuItem(null, true, Bank.isMoney(recipe.getResult()) ? recipe.getResult() : recipe.getIngredients().get(0), null, false, -1, -1))
                        .collect(Collectors.toList());

                Gui.getInstance().openMenu(e.getPlayer(), ChatColor.DARK_GREEN + "銀行", tradeitems);
            }
        }
    }
}
