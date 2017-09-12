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

import com.middevs.local.db.entry.FieldEntry;
import com.middevs.local.db.entry.ObjectEntry;

import static com.middevs.local.db.core.MidDevLDB.db;

/**
 * Created by ubuntu on 2/16/17.
 */

class DBDeleteHelper {

    private final static DBDeleteHelper DB_DELETE_HELPER = new DBDeleteHelper();

    DBDeleteHelper() {

    }

    public static DBDeleteHelper getInstance() {

        return DB_DELETE_HELPER;
    }

    void deleteObjectFields(String mid,
                            String fieldChain,
                            String collectionName) {

        String t = "'" + mid + "'";
        if (mid.startsWith("'") && mid.endsWith("'")) t = mid;
        String SQLStatement = "DELETE FROM " + FieldEntry.TABLE_NAME + " WHERE " + FieldEntry.COLUMN_NAME_MID + " = " + t + " AND" + " "
                + FieldEntry.COLUMN_NAME_FIELD_CHAIN + " = '" + fieldChain + "' AND " + FieldEntry.COLUMN_NAME_COLLECTION_NAME
                + " = '" + collectionName + "'";
        db.rawQuery(SQLStatement, null)
                .close();
    }

    void deleteObjectFields(String mid,
                            String collectionName) {

        String t = "'" + mid + "'";
        if (mid.startsWith("'") && mid.endsWith("'")) t = mid;
        String SQLStatement = "DELETE FROM " + FieldEntry.TABLE_NAME + " WHERE " + FieldEntry.COLUMN_NAME_MID + " = " + t + " AND "
                + FieldEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName + "'";
        db.rawQuery(SQLStatement, null)
                .close();
    }

    void deleteObject(String mid,
                      String collectionName) {

        String x = mid.startsWith("'") && mid.endsWith("'") ? mid : "'" + mid + "'";
        String sqlStatement = "SELECT * FROM " + ObjectEntry.TABLE_NAME + " WHERE " + ObjectEntry.COLUMN_NAME_MID + " " + "= " + x + " AND "
                + ObjectEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName + "'";
        Cursor cursor = db.rawQuery(sqlStatement, null);
        while (cursor.moveToNext()) {
            String data = cursor.getString(cursor.getColumnIndex(ObjectEntry.COLUMN_NAME_JSON));
            if (data.startsWith(LDBConstants.SUFFIX_DB_FILES)) {
                MidDevLDB.context.deleteFile(data);
            }
        }
        cursor.close();
        sqlStatement = "DELETE FROM " + ObjectEntry.TABLE_NAME + " WHERE " + ObjectEntry.COLUMN_NAME_MID + " = " + x + " AND "
                + ObjectEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName + "'";
        db.execSQL(sqlStatement);
    }


    void clearDB() {

        for (int i = 0; i < 10; ++i)
            System.out.println("clearDB");
        String sqlStatement = "DELETE FROM " + ObjectEntry.TABLE_NAME + " WHERE " + ObjectEntry._ID + " > -1";
        db.execSQL(sqlStatement);
        sqlStatement = "DELETE FROM " + FieldEntry.TABLE_NAME + " WHERE " + FieldEntry._ID + " > -1";
        db.execSQL(sqlStatement);
        String[] fileNames = MidDevLDB.context.fileList();
        for (String fn : fileNames) {
            if (fn.startsWith(LDBConstants.SUFFIX_DB_FILES)) {
                MidDevLDB.context.deleteFile(fn);
            }
        }
    }

    void deleteModelDBFiles(String mid) {

        String[] fileNames = MidDevLDB.context.fileList();
        for (String fn : fileNames) {
            if (fn.startsWith(LDBConstants.SUFFIX_DB_FILES + mid)) {
                MidDevLDB.context.deleteFile(fn);
            }
        }
    }
}
