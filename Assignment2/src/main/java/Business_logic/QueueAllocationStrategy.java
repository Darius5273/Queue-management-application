package Business_logic;

import Models.Client;
import Models.Server;

import java.util.List;

public interface QueueAllocationStrategy {
    void addTask(List<Server> servers, Client client);
}