package ru.sstu.vak.gridComputing.ui.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sstu.vak.gridComputing.dataFlow.core.DataManager;
import ru.sstu.vak.gridComputing.dataFlow.entity.Route;
import ru.sstu.vak.gridComputing.dataFlow.entity.RouteData;
import ru.sstu.vak.gridComputing.distributionNode.core.RouteBuilder;
import ru.sstu.vak.gridComputing.distributionNode.core.RouteBuilderBase;
import ru.sstu.vak.gridComputing.ui.BrokerStarter;
import ru.sstu.vak.gridComputing.ui.TaskResultWaiter;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ru.sstu.vak.gridComputing.dataFlow.core.DataManager.readDataFile;
import static ru.sstu.vak.gridComputing.dataFlow.utils.MathUtils.genAdjMatrix;

public class MainController implements Initializable {

    private static final Logger log = LogManager.getLogger(MainController.class.getName());


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane adjMatrixPane;

    @FXML
    private Button saveMatrixBtn;

    @FXML
    private Button loadMatrixBtn;

    @FXML
    private Button genMatrixBtn;

    @FXML
    private TextField randOriginField;

    @FXML
    private TextField randBoundField;

    @FXML
    private ToggleButton genMatrixRandToggle;

    @FXML
    private Button enlargeMatrixBtn;

    @FXML
    private Button reduceMatrixBtn;

    @FXML
    private TextField genMatrixSizeField;

    @FXML
    private AnchorPane taskResultsPane;

    @FXML
    private ProgressBar porgressBar;

    @FXML
    private Label progressBarInfoField;

    @FXML
    private TextArea consoleTextArea;

    @FXML
    private Button clearConsoleBtn;

    @FXML
    private TextField peerDescPathField;

    @FXML
    private TextField brokerPathField;

    @FXML
    private Button peerDescPathBtn;

    @FXML
    private Button brokerPathBtn;

    @FXML
    private TextField jarFilePathField;

    @FXML
    private TextField remoteCommandField;

    @FXML
    private TextField jobNameField;

    @FXML
    private Button jarFilePathBtn;

    @FXML
    private TextField taskSizeField;

    @FXML
    private TextField genDataFileToField;

    @FXML
    private Button genDataFilePathBtn;

    @FXML
    private TextField genTasksFilesToField;

    @FXML
    private Button genTasksFilesPathBtn;

    @FXML
    private TextField genJobFileToField;

    @FXML
    private Button genJobFileToBtn;

    @FXML
    private TextField dataFileNameField;

    @FXML
    private TextField taskFileNameField;

    @FXML
    private TextField taskResFileNameField;

    @FXML
    private TextField filesExtensionField;

    @FXML
    private TextField checkResultTimeoutField;

    @FXML
    private TextField routeLengthField;

    @FXML
    private Button startButton;

    @FXML
    private ProgressBar infinityProgressBar;


    private RouteBuilder routeBuilder;

    private MatrixView matrixView;

    private TaskResultView taskResultView;

    private TaskResultWaiter taskResultWaiter;

    private BigInteger tasksCount;

    private BigInteger taskSize;

    private boolean startToggle = false;

    private LogHelper logHelper;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initLogHelper();
        initInputFields();
        initMatrixViewResize();

        taskSizeField.textProperty().addListener(getFieldChecker(taskSizeField));
        checkResultTimeoutField.textProperty().addListener(getFieldChecker(checkResultTimeoutField));
        routeLengthField.textProperty().addListener(getFieldChecker(routeLengthField));
        randOriginField.textProperty().addListener(getFieldChecker(randOriginField));
        randBoundField.textProperty().addListener(getFieldChecker(randBoundField));
        genMatrixSizeField.textProperty().addListener(getFieldChecker(genMatrixSizeField));


        brokerPathBtn.setOnAction(event -> {
            logHelper.printInfoMessage("Selecting a broker...");
            File broker = selectFile("Select broker!");
            if (broker != null) {
                brokerPathField.setText(broker.getPath());
                logHelper.printInfoMessage("Broker selected.");
            }
        });

        peerDescPathBtn.setOnAction(event -> {
            logHelper.printInfoMessage("Selecting a peer description file...");
            File peerDesc = selectFile(
                    "Select peer description file!",
                    "Peer description file (*.gdf)",
                    "*.gdf"
            );
            if (peerDesc != null) {
                peerDescPathField.setText(peerDesc.getPath());
                logHelper.printInfoMessage("Peer description file selected.");
            }
        });

        clearConsoleBtn.setOnAction(event -> {
            Platform.runLater(() -> consoleTextArea.setText(""));
        });

