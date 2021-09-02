package world.arainu.core.metaverseplugin;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.commands.CommandBase;
import world.arainu.core.metaverseplugin.commands.CommandiPhone;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.iphone.Worldteleport;
import world.arainu.core.metaverseplugin.listener.ServerListener;
import world.arainu.core.metaverseplugin.listener.BungeeMessageListener;
import world.arainu.core.metaverseplugin.listener.SittingListener;
import world.arainu.core.metaverseplugin.store.ServerStore;
import world.arainu.core.metaverseplugin.store.iPhoneStore;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * メタバースプラグインの基本クラス
 * @author kumitatepazuru
 */
public final class MetaversePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("メタバースプラグインが有効になりました。");
        Instance = this;
        createStore();
        loadCommands();
        setListener();
        loadGuis();
    }

    @Override
    public void onDisable() {
        commands.clear();
        Gui.resetInstance();
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getLogger().info("メタバースプラグインが無効になりました。");
    }

    private void loadGuis() {
        ItemStack teleportItem = new ItemStack(Material.STONE);
        ItemMeta teleportMeta = teleportItem.getItemMeta();
        teleportMeta.lore(Collections.singletonList(Component.text("━━━Mod Only━━━").color(NamedTextColor.LIGHT_PURPLE)));
        teleportItem.setItemMeta(teleportMeta);
        iPhoneStore.addGuiItem(new MenuItem("ワールドテレポート",new Worldteleport()::executeGui,teleportItem,null,true),true);
    }

    /**
     * Storeを作成する関数
     */
    public void createStore() {
        new ServerStore();
        new iPhoneStore();
    }

    /**
     * Listenerを設定する関数
     */
    public void setListener() {
        PluginManager PM = getServer().getPluginManager();
        Messenger msg = getServer().getMessenger();

        PM.registerEvents(new ServerListener(), this);
        PM.registerEvents(new SittingListener(), this);
        PM.registerEvents(Gui.getInstance(), this);
        msg.registerOutgoingPluginChannel(this, "BungeeCord");
        msg.registerIncomingPluginChannel(this, "BungeeCord", new BungeeMessageListener());
    }

    /**
     * コマンドをコアシステムに登録します。
     * @param commandName コマンド名
     * @param command コマンドのインスタンス
     */
    private void addCommand(String commandName, CommandBase command) {
        commands.put(commandName, command);
        final PluginCommand cmd = getCommand(commandName);
        if (cmd == null) {
            getLogger().warning("Command " + commandName + " is not defined at the plugin.yml");
            return;
        }
        cmd.setTabCompleter(command);
        getLogger().info("Command " + commandName + " is registered");
    }

    /**
     * コマンドの追加
     */
    private void loadCommands() {
        commands.clear();

        addCommand("worldtp",new Worldteleport());
        addCommand("iphone",new CommandiPhone());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        final String name = command.getName().toLowerCase();

        final CommandBase com = commands.get(name);
        if (com == null) return false;

        return com.execute(sender, command, label, args);
    }


    @Getter private static MetaversePlugin Instance;
    private final HashMap<String, CommandBase> commands = new HashMap<>();
}
