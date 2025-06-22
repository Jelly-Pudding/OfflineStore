package com.jellypudding.offlineStore.listeners;

import com.jellypudding.offlineStore.OfflineStore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final OfflineStore plugin;

    public PlayerJoinListener(OfflineStore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if this is the player's first time joining
        if (!player.hasPlayedBefore()) {
            // Send welcome message after a short delay to ensure player is fully loaded
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                sendWelcomeMessage(player);
            }, 20L); // Delay by 1 second (20 ticks)
        }
    }

    private void sendWelcomeMessage(Player player) {
        // Send welcome header
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("Welcome to the Server " + player.getName()).color(NamedTextColor.YELLOW));
        player.sendMessage(Component.empty());

        // Server information
        player.sendMessage(Component.text("This is an ").color(NamedTextColor.WHITE)
                .append(Component.text("Anarchy Lifesteal Server").color(NamedTextColor.RED)));
        
        player.sendMessage(Component.text("• ").color(NamedTextColor.WHITE)
                .append(Component.text("No rules").color(NamedTextColor.WHITE)));
        
        player.sendMessage(Component.text("• ").color(NamedTextColor.WHITE)
                .append(Component.text("Losing all your hearts results in a ").color(NamedTextColor.WHITE))
                .append(Component.text("ban").color(NamedTextColor.RED)));
        
        player.sendMessage(Component.text("• ").color(NamedTextColor.WHITE)
                .append(Component.text("There is some anticheat to prevent extreme movements").color(NamedTextColor.WHITE)));
        
        player.sendMessage(Component.empty());
        
        // Discord link
        Component discordLink = Component.text("🔗 Join our Discord")
                .color(NamedTextColor.LIGHT_PURPLE)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl("https://discord.gg/a83FESY3jF"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to join our Discord").color(NamedTextColor.YELLOW)));
        player.sendMessage(discordLink);
        
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Use ").color(NamedTextColor.WHITE)
                .append(Component.text("/help").color(NamedTextColor.GREEN))
                .append(Component.text(" for commands").color(NamedTextColor.WHITE)));
        
        player.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
    }
} 