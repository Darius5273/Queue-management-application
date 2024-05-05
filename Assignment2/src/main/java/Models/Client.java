package Models;

public class Client {
    private int id;
    private int timeArrival;
    private int serviceTime;

    public Client(int id, int timeArrival, int serviceTime) {
        this.id = id;
        this.timeArrival = timeArrival;
        this.serviceTime = serviceTime;
    }

    public int getId() {
        return id;
    }

    public int getTimeArrival() {
        return timeArrival;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimeArrival(int timeArrival) {
        this.timeArrival = timeArrival;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }
    @Override
    public String toString() {
        return "(" + id + ", " + timeArrival + ", " + serviceTime + ")";
    }
}
