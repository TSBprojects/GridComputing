package ru.sstu.vak.gridComputing.distributionNode.core;


import ru.sstu.vak.gridComputing.dataFlow.entity.Route;
import ru.sstu.vak.gridComputing.dataFlow.entity.RouteData;
import ru.sstu.vak.gridComputing.dataFlow.entity.TaskResult;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public interface RouteBuilder {

    RouteData getRouteData();

    BigInteger getTaskCount(BigInteger taskSize);

    Route readTaskResultFiles(
            BigInteger taskCount,
            Path folderPath,
            Consumer<TaskResult> iterator
    ) throws IOException;

    Path writeJobAndTaskFiles(
            BigInteger taskSize, String jobName, Path jarFilePath,
            Path dataFilePath, Path tasksFolderPath,
            Path jobFolderPath, String remoteCommand
    ) throws IOException;

}
