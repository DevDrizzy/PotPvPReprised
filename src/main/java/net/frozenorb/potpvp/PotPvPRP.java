package net.frozenorb.potpvp;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.qrakn.morpheus.Morpheus;
import lombok.SneakyThrows;
import net.frozenorb.potpvp.command.binds.KitTypeBind;
import net.frozenorb.potpvp.kt.menu.ButtonListeners;
import net.frozenorb.potpvp.kt.protocol.InventoryAdapter;
import net.frozenorb.potpvp.kt.protocol.LagCheck;
import net.frozenorb.potpvp.kt.protocol.PingAdapter;
import net.frozenorb.potpvp.kt.uuid.RedisUUIDCache;
import net.frozenorb.potpvp.kt.redis.RedisCredentials;
import net.frozenorb.potpvp.kt.redis.Redis;
import net.frozenorb.potpvp.kt.util.serialization.*;
import net.frozenorb.potpvp.kt.uuid.UUIDCache;
import net.frozenorb.potpvp.kt.visibility.VisibilityEngine;
import net.frozenorb.potpvp.morpheus.EventListeners;
import net.frozenorb.potpvp.nametag.NameTagHandler;
import net.frozenorb.potpvp.nametag.provider.PotPvPNametagProvider;
import net.frozenorb.potpvp.pvpclasses.PvPClassHandler;
import net.frozenorb.potpvp.scoreboard.api.ScoreboardHandler;
import net.frozenorb.potpvp.tournament.TournamentHandler;
import net.frozenorb.potpvp.tournament.TournamentListener;
import net.frozenorb.potpvp.util.event.HalfHourEvent;
import net.frozenorb.potpvp.util.event.HourEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.mongodb.client.MongoDatabase;

import lombok.Getter;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.duel.DuelHandler;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.kit.KitHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.KitTypeJsonAdapter;
import net.frozenorb.potpvp.listener.BasicPreventionListener;
import net.frozenorb.potpvp.listener.BowHealthListener;
import net.frozenorb.potpvp.listener.ChatToggleListener;
import net.frozenorb.potpvp.listener.NightModeListener;
import net.frozenorb.potpvp.listener.PearlCooldownListener;
import net.frozenorb.potpvp.listener.RankedMatchQualificationListener;
import net.frozenorb.potpvp.listener.TabCompleteListener;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.postmatchinv.PostMatchInvHandler;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.rematch.RematchHandler;
import net.frozenorb.potpvp.scoreboard.PotPvPScoreboardConfiguration;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.statistics.StatisticsHandler;
import xyz.refinedev.api.CommandHandler;
import xyz.refinedev.api.parametric.DrinkProvider;
import xyz.refinedev.api.util.ClassUtil;
import xyz.refinedev.spigot.chunk.ChunkSnapshot;
import xyz.refinedev.spigot.utils.CC;

@Getter
public final class PotPvPRP extends JavaPlugin {

    @Getter
    private static PotPvPRP instance;

    @Getter
    private static final Gson gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
        .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
        .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
        .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
        .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
        .registerTypeHierarchyAdapter(KitType.class, new KitTypeJsonAdapter()) // custom KitType serializer
        .registerTypeAdapter(ChunkSnapshot.class, new ChunkSnapshotAdapter())
        .serializeNulls()
        .create();

    @Getter
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
    private PvPClassHandler pvpClassHandler;
    private TournamentHandler tournamentHandler;

    public Redis redis;
    public ScoreboardHandler scoreboardEngine;
    public CommandHandler commandHandler;
    public NameTagHandler nameTagEngine;
    public VisibilityEngine visibilityEngine;
    public UUIDCache uuidCache;

    private final ChatColor dominantColor = ChatColor.GOLD;
    private final PotPvPCache cache = new PotPvPCache();

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        this.consoleLog("&c------------------------------------------------");
        this.setupRedis();
        this.setupMongo();

        this.uuidCache = new RedisUUIDCache();
        this.uuidCache.load();
        this.getServer().getPluginManager().registerEvents(this.uuidCache, this);

        this.commandHandler = new CommandHandler(this);
        this.commandHandler.bind(KitType.class).toProvider(new KitTypeBind());
        this.registerCommands();

        this.nameTagEngine = new NameTagHandler(this);
        this.nameTagEngine.registerAdapter(new PotPvPNametagProvider());

        this.scoreboardEngine = new ScoreboardHandler(this, PotPvPScoreboardConfiguration.create());

        this.visibilityEngine = new VisibilityEngine();
        this.visibilityEngine.load();

        PingAdapter pingAdapter = new PingAdapter();

        ProtocolLibrary.getProtocolManager().addPacketListener(pingAdapter);
        ProtocolLibrary.getProtocolManager().addPacketListener(new InventoryAdapter());

        this.getServer().getPluginManager().registerEvents(pingAdapter, this);

