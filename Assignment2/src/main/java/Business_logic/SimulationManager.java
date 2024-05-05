package Business_logic;

import GUI.Controller;
import Models.Client;
import Models.Server;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimulationManager implements Runnable {
    private int N;
    private int Q;
    private int simulationTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int minServiceTime;
    private int maxServiceTime;
    private List<Client> clients;
    private List<Server> servers;
    private ExecutorService executor;
    private AtomicBoolean simulationEnd;
    private List<Future<Integer>> serverResults;
    private Controller controller;
    private int peakTime;
    private int maxQueueSizeSum;
    private int currentTime;
    private QueueAllocationStrategy strategy;

    public SimulationManager(Controller controller) {
        clients = new ArrayList<>();
        servers = new ArrayList<>();
        simulationEnd = new AtomicBoolean(false);
        serverResults = new ArrayList<>();
        currentTime = 0;
        maxQueueSizeSum=0;
        this.controller = controller;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public boolean getSimulationEnd() {
        return simulationEnd.get();
    }
    public void setQueueAllocationStrategy(QueueAllocationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setInputParameters(int N, int Q, int simulationTime, int minArrivalTime, int maxArrivalTime,
                                   int minServiceTime, int maxServiceTime) {
        this.N = N;
        this.Q = Q;
        this.simulationTime = simulationTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
        executor = Executors.newFixedThreadPool(Q);
    }

    @Override
    public void run() {
        clients.addAll(generateClients());
        double averageServiceTime = computeAvgServiceTime();
        initializeServers();
        deleteContentsOfLogFile();

        while (!simulationEnd.get()) {
            distributeClients();

            if (currentTime >= simulationTime) {
                if(noClients())
                    simulationEnd.set(true);
            }

            computePeakTime();
            updateUIAndWriteToLogFile(currentTime, clients, servers);
            currentTime++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        double averageWaitingTime = computeAvgWaitingTime();
        updateUIAndWriteToLogFile(averageWaitingTime,averageServiceTime,peakTime);
    }

    private List<Client> generateClients() {
        List<Client> clients = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < N; i++) {
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int serviceTime = random.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;

            clients.add(new Client(i,arrivalTime, serviceTime));
        }
        clients.sort(Comparator.comparingInt(Client::getTimeArrival));
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).setId(i + 1);
        }
        return clients;
    }

    private void initializeServers() {
        for (int i = 0; i < Q; i++) {
            Server server = new Server(i + 1, this);
            servers.add(server);
            serverResults.add(executor.submit(server));
        }
    }

    private boolean noClients()
    {
        for (Server server : servers) {
             if(!server.getWaitingQueue().isEmpty())
                 return false;
        }
        return clients.isEmpty();
    }
    private void distributeClients() {
        Iterator<Client> iterator = clients.iterator();
        while (iterator.hasNext()) {
            Client client = iterator.next();
            if (client.getTimeArrival() == currentTime) {
                strategy.addTask(servers,client);
                iterator.remove();
            }
        }
    }

    private double computeAvgWaitingTime() {

            double totalWaitingTime = 0;
            for (Future<Integer> result : serverResults) {
                int serverAverageWaitingTime;
                try {
                    serverAverageWaitingTime = result.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                if(serverAverageWaitingTime!=0) {
                    totalWaitingTime += serverAverageWaitingTime;
                }
            }
        return totalWaitingTime / N;
    }
    private double computeAvgServiceTime() {

        double totalServiceTime = 0;
        for (Client client : clients) {
                totalServiceTime += client.getServiceTime();
        }
        return totalServiceTime / N;
    }


    public void computePeakTime() {

            int queueSizeSum = 0;
            for (Server server : servers) {
                queueSizeSum += server.getWaitingQueue().size();
            }
            if (queueSizeSum > maxQueueSizeSum) {
                maxQueueSizeSum = queueSizeSum;
                peakTime = currentTime;
            }
    }

    public void updateUIAndWriteToLogFile(int currentTime, List<Client> clients, List<Server> servers)
    {
        String output=generateOutput(currentTime, clients, servers);
        controller.updateTextArea(output);
        writeToLogFile(output);
    }

    public void updateUIAndWriteToLogFile(Double averageWaitingTime, Double averageServiceTime,int peakTime)
    {
        String output=generateOutput(averageWaitingTime,averageServiceTime,peakTime);
        controller.updateTextArea(output);
        writeToLogFile(output);
    }

    private String generateOutput(Double averageWaitingTime,Double averageServiceTime,int peakTime)
    {
        return "Average Waiting Time: "+averageWaitingTime+"\n"+
                "Average Service Time: "+averageServiceTime+"\n"+
                "Peak Time: "+peakTime+"\n";
    }
    private void deleteContentsOfLogFile()
    {
        try (FileWriter writer = new FileWriter("test1.txt", false)) {
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    private void writeToLogFile(String output) {
        try (FileWriter writer = new FileWriter("test1.txt", true)) {
            writer.write(output);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    private String generateOutput(int currentTime, List<Client> clients, List<Server> servers) {
        String output = "Time " + currentTime + "\n";

        output += "Waiting clients: ";
        if (clients.isEmpty()) {
            output += "none";
        } else {
            for (Client client : clients) {
                output += client.toString() + "; ";
            }
        }
        output += "\n";

        for (int i = 0; i < servers.size(); i++) {
            Server server = servers.get(i);
            output += "Queue " + (i + 1) + ": ";
            if (server.getWaitingQueue().isEmpty()) {
                output += "closed";
            } else {
                output += "(";
                for (Client client : server.getWaitingQueue()) {
                    output += client.toString() + "; ";
                }
                output = output.substring(0, output.length() - 2);
                output += ")";
            }
            output += "\n";
        }
        output += "\n";
        return output;
    }
}
