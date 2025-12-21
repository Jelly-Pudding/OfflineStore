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

public class PluginsCommand implements CommandExecutor {

    private static final String WEBSITE_URL = "https://www.minecraftoffline.net/";

    public PluginsCommand(OfflineStore plugin) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Component message = Component.text("Plugin info can be found on the About page at ")
                .color(NamedTextColor.GRAY)
                .append(Component.text("minecraftoffline.net")
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(WEBSITE_URL))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to open website").color(NamedTextColor.YELLOW))));

        sender.sendMessage(message);
        return true;
    }
}
