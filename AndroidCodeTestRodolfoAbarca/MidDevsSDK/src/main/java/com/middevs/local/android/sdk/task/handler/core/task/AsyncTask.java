/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to NOX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.middevs.local.android.sdk.task.handler.core.task;

import android.os.Handler;
import android.os.Looper;

import com.middevs.local.android.sdk.task.handler.core.engine.ITaskEngine;
import com.middevs.local.android.sdk.task.handler.core.engine.TaskListener;

import static com.middevs.local.android.sdk.task.handler.core.engine.ITaskEngine.OBJECT_LOCK_ASYNC;

/**
 * @author MidDevs
 * @since 3/9/17
 */

public abstract class AsyncTask<R>
        extends Task {

    public AsyncTask() {

        super();
    }

    @Override
    public void execute(ITaskEngine iTaskRunner,
                        TaskListener listener) {

        R r = inBackground();
//			System.out.println ( "AsyncTask after background " + r );
        synchronized (OBJECT_LOCK_ASYNC) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {

//						System.out.println ( "AsyncTask before UI " + r );
                    onUI(r);
//						System.out.println ( "AsyncTask before UI " + r );
                }
            });
//			System.out.println ( "AsyncTask before finish " + r );
        }
        listener.finish();
    }

    protected abstract R inBackground();

    protected abstract void onUI(R r);

}
