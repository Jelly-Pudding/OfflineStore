package com.jellypudding.offlineStore;

import com.jellypudding.chromaTag.ChromaTag;
import com.jellypudding.offlineStore.commands.ShopCommand;
import com.jellypudding.offlineStore.data.ColorOwnershipManager;
import com.jellypudding.simpleLifesteal.SimpleLifesteal;
import com.jellypudding.simpleVote.SimpleVote;
import com.jellypudding.simpleVote.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class OfflineStore extends JavaPlugin {

    private TokenManager tokenManager;
    private ChromaTag chromaTag;
    private ColorOwnershipManager colourOwnershipManager;
    private SimpleLifesteal simpleLifesteal;
    private final Map<String, Integer> colourCosts = new HashMap<>();
    private int heartCost;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfiguration();

        // Initialise data managers
        this.colourOwnershipManager = new ColorOwnershipManager(this);

        // Hook into SimpleVote
        Plugin simpleVotePlugin = Bukkit.getPluginManager().getPlugin("SimpleVote");
        if (simpleVotePlugin instanceof SimpleVote && simpleVotePlugin.isEnabled()) {
            this.tokenManager = ((SimpleVote) simpleVotePlugin).getTokenManager();
            getLogger().info("Successfully hooked into SimpleVote.");
        } else {
            getLogger().severe("SimpleVote plugin not found or not enabled! Disabling OfflineStore.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Hook into ChromaTag
        Plugin chromaTagPlugin = Bukkit.getPluginManager().getPlugin("ChromaTag");
        if (chromaTagPlugin instanceof ChromaTag && chromaTagPlugin.isEnabled()) {
            this.chromaTag = (ChromaTag) chromaTagPlugin;
            getLogger().info("Successfully hooked into ChromaTag.");
        } else {
            getLogger().warning("ChromaTag plugin not found or not enabled! Colour purchasing/setting will be unavailable.");
            this.chromaTag = null;
        }

        // Hook into SimpleLifesteal
        Plugin simpleLifestealPlugin = Bukkit.getPluginManager().getPlugin("SimpleLifesteal");
        if (simpleLifestealPlugin instanceof SimpleLifesteal && simpleLifestealPlugin.isEnabled()) {
            this.simpleLifesteal = (SimpleLifesteal) simpleLifestealPlugin;
            getLogger().info("Successfully hooked into SimpleLifesteal.");
        } else {
            getLogger().warning("SimpleLifesteal plugin not found or not enabled! Heart purchasing will be unavailable.");
            this.simpleLifesteal = null;
        }

        // Register commands
        ShopCommand shopCommand = new ShopCommand(this);
        getCommand("shop").setExecutor(shopCommand);
        getCommand("shop").setTabCompleter(shopCommand);

        getLogger().info("OfflineStore has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (colourOwnershipManager != null) {
            colourOwnershipManager.closeConnection();
        }
        getLogger().info("OfflineStore has been disabled.");
    }

    private void loadConfiguration() {
        // Ensure config is loaded/reloaded
        reloadConfig();

        ConfigurationSection costsSection = getConfig().getConfigurationSection("colour_costs");
        if (costsSection != null) {
            colourCosts.clear(); // Clear previous costs if reloading
            for (String colourName : costsSection.getKeys(false)) {
                int cost = costsSection.getInt(colourName, -1); // Default to -1 if not a valid int
                if (cost >= 0) { // Only add non-negative costs
                    colourCosts.put(colourName.toLowerCase(), cost);
                    getLogger().info("Loaded colour cost: " + colourName + " = " + cost);
                } else {
                    getLogger().warning("Invalid cost for colour '" + colourName + "' in config.yml. Skipping.");
                }
            }
            getLogger().info("Loaded " + colourCosts.size() + " colour costs from config.yml");
        } else {
            getLogger().warning("Could not find 'colour_costs' section in config.yml. No colours will be available in the shop.");
        }

        // Load heart cost
        this.heartCost = getConfig().getInt("heart_cost", 5); // Default to 5 if missing/invalid
        if (this.heartCost < 0) {
             getLogger().warning("Invalid heart_cost in config.yml (must be non-negative). Using default value 5.");
             this.heartCost = 5;
        }
        getLogger().info("Loaded heart cost: " + this.heartCost);
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public ChromaTag getChromaTag() {
        return chromaTag;
    }

    public ColorOwnershipManager getColourOwnershipManager() {
        return colourOwnershipManager;
    }

    public SimpleLifesteal getSimpleLifesteal() {
        return simpleLifesteal;
    }

    public Map<String, Integer> getColourCosts() {
        return colourCosts;
    }

    public int getHeartCost() {
        return heartCost;
    }
}
