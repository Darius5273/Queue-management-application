package Business_logic;

import Models.Client;
import Models.Server;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShortestTimeStrategy implements QueueAllocationStrategy {
    @Override
    public void addTask(List<Server> servers, Client client) {
        Server minServer = servers.get(0);
        for (Server server : servers) {
            if (server.getServerWaitingTime() < minServer.getServerWaitingTime()) {
                minServer = server;
            }
        }
        minServer.addClient(client);
    }
}

