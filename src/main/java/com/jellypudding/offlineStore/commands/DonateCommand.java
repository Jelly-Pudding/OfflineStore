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

public class DonateCommand implements CommandExecutor {

    private final OfflineStore plugin;

    public DonateCommand(OfflineStore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(Component.text("Support MinecraftOffline").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("The server costs £40 per month to run.").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("You can help by purchasing vote tokens:").color(NamedTextColor.AQUA));

        Component link = Component.text("Open the Token Store")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl("https://www.minecraftoffline.net/tokens/store"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to open in your browser").color(NamedTextColor.YELLOW)));
        sender.sendMessage(link);

        return true;
    }
}


