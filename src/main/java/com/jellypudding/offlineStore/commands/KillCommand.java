package com.jellypudding.offlineStore.commands;

import com.jellypudding.offlineStore.OfflineStore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KillCommand implements CommandExecutor {

    private final OfflineStore plugin;

    public KillCommand(OfflineStore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be run by a player.").color(NamedTextColor.RED));
            return true;
        }

        player.setHealth(0.0);
        player.sendMessage(Component.text("You have killed yourself.").color(NamedTextColor.GRAY));

        return true;
    }
}
