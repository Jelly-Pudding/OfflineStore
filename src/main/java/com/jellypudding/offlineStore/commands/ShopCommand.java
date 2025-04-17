package com.jellypudding.offlineStore.commands;

import com.jellypudding.chromaTag.ChromaTag;
import com.jellypudding.offlineStore.OfflineStore;
import com.jellypudding.offlineStore.data.ColorOwnershipManager;
import com.jellypudding.simpleVote.TokenManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Renamed from ColorShopCommand
public class ShopCommand implements CommandExecutor, TabCompleter {

    private final OfflineStore plugin;

    // Copied from ChromaTag for utility - Maps lowercase name to hex string
    private static final Map<String, String> NAMED_COLOURS = new HashMap<>();
    static {
        NAMED_COLOURS.put("black", "#000000");
        NAMED_COLOURS.put("dark_blue", "#0000AA");
        NAMED_COLOURS.put("dark_green", "#00AA00");
        NAMED_COLOURS.put("dark_aqua", "#00AAAA");
        NAMED_COLOURS.put("dark_red", "#AA0000");
        NAMED_COLOURS.put("dark_purple", "#AA00AA");
        NAMED_COLOURS.put("gold", "#FFAA00");
        NAMED_COLOURS.put("gray", "#AAAAAA");
        NAMED_COLOURS.put("dark_gray", "#555555");
        NAMED_COLOURS.put("blue", "#5555FF");
        NAMED_COLOURS.put("green", "#55FF55");
        NAMED_COLOURS.put("aqua", "#55FFFF");
        NAMED_COLOURS.put("red", "#FF5555");
        NAMED_COLOURS.put("light_purple", "#FF55FF");
        NAMED_COLOURS.put("yellow", "#FFFF55");
        NAMED_COLOURS.put("white", "#FFFFFF");
    }

    // Define categories
    private static final List<String> CATEGORIES = List.of("colour");
    // Define actions per category
    private static final Map<String, List<String>> CATEGORY_ACTIONS = Map.of(
            "colour", List.of("list", "buy", "set", "reset")
            // Add other categories and their actions here later
    );

    public ShopCommand(OfflineStore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be run by a player.").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 1) {
            sendUsage(player);
            return true;
        }

        String category = args[0].toLowerCase();

        if (!CATEGORIES.contains(category)) {
            player.sendMessage(Component.text("Unknown shop category: " + category).color(NamedTextColor.RED));
            sendUsage(player);
            return true;
        }

        // Handle specific category logic
        if (category.equals("colour")) {
            handleColourCommand(player, args);
        } else {
            // Handle other categories later
            player.sendMessage(Component.text("Category '" + category + "' not yet implemented.").color(NamedTextColor.YELLOW));
        }

