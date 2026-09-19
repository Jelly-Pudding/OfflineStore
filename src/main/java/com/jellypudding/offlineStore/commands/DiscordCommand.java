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
import org.jetbrains.annotations.NotNull;

public class DiscordCommand implements CommandExecutor {

    public static final String DISCORD_URL = "https://discord.gg/a83FESY3jF";

    public DiscordCommand(OfflineStore plugin) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(Component.text("Join the MinecraftOffline Discord").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("Chat with other players, get support, and hear about updates.").color(NamedTextColor.YELLOW));

        Component link = Component.text("🔗 Click here to join our Discord server")
                .color(NamedTextColor.LIGHT_PURPLE)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl(DISCORD_URL))
                .hoverEvent(HoverEvent.showText(Component.text("Click to open the Discord invite in your browser").color(NamedTextColor.YELLOW)));
        sender.sendMessage(link);

        return true;
    }
}
