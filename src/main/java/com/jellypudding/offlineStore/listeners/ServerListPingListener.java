package com.jellypudding.offlineStore.listeners;

import com.jellypudding.offlineStore.OfflineStore;
import com.jellypudding.offlineStore.data.MotdManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingListener implements Listener {

    private final OfflineStore plugin;

    public ServerListPingListener(OfflineStore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        MotdManager motdManager = plugin.getMotdManager();

        if (motdManager == null) {
            return;
        }

        String randomMotd = motdManager.getRandomMotd();
        event.motd(LegacyComponentSerializer.legacySection().deserialize(randomMotd));

    }
}
