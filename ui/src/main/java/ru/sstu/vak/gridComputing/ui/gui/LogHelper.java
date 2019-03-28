package ru.sstu.vak.gridComputing.ui.gui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogHelper {

    private Logger log;
    private TextArea console;
    private ExceptionListener listener;
    private String alertIcoPath;

    public LogHelper(Logger log, TextArea console, String alertIcoPath) {
        this.log = log;
        this.console = console;
        this.alertIcoPath = alertIcoPath;
    }

    public void setOnExceptionListener(ExceptionListener listener) {
        this.listener = listener;
    }


    public void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText(message);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new javafx.scene.image.Image(alertIcoPath)); // To add an icon
            alert.showAndWait();
        });
    }

    public void showMessage(String message, String title) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(alertIcoPath)); // To add an icon
            alert.showAndWait();
        });
    }

    public void printInfoMessage(String mess) {
        log.info(mess);
        printMessage(mess);
    }

    public void printErrorMessage(String mess, Throwable e) {
        log.error(mess, e);
        printMessage("- ERROR - " + mess);
    }


    public void tryIt(Try callback, Finally finalExec) {
        try {
            callback.executableCode();
        } catch (Exception e) {
            processException(e);
        } finally {
            if (finalExec != null) {
                finalExec.executableCode();
            }
        }
    }

    public void tryIt(Try callback) {
        tryIt(callback, null);
    }

    public void processException(Exception e) {
        printErrorMessage(e.getMessage(), e);
        showError(e.getMessage() + " \nSee logs: 'logs.log'");
        if (listener != null) {
            listener.onException();
        }
    }


    public interface Try {
        void executableCode() throws Exception;
    }

    public interface Finally {
        void executableCode();
    }

    public interface ExceptionListener {
        void onException();
    }

    private void printMessage(String mess) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Platform.runLater(() -> console.appendText("[" + dateFormat.format(new Date()) + "] - " + mess + "\n"));
    }

}
