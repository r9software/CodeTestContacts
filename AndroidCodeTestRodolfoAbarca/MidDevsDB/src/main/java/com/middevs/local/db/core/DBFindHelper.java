/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights reserved.
 * * * The source code, other & all material, and documentation               * contained herein
 * are, and remains the property of HEX Inc. * and its suppliers, if any. The intellectual and
 * technical              * concepts contained herein are proprietary to NOX Inc. and its          *
 * suppliers and may be covered by U.S. and Foreign Patents, patents      * in process, and are
 * protected by trade secret or copyright law. * Dissemination of the foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.middevs.local.db.core;

import android.database.Cursor;

import com.middevs.local.android.sdk.commons.Base64;
import com.middevs.local.android.sdk.commons.Utils;
import com.middevs.local.android.sdk.json.JSONException;
import com.middevs.local.android.sdk.json.JSONObject;
import com.middevs.local.android.sdk.json.serialization.MJSON;
import com.middevs.local.db.common.DBCommons;
import com.middevs.local.db.common.sql.SQLStatementBuilder;
import com.middevs.local.db.entry.FieldEntry;
import com.middevs.local.db.entry.ObjectEntry;
import com.middevs.local.db.exception.MidDevsSQLFormatException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.middevs.local.db.core.MidDevLDB.db;


/**
 * Created by ubuntu on 2/17/17.
 */

class DBFindHelper {

    private final static DBFindHelper DB_FIND_HELPER = new DBFindHelper();

    private DBFindHelper() {

    }

    public static DBFindHelper getInstance() {

        return DB_FIND_HELPER;
    }

    private static String decompressDataFromFile(String data) {

        String vs = data;
        if (data.startsWith(LDBConstants.SUFFIX_DB_FILES)) {
            String fileName = data;
            try {
                InputStream is = MidDevLDB.context.openFileInput(fileName);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = is.read(b)) != -1) {
                    bos.write(b, 0, bytesRead);
                }
                byte[] bytes = bos.toByteArray();
                vs = new String(bytes);
                bos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return vs;
    }

    /**
     * @param collectionName
     * @param mid
     * @return can return null
     */

    JSONObject getObject(String collectionName,
                         String mid) {

        String x = mid.startsWith("'") && mid.endsWith("'") ? mid : "'" + mid + "'";
        String sqlStatement = "SELECT * FROM " + ObjectEntry.TABLE_NAME + " WHERE " + ObjectEntry.COLUMN_NAME_MID + " " + "= " + x + " AND "
                + ObjectEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName + "'";
        Cursor cursor = db.rawQuery(sqlStatement, null);
        JSONObject jsonObject = null;
        while (cursor.moveToNext()) {
            try {
                String data = cursor.getString(cursor.getColumnIndex(ObjectEntry.COLUMN_NAME_JSON));
                data = decompressDataFromFile(data);
                data = new String(Base64.decodeBase64(data));
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
            }
        }
        cursor.close();
        return jsonObject;
    }

    Set<String> getMIDs(JSONObject condition,
                        int limit,
                        int offset) {

        Set<String> mIds = new HashSet<>();
        boolean firstQuery = true;
        if (condition != null) {
            for (String fc : condition.keys()) {
                JSONObject fcCondition = condition.optJSONObject(fc);
                JSONObject conditionStatement = DBCommons.parseGeneralConditionToSQLCondition(fc, fcCondition);
                JSONObject sqlStatementJSON = new JSONObject();
                try {
                    sqlStatementJSON.put("CE", conditionStatement);
                    sqlStatementJSON.put("type", "FIND");
                    if (limit >= 0) sqlStatementJSON.put("LIMIT", limit);
                    if (offset >= 0) sqlStatementJSON.put("OFFSET", offset);
                    String sqlStatement = SQLStatementBuilder.buildSQLStatement(sqlStatementJSON, FieldEntry.TABLE_NAME);
                    Cursor cursor = db.rawQuery(sqlStatement, null);
                    Set<String> set = new HashSet<>();
                    while (cursor.moveToNext()) {
                        String mid = cursor.getString(cursor.getColumnIndex(FieldEntry.COLUMN_NAME_MID));
                        set.add(mid);
                    }
                    if (firstQuery) mIds.addAll(set);
                    else mIds = Utils.findCommonSet(mIds, set);
                    firstQuery = false;
                    cursor.close();
                } catch (JSONException | MidDevsSQLFormatException e) {
                    e.printStackTrace();
                }
            }
        } else {
            String sqlStatement = "SELECT * FROM " + FieldEntry.TABLE_NAME + " GROUP BY " + FieldEntry.COLUMN_NAME_MID;
            Cursor cursor = db.rawQuery(sqlStatement, null);
            while (cursor.moveToNext()) {
                String mid = cursor.getString(cursor.getColumnIndex(FieldEntry.COLUMN_NAME_MID));
                mIds.add(mid);
            }
            cursor.close();
        }
        return mIds;
    }

    List<JSONObject> getAllObjects(String collectionName) {

        Cursor cursor = db.rawQuery("SELECT " + ObjectEntry.COLUMN_NAME_JSON + " FROM " + ObjectEntry.TABLE_NAME + " WHERE "
                + ObjectEntry.COLUMN_NAME_COLLECTION_NAME + "='" + collectionName + "'", null);
        List<JSONObject> objects = new ArrayList<>();
        while (cursor.moveToNext()) {
            String data = cursor.getString(cursor.getColumnIndex(ObjectEntry.COLUMN_NAME_JSON));
            if (data != null) {
                data = new String(Base64.decodeBase64(data));
                data = decompressDataFromFile(data);
                objects.add(MJSON.toJSON(data));
            }
        }
        cursor.close();
        return objects;
    }

    int count(JSONObject condition)
            throws
            MidDevsSQLFormatException {

        String sqlStatement;
        if (condition != null) {
            String sqlStatementString = SQLStatementBuilder.buildSQLConditionStatement(condition);
            sqlStatement = "SELECT COUNT(*) FROM " + FieldEntry.TABLE_NAME + " " + (sqlStatementString == null ? "" : sqlStatementString)
                    + " GROUP BY " + FieldEntry.COLUMN_NAME_MID;

        } else {
            sqlStatement = "SELECT COUNT(*) FROM " + FieldEntry.TABLE_NAME + " GROUP BY " + FieldEntry.COLUMN_NAME_MID;
        }
        Cursor cursor = db.rawQuery(sqlStatement, null);
        int count = 0;
        while (cursor.moveToNext()) count += cursor.getInt(1);
        cursor.close();
        return count;
    }
}
