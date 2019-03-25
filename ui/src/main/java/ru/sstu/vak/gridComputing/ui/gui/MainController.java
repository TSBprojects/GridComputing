package ru.sstu.vak.gridComputing.ui.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sstu.vak.gridComputing.dataFlow.core.DataManager;
import ru.sstu.vak.gridComputing.dataFlow.entity.Route;
import ru.sstu.vak.gridComputing.dataFlow.entity.RouteData;
import ru.sstu.vak.gridComputing.dataFlow.entity.TaskResult;
import ru.sstu.vak.gridComputing.dataFlow.utils.ConsoleExecutor;
import ru.sstu.vak.gridComputing.distributionNode.core.RouteBuilder;
import ru.sstu.vak.gridComputing.distributionNode.core.RouteBuilderBase;
import ru.sstu.vak.gridComputing.ui.TaskResultWaiter;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

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
    private TextField dataFileNameField;

    @FXML
    private TextField taskFileNameField;

    @FXML
    private TextField taskResFileNameField;

    @FXML
    private TextField filesExtensionField;

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
    private TextField jarFilePathField;

    @FXML
    private TextField remoteCommandField;

    @FXML
    private TextField jobNameField;

    @FXML
    private TextField taskSizeField;

    @FXML
    private TextField prefixCommandField;

    @FXML
    private CheckBox consCommCheckBox;

    @FXML
    private Button jarFilePathBtn;

    @FXML
    private ProgressIndicator prefCommProgBar;

    @FXML
    private TextField checkResultTimeoutField;

    @FXML
    private TextField routeLengthField;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private ProgressBar infinityProgressBar;


    private RouteBuilder routeBuilder;

    private MatrixView matrixView;

    private TaskResultView taskResultView;

    private TaskResultWaiter taskResultWaiter;

    private BigInteger tasksCount;

    private BigInteger taskSize;

    private boolean startToggle = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initSettings();

        taskSizeField.textProperty().addListener(getFieldChecker(taskSizeField));
        checkResultTimeoutField.textProperty().addListener(getFieldChecker(checkResultTimeoutField));
        routeLengthField.textProperty().addListener(getFieldChecker(routeLengthField));
        randOriginField.textProperty().addListener(getFieldChecker(randOriginField));
        randBoundField.textProperty().addListener(getFieldChecker(randBoundField));
        genMatrixSizeField.textProperty().addListener(getFieldChecker(genMatrixSizeField));

        consCommCheckBox.setOnAction(event -> {
            if (consCommCheckBox.isSelected()) {
                prefixCommandField.setDisable(false);
            } else {
                prefixCommandField.setDisable(true);
            }
        });

        clearConsoleBtn.setOnAction(event -> {
            Platform.runLater(() -> consoleTextArea.setText(""));
        });

        loadMatrixBtn.setOnAction(event -> {
            printInfoMessage("Selecting data file ...");
            File data = selectFile(
                    "Select data file!",
                    "Route builder files (*.rb)",
                    "*.rb"
            );
            if (data != null) {
                printInfoMessage("Data file selected.");
                tryIt(() -> {
                    printInfoMessage("Load data file...");
                    RouteData routeData = readDataFile(Paths.get(data.getPath()));
                    this.routeLengthField.setText(String.valueOf(routeData.getRouteLength()));
                    initMatrixView(routeData.getAdjMatrix());
                    printInfoMessage("Data file has been loaded.");
                });
            }
        });

        saveMatrixBtn.setOnAction(event -> {
            if (matrixView != null) {
                printInfoMessage("Selecting a folder to save the matrix...");
                File folder = selectFolder("Select a folder to save the matrix!");
                if (folder != null) {
                    printInfoMessage("Folder selected.");
                    tryIt(() -> {
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
                printInfoMessage("Enlarge matrix.");
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
                printInfoMessage("Reduce matrix.");
                matrixView.reduceMatrix();
            }
        });

        genMatrixRandToggle.setOnAction(event -> {
            if (genMatrixRandToggle.isSelected()) {
                randOriginField.setDisable(false);
                randBoundField.setDisable(false);
                printInfoMessage("Generate a matrix of random numbers ON");
            } else {
                randOriginField.setDisable(true);
                randBoundField.setDisable(true);
                printInfoMessage("Generate a matrix of random numbers OFF");
            }
        });

        genMatrixBtn.setOnAction(event -> {
            tryIt(() -> {
                printInfoMessage("Generated matrix...");
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
                printInfoMessage("Matrix has been generated.");
            });
        });

        genDataFilePathBtn.setOnAction(event -> {
            printInfoMessage("Selecting a folder to save the data...");
            File file = selectFolder("Choose a place to save the data");
            if (file != null) {
                genDataFileToField.setText(file.getPath());
                printInfoMessage("Folder selected.");
            }
        });

        genTasksFilesPathBtn.setOnAction(event -> {
            printInfoMessage("Selecting a folder to save tasks...");
            File file = selectFolder("Choose a place to save tasks");
            if (file != null) {
                genTasksFilesToField.setText(file.getPath());
                printInfoMessage("Folder selected.");
            }
        });

        genJobFileToBtn.setOnAction(event -> {
            printInfoMessage("Selecting a folder to save the job...");
            File file = selectFolder("Choose a place to save the job");
            if (file != null) {
                genJobFileToField.setText(file.getPath());
                printInfoMessage("Folder selected.");
            }
        });

        jarFilePathBtn.setOnAction(event -> {
            printInfoMessage("Selecting a jar file...");
            File file = selectFile(
                    "Select jar file!",
                    "Jar file (*.jar)",
                    "*.jar");
            if (file != null) {
                jarFilePathField.setText(file.getPath());
                printInfoMessage("Jar file selected.");
            }
        });


        startButton.setOnAction(event -> {
            tryIt(() -> {
                if (matrixView != null) {
                    printInfoMessage("Starting computing...");
                    initTasksView();
                    startToggle();
                    resetProgressBar();

                    initInputs();

                    writeDataFile(
                            routeBuilder.getRouteData(),
                            Paths.get(genDataFileToField.getText())
                    );

                    writeTaskFiles();

                    executePrefixCommand(writeJobFile());

                    setupResultWaiter();
                }
            });

            stopButton.setOnAction(event1 -> {
                if (taskResultWaiter != null) {
                    taskResultWaiter.stop();
                    startToggle();
                    resetProgressBar();
                    printInfoMessage("Stop listening to the result folder.");
                }
            });
        });
    }


    public static void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText(message);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("/main.png")); // To add an icon
            alert.showAndWait();
        });
    }

    public static void showMessage(String message, String title) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("/main.png")); // To add an icon
            alert.showAndWait();
        });
    }

    private void printInfoMessage(String mess) {
        log.info(mess);
        printMessage(mess);
    }

    private void printErrorMessage(String mess, Throwable e) {
        log.error(mess, e);
        printMessage("- ERROR - " + mess);
    }

    private void printMessage(String mess) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Platform.runLater(() -> consoleTextArea.appendText("[" + dateFormat.format(new Date()) + "] - " + mess + "\n"));
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
        matrixView = new MatrixView(adjMatrix);
        AnchorPane.setBottomAnchor(matrixView, 0d);
        AnchorPane.setLeftAnchor(matrixView, 0d);
        AnchorPane.setRightAnchor(matrixView, 0d);
        AnchorPane.setTopAnchor(matrixView, 0d);
        this.adjMatrixPane.getChildren().clear();
        this.adjMatrixPane.getChildren().add(matrixView);
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

    private void initSettings() {
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
        String jobTooltip;
        if (System.getProperty("os.name").contains("Windows")) {
            jobTooltip = "start broker addjob $JOB";
            genDataFileToField.setPromptText("C:\\Data\\");
            genTasksFilesToField.setPromptText("C:\\Tasks\\");
            genJobFileToField.setPromptText("C:\\Jobs\\");
            jarFilePathField.setPromptText("C:\\Programs\\program.jar");
            prefixCommandField.setPromptText(jobTooltip);

        } else {
            jobTooltip = "bash broker addjob $JOB";
            genDataFileToField.setPromptText("/home/user/data/");
            genTasksFilesToField.setPromptText("/home/user/tasks/");
            genJobFileToField.setPromptText("/home/user/jobs/");
            jarFilePathField.setPromptText("/home/user/programs/program.jar");
            prefixCommandField.setPromptText(jobTooltip);
        }
        prefixCommandField.setTooltip(new Tooltip("Command example:\n" +
                jobTooltip + "\n$JOB - yourJob.jdf file")
        );
    }


    private void initInputs() {
        DataManager.DATA_FILE_NAME = dataFileNameField.getText();
        DataManager.TASK_FILE_NAME = taskFileNameField.getText();
        this.taskSize = new BigInteger(taskSizeField.getText());
        initRouteBuilder();
    }

    private Path writeDataFile(RouteData routeData, Path folderPath) throws IOException {
        printInfoMessage("Writing data file...");
        Path dataPath = DataManager.writeDataFile(routeData, folderPath);
        printInfoMessage("Data file has been written.");

        return dataPath;
    }

    private Path writeTaskFiles() throws IOException {
        Path taskFilesFolder = Paths.get(genTasksFilesToField.getText());

        printInfoMessage("Writing task files...");
        routeBuilder.writeTaskFiles(taskSize, taskFilesFolder);
        printInfoMessage("Task files has been written.");

        return taskFilesFolder;
    }

    private Path writeJobFile() throws IOException {
        printInfoMessage("Writing job file...");
        Path jobPath = routeBuilder.writeJobFile(
                taskSize,
                jobNameField.getText(),
                Paths.get(jarFilePathField.getText()),
                Paths.get(genJobFileToField.getText()),
                remoteCommandField.getText()
        );
        printInfoMessage("Job has been written.");

        return jobPath;
    }

    private void executePrefixCommand(Path jobPath) throws IOException {
        if (consCommCheckBox.isSelected()) {
            printInfoMessage("Execute console command...");
            prefixCommandField.setMaxWidth(426);
            prefixCommandField.setMinWidth(426);
            prefCommProgBar.setVisible(true);
            new Thread(() -> {
                tryIt(() -> {
                            String command = prefixCommandField.getText()
                                    .replace("$JOB", jobPath.toString());
                            printInfoMessage(ConsoleExecutor.execute(command));
                        },
                        () -> {
                            Platform.runLater(() -> {
                                prefixCommandField.setMaxWidth(460);
                                prefixCommandField.setMinWidth(460);
                                prefCommProgBar.setVisible(false);
                            });
                        });
            }).start();
        }
    }

    private void setupResultWaiter() {

        taskResultWaiter = new TaskResultWaiter(
                genJobFileToField.getText(),
                tasksCount
        );

        printInfoMessage("Start listening to the result folder....");
        taskResultWaiter.start(new TaskResultWaiter.Callback() {
            @Override
            public boolean onTaskReceive(TaskResult taskResult) {
                boolean isDuplicate = taskResultView.contains(taskResult);
                if (isDuplicate) {
                    printInfoMessage("Found duplicate result file!");
                }
                return !isDuplicate;
            }

            @Override
            public void onTaskComplete(TaskResult taskResult, BigInteger index) {
                printInfoMessage("Found result file.");
                Platform.runLater(() -> {
                    taskResultView.addTask(taskResult);
                    setProgressBar(
                            index.doubleValue() / tasksCount.doubleValue(),
                            String.format("Tasks %1$s/%2$s", index, tasksCount)
                    );
                });
            }

            @Override
            public void onWorkComplete(Route minRoute) {
                printInfoMessage("Work done! Min route - " + minRoute);
                showMessage(
                        "Work done!\nMin route - " + minRoute,
                        "Results"
                );
                Platform.runLater(() -> {
                    startToggle();
                    setProgressBar(1, String.format("Tasks %1$s/%1$s", tasksCount));
                });
            }
        }, Integer.parseInt(checkResultTimeoutField.getText()));
    }


    private void startToggle() {
        if (startToggle) {
            startToggle = false;
            startButton.setVisible(true);
            stopButton.setVisible(false);
            infinityProgressBar.setVisible(false);
        } else {
            startToggle = true;
            startButton.setVisible(false);
            stopButton.setVisible(true);
            infinityProgressBar.setVisible(true);
        }
    }

    private void setProgressBar(double progress, String info) {
        porgressBar.setProgress(progress);
        progressBarInfoField.setText(info);
    }

    private void resetProgressBar() {
        setProgressBar(0, "Tasks 0/0");
    }


    private ChangeListener<String> getFieldChecker(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
    }


    private void tryIt(Try callback, Finally finalExec) {
        try {
            callback.executableCode();
        } catch (Exception e) {
            printErrorMessage(e.getMessage(), e);
            showError(e.getMessage() + " \nSee logs: 'logs.log'");
            startToggle = true;
            startToggle();
        } finally {
            if (finalExec != null) {
                finalExec.executableCode();
            }
        }
    }

    private void tryIt(Try callback) {
        tryIt(callback, null);
    }

    private interface Try {
        void executableCode() throws Exception;
    }

    private interface Finally {
        void executableCode();
    }
}
