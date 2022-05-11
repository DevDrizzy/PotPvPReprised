package net.frozenorb.potpvp;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.frozenorb.potpvp.adapter.nametag.NameTagAdapter;
import net.frozenorb.potpvp.adapter.scoreboard.ScoreboardAdapter;
import net.frozenorb.potpvp.adapter.tablist.TablistAdapter;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.command.binds.*;
import net.frozenorb.potpvp.command.impl.*;
import net.frozenorb.potpvp.command.impl.duel.AcceptCommand;
import net.frozenorb.potpvp.command.impl.duel.DuelCommand;
import net.frozenorb.potpvp.command.impl.events.ForceEndCommand;
import net.frozenorb.potpvp.command.impl.events.HostCommand;
import net.frozenorb.potpvp.command.impl.match.LeaveCommand;
import net.frozenorb.potpvp.command.impl.match.MapCommand;
import net.frozenorb.potpvp.command.impl.match.SpectateCommand;
import net.frozenorb.potpvp.command.impl.misc.*;
import net.frozenorb.potpvp.command.impl.settings.NightCommand;
import net.frozenorb.potpvp.command.impl.settings.SettingsCommand;
import net.frozenorb.potpvp.command.impl.settings.ToggleDuelCommand;
import net.frozenorb.potpvp.command.impl.settings.ToggleGlobalChatCommand;
import net.frozenorb.potpvp.command.impl.silent.FollowCommand;
import net.frozenorb.potpvp.command.impl.silent.SilentCommand;
import net.frozenorb.potpvp.command.impl.silent.SilentFollowCommand;
import net.frozenorb.potpvp.command.impl.silent.UnfollowCommand;
import net.frozenorb.potpvp.command.impl.stats.EloSetCommands;
import net.frozenorb.potpvp.command.impl.stats.StatsResetCommands;
import net.frozenorb.potpvp.events.EventListeners;
import net.frozenorb.potpvp.events.GameHandler;
import net.frozenorb.potpvp.hologram.HologramHandler;
import net.frozenorb.potpvp.hologram.HologramType;
import net.frozenorb.potpvp.hologram.PracticeHologram;
import net.frozenorb.potpvp.kit.KitHandler;
import net.frozenorb.potpvp.kit.kittype.KitType;
import net.frozenorb.potpvp.kit.kittype.KitTypeJsonAdapter;
import net.frozenorb.potpvp.listener.*;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.duel.DuelHandler;
import net.frozenorb.potpvp.match.postmatchinv.PostMatchInvHandler;
import net.frozenorb.potpvp.match.rematch.RematchHandler;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.profile.elo.EloHandler;
import net.frozenorb.potpvp.profile.follow.FollowHandler;
import net.frozenorb.potpvp.profile.setting.SettingHandler;
import net.frozenorb.potpvp.profile.statistics.StatisticsHandler;
import net.frozenorb.potpvp.pvpclasses.PvPClassHandler;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.tournament.TournamentHandler;
import net.frozenorb.potpvp.tournament.TournamentListener;
import net.frozenorb.potpvp.util.ChunkSnapshotAdapter;
import net.frozenorb.potpvp.util.event.HalfHourEvent;
import net.frozenorb.potpvp.util.menu.ButtonListener;
import net.frozenorb.potpvp.util.nametag.NameTagHandler;
import net.frozenorb.potpvp.util.scoreboard.api.AssembleStyle;
import net.frozenorb.potpvp.util.scoreboard.api.ScoreboardHandler;
import net.frozenorb.potpvp.util.serialization.*;
import net.frozenorb.potpvp.util.uuid.UUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import xyz.refinedev.command.CommandHandler;
import xyz.refinedev.spigot.chunk.ChunkSnapshot;
import xyz.refinedev.spigot.utils.CC;
import xyz.refinedev.tablist.TablistHandler;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public final class PotPvPRP extends JavaPlugin {

    private static PotPvPRP instance;

    @Getter
    private final static Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .registerTypeHierarchyAdapter(KitType.class, new KitTypeJsonAdapter()) // custom KitType serializer
            .registerTypeAdapter(ChunkSnapshot.class, new ChunkSnapshotAdapter())
            .serializeNulls()
            .create();

    public static Gson plainGson = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .serializeNulls()
            .create();

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private SettingHandler settingHandler;
    private DuelHandler duelHandler;
    private KitHandler kitHandler;
    private LobbyHandler lobbyHandler;
    private ArenaHandler arenaHandler;
    private MatchHandler matchHandler;
    private PartyHandler partyHandler;
    private QueueHandler queueHandler;
    private RematchHandler rematchHandler;
    private PostMatchInvHandler postMatchInvHandler;
    private FollowHandler followHandler;
    private EloHandler eloHandler;
    private GameHandler gameHandler;
    private PvPClassHandler pvpClassHandler;
    private TournamentHandler tournamentHandler;

    public ScoreboardHandler scoreboardHandler;
    public HologramHandler hologramHandler;
    public CommandHandler commandHandler;
    public NameTagHandler nameTagHandler;
    public TablistHandler tablistHandler;

    public UUIDCache uuidCache;

    private final ChatColor dominantColor = ChatColor.RED;
    private final PotPvPCache cache = new PotPvPCache();

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        this.setupMongo();

        this.uuidCache = new UUIDCache();
		
        this.commandHandler = new CommandHandler(this);
        this.commandHandler.bind(KitType.class).toProvider(new KitTypeProvider());
        this.commandHandler.bind(ChatColor.class).toProvider(new ChatColorProvider());
        this.commandHandler.bind(UUID.class).toProvider(new UUIDDrinkProvider());
		
        this.registerExpansions();
        this.registerCommands();
        this.registerPermission();

        kitHandler = new KitHandler();
        eloHandler = new EloHandler();
        gameHandler = new GameHandler();
        duelHandler = new DuelHandler();
        lobbyHandler = new LobbyHandler();
        arenaHandler = new ArenaHandler();
        matchHandler = new MatchHandler();
        partyHandler = new PartyHandler();
        queueHandler = new QueueHandler();
        followHandler = new FollowHandler();
        rematchHandler = new RematchHandler();
        settingHandler = new SettingHandler();
        pvpClassHandler = new PvPClassHandler();
        tournamentHandler = new TournamentHandler();
        postMatchInvHandler = new PostMatchInvHandler();

        this.getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        this.getServer().getPluginManager().registerEvents(new BowHealthListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChatToggleListener(), this);
        this.getServer().getPluginManager().registerEvents(new NightModeListener(), this);
        this.getServer().getPluginManager().registerEvents(new PearlCooldownListener(), this);
        this.getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
        this.getServer().getPluginManager().registerEvents(new StatisticsHandler(), this);
        this.getServer().getPluginManager().registerEvents(new EventListeners(), this);
        this.getServer().getPluginManager().registerEvents(new TournamentListener(), this);
        this.getServer().getPluginManager().registerEvents(new ButtonListener(), this);
        this.logger("&7Registering &clisteners&7...");

        this.setupHourEvents();

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, cache, 20L, 20L);
        this.consoleLog("&7Initialized &cPotPvP &7Successfully!");
    }

    @Override
    public void onDisable() {
        matchHandler.cleanup();
        arenaHandler.saveSchematics();
        scoreboardHandler.shutdown();
    }

    //TODO: Get rid of this
    private void setupHourEvents() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((new ThreadFactoryBuilder()).setNameFormat("PotPvP - Hour Event Thread").setDaemon(true).build());
        int minOfHour = Calendar.getInstance().get(Calendar.MINUTE);
        int minToHour = 60 - minOfHour;
        int minToHalfHour = (minToHour >= 30) ? minToHour : (30 - minOfHour);

        executor.scheduleAtFixedRate(() -> this.getServer().getScheduler().runTask(this, () -> this.getServer().getPluginManager().callEvent(new HalfHourEvent())), minToHalfHour, 30L, TimeUnit.MINUTES);
    }

    private void setupMongo() {
        if (this.getConfig().getBoolean("MONGO.URI-MODE")) {
            this.mongoClient = MongoClients.create(this.getConfig().getString("MONGO.URI.CONNECTION_STRING"));
            this.mongoDatabase = mongoClient.getDatabase(this.getConfig().getString("MONGO.URI.DATABASE"));

            this.logger("&7Initialized &cMongoDB &7successfully!");
            return;
        }

        boolean auth = this.getConfig().getBoolean("MONGO.NORMAL.AUTHENTICATION.ENABLED");
        String host = this.getConfig().getString("MONGO.NORMAL.HOST");
        int port = this.getConfig().getInt("MONGO.NORMAL.PORT");

        String uri = "mongodb://" + host + ":" + port;

        if (auth) {
            String username = this.getConfig().getString("MONGO.NORMAL.AUTHENTICATION.USERNAME");
            String password = this.getConfig().getString("MONGO.NORMAL.AUTHENTICATION.PASSWORD");
            uri = "mongodb://" + username + ":" + password + "@" + host + ":" + port;
        }


        this.mongoClient = MongoClients.create(uri);
        this.mongoDatabase = mongoClient.getDatabase(this.getConfig().getString("MONGO.URI.DATABASE"));

        this.logger("&7Initialized &cMongoDB &7successfully!");
    }

    // kaya was here
    private void registerCommands() {
        commandHandler.register(new ArenaCommands(), "arena");
        commandHandler.register(new KitCommands(), "kit", "kitType");
        commandHandler.register(new MatchCommands(), "match");
        commandHandler.register(new PartyCommands(), "party", "p", "f", "team");
        commandHandler.register(new ToggleMatchCommands(), "toggleMatches");
        commandHandler.register(new TournamentCommands(), "tournament", "tourney", "t");

        commandHandler.register(new EloSetCommands(), "elo");
        commandHandler.register(new StatsResetCommands(), "statsreset");

        commandHandler.register(new FollowCommand(), "follow");
        commandHandler.register(new SilentCommand(), "silent");
        commandHandler.register(new SilentFollowCommand(), "silentfollow");
        commandHandler.register(new UnfollowCommand(), "unfollow");

        commandHandler.register(new NightCommand(), "night", "nightMode");
        commandHandler.register(new SettingsCommand(), "settings");
        commandHandler.register(new ToggleDuelCommand(), "toggleduels", "tduels", "td");
        commandHandler.register(new ToggleGlobalChatCommand(), "toggleGlobalChat", "tgc", "togglechat");

        commandHandler.register(new SetSpawnCommand(), "setspawn");
        commandHandler.register(new PingCommand(), "ping");
        commandHandler.register(new ManageCommand(), "manage");
        commandHandler.register(new HelpCommand(), "help", "?", "halp", "helpme");
        commandHandler.register(new EditPotionModifyCommand(), "editpotion");
        commandHandler.register(new DJMCommand(), "djm");
        commandHandler.register(new DEMCommand(), "dem");
        commandHandler.register(new CheckPostMatchInvCommand(), "checkPostMatchInv", "_");
        commandHandler.register(new BuildCommand(), "build", "buildmode");

        commandHandler.register(new SpectateCommand(), "spec", "spectate");
        commandHandler.register(new MapCommand(), "map");
        commandHandler.register(new LeaveCommand(), "leave", "spawn");

        commandHandler.register(new ForceEndCommand(), "forceend");
        commandHandler.register(new HostCommand(), "host", "events");

        commandHandler.register(new AcceptCommand(), "accept");
        commandHandler.register(new DuelCommand(), "duel");

        commandHandler.registerCommands();
        this.logger("&7Registering &ccommands&7...");
    }

    private void registerPermission() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.addPermission(new Permission("potpvp.toggleduels", PermissionDefault.OP));
        pm.addPermission(new Permission("potpvp.togglelightning", PermissionDefault.OP));
        pm.addPermission(new Permission("potpvp.silent", PermissionDefault.OP));
        pm.addPermission(new Permission("potpvp.famous", PermissionDefault.OP));
        pm.addPermission(new Permission("potpvp.spectate", PermissionDefault.OP));

        this.commandHandler.registerPermissions();
        this.logger("&7Registering &cpermissions&7...");
    }

    private void registerExpansions() {
        ScoreboardAdapter scoreboardAdapter = new ScoreboardAdapter();
        NameTagAdapter nameTagAdapter = new NameTagAdapter();
        TablistAdapter tablistAdapter = new TablistAdapter();

        this.scoreboardHandler = new ScoreboardHandler(this, scoreboardAdapter);
        this.scoreboardHandler.setAssembleStyle(AssembleStyle.KOHI);
        this.scoreboardHandler.setTicks(2L);

        this.nameTagHandler = new NameTagHandler(this);
        this.nameTagHandler.registerAdapter(nameTagAdapter);

        this.tablistHandler = new TablistHandler(this);
        this.tablistHandler.registerAdapter(tablistAdapter, 20L);

        if (this.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            this.logger("&7Found &cHolographicDisplays&7, Hooking holograms...");
            this.hologramHandler = new HologramHandler();

            this.commandHandler.bind(PracticeHologram.class).toProvider(new HologramProvider());
            this.commandHandler.bind(HologramType.class).toProvider(new HologramTypeProvider());
            this.commandHandler.register(new HologramCommands(), "prachologram");
        }
    }

    public void logger(String message) {
        this.getServer().getConsoleSender().sendMessage(CC.translate("&7[&cPotPvPRP&7] &r" + message));
    }

    public void consoleLog(String string) {
        this.getServer().getConsoleSender().sendMessage(CC.translate(string));
    }

    //fuck you kotlin
    public static PotPvPRP getInstance() { return instance; }

    public ArenaHandler getArenaHandler() { return arenaHandler; }

    public GameHandler getGameHandler() { return gameHandler; }
    // fuck your mother, kotlin
}