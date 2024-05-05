package GUI;

import Business_logic.ShortestQueueStrategy;
import Business_logic.ShortestTimeStrategy;
import Business_logic.SimulationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextField clientsTf;

    @FXML
    private TextField queuesTf;

    @FXML
    private Button start;

    @FXML
    private ComboBox<String> strategyCb;

    @FXML
    private TextField tMaxATf;

    @FXML
    private TextField tMaxSTf;

    @FXML
    private TextField tMinATf;

    @FXML
    private TextField tMinSTf;

    @FXML
    private TextField tSimTf;

    @FXML
    private TextArea textArea;
    private SimulationManager simulationManager;

    @FXML
    private void initialize() {
        start.setOnAction(event -> startSimulation());
        ObservableList<String> items = FXCollections.observableArrayList(
                "Shortest time",
                "Shortest queue"
        );
        strategyCb.setItems(items);

        strategyCb.getSelectionModel().selectFirst();

        strategyCb.setOnAction(event -> changeStrategy());
    }

    private void changeStrategy()
    {
        String option = strategyCb.getValue();
        if(option.equals("Shortest time"))
            simulationManager.setQueueAllocationStrategy(new ShortestTimeStrategy());
        else
            simulationManager.setQueueAllocationStrategy(new ShortestQueueStrategy());
    }
    private void startSimulation() {
        textArea.clear();
        String clientsStr = clientsTf.getText();
        String queuesStr = queuesTf.getText();
        String tMaxAStr = tMaxATf.getText();
        String tMaxSStr = tMaxSTf.getText();
        String tMinAStr = tMinATf.getText();
        String tMinSStr = tMinSTf.getText();
        String tSimStr = tSimTf.getText();

        if (!isValidInteger(clientsStr) || !isValidInteger(queuesStr) || !isValidInteger(tSimStr) ||
                !isValidInteger(tMinAStr) || !isValidInteger(tMaxAStr) || !isValidInteger(tMinSStr) ||
                !isValidInteger(tMaxSStr)) {
            textArea.appendText("All fields must be integer numbers.\n");
            return;
        }

        int N = Integer.parseInt(clientsStr);
        int Q = Integer.parseInt(queuesStr);
        int simulationTime = Integer.parseInt(tSimStr);
        int minArrivalTime = Integer.parseInt(tMinAStr);
        int maxArrivalTime = Integer.parseInt(tMaxAStr);
        int minServiceTime = Integer.parseInt(tMinSStr);
        int maxServiceTime = Integer.parseInt(tMaxSStr);

        if (minArrivalTime > maxArrivalTime) {
            textArea.appendText("Minimum arrival time must be smaller than maximum arrival time.\n");
            return;
        }
        if (minServiceTime > maxServiceTime) {
            textArea.appendText("Minimum service time must be smaller than maximum service time.\n");
            return;
        }

        startSimulation(N, Q, simulationTime, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime);
    }

    private boolean isValidInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void startSimulation(int N, int Q, int simulationTime, int minArrivalTime, int maxArrivalTime,
                                 int minServiceTime, int maxServiceTime) {

        simulationManager = new SimulationManager(this);
        simulationManager.setInputParameters(N, Q, simulationTime, minArrivalTime, maxArrivalTime,
                minServiceTime, maxServiceTime);
        changeStrategy();

        Thread simulationThread = new Thread(simulationManager);
        simulationThread.start();
    }

    public void updateTextArea(String output)
    {
        textArea.setText(output);
    }

}
