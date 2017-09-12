package com.middevs.local.android.sdk.task.handler.core.task;


import com.middevs.local.android.sdk.task.handler.core.engine.ITaskEngine;
import com.middevs.local.android.sdk.task.handler.core.engine.TaskListener;

/**
 * @author MidDevs
 * @since 8/8/16
 */
public interface ITask {

    /**
     * @param iTaskRunner will be used if need to add more task to pool
     */
    void execute(ITaskEngine iTaskRunner,
                 final TaskListener listener);


    TaskType getTaskType();

    int getRepetition();
}
