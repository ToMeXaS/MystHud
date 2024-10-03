package lt.tomexas.mysthud;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import lt.tomexas.mysthud.dependencies.placeholders.MystHoldersExpansion;
import lt.tomexas.mysthud.dependencies.skills.PlayerSkillXpGainListener;
import lt.tomexas.mysthud.hud.HudManager;
import lt.tomexas.mysthud.playerfontimage.PlayerFontImage;
import lt.tomexas.mysthud.utils.Utilities;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.sql.SQLException;

public final class Main extends JavaPlugin implements Listener {

    private static Main instance;
    private BukkitAudiences audiences;
    private PlayerFontImage playerFontImage;
    private Database database;
    private Utilities utilities;
    private LiteCommands liteCommands;
    private Economy economy;
    private HudManager hudManager;

    public @Nonnull BukkitAudiences getAudience() {
        if (this.audiences == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.audiences;
    }

    public Main() {
        instance = this;
    }

    public static Main get() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (!checkForDependencies()) getServer().getPluginManager().disablePlugin(this);

        this.playerFontImage = PlayerFontImage.initialize(this);
        this.audiences = BukkitAudiences.create(this);
        this.setupDatabase();
        this.utilities = new Utilities(this);

        this.hudManager = new HudManager();
        this.hudManager.registerEvents();
        this.hudManager.registerTask();
        this.hudManager.parsedHudDisplays = hudManager.generateHudDisplays();

        getServer().getPluginManager().registerEvents(new PlayerSkillXpGainListener(this), this);

        this.liteCommands = LiteBukkitFactory.builder("MystHolders")
                .commands(new TestCommand(this))
                .build();

        new MystHoldersExpansion(this).register();

    }

    @Override
    public void onDisable() {
        if (this.audiences != null) {
            this.audiences.close();
            this.audiences = null;
        }

        try {
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
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
        economy = rsp.getProvider();
        return economy != null;
    }

    private void setupDatabase() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            database = new Database(getDataFolder().getAbsolutePath() + "/players.db");
        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().severe("Failed to connect to the database! " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private boolean checkForDependencies() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)  {
            getLogger().info("Could not find PlaceholderAPI! This plugin is required.");
            return false;
        }
        if (Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2") == null) {
            getLogger().info("Could not find PlaceholderAPI! This plugin is required.");
            return false;
        }
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            return false;
        }
        return true;
    }

    public Utilities getUtil() {
        return utilities;
    }

    public Database getDatabase() {
        return database;
    }

    public Economy getEconomy() {
        return economy;
    }

    public HudManager getHudManager() {
        return hudManager;
    }

    public PlayerFontImage getPlayerFontImage() {
        return playerFontImage;
    }
}