        loadMatrixBtn.setOnAction(event -> {
            logHelper.printInfoMessage("Selecting data file ...");
            File data = selectFile(
                    "Select data file!",
                    "Route builder files (*.rb)",
                    "*.rb"
            );
            if (data != null) {
                logHelper.printInfoMessage("Data file selected.");
                logHelper.tryIt(() -> {
                    logHelper.printInfoMessage("Load data file...");
                    RouteData routeData = readDataFile(Paths.get(data.getPath()));
                    this.routeLengthField.setText(String.valueOf(routeData.getRouteLength()));
                    initMatrixView(routeData.getAdjMatrix());
                    logHelper.printInfoMessage("Data file has been loaded.");
                });
            }
        });

        saveMatrixBtn.setOnAction(event -> {
            if (matrixView != null) {
                logHelper.printInfoMessage("Selecting a folder to save the matrix...");
                File folder = selectFolder("Select a folder to save the matrix!");
                if (folder != null) {
                    logHelper.printInfoMessage("Folder selected.");
                    logHelper.tryIt(() -> {
                        writeDataFile(new RouteData(
                                matrixView.getAdjMatrix(),
                                Integer.parseInt(routeLengthField.getText())
                        ), Paths.get(folder.getPath()));
                    });
                }
            }
        });

        enlargeMatrixBtn.setOnAction(event -> {
            if (matrixView != null) {
                logHelper.printInfoMessage("Enlarge matrix.");
                if (genMatrixRandToggle.isSelected()) {
                    matrixView.enlargeMatrix(
                            Integer.parseInt(randOriginField.getText()),
                            Integer.parseInt(randBoundField.getText())
                    );
                } else {
                    matrixView.enlargeMatrix();
                }
            }
        });

        reduceMatrixBtn.setOnAction(event -> {
            if (matrixView != null) {
                logHelper.printInfoMessage("Reduce matrix.");
                matrixView.reduceMatrix();
            }
        });

        genMatrixRandToggle.setOnAction(event -> {
            if (genMatrixRandToggle.isSelected()) {
                randOriginField.setDisable(false);
                randBoundField.setDisable(false);
                logHelper.printInfoMessage("Generate a matrix of random numbers ON");
            } else {
                randOriginField.setDisable(true);
                randBoundField.setDisable(true);
                logHelper.printInfoMessage("Generate a matrix of random numbers OFF");
            }
        });

        genMatrixBtn.setOnAction(event -> {
            logHelper.tryIt(() -> {
                logHelper.printInfoMessage("Generated matrix...");
                int[][] adjMatrix;

                if (genMatrixRandToggle.isSelected()) {
                    adjMatrix = genAdjMatrix(
                            Integer.parseInt(genMatrixSizeField.getText()),
                            Integer.parseInt(randOriginField.getText()),
                            Integer.parseInt(randBoundField.getText())
                    );
                } else {
                    adjMatrix = genAdjMatrix(
                            Integer.parseInt(genMatrixSizeField.getText())
                    );
                }
                initMatrixView(adjMatrix);
                logHelper.printInfoMessage("Matrix has been generated.");
            });
        });

        genDataFilePathBtn.setOnAction(event -> {
            logHelper.printInfoMessage("Selecting a folder to save the data...");
            File file = selectFolder("Choose a place to save the data");
            if (file != null) {
                genDataFileToField.setText(file.getPath());
                logHelper.printInfoMessage("Folder selected.");
            }
        });

        genTasksFilesPathBtn.setOnAction(event -> {
            logHelper.printInfoMessage("Selecting a folder to save tasks...");
            File file = selectFolder("Choose a place to save tasks");
            if (file != null) {
                genTasksFilesToField.setText(file.getPath());
                logHelper.printInfoMessage("Folder selected.");
            }
        });

        genJobFileToBtn.setOnAction(event -> {
            logHelper.printInfoMessage("Selecting a folder to save the job...");
            File file = selectFolder("Choose a place to save the job");
            if (file != null) {
                genJobFileToField.setText(file.getPath());
                logHelper.printInfoMessage("Folder selected.");
            }
        });

        jarFilePathBtn.setOnAction(event -> {
            logHelper.printInfoMessage("Selecting a jar file...");
            File file = selectFile(
                    "Select jar file!",
                    "Jar file (*.jar)",
                    "*.jar");
            if (file != null) {
                jarFilePathField.setText(file.getPath());
                logHelper.printInfoMessage("Jar file selected.");
            }
        });


