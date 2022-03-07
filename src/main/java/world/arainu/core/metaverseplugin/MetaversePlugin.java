package world.arainu.core.metaverseplugin;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import lombok.Getter;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.jetbrains.annotations.NotNull;
import world.arainu.core.metaverseplugin.commands.CommandBase;
import world.arainu.core.metaverseplugin.commands.CommandSpawn;
import world.arainu.core.metaverseplugin.commands.CommandWhitelist;
import world.arainu.core.metaverseplugin.commands.CommandiPhone;
import world.arainu.core.metaverseplugin.gui.Gui;
import world.arainu.core.metaverseplugin.gui.MenuItem;
import world.arainu.core.metaverseplugin.iphone.Bank;
import world.arainu.core.metaverseplugin.iphone.ChestLock;
import world.arainu.core.metaverseplugin.iphone.Drilling;
import world.arainu.core.metaverseplugin.iphone.LinkDiscord;
import world.arainu.core.metaverseplugin.iphone.MoveSurvival;
import world.arainu.core.metaverseplugin.iphone.Municipal;
import world.arainu.core.metaverseplugin.iphone.Spawn;
import world.arainu.core.metaverseplugin.iphone.TrapTower;
import world.arainu.core.metaverseplugin.iphone.Worldteleport;
import world.arainu.core.metaverseplugin.iphone.iPhoneEnderDragon;
import world.arainu.core.metaverseplugin.listener.AdvancementListener;
import world.arainu.core.metaverseplugin.listener.BankListener;
import world.arainu.core.metaverseplugin.listener.ChestLockListener;
import world.arainu.core.metaverseplugin.listener.DrillingListener;
import world.arainu.core.metaverseplugin.listener.MoneyListener;
import world.arainu.core.metaverseplugin.listener.MunicipalCreateListener;
import world.arainu.core.metaverseplugin.listener.PublicListener;
import world.arainu.core.metaverseplugin.listener.ServerListener;
import world.arainu.core.metaverseplugin.listener.SittingListener;
import world.arainu.core.metaverseplugin.listener.VillagerListener;
import world.arainu.core.metaverseplugin.scheduler.*;
import world.arainu.core.metaverseplugin.store.ServerStore;
import world.arainu.core.metaverseplugin.store.iPhoneStore;
import world.arainu.core.metaverseplugin.utils.sqlUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    @Getter
    private static DynmapAPI dynmap;
    private final HashMap<String, CommandBase> commands = new HashMap<>();

    /**
     * プラグインのLoggerを取得する関数。
     *
     * @return Logger
     */
    static public @NotNull Logger logger() {
        return getInstance().getLogger();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configuration = getConfig();
        Instance = this;
        sqlUtil.connect();
        ServerStore.setServerName(configuration.getString("servername"));
        loadCommands();
        loadGuis();
        EnablePlugins();
        setListener();
        setScheduler();
        if (Objects.equals(ServerStore.getServerName(), "survival")) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                getLogger().info("saving advancements data...");
                sqlUtil.truncateAdvancement();
                Bukkit.advancementIterator().forEachRemaining(e -> {
                    if (e.getDisplay() != null) {
                        String id = e.getKey().getNamespace() + ":" + e.getKey().getKey();
                        String title = ((TranslatableComponent) e.getDisplay().title()).key();
                        String description = ((TranslatableComponent) e.getDisplay().description()).key();
                        List<String> children = e.getChildren().stream().map(i -> i.getKey().getNamespace() + ":" + i.getKey().getKey()).collect(Collectors.toList());
                        String icon = e.getDisplay().icon().getType().name().toLowerCase();
                        String type = e.getDisplay().frame().name();
                        sqlUtil.addAdvancement(id, title, description, children, icon, type);
                    }
                });
                getLogger().info("saved");
            });
        }
        getLogger().info("メタバースプラグインが有効になりました。");
    }

    private void setScheduler() {
        new LateScheduler().runTaskTimer(this, 0, 20);
        new DiscordScheduler().runTaskTimer(this, 0, 20 * 10);
        new SqlScheduler().runTaskTimer(this, 0, 20 * 60 * 60);
        new ParticleScheduler().runTaskTimer(this, 0, 2);
        // new AdvancementScheduler().runTaskTimer(this, 0, 40);
        createStairsYml();
    }

    private void EnablePlugins() {
        dynmap = (DynmapAPI) getServer().getPluginManager().getPlugin("Dynmap");
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
        DrillingListener.getInstance().saveData();
        sqlUtil.disconnect();
        getLogger().info("メタバースプラグインが無効になりました。");
    }

    private void loadGuis() {
        ItemStack teleportItem = new ItemStack(Material.STONE);
        ItemMeta teleportMeta = teleportItem.getItemMeta();
        teleportMeta.lore(Collections.singletonList(Component.text("━━━Mod Only━━━").color(NamedTextColor.LIGHT_PURPLE)));
        teleportItem.setItemMeta(teleportMeta);
        iPhoneStore.addGuiItem(new MenuItem("ワールドテレポート", new Worldteleport()::executeGui, true, teleportItem, null, true), true);
        iPhoneStore.addGuiItem(new MenuItem("ネット銀行", new Bank()::executeGui, true, Material.EMERALD_BLOCK));
        ItemStack teleportItem2 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta teleportMeta2 = teleportItem2.getItemMeta();
        teleportMeta2.lore(Collections.singletonList(Component.text("500円/回").color(NamedTextColor.GOLD)));
        teleportItem2.setItemMeta(teleportMeta2);
        iPhoneStore.addGuiItem(new MenuItem("初期スポーン地点にテレポート", new Spawn()::executeGui, true, teleportItem2));
        ItemStack traptowerItem = new ItemStack(Material.CRACKED_STONE_BRICKS);
        ItemMeta traptowerMeta = teleportItem.getItemMeta();
        traptowerMeta.lore(Collections.singletonList(Component.text("利用料金 200円/分").color(NamedTextColor.RED)));
        traptowerItem.setItemMeta(traptowerMeta);
        iPhoneStore.addGuiItem(new MenuItem("トラップタワーに行く", new TrapTower()::executeGui, true, traptowerItem), (p) -> !p.getWorld().getName().equals(configuration.getString("world.traptower")) && Objects.equals(ServerStore.getServerName(), "survival"));
        iPhoneStore.addGuiItem(new MenuItem("自動採掘をする", new Drilling()::executeGui, true, Material.BRICKS), (p) -> !p.getWorld().getName().equals(configuration.getString("world.traptower")) && Objects.equals(ServerStore.getServerName(), "survival"));
        iPhoneStore.addGuiItem(new MenuItem("サバイバルサーバーに戻る", new MoveSurvival()::executeGui, true, Material.GRASS_BLOCK), (p) -> p.getWorld().getName().equals(configuration.getString("world.traptower")));
        iPhoneStore.addGuiItem(new MenuItem("エンドラを復活させる", new iPhoneEnderDragon()::executeGui, true, Material.END_STONE), (p) -> !Gui.isEnderDragonLiving(p) && Gui.isPlayerInEnd(p));
        iPhoneStore.addGuiItem(new MenuItem("自治体", new Municipal()::executeGui, true, Material.END_STONE), (p) -> Objects.equals(ServerStore.getServerName(), "survival"));
        iPhoneStore.addGuiItem(new MenuItem("discordと連携する", new LinkDiscord()::executeGui, true, Material.PAPER), (p) -> DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(p.getUniqueId()) == null);
        ItemStack chestItem = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta chestMeta = chestItem.getItemMeta();
        chestMeta.lore(Arrays.asList(Component.text("チェストに向かって使用することで"), Component.text("チェストを個人用チェストにすることができます。"), Component.text("300円/個").color(NamedTextColor.GOLD)));
        chestItem.setItemMeta(chestMeta);
        iPhoneStore.addGuiItem(new MenuItem("チェストの鍵を購入する", new ChestLock()::executeGui, true, chestItem), (p) -> Objects.equals(ServerStore.getServerName(), "survival"));
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
        PM.registerEvents(new CommandWhitelist(), this);
        PM.registerEvents(new MunicipalCreateListener(), this);
        PM.registerEvents(new MoneyListener(), this);
        PM.registerEvents(new DrillingListener(), this);
        PM.registerEvents(new ChestLockListener(), this);
        if (Objects.equals(ServerStore.getServerName(), "survival")) {
            PM.registerEvents(new AdvancementListener(), this);
        }
        DiscordSRV.api.subscribe(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    /**
     * JDAがログインできてReadyになったときにServerListenerを定義する
     * ぬるぽ対策
     *
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
        addCommand("whitelist", new CommandWhitelist());
    }

    public CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 7) {
            return null;
        }

        return CoreProtect;
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
            if (!stairsYml.exists()) stairsConfig.save(stairsYml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
