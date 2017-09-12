/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights
 * reserved. * * The source
 * code, other & all material, and documentation               * contained herein are, and
 * remains the property of HEX
 * Inc.             * and its suppliers, if any. The intellectual and technical * concepts
 * contained herein are
 * proprietary to HEX Inc. and its          * suppliers and may be covered by U.S. and Foreign
 * Patents, patents      *
 * in process, and are protected by trade secret or copyright law.        * Dissemination of the
 * foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.middevs.local.android.sdk.task.handler.core.engine;


import com.middevs.local.android.sdk.task.handler.core.task.ITask;

/**
 * @author MidDevs
 * @since 8/8/16
 */
public abstract class ITaskEngine {

    public final static Object OBJECT_LOCK_ASYNC = new Object();

    public static ITaskEngine buildTaskRunner(int concurrency) {

        return new TaskEngine(concurrency);
    }

    /**
     * used for infinite loop, N-loop, or put new tasks upon completion of a task
     *
     * @param newITask
     */
    public abstract void add(ITask newITask);

    public abstract boolean isRunning();

    public abstract void stop();

    public abstract void clearTasks();
}
