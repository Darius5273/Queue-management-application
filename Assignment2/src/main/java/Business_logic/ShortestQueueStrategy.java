package Business_logic;

import Models.Client;
import Models.Server;

import java.util.List;

public class ShortestQueueStrategy implements QueueAllocationStrategy {
    @Override
    public void addTask(List<Server> servers, Client client) {
        Server shortestQueueServer = servers.get(0);
        for (Server server : servers) {
            if (server.getWaitingQueue().size() < shortestQueueServer.getWaitingQueue().size()) {
                shortestQueueServer = server;
            }
        }
        shortestQueueServer.addClient(client);
    }
}
