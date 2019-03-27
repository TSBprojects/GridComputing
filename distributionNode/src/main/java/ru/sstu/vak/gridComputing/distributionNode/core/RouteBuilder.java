package ru.sstu.vak.gridComputing.distributionNode.core;


import ru.sstu.vak.gridComputing.dataFlow.entity.Route;
import ru.sstu.vak.gridComputing.dataFlow.entity.RouteData;
import ru.sstu.vak.gridComputing.dataFlow.entity.TaskResult;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;

public interface RouteBuilder {

    RouteData getRouteData();

    BigInteger getTaskCount(BigInteger taskSize);


    Path writeJobAndTaskFiles(
            BigInteger taskSize, String jobName, Path jarFilePath,
            Path dataFilePath, Path tasksFolderPath,
            Path jobFolderPath, String remoteCommand

    ) throws IOException;

    Route getFinalResult(List<TaskResult> taskResults);

}
