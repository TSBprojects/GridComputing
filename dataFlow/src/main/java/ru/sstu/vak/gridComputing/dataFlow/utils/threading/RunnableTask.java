package ru.sstu.vak.gridComputing.dataFlow.utils.threading;

public class RunnableTask implements Runnable {

    private final Runnable task;

    private final Callback callback;

    public RunnableTask(Runnable task, Callback callback) {
        this.task = task;
        this.callback = callback;
    }

    public void run() {
        task.run();
        callback.complete();
    }

    public interface Callback {
        void complete();
    }

}