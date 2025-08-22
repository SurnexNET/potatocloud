package net.potatocloud.api.service;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.property.PropertyHolder;
import net.potatocloud.api.utils.TimeFormatter;

import java.util.Set;
import java.util.stream.Collectors;

public interface Service extends PropertyHolder {

    String getName();

    int getServiceId();

    default boolean isOnline() {
        return getStatus() == ServiceStatus.RUNNING;
    }

    ServiceStatus getStatus();

    void setStatus(ServiceStatus status);

    long getStartTimestamp();

    default String getFormattedStartTimestamp() {
        return TimeFormatter.formatAsDateAndTime(getStartTimestamp());
    }

    default long getUptime() {
        return System.currentTimeMillis() - getStartTimestamp();
    }

    default String getFormattedUptime() {
        return TimeFormatter.formatAsDuration(getUptime());
    }

    default Set<CloudPlayer> getOnlinePlayers() {
        return CloudAPI.getInstance().getPlayerManager().getOnlinePlayers().stream()
                .filter(player -> getName().equals(player.getConnectedServiceName()))
                .collect(Collectors.toSet());
    }

    default int getOnlinePlayerCount() {
        return getOnlinePlayers().size();
    }

    default boolean isFull() {
        return getOnlinePlayerCount() >= getMaxPlayers();
    }

    int getMaxPlayers();

    void setMaxPlayers(int maxPlayers);

    int getUsedMemory();

    int getPort();

    ServiceGroup getServiceGroup();

    void shutdown();

    boolean executeCommand(String command);

    void copy(String template, String filter);

    default void copy(String template) {
        copy(template, "");
    }

    default void update() {
        CloudAPI.getInstance().getServiceManager().updateService(this);
    }
}
