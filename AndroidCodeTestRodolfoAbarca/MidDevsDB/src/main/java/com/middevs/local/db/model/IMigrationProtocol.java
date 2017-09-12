package com.middevs.local.db.model;

import com.middevs.local.android.sdk.json.JSONObject;

/**
 * @author MidDevs
 * @since 4/27/17
 */
public interface IMigrationProtocol {

    JSONObject migrate(JSONObject rowModel);
}