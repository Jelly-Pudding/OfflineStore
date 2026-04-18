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

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements CommandExecutor {

    private static final int TOTAL_PAGES = 3;

    private final OfflineStore plugin;

    public HelpCommand(OfflineStore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int page = 1;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                page = 1;
            }
        }
        if (page < 1) page = 1;
        if (page > TOTAL_PAGES) page = TOTAL_PAGES;

        sendPage(sender, page);
        return true;
    }

    private void sendPage(CommandSender sender, int page) {
        sender.sendMessage(Component.text("═════════").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("Help - Page " + page + " of " + TOTAL_PAGES).color(NamedTextColor.GOLD));

        if (page == 1) {
            sender.sendMessage(Component.text("Available Commands:").color(NamedTextColor.AQUA));
        }

        switch (page) {
            case 1 -> sendPageOne(sender);
            case 2 -> sendPageTwo(sender);
            case 3 -> sendPageThree(sender);
        }

        sender.sendMessage(Component.empty());
        sendNavigation(sender, page);
        sender.sendMessage(Component.text("═════════").color(NamedTextColor.GOLD));
    }

    private void sendPageOne(CommandSender sender) {
        sender.sendMessage(Component.text("• Spawn: ").color(NamedTextColor.WHITE)
                .append(Component.text("/spawn").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• Home commands: ").color(NamedTextColor.WHITE)
                .append(Component.text("/sethome").color(NamedTextColor.GREEN))
                .append(Component.text(" and ").color(NamedTextColor.WHITE))
                .append(Component.text("/home").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• Teleport requests: ").color(NamedTextColor.WHITE)
                .append(Component.text("/tpa").color(NamedTextColor.GREEN))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/tpaccept").color(NamedTextColor.GREEN))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/tpdeny").color(NamedTextColor.RED))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/tpacancel").color(NamedTextColor.RED)));

        sender.sendMessage(Component.text("• Vote to skip the day: ").color(NamedTextColor.WHITE)
                .append(Component.text("/goodnight").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• Kill yourself: ").color(NamedTextColor.WHITE)
                .append(Component.text("/kill").color(NamedTextColor.RED)));

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
    }

    private void sendPageTwo(CommandSender sender) {
        sender.sendMessage(Component.text("• Player stats: ").color(NamedTextColor.WHITE)
                .append(Component.text("/firstseen").color(NamedTextColor.GREEN))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/lastseen").color(NamedTextColor.GREEN))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/timeplayed").color(NamedTextColor.GREEN))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/kills").color(NamedTextColor.GREEN))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/deaths").color(NamedTextColor.GREEN))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/chatter").color(NamedTextColor.GREEN))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/rep").color(NamedTextColor.GREEN))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/leaderboard").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• Give reputation: ").color(NamedTextColor.WHITE)
                .append(Component.text("/goodrep <player>").color(NamedTextColor.GREEN))
                .append(Component.text(" or ").color(NamedTextColor.WHITE))
                .append(Component.text("/badrep <player>").color(NamedTextColor.RED)));

        sender.sendMessage(Component.text("• LifeSteal commands: ").color(NamedTextColor.WHITE)
                .append(Component.text("/withdrawheart").color(NamedTextColor.GREEN))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/heartrecipe").color(NamedTextColor.GREEN))
                .append(Component.text(", ").color(NamedTextColor.WHITE))
                .append(Component.text("/shrine unban <name>").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• Shop for coloured names and custom MOTDs with tokens: ").color(NamedTextColor.WHITE)
                .append(Component.text("/shop").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• Get tokens by voting: ").color(NamedTextColor.WHITE)
                .append(Component.text("/vote").color(NamedTextColor.GREEN)));
    }

    private void sendPageThree(CommandSender sender) {
        sender.sendMessage(Component.text("• Browse active bounties: ").color(NamedTextColor.WHITE)
                .append(Component.text("/bounty").color(NamedTextColor.GREEN))
                .append(Component.text(" or ").color(NamedTextColor.WHITE))
                .append(Component.text("/bounty list").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• Place a bounty on a player: ").color(NamedTextColor.WHITE)
                .append(Component.text("/bounty place <player>").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• View bounties on a player: ").color(NamedTextColor.WHITE)
                .append(Component.text("/bounty view <player>").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• Bounties you've placed: ").color(NamedTextColor.WHITE)
                .append(Component.text("/bounty mine").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• Bounties placed on you: ").color(NamedTextColor.WHITE)
                .append(Component.text("/bounty me").color(NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("• Cancel a bounty: ").color(NamedTextColor.WHITE)
                .append(Component.text("/bounty cancel <id>").color(NamedTextColor.RED)));

        sender.sendMessage(Component.text("• Claim returned items: ").color(NamedTextColor.WHITE)
                .append(Component.text("/bounty claimreturns").color(NamedTextColor.GREEN)));
    }

    private void sendNavigation(CommandSender sender, int page) {
        List<Component> parts = new ArrayList<>();

        if (page > 1) {
            parts.add(Component.text("« Previous")
                    .color(NamedTextColor.YELLOW)
                    .decorate(TextDecoration.UNDERLINED)
                    .clickEvent(ClickEvent.runCommand("/help " + (page - 1)))
                    .hoverEvent(HoverEvent.showText(Component.text("Go to page " + (page - 1)).color(NamedTextColor.GRAY))));
        } else {
            parts.add(Component.text("« Previous").color(NamedTextColor.DARK_GRAY));
        }

        parts.add(Component.text("   Page " + page + "/" + TOTAL_PAGES + "   ").color(NamedTextColor.GRAY));

        if (page < TOTAL_PAGES) {
            parts.add(Component.text("Next »")
                    .color(NamedTextColor.YELLOW)
                    .decorate(TextDecoration.UNDERLINED)
                    .clickEvent(ClickEvent.runCommand("/help " + (page + 1)))
                    .hoverEvent(HoverEvent.showText(Component.text("Go to page " + (page + 1)).color(NamedTextColor.GRAY))));
        } else {
            parts.add(Component.text("Next »").color(NamedTextColor.DARK_GRAY));
        }

        Component line = Component.empty();
        for (Component part : parts) {
            line = line.append(part);
        }
        sender.sendMessage(line);
    }
}
