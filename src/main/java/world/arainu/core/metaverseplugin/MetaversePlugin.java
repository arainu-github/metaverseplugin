package world.arainu.core.metaverseplugin;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.commands.CommandBase;
import world.arainu.core.metaverseplugin.commands.CommandWorldtp;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.listener.ServerListener;
import world.arainu.core.metaverseplugin.listener.BungeeMessageListener;
import world.arainu.core.metaverseplugin.store.ServerStore;

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
        loadCommands();
        createStore();
        setListener();
    }

    @Override
    public void onDisable() {
        commands.clear();
        Gui.resetInstance();
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getLogger().info("メタバースプラグインが無効になりました。");
    }

    /**
     * Storeを作成する関数
     */
    public void createStore() {
        new ServerStore();
    }

    /**
     * Listenerを設定する関数
     */
    public void setListener() {
        PluginManager PM = getServer().getPluginManager();
        Messenger msg = getServer().getMessenger();

        PM.registerEvents(new ServerListener(), this);
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
        final var cmd = getCommand(commandName);
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

        addCommand("worldtp",new CommandWorldtp());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        final var name = command.getName().toLowerCase();

        final var com = commands.get(name);
        if (com == null) return false;

        return com.execute(sender, command, label, args);
    }


    @Getter private static MetaversePlugin Instance;
    private final HashMap<String, CommandBase> commands = new HashMap<>();
}
