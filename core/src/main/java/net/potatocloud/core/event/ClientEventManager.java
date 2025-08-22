package net.potatocloud.core.event;

import net.potatocloud.api.event.Event;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.packets.EventPacket;

public class ClientEventManager extends BaseEventManager {

    private final NetworkClient client;

    public ClientEventManager(NetworkClient client) {
        this.client = client;

        client.registerPacketListener(PacketIds.EVENT, (NetworkConnection connection, EventPacket packet) -> {
            final Event event = EventSerializer.deserialize(packet);
            if (event != null) {
                callLocal(event);
            }
        });
    }

    @Override
    public <T extends Event> void call(T event) {
        callLocal(event);
        client.send(EventSerializer.serialize(event));
    }
}