        return true;
    }

    private void handleColourCommand(Player player, String[] args) {
        if (args.length < 2) {
            sendColourUsage(player);
            return;
        }

        String action = args[1].toLowerCase();
        String value = args.length > 2 ? args[2] : null;

        switch (action) {
            case "list":
                listColours(player);
                break;
            case "buy":
                if (value == null) {
                    player.sendMessage(Component.text("Usage: /shop colour buy <colourName>").color(NamedTextColor.RED));
                    return;
                }
                buyColour(player, value);
                break;
            case "set":
                 if (value == null) {
                    player.sendMessage(Component.text("Usage: /shop colour set <colourName>").color(NamedTextColor.RED));
                    return;
                }
                setColour(player, value);
                break;
            case "reset":
                resetColour(player);
                break;
            default:
                player.sendMessage(Component.text("Unknown action for colour category: " + action).color(NamedTextColor.RED));
                sendColourUsage(player);
                break;
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage(Component.text("Usage: /shop <category> <action> [value]").color(NamedTextColor.RED));
        player.sendMessage(Component.text("Available categories: " + String.join(", ", CATEGORIES)).color(NamedTextColor.YELLOW));
    }

     private void sendColourUsage(Player player) {
        player.sendMessage(Component.text("Colour Shop Usage:").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text(" /shop colour list").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text(" /shop colour buy <colourName>").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text(" /shop colour set <colourName>").color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text(" /shop colour reset").color(NamedTextColor.YELLOW));
    }

    // Utility to get TextColor from string name/hex
    private TextColor getColourFromString(String colourString) {
        if (colourString == null) {
            return null;
        }
        String lowerCaseColourString = colourString.toLowerCase();
        String hexString = NAMED_COLOURS.getOrDefault(lowerCaseColourString, lowerCaseColourString);

        if (!hexString.startsWith("#")) {
            hexString = "#" + hexString;
        }
        try {
            return TextColor.fromHexString(hexString);
        } catch (IllegalArgumentException e) {
            NamedTextColor named = NamedTextColor.NAMES.value(lowerCaseColourString);
            if (named != null) {
                return named;
            }
            plugin.getLogger().warning("Invalid colour string format: " + colourString);
            return null;
        }
    }

    // Utility to find the name of a TextColor
    private String getColourName(TextColor colour) {
        if (colour == null) return "default";
        // Check standard named colours first
        String name = NamedTextColor.NAMES.key(NamedTextColor.nearestTo(colour)); // Find closest standard name
        if (name != null && TextColor.fromHexString(NAMED_COLOURS.get(name)).equals(colour)) {
             return name; // Return standard name if exact match
        }
        // Check our map (which includes named colours but ensures exact match)
        for (Map.Entry<String, String> entry : NAMED_COLOURS.entrySet()) {
            try {
                if (TextColor.fromHexString(entry.getValue()).equals(colour)) {
                    return entry.getKey();
                }
            } catch (IllegalArgumentException ignored) {}
        }
        // Fallback to hex if no name found
        return colour.asHexString();
    }

    private void listColours(Player player) {
        Map<String, Integer> costs = plugin.getColourCosts();
        TokenManager tokenManager = plugin.getTokenManager();
        ChromaTag chromaTag = plugin.getChromaTag();
        ColorOwnershipManager ownershipManager = plugin.getColourOwnershipManager();

        if (tokenManager == null || ownershipManager == null) {
            player.sendMessage(Component.text("Error: A required system (Tokens or Ownership) is unavailable.").color(NamedTextColor.RED));
            return;
        }
        if (chromaTag == null) {
             player.sendMessage(Component.text("Warning: ChromaTag system is unavailable. Cannot show current colour.").color(NamedTextColor.YELLOW));
        }

        int currentTokens = tokenManager.getTokens(player.getUniqueId());
        TextColor currentColour = (chromaTag != null) ? chromaTag.getPlayerColor(player.getUniqueId()) : null;
        String currentColourName = getColourName(currentColour); // Get the name
        Set<String> ownedColours = ownershipManager.getOwnedColors(player.getUniqueId());

        player.sendMessage(Component.text("--- Colour Shop ---").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("Your tokens: " + currentTokens).color(NamedTextColor.YELLOW));
        if (currentColour != null) {
             // Display the name, coloured
             player.sendMessage(Component.text("Your current colour: ").color(NamedTextColor.YELLOW)
                     .append(Component.text(currentColourName, currentColour)));
        } else {
            player.sendMessage(Component.text("Your current colour: default").color(NamedTextColor.YELLOW));
        }
         player.sendMessage(Component.text("Click item to buy/set. Hover for details.").color(NamedTextColor.GRAY));

        if (costs.isEmpty()) {
            player.sendMessage(Component.text("No colours are currently available in the shop.").color(NamedTextColor.YELLOW));
            return;
        }

        costs.entrySet().stream()
             .sorted(Map.Entry.comparingByKey())
             .forEach(entry -> {
                 String colourName = entry.getKey().toLowerCase();
                 int cost = entry.getValue();
                 TextColor colour = getColourFromString(colourName);
                 boolean isOwned = ownedColours.contains(colourName) || cost <= 0;

                 if (colour == null) {
                     plugin.getLogger().warning("Skipping invalid colour in config: " + colourName);
                     return;
                 }

                 TextComponent.Builder message = Component.text();
                 message.append(Component.text("  - ").color(NamedTextColor.GRAY));
                 message.append(Component.text(colourName).color(colour)); // Colour name coloured

                 Component hoverText;
                 ClickEvent clickAction = null;

                 if (isOwned) {
                     message.append(Component.text(" (Owned)").color(NamedTextColor.GREEN));
                     hoverText = Component.text("Click to set your colour to ").color(NamedTextColor.GREEN)
                                          .append(Component.text(colourName, colour));
                     clickAction = ClickEvent.runCommand("/shop colour set " + colourName);
                 } else {
                     message.append(Component.text(": " + cost + " tokens").color(NamedTextColor.AQUA));
                     if (currentTokens < cost) {
                         message.append(Component.text(" (Insufficient Funds)").color(NamedTextColor.RED));
                         hoverText = Component.text("Requires " + cost + " tokens.").color(NamedTextColor.RED);
                     } else {
                         hoverText = Component.text("Click to buy " + colourName).color(colour)
                                              .append(Component.text(" for " + cost + " tokens.").color(NamedTextColor.AQUA));
                         clickAction = ClickEvent.suggestCommand("/shop colour buy " + colourName);
                     }
                 }

                 message.hoverEvent(HoverEvent.showText(hoverText));
                 if (clickAction != null) {
                     message.clickEvent(clickAction);
                 }

                 player.sendMessage(message.build());
             });

        player.sendMessage(Component.text("-------------------").color(NamedTextColor.GOLD));
    }

    private void buyColour(Player player, String colourName) {
        TokenManager tokenManager = plugin.getTokenManager();
        ChromaTag chromaTag = plugin.getChromaTag();
        ColorOwnershipManager ownershipManager = plugin.getColourOwnershipManager();
        Map<String, Integer> costs = plugin.getColourCosts();
        String lowerCaseColourName = colourName.toLowerCase();

        if (tokenManager == null || chromaTag == null || ownershipManager == null) {
            player.sendMessage(Component.text("Error: A required system is unavailable.").color(NamedTextColor.RED));
            return;
        }

        if (!costs.containsKey(lowerCaseColourName)) {
            player.sendMessage(Component.text("Unknown colour: " + colourName).color(NamedTextColor.RED));
            return;
        }
        int cost = costs.get(lowerCaseColourName);

        TextColor colour = getColourFromString(lowerCaseColourName);
        if (colour == null) {
            player.sendMessage(Component.text("Invalid colour format for: " + colourName).color(NamedTextColor.RED));
            return;
        }

        if (cost <= 0 || ownershipManager.hasColor(player.getUniqueId(), lowerCaseColourName)) {
            player.sendMessage(Component.text("You already own this colour or it's free. Use ").color(NamedTextColor.YELLOW)
                             .append(Component.text("/shop colour set " + lowerCaseColourName).decorate(TextDecoration.UNDERLINED)
                                      .clickEvent(ClickEvent.suggestCommand("/shop colour set " + lowerCaseColourName)))
                             .append(Component.text(" to use it.")));
            return;
        }

        int currentTokens = tokenManager.getTokens(player.getUniqueId());
        if (currentTokens < cost) {
            player.sendMessage(Component.text("You don't have enough tokens! Need " + cost + ", have " + currentTokens + ".").color(NamedTextColor.RED));
            return;
        }

        if (tokenManager.removeTokens(player.getUniqueId(), cost)) {
            if (ownershipManager.addColorOwnership(player.getUniqueId(), lowerCaseColourName)) {
                boolean colourSetSuccess = chromaTag.setPlayerColor(player.getUniqueId(), colour);
                if (colourSetSuccess) {
                    player.sendMessage(Component.text("Purchased and set name colour to ").color(NamedTextColor.GREEN)
                                            .append(Component.text(lowerCaseColourName, colour))
                                            .append(Component.text(" for " + cost + " tokens!")));
                } else {
                    player.sendMessage(Component.text("Purchase successful (" + cost + " tokens deducted), but failed to apply colour immediately.").color(NamedTextColor.YELLOW)
                                     .append(Component.text(" You now own ").append(Component.text(lowerCaseColourName, colour)))
                                     .append(Component.text(". Use /shop colour set " + lowerCaseColourName + " to apply it.")));
                    plugin.getLogger().warning("Failed to set colour for " + player.getName() + " via ChromaTag after purchase and ownership save.");
                }
            } else {
                tokenManager.addTokens(player.getUniqueId(), cost);
                player.sendMessage(Component.text("Purchase failed due to a data saving error. Tokens refunded.").color(NamedTextColor.RED));
                plugin.getLogger().severe("Failed to save colour ownership for " + player.getName() + " after tokens were removed. Refunding.");
            }
        } else {
            player.sendMessage(Component.text("Failed to process token transaction.").color(NamedTextColor.RED));
        }
    }

    private void setColour(Player player, String colourName) {
        ChromaTag chromaTag = plugin.getChromaTag();
        ColorOwnershipManager ownershipManager = plugin.getColourOwnershipManager();
        String lowerCaseColourName = colourName.toLowerCase();

        if (chromaTag == null || ownershipManager == null) {
            player.sendMessage(Component.text("Error: A required system is unavailable.").color(NamedTextColor.RED));
            return;
        }

        int cost = plugin.getColourCosts().getOrDefault(lowerCaseColourName, -1);
        if (cost == -1) {
             player.sendMessage(Component.text("Unknown colour: " + colourName).color(NamedTextColor.RED));
             return;
        }

        if (cost > 0 && !ownershipManager.hasColor(player.getUniqueId(), lowerCaseColourName)) {
            player.sendMessage(Component.text("You don't own the colour '" + lowerCaseColourName + "'. Use /shop colour buy " + lowerCaseColourName + " first.").color(NamedTextColor.RED));
            return;
        }

        TextColor colour = getColourFromString(lowerCaseColourName);
        if (colour == null) {
            player.sendMessage(Component.text("Invalid colour format: " + colourName).color(NamedTextColor.RED));
            return;
        }

        boolean success = chromaTag.setPlayerColor(player.getUniqueId(), colour);
        if (success) {
            player.sendMessage(Component.text("Name colour set to ").color(NamedTextColor.GREEN)
                                    .append(Component.text(lowerCaseColourName, colour)));
        } else {
            player.sendMessage(Component.text("Failed to set colour. Please contact an admin.").color(NamedTextColor.RED));
        }
    }

    private void resetColour(Player player) {
        ChromaTag chromaTag = plugin.getChromaTag();

        if (chromaTag == null) {
            player.sendMessage(Component.text("Error: ChromaTag system is unavailable.").color(NamedTextColor.RED));
            return;
        }

        boolean success = chromaTag.resetPlayerColor(player.getUniqueId());
        if (success) {
            player.sendMessage(Component.text("Your name colour has been reset to default.").color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("You didn't have a custom colour set.").color(NamedTextColor.YELLOW));
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();
        String currentArg = args[args.length - 1].toLowerCase();

        // Suggest Category (arg 1)
        if (args.length == 1) {
            for (String category : CATEGORIES) {
                if (category.startsWith(currentArg)) {
                    completions.add(category);
                }
            }
            return completions;
        }

        String category = args[0].toLowerCase();

        // Suggest Action (arg 2) based on Category
        if (args.length == 2) {
            List<String> actions = CATEGORY_ACTIONS.get(category);
            if (actions != null) {
                for (String action : actions) {
                    if (action.startsWith(currentArg)) {
                        completions.add(action);
                    }
                }
            }
             return completions;
        }

        // Suggest Value (arg 3) based on Category and Action
        if (args.length == 3) {
            String action = args[1].toLowerCase();

            if (category.equals("colour")) {
                Set<String> possibleColours = plugin.getColourCosts().keySet();
                ColorOwnershipManager ownershipManager = plugin.getColourOwnershipManager();

                if (ownershipManager != null) {
                    if (action.equals("buy")) {
                         Set<String> owned = ownershipManager.getOwnedColors(player.getUniqueId());
                         possibleColours.stream()
                             .filter(c -> plugin.getColourCosts().get(c) > 0)
                             .filter(c -> !owned.contains(c))
                             .filter(c -> c.startsWith(currentArg))
                             .forEach(completions::add);
                    } else if (action.equals("set")) {
                         Set<String> owned = ownershipManager.getOwnedColors(player.getUniqueId());
                         plugin.getColourCosts().forEach((colourName, cost) -> {
                             if (cost <= 0) owned.add(colourName);
                         });
                         owned.stream()
                             .filter(c -> c.startsWith(currentArg))
                             .forEach(completions::add);
                    }
                }
                Collections.sort(completions);
                return completions;
            }
        }

        return Collections.emptyList();
    }
} 