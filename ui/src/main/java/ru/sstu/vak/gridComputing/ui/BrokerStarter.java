package ru.sstu.vak.gridComputing.ui;

import ru.sstu.vak.gridComputing.dataFlow.utils.console.ConsoleExecutor;
import ru.sstu.vak.gridComputing.ui.gui.LogHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrokerStarter {

    private Callback callback;

    private String startBroker;
    private String setGrid;
    private String addJob;
    private String waitForJob;


    public interface Callback {
        void onOutputLineRead(String outputLine);

        void onJobDone();

        void onException(Exception e);
    }


    public BrokerStarter() {
    }

    public void setCommandOutputListener(Callback callback) {
        this.callback = callback;
    }

    public void startAndRunJob(Path brokerPath, Path peerDescPath, Path jobPath) throws Exception {

        String start;
        if (System.getProperty("os.name").contains("Windows")) {
            start = "start";
        } else {
            start = "bash";
        }

        startBroker = String.format("%1$s %2$s start", start, brokerPath);
        setGrid = String.format("%1$s %2$s setgrid %3$s", start, brokerPath, peerDescPath);
        addJob = String.format("%1$s %2$s addjob %3$s", start, brokerPath, jobPath);
        waitForJob = String.format("%1$s %2$s waitforjob ", start, brokerPath);

        startBroker();
    }

    private void startBroker() throws Exception {

        ConsoleExecutor.execute(startBroker, new ConsoleExecutor.Callback() {
            @Override
            public void onOutputLineRead(String outputLine) {
                if (callback != null) {
                    callback.onOutputLineRead(outputLine);
                }
                if (contains(outputLine, "MyGrid") && contains(outputLine, "running")) {
                    try {
                        setGrid();
                    } catch (IOException e) {
                        if (callback != null) {
                            callback.onException(e);
                        }
                    }
                }
            }

            @Override
            public void onCommandComplete() {
                // nothing
            }

            @Override
            public void onException(Exception e) {
                if (callback != null) {
                    callback.onException(e);
                }
            }
        });
    }

    private void setGrid() throws IOException {
        ConsoleExecutor.execute(setGrid, new ConsoleExecutor.Callback() {
            @Override
            public void onOutputLineRead(String outputLine) {
                if (callback != null) {
                    callback.onOutputLineRead(outputLine);
                }
            }

            @Override
            public void onCommandComplete() {
                try {
                    addJob();
                } catch (IOException e) {
                    if (callback != null) {
                        callback.onException(e);
                    }
                }
            }

            @Override
            public void onException(Exception e) {
                if (callback != null) {
                    callback.onException(e);
                }
            }
        });
    }

    private void addJob() throws IOException {
        ConsoleExecutor.execute(addJob, new ConsoleExecutor.Callback() {
            @Override
            public void onOutputLineRead(String outputLine) {
                try {
                    waitForJob(waitForJob + getJobId(outputLine));
                } catch (IOException e) {
                    if (callback != null) {
                        callback.onException(e);
                    }
                }
                if (callback != null) {
                    callback.onOutputLineRead(outputLine);
                }
            }

            @Override
            public void onCommandComplete() {
                // nothing
            }

            @Override
            public void onException(Exception e) {
                if (callback != null) {
                    callback.onException(e);
                }
            }
        });
    }

    private void waitForJob(String waitForJob) throws IOException {
        ConsoleExecutor.execute(waitForJob, new ConsoleExecutor.Callback() {
            @Override
            public void onOutputLineRead(String outputLine) {
                if (callback != null) {
                    callback.onOutputLineRead(outputLine);
                }
            }

            @Override
            public void onCommandComplete() {
                if (callback != null) {
                    callback.onJobDone();
                }
            }

            @Override
            public void onException(Exception e) {
                if (callback != null) {
                    callback.onException(e);
                }
            }
        });
    }

    private int getJobId(String outputLine) {
        Pattern p = Pattern.compile("(\\d+)");
        Matcher mId = p.matcher(outputLine);

        if (mId.find()) {
            return Integer.parseInt(mId.group(0));
        }
        return -1;
    }

    private boolean contains(String str, String match) {
        Pattern p = Pattern.compile(match.toLowerCase());
        Matcher matcher = p.matcher(str.toLowerCase());
        return matcher.find();
    }

}