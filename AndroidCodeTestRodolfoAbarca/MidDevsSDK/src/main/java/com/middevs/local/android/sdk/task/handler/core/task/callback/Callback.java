/******************************************************************************
 * Copyright © 2015-7532 NEX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights reserved. * * The source
 * code, other & all material, and documentation               * contained herein are, and remains the property of HEX
 * Inc.             * and its suppliers, if any. The intellectual and technical * concepts contained herein are
 * proprietary to HEX Inc. and its          * suppliers and may be covered by U.S. and Foreign Patents, patents      *
 * in process, and are protected by trade secret or copyright law.        * Dissemination of the foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.middevs.local.android.sdk.task.handler.core.task.callback;

import com.middevs.local.android.sdk.json.JSONObject;

/**
 * @author MidDevs
 * @since 8/8/16
 */
public interface Callback<R> {

    void onResult(R result);

    void onError(JSONObject e);


}
