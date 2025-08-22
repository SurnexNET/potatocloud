package net.potatocloud.api.service;

import net.potatocloud.api.group.ServiceGroup;

import java.util.List;

public interface ServiceManager {

    Service getService(String serviceName);

    List<Service> getAllServices();

    default List<Service> getOnlineServices() {
        return getAllServices().stream().filter(Service::isOnline).toList();
    }

    default List<Service> getAllServices(String groupName) {
        return getAllServices().stream().filter(service -> service.getServiceGroup().getName().equals(groupName)).toList();
    }

    default List<Service> getOnlineServices(String groupName) {
        return getOnlineServices().stream().filter(service -> service.getServiceGroup().getName().equals(groupName)).toList();
    }

    void updateService(Service service);

    void startService(String groupName);

    default void startService(ServiceGroup group) {
        startService(group.getName());
    }

    void startServices(String groupName, int count);

    default void startServices(ServiceGroup group, int count) {
        startServices(group.getName(), count);
    }

    Service getCurrentService();

}
