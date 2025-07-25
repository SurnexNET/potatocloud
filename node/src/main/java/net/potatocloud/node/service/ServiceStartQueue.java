package net.potatocloud.node.service;

import net.potatocloud.api.event.events.property.PropertyChangedEvent;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.node.Node;

import java.util.Comparator;
import java.util.List;

public class ServiceStartQueue extends Thread {

    private final ServiceGroupManager groupManager;
    private final ServiceManager serviceManager;
    private final CloudPlayerManager playerManager;

    private boolean running = true;

    public ServiceStartQueue(ServiceGroupManager groupManager, ServiceManager serviceManager, CloudPlayerManager playerManager) {
        this.groupManager = groupManager;
        this.serviceManager = serviceManager;
        this.playerManager = playerManager;

        Node.getInstance().getEventManager().on(PropertyChangedEvent.class, event -> {
            if (!event.getPropertyData().getName().equals(Property.GAME_STATE.getName())) {
                return;
            }

            if (event.getNewValue() == null) {
                return;
            }

            if (!event.getNewValue().equals("INGAME")) {
                return;
            }

            final Service service = Node.getInstance().getServiceManager().getService(event.getHolderName());
            if (service == null) {
                return;
            }

            serviceManager.startService(service.getServiceGroup());
        });

        setName("ServiceStartQueue");
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            try {
                final List<ServiceGroup> groups = groupManager.getAllServiceGroups().stream()
                        .sorted(Comparator.comparingInt(ServiceGroup::getStartPriority).reversed())
                        .toList();

                for (final ServiceGroup group : groups) {
                    if (!groupManager.existsServiceGroup(group.getName())) {
                        continue;
                    }

                    final List<Service> services = serviceManager.getAllServices().stream().
                            filter(s -> s.getServiceGroup().getName().equals(group.getName()))
                            .filter(s -> s.getStatus() == ServiceStatus.RUNNING || s.getStatus() == ServiceStatus.STARTING || s.getStatus() == ServiceStatus.STOPPING)
                            .toList();

                    if (services.size() < group.getMinOnlineCount()) {
                        serviceManager.startServices(group, group.getMinOnlineCount() - services.size());
                        continue;
                    }

                    final int onlinePlayersInGroup = playerManager.getOnlinePlayersByGroup(group).size();
                    final int maxPlayersInGroup = services.stream().mapToInt(Service::getMaxPlayers).sum();

                    if (maxPlayersInGroup == 0) {
                        continue;
                    }

                    int requiredServices = group.getMinOnlineCount();

                    if (services.size() >= requiredServices) {
                        requiredServices = services.size();
                    }

                    final double onlinePercentage = (double) onlinePlayersInGroup / maxPlayersInGroup * 100;

                    if (onlinePercentage >= group.getStartPercentage()) {
                        requiredServices = Math.min(requiredServices + 1, group.getMaxOnlineCount());
                    }

                    if (requiredServices - services.size() > 0) {
                        serviceManager.startServices(group, requiredServices - services.size());
                    }
                }

                Thread.sleep(1000);

            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void close() {
        running = false;
        interrupt();
    }
}
