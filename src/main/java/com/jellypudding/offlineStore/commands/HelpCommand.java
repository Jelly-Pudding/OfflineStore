package com.jellypudding.offlineStore.commands;

import com.jellypudding.offlineStore.OfflineStore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    
    private final OfflineStore plugin;
    
    public HelpCommand(OfflineStore plugin) {
        this.plugin = plugin;
    }
    
        @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("      HELP & COMMANDS").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.empty());
        
        sender.sendMessage(Component.text("Available Commands:").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.empty());
        
        sender.sendMessage(Component.text("• Home commands: ").color(NamedTextColor.WHITE)
                .append(Component.text("/sethome").color(NamedTextColor.GREEN))
                .append(Component.text(" and ").color(NamedTextColor.WHITE))
                .append(Component.text("/home").color(NamedTextColor.GREEN)));
        
        sender.sendMessage(Component.text("• Teleport requests: ").color(NamedTextColor.WHITE)
                .append(Component.text("/tpa").color(NamedTextColor.GREEN))
                .append(Component.text(" and ").color(NamedTextColor.WHITE))
                .append(Component.text("/tpaccept").color(NamedTextColor.GREEN)));
        
        sender.sendMessage(Component.text("• Vote to skip the day: ").color(NamedTextColor.WHITE)
                .append(Component.text("/goodnight").color(NamedTextColor.GREEN)));
        
        sender.sendMessage(Component.text("• Shop to buy hearts and cosmetics with tokens: ").color(NamedTextColor.WHITE)
                .append(Component.text("/shop").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• Get tokens by voting: ").color(NamedTextColor.WHITE)
                .append(Component.text("/vote").color(NamedTextColor.GREEN)));
        
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("Links:").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.empty());
        
        Component discordLink = Component.text("🔗 Click Here to Join Our Discord Server")
                .color(NamedTextColor.LIGHT_PURPLE)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl("https://discord.gg/a83FESY3jF"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to open Discord invite").color(NamedTextColor.YELLOW)));
        sender.sendMessage(discordLink);
        
        sender.sendMessage(Component.empty());
        
        Component websiteLink = Component.text("🌐 Visit Our Website")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl("https://www.minecraftoffline.net"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to open website").color(NamedTextColor.YELLOW)));
        sender.sendMessage(websiteLink);
        
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("═══════════════════════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.empty());
        
        return true;
    }
} 