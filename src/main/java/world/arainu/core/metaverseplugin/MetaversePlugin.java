package world.arainu.core.metaverseplugin;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.commands.CommandBase;
import world.arainu.core.metaverseplugin.commands.CommandSpawn;
import world.arainu.core.metaverseplugin.commands.CommandiPhone;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.iphone.BEAdvancements;
import world.arainu.core.metaverseplugin.iphone.BEStatistics;
import world.arainu.core.metaverseplugin.iphone.Bank;
import world.arainu.core.metaverseplugin.iphone.MoveSurvival;
import world.arainu.core.metaverseplugin.iphone.TrapTower;
import world.arainu.core.metaverseplugin.iphone.Worldteleport;
import world.arainu.core.metaverseplugin.listener.BankListener;
import world.arainu.core.metaverseplugin.listener.PublicListener;
import world.arainu.core.metaverseplugin.listener.ServerListener;
import world.arainu.core.metaverseplugin.listener.SittingListener;
import world.arainu.core.metaverseplugin.listener.VillagerListener;
import world.arainu.core.metaverseplugin.scheduler.LateScheduler;
import world.arainu.core.metaverseplugin.scheduler.MoneyScheduler;
import world.arainu.core.metaverseplugin.scheduler.SqlScheduler;
import world.arainu.core.metaverseplugin.store.ServerStore;
import world.arainu.core.metaverseplugin.store.iPhoneStore;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

/**
 * メタバースプラグインの基本クラス
 *
 * @author kumitatepazuru
 */
public final class MetaversePlugin extends JavaPlugin {
    @Getter
    private static Economy econ = null;
    @Getter
    private static MetaversePlugin Instance;
    @Getter
    private static FileConfiguration configuration;
    private final HashMap<String, CommandBase> commands = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configuration = getConfig();
        getLogger().info("メタバースプラグインが有効になりました。");
        Instance = this;
        loadCommands();
        setListener();
        loadGuis();
        EnablePlugins();
        setScheduler();
        ServerStore.setServerName(configuration.getString("servername"));
        sqlUtil.connect();
    }

    private void setScheduler() {
        new MoneyScheduler().runTaskTimer(this, 0, 20);
        new LateScheduler().runTaskTimer(this, 0, 20);
        new SqlScheduler().runTaskTimer(this, 0, 20*60*60);
        createStairsYml();
    }

    private void EnablePlugins() {
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Vaultが依存する経済プラグインがなかったためメタバースプラグインを無効にしました！！！", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    @Override
    public void onDisable() {
        commands.clear();
        Gui.resetInstance();
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getLogger().info("メタバースプラグインが無効になりました。");
        sqlUtil.disconnect();
    }

    private void loadGuis() {
        ItemStack teleportItem = new ItemStack(Material.STONE);
        ItemMeta teleportMeta = teleportItem.getItemMeta();
        teleportMeta.lore(Collections.singletonList(Component.text("━━━Mod Only━━━").color(NamedTextColor.LIGHT_PURPLE)));
        teleportItem.setItemMeta(teleportMeta);
        iPhoneStore.addGuiItem(new MenuItem("ワールドテレポート", new Worldteleport()::executeGui, true, teleportItem, null, true), true);
        iPhoneStore.addGuiItem(new MenuItem("ネット銀行", new Bank()::executeGui, true, Material.EMERALD_BLOCK));
        ItemStack traptowerItem = new ItemStack(Material.CRACKED_STONE_BRICKS);
        ItemMeta traptowerMeta = teleportItem.getItemMeta();
        traptowerMeta.lore(Collections.singletonList(Component.text("利用料金 200円/分").color(NamedTextColor.RED)));
        traptowerItem.setItemMeta(traptowerMeta);
        iPhoneStore.addGuiItem(new MenuItem("トラップタワーに行く", new TrapTower()::executeGui, true, traptowerItem), (p) -> !p.getWorld().getName().equals(configuration.getString("world.traptower")) && Objects.equals(ServerStore.getServerName(), "survival"));
        iPhoneStore.addGuiItem(new MenuItem("サバイバルサーバーに戻る", new MoveSurvival()::executeGui, true, Material.GRASS_BLOCK), (p) -> p.getWorld().getName().equals(configuration.getString("world.traptower")));
        iPhoneStore.addGuiItem(new MenuItem("進捗(java版の機能)を表示する", new BEAdvancements()::executeGui), Gui::isBedrock);
        iPhoneStore.addGuiItem(new MenuItem("統計(java版の機能)を表示する", new BEStatistics()::executeGui), Gui::isBedrock);
    }

    /**
     * Listenerを設定する関数
     */
    public void setListener() {
        final PluginManager PM = getServer().getPluginManager();

        PM.registerEvents(new SittingListener(), this);
        PM.registerEvents(new BankListener(), this);
        PM.registerEvents(Gui.getInstance(), this);
        PM.registerEvents(new PublicListener(), this);
        PM.registerEvents(new VillagerListener(), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        DiscordSRV.api.subscribe(this);
    }

    /**
     * JDAがログインできてReadyになったときにServerListenerを定義する
     * ぬるぽ対策
     * @param event イベント
     */
    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        final PluginManager PM = getServer().getPluginManager();

        PM.registerEvents(new ServerListener(), this);
    }

    /**
     * コマンドをコアシステムに登録します。
     *
     * @param commandName コマンド名
     * @param command     コマンドのインスタンス
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

        addCommand("worldtp", new Worldteleport());
        addCommand("iphone", new CommandiPhone());
        addCommand("spawn", new CommandSpawn());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        final String name = command.getName().toLowerCase();

        final CommandBase com = commands.get(name);
        if (com == null) return false;

        return com.execute(sender, command, label, args);
    }

    /**
     * stairs.ymlの作成
     */
    private void createStairsYml() {
        saveResource("stairs.yml", false);
        File stairsYml = new File(Instance.getDataFolder() + File.separator + "stairs.yml");
        FileConfiguration stairsConfig = YamlConfiguration.loadConfiguration(stairsYml);

        try {
            stairsConfig.save(stairsYml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
