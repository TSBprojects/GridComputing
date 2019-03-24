package ru.sstu.vak.gridComputing.computeNode.core;

import ru.sstu.vak.gridComputing.dataFlow.entity.Route;
import ru.sstu.vak.gridComputing.dataFlow.entity.RouteTask;
import ru.sstu.vak.gridComputing.dataFlow.entity.TaskResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public interface RouteTaskExecutor {

    void setExistRoutesListener(Consumer<Route> existRoutesListener);

    void setAllRoutesListener(Consumer<Route> allRoutesListener);


    TaskResult executeTask(Path filePath) throws IOException;

    TaskResult executeTask(RouteTask routeTask);

}