        new LagCheck().runTaskTimerAsynchronously(this, 100L, 100L);

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(6_000L);
        }

        kitHandler = new KitHandler();
        eloHandler = new EloHandler();
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
        this.getServer().getPluginManager().registerEvents(new RankedMatchQualificationListener(), this);
        this.getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
        this.getServer().getPluginManager().registerEvents(new StatisticsHandler(), this);
        this.getServer().getPluginManager().registerEvents(new EventListeners(), this);
        this.getServer().getPluginManager().registerEvents(new TournamentListener(), this);
        this.getServer().getPluginManager().registerEvents(new ButtonListeners(), this);
        this.logger("Registering listeners...");

        this.setupHourEvents();

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, cache, 20L, 20L);

        new Morpheus(this);

        this.consoleLog("");
        this.consoleLog("&7Initialized &cPotPvP &7Successfully!");
        this.consoleLog("&c------------------------------------------------");
    }

    @Override
    public void onDisable() {
        for (Match match : this.matchHandler.getHostedMatches()) {
            if (match.getKitType().isBuildingAllowed()) match.getArena().restore();
        }

        try {
            arenaHandler.saveSchematics();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
            PvPClassHandler.getEquippedKits().get(playerName).remove(this.getServer().getPlayerExact(playerName));
        }
        scoreboardEngine.cleanup();
    }

    private void setupHourEvents() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((new ThreadFactoryBuilder()).setNameFormat("PotPvP - Hour Event Thread").setDaemon(true).build());
        int minOfHour = Calendar.getInstance().get(Calendar.MINUTE);
        int minToHour = 60 - minOfHour;
        int minToHalfHour = (minToHour >= 30) ? minToHour : (30 - minOfHour);

        executor.scheduleAtFixedRate(() -> this.getServer().getScheduler().runTask(this, () -> {
            this.getServer().getPluginManager().callEvent(new HourEvent(Date.from(Instant.now()).getHours()));
        }), minToHour, 60L, TimeUnit.MINUTES);

        executor.scheduleAtFixedRate(() -> this.getServer().getScheduler().runTask(this, () -> {
            Date dateNow = Date.from(Instant.now());
            this.getServer().getPluginManager().callEvent(new HalfHourEvent(dateNow.getHours(), dateNow.getMinutes()));
        }), minToHalfHour, 30L, TimeUnit.MINUTES);
    }

    private void setupRedis() {
        redis = new Redis();

        RedisCredentials.Builder localBuilder = new RedisCredentials.Builder()
                .host(getConfig().getString("REDIS.HOST"))
                .port(getConfig().getInt("REDIS.PORT"));

        if (getConfig().contains("REDIS.PASSWORD") && !getConfig().getString("REDIS.PASSWORD").equalsIgnoreCase("")) {
            localBuilder.password(getConfig().getString("REDIS.PASSWORD"));
        }

        if (getConfig().contains("REDIS.DBID")) {
            localBuilder.dbId(getConfig().getInt("REDIS.DBID"));
        }

        redis.load(localBuilder.build(), localBuilder.build());
        this.logger("Initialized Redis successfully!");
    }

    private void setupMongo() {
        if (getConfig().getBoolean("MONGO.URI-MODE")) {
            this.mongoClient = MongoClients.create(getConfig().getString("MONGO.URI.CONNECTION_STRING"));
            this.mongoDatabase = mongoClient.getDatabase(getConfig().getString("MONGO.URI.DATABASE"));

            this.logger("Initialized MongoDB successfully!");
            return;
        }

        boolean auth = getConfig().getBoolean("MONGO.NORMAL.AUTHENTICATION.ENABLED");
        String host = getConfig().getString("MONGO.NORMAL.HOST");
        int port = getConfig().getInt("MONGO.NORMAL.PORT");

        String uri = "mongodb://" + host + ":" + port;

        if (auth) {
            String username = getConfig().getString("MONGO.NORMAL.AUTHENTICATION.USERNAME");
            String password = getConfig().getString("MONGO.NORMAL.AUTHENTICATION.PASSWORD");
            uri = "mongodb://" + username + ":" + password + "@" + host + ":" + port;
        }


        this.mongoClient = MongoClients.create(uri);
        this.mongoDatabase = mongoClient.getDatabase(getConfig().getString("MONGO.URI.DATABASE"));

        this.logger("Initialized MongoDB successfully!");
    }

    @SneakyThrows
    private void registerCommands() {
        for ( Class<?> clazz : ClassUtil.getClassesInPackage(this, "net.frozenorb.potpvp.command") ) {
            if (clazz.isInstance(DrinkProvider.class)) continue;

            Object instance = clazz.newInstance();
            String name = null;
            String[] aliases = null;

            for ( Method method : clazz.getMethods() ) {
                if (method.getName().equalsIgnoreCase("getCommandName")) {
                    name = (String) method.invoke(instance);
                } else if (method.getName().equalsIgnoreCase("getAliases")) {
                    aliases = (String[]) method.invoke(instance);
                }
            }

            if (name == null || aliases == null) continue;

            if (aliases.length != 0) {
                commandHandler.register(instance, name, aliases);
            } else {
                commandHandler.register(instance, name);
            }
        }
        commandHandler.registerCommands();
        this.logger("Registering commands...");
    }

    public void registerPermission() {
        commandHandler.registerPermissions();

        PluginManager pm = this.getServer().getPluginManager();
        pm.addPermission(new Permission("potpvp.toggleduels", PermissionDefault.OP));
        pm.addPermission(new Permission("potpvp.togglelightning", PermissionDefault.OP));
        pm.addPermission(new Permission("potpvp.silent", PermissionDefault.OP));

        this.logger("Registering permissions...");
    }

    public void logger(String message) {
        this.getServer().getConsoleSender().sendMessage(CC.translate("&c• " + message));
    }

    public void consoleLog(String string) {
        this.getServer().getConsoleSender().sendMessage(CC.translate(string));
    }

    // This is here because chunk snapshots are (still) being deserialized, and serialized sometimes.
    private static class ChunkSnapshotAdapter extends TypeAdapter<ChunkSnapshot> {

        @Override
        public ChunkSnapshot read(JsonReader arg0) {
            return null;
        }

        @Override
        public void write(JsonWriter arg0, ChunkSnapshot arg1) throws IOException {
            
        }
        
    }
}