package com.middevs.local.db.core;

import android.content.Context;

import com.middevs.local.android.sdk.commons.Base64;
import com.middevs.local.android.sdk.json.serialization.MJSON;
import com.middevs.local.db.common.DBCommons;
import com.middevs.local.db.entry.FieldEntry;
import com.middevs.local.db.entry.ObjectEntry;
import com.middevs.local.db.json.FlattenJSONObject;
import com.middevs.local.db.json.JSONHelper;
import com.middevs.local.db.model.MModel;

import java.io.FileOutputStream;
import java.util.List;

import static com.middevs.local.db.common.DBCommons.FIELD_TYPE_BOOLEAN;
import static com.middevs.local.db.common.DBCommons.FIELD_TYPE_DOUBLE;
import static com.middevs.local.db.common.DBCommons.FIELD_TYPE_INTEGER;
import static com.middevs.local.db.common.DBCommons.FIELD_TYPE_LONG;
import static com.middevs.local.db.common.DBCommons.FIELD_TYPE_STRING;
import static com.middevs.local.db.common.DBCommons.escapeString$QueryExecute;
import static com.middevs.local.db.core.LDBConstants.MAXIMUM_DATA_SIZE;
import static com.middevs.local.db.core.LDBConstants.SUFFIX_DB_FILES;
import static com.middevs.local.db.core.MidDevLDB.db;
import static com.middevs.local.db.entry.ObjectEntry.COLUMN_NAME_COLLECTION_NAME;
import static com.middevs.local.db.entry.ObjectEntry.COLUMN_NAME_HASH;
import static com.middevs.local.db.entry.ObjectEntry.COLUMN_NAME_JSON;
import static com.middevs.local.db.entry.ObjectEntry.COLUMN_NAME_MID;

/**
 * Created by ubuntu on 2/16/17.
 */

class DBSaveHelper {

    private final static DBSaveHelper DB_SAVE_HELPER = new DBSaveHelper();

    private DBSaveHelper() {

    }

    public static DBSaveHelper getInstance() {

        return DB_SAVE_HELPER;
    }

    private static String compressLargeDataToFile(String data,
                                                  MModel object,
                                                  String fc) {

        String vs = data;
        if (data.length() > MAXIMUM_DATA_SIZE) {
            //SAVE in FILE
            String fileName = SUFFIX_DB_FILES + object.getMid() + (fc != null ? "$" + fc : "");
            FileOutputStream outputStream;
            try {
                outputStream = MidDevLDB.context.openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(data.getBytes());
                outputStream.close();
                vs = fileName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vs;
    }

    void save(MModel object) {

        DBDeleteHelper dbDeleteHelper = DBDeleteHelper.getInstance();
        dbDeleteHelper.deleteObject(object.getMid(), object.modelName());
        saveModel(object);
        saveObjectFields(object);
    }

    private void saveModel(MModel mModel) {

        String deleteStatement = "DELETE FROM " + ObjectEntry.TABLE_NAME + " WHERE " + COLUMN_NAME_MID + " = '" + mModel.getMid() + "'";
        db.execSQL(deleteStatement);
        String vs = Base64.toBase64(MJSON.toJSON(mModel)
                .toString()
                .getBytes());
        vs = compressLargeDataToFile(vs, mModel, null);
        String saveStatement = "INSERT INTO " + ObjectEntry.TABLE_NAME + " (" + COLUMN_NAME_MID + ", " + COLUMN_NAME_JSON + ", "
                + COLUMN_NAME_HASH + ", " + COLUMN_NAME_COLLECTION_NAME + ")" + " VALUES (" + "'" + mModel.getMid() + "'"
                + ", " + "'" + vs + "'" + ", " + 0 + ", " + "'" + mModel.modelName() + "'" + ")";
        db.execSQL(saveStatement);
    }

    private void saveObjectFields(MModel object) {

        FlattenJSONObject flattenJSONObject = JSONHelper.flattenObjectFields(MJSON.toJSON(object));
        List<String> fieldChains = flattenJSONObject.$getFieldChains();
        DBDeleteHelper dbDeleteHelper = DBDeleteHelper.getInstance();
        for (String fc : fieldChains) {
            dbDeleteHelper.deleteObjectFields(object.getMid(), fc, object.modelName());
            List<Object> values = flattenJSONObject.$getValues(fc);
            for (Object v : values) {
                if (v == null) continue;
                String type = DBCommons.getDBFieldType(v);
                String deleteStatement = "DELETE FROM " + FieldEntry.TABLE_NAME + " WHERE " + FieldEntry.COLUMN_NAME_MID + " = '"
                        + object.getMid() + "' AND " + FieldEntry.COLUMN_NAME_FIELD_CHAIN + " = '" + fc + "' AND "
                        + FieldEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + object.modelName() + "' AND "
                        + FieldEntry.COLUMN_NAME_FIELD_TYPE + " = '" + type + "'";
                db.execSQL(deleteStatement);

                String saveStatement = "INSERT INTO " + FieldEntry.TABLE_NAME + " (" + FieldEntry.COLUMN_NAME_MID + ", "
                        + FieldEntry.COLUMN_NAME_FIELD_CHAIN + ", " + FieldEntry.COLUMN_NAME_HASH + ", "
                        + FieldEntry.COLUMN_NAME_COLLECTION_NAME + ", " + FieldEntry.COLUMN_NAME_FIELD_TYPE + ", "
                        + FieldEntry.COLUMN_NAME_VALUE_INT + ", " + FieldEntry.COLUMN_NAME_VALUE_DOUBLE + ", "
                        + FieldEntry.COLUMN_NAME_VALUE_STRING + ") VALUES (" + "'" + object.getMid() + "'" + ", " + "'" + fc
                        + "'" + ", " + 0 + ", " + "'" + object.modelName() + "'" + ", " + "'" + type + "', ";
                if (type.equals(FIELD_TYPE_INTEGER) || type.equals(FIELD_TYPE_LONG)) {
                    saveStatement = saveStatement + Long.parseLong(v.toString()) + ", " + 0.0 + ", 'NULL'" + ")";
                }
                if (type.equals(FIELD_TYPE_BOOLEAN)) {
                    saveStatement = saveStatement + ((Boolean) v ? 1L : 0L) + ", " + 0.0 + ", " + "'NULL'" + ")";
                }
                if (type.equals(FIELD_TYPE_DOUBLE)) {
                    saveStatement = saveStatement + 0 + ", " + Double.parseDouble(v.toString()) + ", 'NULL'" + ")";
                }
                if (type.equals(FIELD_TYPE_STRING)) {
                    String vs = escapeString$QueryExecute(v.toString());
                    vs = compressLargeDataToFile(vs, object, fc);
                    saveStatement = saveStatement + 0 + ", " + 0.0 + ", " + "'" + vs + "'" + ")";
                }
                db.execSQL(saveStatement);
            }
        }
    }
}
