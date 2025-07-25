package net.potatocloud.core.networking.packets.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

@Data
@AllArgsConstructor
public class ServiceExecuteCommandPacket implements Packet {

    private String serviceName;
    private String command;

    @Override
    public String getType() {
        return PacketTypes.SERVICE_EXECUTE_COMMAND;
    }
}
