package com.jellypudding.offlineStore.commands;

import com.jellypudding.chromaTag.ChromaTag;
import com.jellypudding.offlineStore.OfflineStore;
import com.jellypudding.offlineStore.data.ColorOwnershipManager;
import com.jellypudding.simpleLifesteal.SimpleLifesteal;
import com.jellypudding.simpleLifesteal.managers.PlayerDataManager;
import com.jellypudding.simpleVote.TokenManager;
import com.jellypudding.simpleHome.SimpleHome;
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

public class ShopCommand implements CommandExecutor, TabCompleter {

    private final OfflineStore plugin;

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

    private static final List<String> CATEGORIES = List.of("colour", "heart", "home");
    private static final Map<String, List<String>> CATEGORY_ACTIONS = Map.of(
            "colour", List.of("list", "buy", "set", "reset"),
            "heart", List.of("info", "buy"),
            "home", List.of("info", "buy")
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

        if (category.equals("colour")) {
            handleColourCommand(player, args);
        } else if (category.equals("heart")) {
            handleHeartCommand(player, args);
        } else if (category.equals("home")) {
            handleHomeCommand(player, args);
        } else {
            player.sendMessage(Component.text("Category '" + category + "' not yet implemented.").color(NamedTextColor.YELLOW));
        }

        return true;
    }

    private void handleColourCommand(Player player, String[] args) {
        ChromaTag chromaTag = plugin.getChromaTag();
        if (chromaTag == null) {
            player.sendMessage(Component.text("Colour features are currently unavailable (ChromaTag plugin missing or disabled).").color(NamedTextColor.RED));
            return;
        }

        if (args.length < 2) {
            sendColourUsage(player);
            return;
        }

        String action = args[1].toLowerCase();
        String value = args.length > 2 ? args[2] : null;

        switch (action) {
            case "list":
                listColours(player, chromaTag);
                break;
            case "buy":
                if (value == null) {
                    player.sendMessage(Component.text("Usage: /shop colour buy <colourName>").color(NamedTextColor.RED));
                    return;
                }
                buyColour(player, value, chromaTag);
                break;
            case "set":
                 if (value == null) {
                    player.sendMessage(Component.text("Usage: /shop colour set <colourName>").color(NamedTextColor.RED));
                    return;
                }
                setColour(player, value, chromaTag);
                break;
            case "reset":
                resetColour(player, chromaTag);
                break;
            default:
                player.sendMessage(Component.text("Unknown action for colour category: " + action).color(NamedTextColor.RED));
                sendColourUsage(player);
                break;
        }
    }

    private void handleHeartCommand(Player player, String[] args) {
        SimpleLifesteal slApi = plugin.getSimpleLifesteal();
        TokenManager tokenManager = plugin.getTokenManager();

        if (slApi == null) {
            player.sendMessage(Component.text("Error: SimpleLifesteal integration is disabled or not loaded.").color(NamedTextColor.RED));
            return;
        }
         if (tokenManager == null) {
            player.sendMessage(Component.text("Error: Token system is unavailable.").color(NamedTextColor.RED));
            return;
        }

        String action = (args.length > 1) ? args[1].toLowerCase() : "info";

        switch (action) {
            case "buy":
                buyHeart(player, slApi, tokenManager, plugin.getHeartCost());
                break;
            case "info":
                showHeartInfo(player, slApi, plugin.getHeartCost());
                break;
            default:
                 player.sendMessage(Component.text("Unknown action for heart category: " + action).color(NamedTextColor.RED));
                 sendHeartUsage(player, plugin.getHeartCost());
                 break;
        }
    }

