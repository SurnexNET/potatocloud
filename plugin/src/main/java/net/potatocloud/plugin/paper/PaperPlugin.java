package net.potatocloud.plugin.paper;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.service.Service;
import net.potatocloud.core.networking.packets.service.ServiceStartedPacket;
import net.potatocloud.plugin.impl.PluginCloudAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperPlugin extends JavaPlugin implements Listener {

    private PluginCloudAPI api;
    private Service currentService;

    @Override
    public void onLoad() {
        api = new PluginCloudAPI();
    }

    @Override
    public void onEnable() {
        initCurrentService();

        getServer().getPluginManager().registerEvents(this, this);
    }

    private void initCurrentService() {
        currentService = CloudAPI.getInstance().getServiceManager().getCurrentService();
        // service manager is still null or the services have not finished loading
        if (currentService == null) {
            // retry after 1 second
            getServer().getScheduler().runTaskLater(this, this::initCurrentService, 20L);
            return;
        }

        api.getClient().send(new ServiceStartedPacket(currentService.getName()));
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (currentService == null) {
            return;
        }
        event.setMaxPlayers(currentService.getMaxPlayers());
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (currentService == null) {
            return;
        }
        if (getServer().getOnlinePlayers().size() < currentService.getMaxPlayers()) {
            return;
        }
        if (event.getPlayer().hasPermission("potatocloud.maxplayers.bypass")) {
            return;
        }
        event.disallow(PlayerLoginEvent.Result.KICK_FULL, MiniMessage.miniMessage().deserialize("<red>The server has reached its maximum players!"));
    }

    @Override
    public void onDisable() {
        api.shutdown();
    }
}
