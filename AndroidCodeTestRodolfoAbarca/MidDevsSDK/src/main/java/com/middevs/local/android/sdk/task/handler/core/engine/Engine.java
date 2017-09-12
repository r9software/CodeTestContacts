/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights reserved.
 * * * The source code, other & all material, and documentation               * contained herein
 * are, and remains the property of HEX Inc.             * and its suppliers, if any. The
 * intellectual and technical * concepts contained herein are proprietary to HEX Inc. and its
 *   * suppliers and may be covered by U.S. and Foreign Patents, patents      * in process, and are
 * protected by trade secret or copyright law.        * Dissemination of the foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.middevs.local.android.sdk.task.handler.core.engine;


import android.util.Log;

import com.middevs.local.android.sdk.task.handler.core.task.ITask;
import com.middevs.local.android.sdk.task.handler.core.task.Task;
import com.middevs.local.android.sdk.task.handler.core.task.callback.condition.ConditionalTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.middevs.local.android.sdk.task.handler.core.task.TaskType.INFINITE;
import static com.middevs.local.android.sdk.task.handler.core.task.TaskType.RECURRENT;

/**
 * @author MidDevs
 * @since 8/8/16
 */
class TaskEngine
        extends ITaskEngine
        implements Runnable

{

    private final static int NUM_CORES = Runtime.getRuntime()
            .availableProcessors() > 0 ? Runtime.getRuntime
            ()
            .availableProcessors() : 1;
    private final BlockingQueue<ITask> taskQueue;
    private Thread[] threads;
    private EngineWorker runners[];
    private boolean engineRunning;

    TaskEngine(int concurrency) {

        this.taskQueue = new LinkedBlockingQueue<>();
        engineRunning = true;
        if (concurrency == -101) concurrency = NUM_CORES / 2;//comes from web-client
        concurrency = concurrency < 1 ? NUM_CORES / 2 : concurrency;
        concurrency = concurrency > NUM_CORES ? NUM_CORES : concurrency;
        System.err.println("CONCURRENCY=" + concurrency);
        threads = new Thread[concurrency];
        runners = new EngineWorker[concurrency];
        for (int i = 0; i < concurrency; ++i) {
            runners[i] = new EngineWorker(this);
            threads[i] = new Thread(runners[i]);
            threads[i].start();
        }
        new Thread(this).start();
    }

    @Override
    public void add(ITask newITask) {

        synchronized (taskQueue) {
            taskQueue.offer(newITask);
        }
    }

    @Override
    public void clearTasks() {

        synchronized (taskQueue) {
            for (EngineWorker runner : runners) {
                runner.cancel();
            }
            taskQueue.clear();
        }
    }

    @Override
    public boolean isRunning() {

        return engineRunning;
    }


    @Override
    public void run() {

        while (engineRunning) {
            try {
                ITask iTask = taskQueue.take();
                EngineWorker taskRunner = getFreeTaskRunner();
                taskRunner.newTask(iTask);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int queueSize() {

        return taskQueue.size();
    }


    EngineWorker getFreeTaskRunner() {

        int minSize = Integer.MAX_VALUE;
        EngineWorker taskRunner = null;
        for (EngineWorker r : runners) {
            if (r.queueSize() < minSize) {
                minSize = r.queueSize();
                taskRunner = r;
            }
        }
        return taskRunner;
    }

    @Override
    public void stop() {

        engineRunning = false;
        clearTasks();
        for (Thread t : threads) t.interrupt();
        Thread.currentThread()
                .interrupt();
    }
}

final class EngineWorker
        implements Runnable {

    private final BlockingQueue<ITask> workerTasks;

    private final ITaskEngine ITaskRunner;


    EngineWoboolean inCancel;

    boolrridrker(ITaskEngine ITaskRunner) {

        workerTasks = new LinkedBlockingQueue<>();
        this.ITaskRunner = ITaskRunner;
    }

    @Oveate
    e

    public void run() {

        while (ITaskRunner.isRunning()) {
            ITask iTask;
            try {
                iTask = workerTasks.take();
                iTask.execute(ITaskRunner, new TaskListener<Object>() {

                    @Override
                    public void finish() {
//										 Log.v ( "run-TaskRunner", "FINISH A" );
                        try {
                            if (iTask != null) handleRecurrenceTask(iTask);
                            if (iTask != null) handleConditionalTask(iTask);
//												Log.v ( "run-TaskRunner", "FINISH B" );
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
//												Log.v ( "run-TaskRunner", "FINISH C" );
                        }
                    }

                    @Override
                    public void setResult(Object result) {

                        Log.v("run-TaskRunner", "setResult:" + result);
//										 System.out.println ( "xxx " + result.toString ( ) );
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private
    synchronized void handleRecurrenceTask(ITask ITask) {

        if (ITask != null) {
            if (ITask.getTaskType()
                    .equals(INFINITE)) {
                ITaskRunner.add(ITask);
            } else {
                if (ITask.getTaskType()
                        .equals(RECURRENT) && ITask.getRepetition() > 0) {
                    Task abstractTask = (Task) ITask;
                    abstractTask.tickRepetition();
                    ITaskRunner.add(ITask);
                }
            }
        }
    }

    private
    synchronized void handleConditionalTask(ITask iTask) {

        if (iTask != null) {
            if (iTask instanceof ConditionalTask) {
                ConditionalTask task = (ConditionalTask) iTask;
                if (!task.taskCondition()
                        .passCondition()) ITaskRunner.add(iTask);
                else task.runFinalCallback(task.getResult(), task.getError());
            }
        }
    }

    privean
    newTask(ITask ITask) {

        synchronized (workerTasks) {
            while (inCancel) {
            }
            return workerTasks.offer(ITask);
        }
    }

    int queueSize() {

        return workerTasks.size();
    }

    void cancel() {

        inCancel = true;
        synchronized (workerTasks) {
            Log.d("Worker cancel tasks", "size=" + workerTasks.size());
            workerTasks.clear();
            inCancel = false;
        }
    }

    void stop() {

        cancel();
    }
}