    private void handleHomeCommand(Player player, String[] args) {
        SimpleHome shApi = plugin.getSimpleHome();
        TokenManager tokenManager = plugin.getTokenManager();

        if (shApi == null) {
            player.sendMessage(Component.text("Error: SimpleHome integration is disabled or not loaded.").color(NamedTextColor.RED));
            return;
        }
         if (tokenManager == null) {
            player.sendMessage(Component.text("Error: Token system is unavailable.").color(NamedTextColor.RED));
            return;
        }

        String action = (args.length > 1) ? args[1].toLowerCase() : "info";

        switch (action) {
            case "buy":
                buyHomeSlot(player, shApi, tokenManager);
                break;
            case "info":
                showHomeInfo(player, shApi);
                break;
            default:
                 player.sendMessage(Component.text("Unknown action for home category: " + action).color(NamedTextColor.RED));
                 sendHomeUsage(player);
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

    private void sendHeartUsage(Player player, int heartCost) {
        player.sendMessage(Component.text("Heart Shop Usage:").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text(" /shop heart info").color(NamedTextColor.YELLOW).append(Component.text(" - Show current/max hearts")));
        player.sendMessage(Component.text(" /shop heart buy").color(NamedTextColor.YELLOW).append(Component.text(" - Buy 1 heart for " + heartCost + " tokens")));
    }

    private void sendHomeUsage(Player player) {
        SimpleHome shApi = plugin.getSimpleHome();
        Map<Integer, Integer> homeCosts = plugin.getHomeSlotCosts();
        if (shApi == null || homeCosts == null) {
             player.sendMessage(Component.text("Home shop features are currently unavailable.").color(NamedTextColor.RED));
             return;
        }

        int currentLimit = shApi.getHomeLimit(player.getUniqueId());
        int maxLimit = 5;

        player.sendMessage(Component.text("Home Slot Shop Usage:").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text(" /shop home info").color(NamedTextColor.YELLOW).append(Component.text(" - Show current/max homes")));

        if (currentLimit < maxLimit) {
            int nextSlot = currentLimit + 1;
            Integer nextCost = homeCosts.get(nextSlot);
            if (nextCost != null) {
                player.sendMessage(Component.text(" /shop home buy").color(NamedTextColor.YELLOW)
                         .append(Component.text(" - Buy slot #" + nextSlot + " for " + nextCost + " tokens")));
            } else {
                 player.sendMessage(Component.text(" /shop home buy").color(NamedTextColor.YELLOW)
                         .append(Component.text(" - Next slot cost not configured.").color(NamedTextColor.GRAY)));
            }
        } else {
             player.sendMessage(Component.text("You have reached the maximum home limit ("+ maxLimit +").").color(NamedTextColor.GREEN));
        }
    }

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

    private String getColourName(TextColor colour) {
        if (colour == null) return "default";
        String name = NamedTextColor.NAMES.key(NamedTextColor.nearestTo(colour));
        if (name != null && TextColor.fromHexString(NAMED_COLOURS.get(name)).equals(colour)) {
             return name;
        }
        for (Map.Entry<String, String> entry : NAMED_COLOURS.entrySet()) {
            try {
                if (TextColor.fromHexString(entry.getValue()).equals(colour)) {
                    return entry.getKey();
                }
            } catch (IllegalArgumentException ignored) {}
        }
        return colour.asHexString();
    }

    private void listColours(Player player, @NotNull ChromaTag chromaTag) {
        Map<String, Integer> costs = plugin.getColourCosts();
        TokenManager tokenManager = plugin.getTokenManager();
        ColorOwnershipManager ownershipManager = plugin.getColourOwnershipManager();

        if (tokenManager == null || ownershipManager == null) {
            player.sendMessage(Component.text("Error: A required system (Tokens or Ownership) is unavailable.").color(NamedTextColor.RED));
            return;
        }

        int currentTokens = tokenManager.getTokens(player.getUniqueId());
        TextColor currentColour = chromaTag.getPlayerColor(player.getUniqueId());
        String currentColourName = getColourName(currentColour);
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

    private void buyColour(Player player, String colourName, @NotNull ChromaTag chromaTag) {
        TokenManager tokenManager = plugin.getTokenManager();
        ColorOwnershipManager ownershipManager = plugin.getColourOwnershipManager();
        Map<String, Integer> costs = plugin.getColourCosts();
        String lowerCaseColourName = colourName.toLowerCase();

        if (tokenManager == null || ownershipManager == null) {
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

    private void setColour(Player player, String colourName, @NotNull ChromaTag chromaTag) {
        ColorOwnershipManager ownershipManager = plugin.getColourOwnershipManager();
        String lowerCaseColourName = colourName.toLowerCase();

        if (ownershipManager == null) {
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

    private void resetColour(Player player, @NotNull ChromaTag chromaTag) {
        boolean success = chromaTag.resetPlayerColor(player.getUniqueId());
        if (success) {
            player.sendMessage(Component.text("Your name colour has been reset to default.").color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("You didn't have a custom colour set.").color(NamedTextColor.YELLOW));
        }
    }

    private void showHeartInfo(Player player, SimpleLifesteal slApi, int heartCost) {
        PlayerDataManager playerDataManager = slApi.getPlayerDataManager();
        if (playerDataManager == null) {
             player.sendMessage(Component.text("Error: Could not access PlayerDataManager.").color(NamedTextColor.RED));
             return;
        }
        // Load data async and show info in callback
        playerDataManager.loadPlayerData(player, currentHearts -> {
            int maxHearts = slApi.getMaxHearts();
            player.sendMessage(Component.text("--- Heart Info ---").color(NamedTextColor.GOLD));
            player.sendMessage(Component.text("Your current hearts: " + currentHearts + " / " + maxHearts).color(NamedTextColor.YELLOW));
            if (currentHearts < maxHearts) {
                player.sendMessage(Component.text("You can buy more hearts!").color(NamedTextColor.GREEN));
                player.sendMessage(Component.text("Use ")
                        .append(Component.text("/shop heart buy", NamedTextColor.AQUA).clickEvent(ClickEvent.suggestCommand("/shop heart buy")))
                        .append(Component.text(" to purchase 1 heart for " + heartCost + " tokens.")));
            } else {
                player.sendMessage(Component.text("You are at the maximum number of hearts!").color(NamedTextColor.GREEN));
            }
            player.sendMessage(Component.text("------------------").color(NamedTextColor.GOLD));
        });
    }

     private void buyHeart(Player player, SimpleLifesteal slApi, TokenManager tokenManager, int heartCost) {
         PlayerDataManager playerDataManager = slApi.getPlayerDataManager();
         if (playerDataManager == null) {
             player.sendMessage(Component.text("Error: Could not access PlayerDataManager.").color(NamedTextColor.RED));
             return;
         }

         int currentHearts = playerDataManager.getPlayerHearts(player.getUniqueId());
         int maxHearts = slApi.getMaxHearts();

         if (currentHearts >= maxHearts) {
             player.sendMessage(Component.text("You are already at the maximum number of hearts (" + maxHearts + ").").color(NamedTextColor.RED));
             return;
         }

         int currentTokens = tokenManager.getTokens(player.getUniqueId());
         if (currentTokens < heartCost) {
             player.sendMessage(Component.text("You don't have enough tokens! Need " + heartCost + ", have " + currentTokens + ".").color(NamedTextColor.RED));
             return;
         }

         if (tokenManager.removeTokens(player.getUniqueId(), heartCost)) {
             boolean heartAdded = slApi.addHearts(player.getUniqueId(), 1);

             if (heartAdded) {
                 int newHearts = playerDataManager.getPlayerHearts(player.getUniqueId());
                 player.sendMessage(Component.text("Purchased 1 heart for " + heartCost + " tokens! You now have " + newHearts + " hearts.").color(NamedTextColor.GREEN));
             } else {
                 tokenManager.addTokens(player.getUniqueId(), heartCost);
                 player.sendMessage(Component.text("Failed to add heart after purchase (perhaps already at max?). Tokens refunded.").color(NamedTextColor.RED));
                 plugin.getLogger().warning("Failed to add heart for " + player.getName() + " via SimpleLifesteal API after tokens were removed. Refunding.");
             }
         } else {
             player.sendMessage(Component.text("Failed to process token transaction.").color(NamedTextColor.RED));
         }
     }

    private void showHomeInfo(Player player, SimpleHome shApi) {
        UUID playerUUID = player.getUniqueId();
        int currentLimit = shApi.getHomeLimit(playerUUID);
        int currentHomes = shApi.getCurrentHomeCount(playerUUID);
        int maxLimit = 5;
        Map<Integer, Integer> homeCosts = plugin.getHomeSlotCosts();

        player.sendMessage(Component.text("--- Home Slot Info ---").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("Homes Set: " + currentHomes + " / " + currentLimit).color(NamedTextColor.YELLOW));
        player.sendMessage(Component.text("Max Possible Limit: " + maxLimit).color(NamedTextColor.YELLOW));

        if (currentLimit < maxLimit) {
            int nextSlot = currentLimit + 1;
            Integer cost = homeCosts.get(nextSlot);
            if (cost != null) {
                player.sendMessage(Component.text("Next slot (#" + nextSlot + ") cost: " + cost + " tokens.").color(NamedTextColor.GREEN));
                player.sendMessage(Component.text("Use ")
                        .append(Component.text("/shop home buy", NamedTextColor.AQUA)
                                .clickEvent(ClickEvent.suggestCommand("/shop home buy"))
                                .hoverEvent(HoverEvent.showText(Component.text("Click to buy slot #" + nextSlot + " for " + cost + " tokens"))))
                        .append(Component.text(" to purchase.")));
            } else {
                player.sendMessage(Component.text("Cost for the next home slot (#" + nextSlot + ") is not configured.").color(NamedTextColor.GRAY));
            }
        } else {
            player.sendMessage(Component.text("You have reached the maximum home limit!").color(NamedTextColor.GREEN));
        }
        player.sendMessage(Component.text("---------------------").color(NamedTextColor.GOLD));
    }

    private void buyHomeSlot(Player player, SimpleHome shApi, TokenManager tokenManager) {
        UUID playerUUID = player.getUniqueId();
        int currentLimit = shApi.getHomeLimit(playerUUID);
        int maxLimit = 5;
        Map<Integer, Integer> homeCosts = plugin.getHomeSlotCosts();

        if (currentLimit >= maxLimit) {
            player.sendMessage(Component.text("You already have the maximum number of home slots (" + maxLimit + ").").color(NamedTextColor.RED));
            return;
        }

        int nextSlot = currentLimit + 1;
        Integer cost = homeCosts.get(nextSlot);

        if (cost == null) {
            player.sendMessage(Component.text("The cost for the next home slot (#" + nextSlot + ") is not configured. Please contact an admin.").color(NamedTextColor.RED));
            plugin.getLogger().warning("Player " + player.getName() + " tried to buy home slot " + nextSlot + " but no cost was found in config.");
            return;
        }
        if (cost < 0) {
             player.sendMessage(Component.text("Invalid cost configured for home slot #" + nextSlot + ". Please contact an admin.").color(NamedTextColor.RED));
            return;
        }

        int currentTokens = tokenManager.getTokens(playerUUID);
        if (currentTokens < cost) {
            player.sendMessage(Component.text("You don't have enough tokens! Need " + cost + ", have " + currentTokens + ".").color(NamedTextColor.RED));
            return;
        }

        // Attempt transaction
        if (tokenManager.removeTokens(playerUUID, cost)) {
            boolean limitIncreased = shApi.increaseHomeLimit(playerUUID);

            if (limitIncreased) {
                int newLimit = shApi.getHomeLimit(playerUUID); // Re-fetch to confirm
                player.sendMessage(Component.text("Purchased home slot #" + nextSlot + " for " + cost + " tokens! Your new home limit is " + newLimit + ".").color(NamedTextColor.GREEN));
            } else {
                // Refund tokens if increasing limit failed
                tokenManager.addTokens(playerUUID, cost);
                player.sendMessage(Component.text("Failed to increase home limit after purchase (perhaps you already reached the max?). Tokens refunded.").color(NamedTextColor.RED));
                plugin.getLogger().warning("Failed to increase home limit for " + player.getName() + " via SimpleHome API after tokens were removed. Refunding.");
            }
        } else {
            player.sendMessage(Component.text("Failed to process token transaction. Please try again.").color(NamedTextColor.RED));
            plugin.getLogger().warning("Token removal failed for " + player.getName() + " attempting to buy home slot " + nextSlot);
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
            } else if (category.equals("heart")) {
                 return Collections.emptyList();
            } else if (category.equals("home")) {
                return Collections.emptyList();
            }
        }

        return Collections.emptyList();
    }
} 