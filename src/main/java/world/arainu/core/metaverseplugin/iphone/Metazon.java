package world.arainu.core.metaverseplugin.iphone;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import world.arainu.core.metaverseplugin.MetaversePlugin;
import world.arainu.core.metaverseplugin.commands.CommandSpawn;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.listener.VillagerListener;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.util.Objects;
import java.util.stream.Collectors;

public class Metazon extends iPhoneBase {
    @Override
    public void executeGui(MenuItem menuItem) {
        Gui.getInstance().openMenu(menuItem.getClicker(), "Metazon", CommandSpawn.getMETAZON_VILLAGER().entrySet().stream().map((n) -> new MenuItem(n.getValue(),this::openMetazon, true,Material.STONE,n.getKey())).collect(Collectors.toList()));
    }

    private void openMetazon(MenuItem menuItem){
        Player p = menuItem.getClicker();
        // テスト鯖でデバッグ時に例外が発生しないようにするコード
        if(Objects.requireNonNull(sqlUtil.getuuidsbytype((String) menuItem.getCustomData())).size() == 0){
            MetaversePlugin.logger().warning("Villager entityが存在しないため、新規作成します（自動破棄されます）");
            CommandSpawn.getInstance().execute(p, new String[]{(String) menuItem.getCustomData(),"invisible"});
        }
        VillagerListener.getInstance().open(p, (Villager) Objects.requireNonNull(Bukkit.getEntity(Objects.requireNonNull(sqlUtil.getuuidsbytype((String) menuItem.getCustomData())).get(0))), (int) p.getWorld().getSpawnLocation().distance(p.getLocation()));
    }
}
