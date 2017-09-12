package com.middevs.local.db.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.middevs.local.android.sdk.json.JSONObject;
import com.middevs.local.android.sdk.json.serialization.MJSON;
import com.middevs.local.android.sdk.task.handler.core.engine.ITaskEngine;
import com.middevs.local.android.sdk.task.handler.core.engine.TaskListener;
import com.middevs.local.android.sdk.task.handler.core.task.Task;
import com.middevs.local.db.core.callback.CallbackFind;
import com.middevs.local.db.exception.MidDevsSQLFormatException;
import com.middevs.local.db.model.MModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ubuntu on 2/15/17.
 */

public class MidDevLDB {

    private final static String keyValueCacheName = "MISHA-DB-Cache";

    private final static String CACHE_KEY_DB_VERSION = "DB_VERSION";
    private final static MidevsCache MISHA_CACHE = new MidevsCache();
    static SQLiteDatabase db;
    static Context context;
    private static MidDevLDB MISHA_LDB = null;
    private LDBHelper LDB_HELPER;

    private ITaskEngine iTaskRunner;

    private SharedPreferences keyValueCache;

    private MidDevLDB(Context context,
                      ITaskEngine taskRunner) {

        MidDevLDB.context = context;
        LDB_HELPER = new LDBHelper(context, taskRunner);
        iTaskRunner = taskRunner;
        keyValueCache = context.getSharedPreferences(keyValueCacheName, Context.MODE_PRIVATE);
        db = LDB_HELPER.getWritableDatabase();
    }

    public static MidDevLDB getInstance(Context context,
                                        ITaskEngine taskRunner) {

        if (MISHA_LDB == null) MISHA_LDB = new MidDevLDB(context, taskRunner);
        return MISHA_LDB;
    }

    public MidevsCache getMishaCache() {

        return MISHA_CACHE;
    }

    public void save(MModel object) {


        DBSaveHelper.getInstance()
                .save(object);
    }

    public void saveAsync(MModel object) {

        iTaskRunner.add(new Task() {

            @Override
            public void execute(ITaskEngine iTaskRunner,
                                TaskListener listener) {

                save(object);
                listener.finish();
            }
        });
    }

    public void delete(JSONObject condition,
                       String collectionName) {

        DBDeleteHelper dbDeleteHelper = DBDeleteHelper.getInstance();
        if (condition != null && condition.keys()
                .contains("mid")) {
            JSONObject midCondition = condition.optJSONObject("mid");
            String mid = midCondition.optString(midCondition.keys()
                    .get(0));
            dbDeleteHelper.deleteObject(mid, collectionName);
        } else {
            Set<String> mids = DBFindHelper.getInstance()
                    .getMIDs(condition, -1, -1);
            for (String mid : mids) {
                dbDeleteHelper.deleteObjectFields(mid, collectionName);
                dbDeleteHelper.deleteObject(mid, collectionName);
                dbDeleteHelper.deleteModelDBFiles(mid);
            }
        }
    }

    public <T extends MModel> void delete(JSONObject condition,
                                          Class<T> clazz) {

        String collectionName = MModel.getModelName(clazz);
        delete(condition, collectionName);
    }

    public void deleteAsync(final JSONObject condition,
                            final String collectionName) {

        iTaskRunner.add(new Task() {

            @Override
            public void execute(ITaskEngine iTaskRunner,
                                TaskListener listener) {

                delete(condition, collectionName);
                listener.finish();
            }
        });
    }

    public void cleanSlate() {

        DBDeleteHelper.getInstance()
                .clearDB();
    }

    public List<JSONObject> fetchAllObjects(String collectionName) {

        return DBFindHelper.getInstance()
                .getAllObjects(collectionName);
    }

    public <T extends MModel> List<T> fetchAllObjects(Class<T> clazz) {

        List<T> result = new ArrayList<>();
        String collectionName = MModel.getModelName(clazz);
        List<JSONObject> objects = fetchAllObjects(collectionName);
        for (JSONObject object : objects) {
            result.add((T) MJSON.fromJSON(object, clazz));
        }
        return result;
    }

    public List<JSONObject> find(JSONObject query,
                                 int limit,
                                 int offset,
                                 String collectionName) {

        List<JSONObject> objects = new ArrayList<>();
        if (query != null && query.keys()
                .contains("mid")) {
            JSONObject midCondition = query.optJSONObject("mid");
            String mid = midCondition.optString(midCondition.keys()
                    .get(0));
            JSONObject object = findOne(mid, collectionName);
            if (object != null) objects.add(object);
        } else {
            Set<String> mids = DBFindHelper.getInstance()
                    .getMIDs(query, limit, offset);
            for (String mid : mids) {
                JSONObject object = findOne(mid, collectionName);
                if (object != null) objects.add(object);
            }
        }
        return objects;
    }

    /**
     * @param query
     * @param limit  <0 ignore
     * @param offset <0 ignore
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends MModel> List<T> find(JSONObject query,
                                           int limit,
                                           int offset,
                                           Class<T> clazz) {

        List<T> result = new ArrayList<>();
        String collectionName = MModel.getModelName(clazz);
        List<JSONObject> objects = find(query, limit, offset, collectionName);
        for (JSONObject object : objects) {
            result.add((T) MJSON.fromJSON(object, clazz));
        }
        return result;
    }

    public <T extends MModel> void findAsync(JSONObject query,
                                             int limit,
                                             int offset,
                                             Class<T> clazz,
                                             CallbackFind<T> callback) {

        if (callback == null) throw new NullPointerException("callback can't be null");
        iTaskRunner.add(new Task() {

            @Override
            public void execute(ITaskEngine iTaskRunner,
                                TaskListener listener) {

                List<T> result = find(query, limit, offset, clazz);
                callback.onResult(result);
                listener.finish();
            }
        });
    }

    public JSONObject findOne(String mid,
                              String collectionName) {

        mid = mid.replace("'", "");
        return DBFindHelper.getInstance()
                .getObject(collectionName, mid);
    }

    public <T extends MModel> T findOne(String mid,
                                        Class<T> clazz) {

        String collectionName = MModel.getModelName(clazz);
        JSONObject object = findOne(mid, collectionName);
        if (object != null) return (T) MJSON.fromJSON(object, clazz);
        else return null;
    }

    public <T extends MModel> T findOne(JSONObject query,
                                        Class<T> clazz) {

        List<T> tList = find(query, 1, 0, clazz);
        if (tList.isEmpty()) return null;
        else return tList.get(0);
    }

    public int count(JSONObject query)
            throws
            MidDevsSQLFormatException {

        return DBFindHelper.getInstance()
                .count(query);
    }
}
