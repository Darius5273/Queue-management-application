package Models;


import Business_logic.SimulationManager;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Callable<Integer> {
    private BlockingQueue<Client> waiting;
    private AtomicInteger totalWaitingTime;
    private int serverId;
    private AtomicInteger serverWaitingTime;
    private SimulationManager simulationManager;

    public Server(int serverId, SimulationManager simulationManager) {
        waiting = new LinkedBlockingQueue<>();
        this.serverId = serverId;
        this.serverWaitingTime = new AtomicInteger(0);
        this.totalWaitingTime = new AtomicInteger(0);
        this.simulationManager = simulationManager;
    }

    public int getServerId() {
        return serverId;
    }

    public int getServerWaitingTime() {
        return serverWaitingTime.get();
    }

    public BlockingQueue<Client> getWaitingQueue() {
        return waiting;
    }

    @Override
    public Integer call() throws Exception {

        while (!simulationManager.getSimulationEnd() || !waiting.isEmpty()) {

            if (waiting.isEmpty()) {
                continue;
            }

            Client client = waiting.peek();
            if(simulationManager.getCurrentTime()>=client.getTimeArrival()){
                Thread.sleep(1000);
                client.setServiceTime(client.getServiceTime() - 1);
                serverWaitingTime.decrementAndGet();
            }
            else
                Thread.sleep(1000);

            if (client.getServiceTime() == 0) {
                waiting.poll();
                totalWaitingTime.addAndGet(simulationManager.getCurrentTime()-client.getTimeArrival());
            }
        }
        return totalWaitingTime.get();
    }

    public void addClient(Client client) {
        waiting.offer(client);
        serverWaitingTime.addAndGet(client.getServiceTime());
    }

}
