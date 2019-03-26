package ru.sstu.vak.gridComputing.ui.gui;

import javafx.beans.value.ObservableValueBase;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ru.sstu.vak.gridComputing.dataFlow.entity.TaskResult;

import java.util.Arrays;

public class TaskResultView extends TableView<String[]> {

    private final static int PREF_COLUMN_WIDTH = 250;

    private ObservableList<String[]> completedTasks;

    public TaskResultView() {
        this.setEditable(false);
        this.completedTasks = new ObservableLinkedList<>();
    }


    public void addTask(TaskResult taskResult) {

        String[] row = new String[]{
                taskResult.getTaskIndex().toString(),
                Arrays.toString(taskResult.getMinRoute().getNodes()),
                String.valueOf(taskResult.getMinRoute().getWeight())
        };

        this.completedTasks.add(row);
        initColumns();
    }

    public boolean contains(TaskResult taskResult) {

        String[] row = new String[]{
                taskResult.getTaskIndex().toString(),
                Arrays.toString(taskResult.getMinRoute().getNodes()),
                String.valueOf(taskResult.getMinRoute().getWeight())
        };

        for (String[] r : completedTasks) {
            if (Arrays.equals(r, row)) {
                return true;
            }
        }

        return false;
    }

    private TableColumn<String[], String> createColumn(int columnIndex) {

        TableColumn<String[], String> tableColumn = new TableColumn<>(getName(columnIndex));

        tableColumn.setSortable(false);
        tableColumn.setEditable(false);
        tableColumn.setPrefWidth(PREF_COLUMN_WIDTH);
        tableColumn.setCellValueFactory(param -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return param.getValue()[columnIndex];
            }
        });

        return tableColumn;
    }

    private void initColumns() {
        this.setItems(completedTasks);
        this.getColumns().clear();
        for (int i = 0; i < completedTasks.get(0).length; i++) {
            this.getColumns().add(i, createColumn(i));
        }
    }

    private String getName(int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return "Task#";
            }
            case 1: {
                return "Finded minimal path";
            }
            case 2: {
                return "Path's cost";
            }
            default: {
                return "none";
            }
        }
    }

}