        startButton.setOnAction(event -> {
            logHelper.tryIt(() -> {
                if (matrixView != null) {
                    logHelper.printInfoMessage("Starting computing...");
                    initTasksView();
                    startToggle();
                    resetTaskResultPane();

                    initInputValues();

                    Path dataPath = writeDataFile(
                            routeBuilder.getRouteData(),
                            Paths.get(genDataFileToField.getText())
                    );

                    Path jobPath = writeJobAndTaskFiles(dataPath);

                    startBroker(jobPath);

                    setupResultWaiter();
                }
            });
        });
    }


    private File selectFile(String title, String extDesc, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                extDesc,
                extensions
        );
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(null);
    }

    private File selectFile(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        return fileChooser.showOpenDialog(null);
    }

    private File selectFolder(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        return directoryChooser.showDialog(null);
    }


    private void initRouteBuilder() {
        routeBuilder = new RouteBuilderBase(
                matrixView.getAdjMatrix(),
                Integer.parseInt(routeLengthField.getText())
        );
        tasksCount = routeBuilder.getTaskCount(taskSize);
    }

    private void initMatrixView(int[][] adjMatrix) {
        this.matrixView = new MatrixView(adjMatrix);
        AnchorPane.setLeftAnchor(matrixView, 0d);
        this.matrixView.setPrefHeight(adjMatrixPane.getHeight());
        this.matrixView.setPrefWidth(adjMatrixPane.getWidth());
        AnchorPane.setTopAnchor(matrixView, 0d);
        this.adjMatrixPane.getChildren().clear();
        this.adjMatrixPane.getChildren().add(matrixView);
    }

    private void initMatrixViewResize() {
        Platform.runLater(() -> {
            adjMatrixPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                double newV = (double) newValue;
                matrixView.setPrefWidth(newV);
                matrixView.setMinWidth(newV);
                matrixView.setMinWidth(newV);
            });
            adjMatrixPane.heightProperty().addListener((observable, oldValue, newValue) -> {
                double newV = (double) newValue;
                matrixView.setPrefHeight(newV);
                matrixView.setMinHeight(newV);
                matrixView.setMaxHeight(newV);
            });
        });
    }

    private void initTasksView() {
        taskResultView = new TaskResultView();
        AnchorPane.setBottomAnchor(taskResultView, 0d);
        AnchorPane.setLeftAnchor(taskResultView, 0d);
        AnchorPane.setRightAnchor(taskResultView, 0d);
        AnchorPane.setTopAnchor(taskResultView, 0d);
        this.taskResultsPane.getChildren().clear();
        taskResultsPane.getChildren().add(taskResultView);
    }

    private void initInputFields() {
        dataFileNameField.setText(DataManager.DATA_FILE_NAME);
        taskFileNameField.setText(DataManager.TASK_FILE_NAME);
        taskResFileNameField.setText(DataManager.TASK_RESULT_NAME);
        filesExtensionField.setText(DataManager.FILE_EXTENSION);
        remoteCommandField.setTooltip(new Tooltip("Command example:\n" +
                "java -jar $JAR $DATA $TASK > $RESULT\n" +
                "$JAR - jar file\n" +
                "$DATA - data file\n" +
                "$TASK - task file\n" +
                "$RESULT – result file\n")
        );
        if (System.getProperty("os.name").contains("Windows")) {
            genDataFileToField.setPromptText("C:\\Data\\");
            genTasksFilesToField.setPromptText("C:\\Tasks\\");
            genJobFileToField.setPromptText("C:\\Jobs\\");
            jarFilePathField.setPromptText("C:\\Programs\\program.jar");
            brokerPathField.setPromptText("C:\\broker");
            peerDescPathField.setPromptText("C:\\setPeer.gdf");
        } else {
            genDataFileToField.setPromptText("/home/user/data/");
            genTasksFilesToField.setPromptText("/home/user/tasks/");
            genJobFileToField.setPromptText("/home/user/jobs/");
            jarFilePathField.setPromptText("/home/user/programs/program.jar");
            brokerPathField.setPromptText("/home/user/broker");
            peerDescPathField.setPromptText("/home/user/setPeer.gdf");
        }
    }

    private void initInputValues() {
        DataManager.DATA_FILE_NAME = dataFileNameField.getText();
        DataManager.TASK_FILE_NAME = taskFileNameField.getText();
        this.taskSize = new BigInteger(taskSizeField.getText());
        initRouteBuilder();
    }

    private void initLogHelper() {
        Platform.runLater(() -> {
            logHelper = new LogHelper(log, consoleTextArea, "/main.png");
            logHelper.setOnExceptionListener(() -> {
                startToggle = true;
                startToggle();
            });
        });
    }


    private Path writeDataFile(RouteData routeData, Path folderPath) throws IOException {
        logHelper.printInfoMessage("Writing data file...");
        Path dataPath = DataManager.writeDataFile(routeData, folderPath);
        logHelper.printInfoMessage("Data file has been written.");
        return dataPath;
    }

    private Path writeJobAndTaskFiles(Path dataFilePath) throws IOException {
        logHelper.printInfoMessage("Writing job and task files...");
        Path jobPath = routeBuilder.writeJobAndTaskFiles(
                taskSize,
                jobNameField.getText(),
                Paths.get(jarFilePathField.getText()),
                dataFilePath,
                Paths.get(genTasksFilesToField.getText()),
                Paths.get(genJobFileToField.getText()),
                remoteCommandField.getText()
        );
        logHelper.printInfoMessage("Job and task files has been written.");
        return jobPath;
    }

    private void startBroker(Path jobPath) throws Exception {
        BrokerStarter brokerStarter = new BrokerStarter();

        brokerStarter.setCommandOutputListener(new BrokerStarter.Callback() {
            @Override
            public void onOutputLineRead(String outputLine) {
                log.info(outputLine);
                Platform.runLater(() -> {
                    String[] consoleLines = consoleTextArea.getText().split("\n");
                    if (consoleLines[consoleLines.length - 1].contains("Command executed....")) {
                        consoleLines[consoleLines.length - 1] = outputLine;
                        consoleTextArea.setText(Arrays.stream(consoleLines)
                                .collect(Collectors.joining("\n")) + "\n");
                    } else {
                        consoleTextArea.appendText(outputLine);
                    }
                });
            }

            @Override
            public void onJobDone() {
                Platform.runLater(() -> {
                    String[] consoleLines = consoleTextArea.getText().split("\n");
                    String[] completeConsLines = Arrays.copyOf(consoleLines, consoleLines.length - 1);
                    consoleTextArea.setText(Arrays.stream(completeConsLines)
                            .collect(Collectors.joining("\n")) + "\n");
                });
                try {
                    Route minRoute = routeBuilder.readTaskResultFiles(
                            tasksCount, Paths.get(genJobFileToField.getText()),
                            taskResult -> {
                                logHelper.printInfoMessage("Read result file №" +
                                        taskResult.getTaskIndex() + ". " +
                                        taskResult.getMinRoute()
                                );
                                Platform.runLater(() -> {
                                    taskResultView.addTask(taskResult);
                                });
                            }
                    );
                    String workDone = "Work done! Min route - " + minRoute;
                    logHelper.printInfoMessage(workDone);
                    logHelper.showMessage(workDone, "Results");
                    startToggle();
                } catch (IOException e) {
                    logHelper.processException(e);
                }
            }

            @Override
            public void onException(Exception e) {
                logHelper.processException(e);
            }
        });

        logHelper.printInfoMessage("Launch a broker...");
        brokerStarter.startAndRunJob(
                Paths.get(brokerPathField.getText()),
                Paths.get(peerDescPathField.getText()),
                jobPath
        );

    }

    private void setupResultWaiter() {

        taskResultWaiter = new TaskResultWaiter(
                genJobFileToField.getText(),
                tasksCount
        );

        logHelper.printInfoMessage("Start listening to the result folder....");
        taskResultWaiter.start(new TaskResultWaiter.Callback() {
            @Override
            public void onTimeoutTick(BigInteger resultsCount) {
                Platform.runLater(() -> {
                    setProgressBar(
                            resultsCount.doubleValue() / tasksCount.doubleValue(),
                            String.format("Tasks %1$s/%2$s", resultsCount, tasksCount)
                    );
                });
            }

            @Override
            public void onAllResultsExist() {
                Platform.runLater(() -> {
                    setProgressBar(1, String.format("Tasks %1$s/%1$s", tasksCount));
                });
            }

            @Override
            public void onException(Exception e) {
                logHelper.processException(e);
            }
        }, Integer.parseInt(checkResultTimeoutField.getText()));
    }


    private void startToggle() {
        Platform.runLater(() -> {
            if (startToggle) {
                startToggle = false;
                startButton.setDisable(false);
                startButton.setText("Start");
                startButton.setStyle("-fx-background-color: F39C63");
                infinityProgressBar.setVisible(false);
                if (taskResultWaiter != null) {
                    taskResultWaiter.stop();
                }
            } else {
                startToggle = true;
                startButton.setDisable(true);
                startButton.setText("Wait...");
                startButton.setStyle("-fx-background-color: red");
                infinityProgressBar.setVisible(true);
            }
        });
    }

    private synchronized void setProgressBar(double progress, String info) {
        porgressBar.setProgress(progress);
        progressBarInfoField.setText(info);
    }

    private void resetTaskResultPane() {
        setProgressBar(0, "Tasks 0/0");
        this.taskResultsPane.getChildren().clear();
    }


    private ChangeListener<String> getFieldChecker(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
    }

}
