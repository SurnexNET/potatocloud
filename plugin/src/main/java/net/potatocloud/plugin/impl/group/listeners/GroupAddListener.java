package net.potatocloud.plugin.impl.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.group.GroupAddPacket;
import net.potatocloud.plugin.impl.group.ServiceGroupManagerImpl;

@RequiredArgsConstructor
public class GroupAddListener implements PacketListener<GroupAddPacket> {

    private final ServiceGroupManagerImpl groupManager;

    @Override
    public void onPacket(NetworkConnection connection, GroupAddPacket packet) {
        if (groupManager.existsServiceGroup(packet.getName())) {
            return;
        }

        final ServiceGroupImpl group = new ServiceGroupImpl(
                packet.getName(),
                packet.getPlatformName(),
                packet.getMinOnlineCount(),
                packet.getMaxOnlineCount(),
                packet.getMaxPlayers(),
                packet.getMaxMemory(),
                packet.isFallback(),
                packet.isStatic(),
                packet.getStartPriority(),
                packet.getStartPercentage(),
                packet.getJavaCommand(),
                packet.getCustomJvmFlags(),
                packet.getProperties()
        );

        groupManager.addServiceGroup(group);
    }
}
