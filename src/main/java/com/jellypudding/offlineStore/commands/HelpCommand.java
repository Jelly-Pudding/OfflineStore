package com.jellypudding.offlineStore.commands;

import com.jellypudding.offlineStore.OfflineStore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements TabExecutor {

    private static final String[] PAGE_TITLES = {
            "Getting Around & Chat",
            "Stats, Reputation & Shop",
            "Bounties"
    };
    private static final int TOTAL_PAGES = PAGE_TITLES.length;

    private static final String WEBSITE_URL = "https://www.minecraftoffline.net";

    private final OfflineStore plugin;

    public HelpCommand(OfflineStore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (int i = 1; i <= TOTAL_PAGES; i++) {
                String pageNumber = String.valueOf(i);
                if (pageNumber.startsWith(args[0])) {
                    completions.add(pageNumber);
                }
            }
        }
        return completions;
    }

    private void sendPage(CommandSender sender, int page) {
        sender.sendMessage(Component.empty());
        sendHeader(sender, page);
        sender.sendMessage(Component.text("Click any command to put it in your chat box.")
                .color(NamedTextColor.DARK_GRAY)
                .decorate(TextDecoration.ITALIC));
        sender.sendMessage(Component.empty());

        switch (page) {
            case 1 -> sendPageOne(sender);
            case 2 -> sendPageTwo(sender);
            case 3 -> sendPageThree(sender);
        }

        sender.sendMessage(Component.empty());
        sendNavigation(sender, page);
    }

    private void sendHeader(CommandSender sender, int page) {
        sender.sendMessage(Component.text("═══ ").color(NamedTextColor.GOLD)
                .append(Component.text("Help").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .append(Component.text(" • ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(PAGE_TITLES[page - 1]).color(NamedTextColor.AQUA))
                .append(Component.text(" (" + page + "/" + TOTAL_PAGES + ")").color(NamedTextColor.GRAY))
                .append(Component.text(" ═══").color(NamedTextColor.GOLD)));
    }

    private void sendPageOne(CommandSender sender) {
        section(sender, "Getting around");
        entry(sender, "Go to spawn", cmd("/spawn"));
        entry(sender, "Set and use your home", cmd("/sethome"), cmd("/home"));
        entry(sender, "Teleport to a player", cmd("/tpa <player>"));
        entry(sender, "Ask a player to teleport to you", cmd("/tpahere <player>"));
        entry(sender, "Respond to a request", cmd("/tpaccept"), danger("/tpdeny"), danger("/tpacancel"));
        entry(sender, "Vote to skip the night", cmd("/goodnight"));
        entry(sender, "Kill yourself", danger("/kill"));

        sender.sendMessage(Component.empty());
        section(sender, "Chat");
        entry(sender, "Talk if your account can't chat", cmd("/c <message>"));
        entry(sender, "Private message a player", cmd("/msg <player> <message>"), cmd("/r <message>"));
        entry(sender, "Do an emote", cmd("/me <action>"));

        sender.sendMessage(Component.empty());
        section(sender, "Community");
        entry(sender, "Join our Discord", cmd("/discord"));
        entry(sender, "Support the server", cmd("/donate"));

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("🔗 Discord")
                .color(NamedTextColor.LIGHT_PURPLE)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl(DiscordCommand.DISCORD_URL))
                .hoverEvent(HoverEvent.showText(Component.text("Click to open the Discord invite").color(NamedTextColor.YELLOW)))
                .append(Component.text("   ").decoration(TextDecoration.UNDERLINED, false))
                .append(Component.text("🌐 Website")
                        .color(NamedTextColor.GREEN)
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(WEBSITE_URL))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to open minecraftoffline.net").color(NamedTextColor.YELLOW)))));
    }

    private void sendPageTwo(CommandSender sender) {
        section(sender, "Player stats");
        entry(sender, "When a player was first and last seen", cmd("/firstseen <player>"), cmd("/lastseen <player>"));
        entry(sender, "Time played, kills and deaths", cmd("/timeplayed"), cmd("/kills"), cmd("/deaths"));
        entry(sender, "Chat activity and reputation", cmd("/chatter"), cmd("/rep"));
        entry(sender, "Top players", cmd("/leaderboard"));

        sender.sendMessage(Component.empty());
        section(sender, "Reputation");
        entry(sender, "Give a player good or bad rep", cmd("/goodrep <player>"), danger("/badrep <player>"));

        sender.sendMessage(Component.empty());
        section(sender, "Lifesteal");
        entry(sender, "Turn a heart into an item", cmd("/withdrawheart"));
        entry(sender, "See how to craft a heart", cmd("/heartrecipe"));
        entry(sender, "Bring back a player who lost all hearts", cmd("/shrine unban <player>"));

        sender.sendMessage(Component.empty());
        section(sender, "Shop");
        entry(sender, "Buy name colours, hearts and custom MOTDs with tokens", cmd("/shop"));
        entry(sender, "Earn tokens by voting", cmd("/vote"));
    }

    private void sendPageThree(CommandSender sender) {
        section(sender, "Bounties");
        entry(sender, "Browse active bounties", cmd("/bounty"), cmd("/bounty list"));
        entry(sender, "Place a bounty on a player", cmd("/bounty place <player>"));
        entry(sender, "View bounties on a player", cmd("/bounty view <player>"));
        entry(sender, "Bounties you've placed", cmd("/bounty mine"));
        entry(sender, "Bounties placed on you", cmd("/bounty me"));
        entry(sender, "Cancel a bounty", danger("/bounty cancel <id>"));
        entry(sender, "Claim returned items", cmd("/bounty claimreturns"));
    }

    private void sendNavigation(CommandSender sender, int page) {
        Component line = Component.empty();

        if (page > 1) {
            line = line.append(pageLink("« Previous", page - 1));
        } else {
            line = line.append(Component.text("« Previous").color(NamedTextColor.DARK_GRAY));
        }

        line = line.append(Component.text("   "));
        for (int i = 1; i <= TOTAL_PAGES; i++) {
            if (i == page) {
                line = line.append(Component.text("[" + i + "]").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            } else {
                line = line.append(pageLink("[" + i + "]", i));
            }
            if (i < TOTAL_PAGES) {
                line = line.append(Component.text(" "));
            }
        }
        line = line.append(Component.text("   "));

        if (page < TOTAL_PAGES) {
            line = line.append(pageLink("Next »", page + 1));
        } else {
            line = line.append(Component.text("Next »").color(NamedTextColor.DARK_GRAY));
        }

        sender.sendMessage(line);
    }

    private static void section(CommandSender sender, String title) {
        sender.sendMessage(Component.text(title).color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
    }

    private static void entry(CommandSender sender, String description, Component... commands) {
        Component line = Component.text(" • ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text(description + ": ").color(NamedTextColor.WHITE));

        for (int i = 0; i < commands.length; i++) {
            if (i > 0) {
                line = line.append(Component.text(", ").color(NamedTextColor.GRAY));
            }
            line = line.append(commands[i]);
        }

        sender.sendMessage(line);
    }

    private static Component cmd(String command) {
        return clickable(command, NamedTextColor.GREEN);
    }

    private static Component danger(String command) {
        return clickable(command, NamedTextColor.RED);
    }

    private static Component clickable(String command, NamedTextColor colour) {
        return Component.text(command)
                .color(colour)
                .clickEvent(ClickEvent.suggestCommand(typeablePart(command)))
                .hoverEvent(HoverEvent.showText(Component.text("Click to type ").color(NamedTextColor.GRAY)
                        .append(Component.text(command).color(colour))));
    }

    private static String typeablePart(String command) {
        int placeholder = command.indexOf('<');
        if (placeholder < 0) {
            return command;
        }
        return command.substring(0, placeholder).stripTrailing() + " ";
    }

    private static Component pageLink(String text, int targetPage) {
        return Component.text(text)
                .color(NamedTextColor.YELLOW)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.runCommand("/help " + targetPage))
                .hoverEvent(HoverEvent.showText(Component.text("Page " + targetPage + ": ").color(NamedTextColor.GRAY)
                        .append(Component.text(PAGE_TITLES[targetPage - 1]).color(NamedTextColor.AQUA))));
    }
}
