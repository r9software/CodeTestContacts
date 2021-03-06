package com.middevs.local.db.model;


import com.middevs.local.android.sdk.commons.Utils;
import com.middevs.local.android.sdk.json.JSONException;
import com.middevs.local.android.sdk.json.JSONObject;
import com.middevs.local.db.core.MidDevLDB;

import java.io.Serializable;

/**
 * Created by ubuntu on 2/15/17.
 */

public abstract class MModel
        implements Serializable {

    protected final static String REMOVAL_PREFIX_SYMBOL = "_$";

    protected String mid;

    protected String createdDate;

    protected String updatedDate;

    protected long createTime;

    protected long updateTime;

    protected boolean deleted;

    protected int version;

    public MModel() {

    }

    public static <T extends MModel> String getModelName(Class<T> clazz) {

        try {
            MModel mModel = clazz.newInstance();
            return mModel.modelName();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public static <T extends MModel> JSONObject $modelMigration(JSONObject rowModel,
                                                                Class<T> clazz,
                                                                int newVersion) {

        int version = rowModel.optInt("version", 0);
        if (newVersion != version) {
            try {
                MModel mModel = clazz.newInstance();
                IMigrationProtocol iMigrationProtocol = mModel.$getMigrationProtocol(newVersion);
                if (iMigrationProtocol != null) return iMigrationProtocol.migrate(rowModel);
                else throw new NullPointerException("no migration protocol was found");
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return rowModel;
    }

    public static <E extends MModel> void $deleteDB(E mModel,
                                                    MidDevLDB db) {

        JSONObject condition = new JSONObject();
        JSONObject subCondition = new JSONObject();
        try {
            subCondition.put("$=", "'" + mModel.getMid() + "'");
            condition.put("mid", subCondition.clone());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.delete(condition, mModel.modelName());
    }

    public static <E extends MModel> void $saveDB(E mModel,
                                                  MidDevLDB db) {

        try {
            mModel.updatedDate = Utils.getCurrentFormattedUTCTime();
            mModel.updateTime = Utils.getCurrentUTCTime();
            db.save(mModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract String modelName();

    protected abstract IMigrationProtocol $getMigrationProtocol(int version);

    public String getMid() {

        return mid;
    }

    public void setMid(String mid) {

        this.mid = mid;
    }

    public int getVersion() {

        return version;
    }

    public void setVersion(int version) {

        this.version = version;
    }

    public String getCreatedDate() {

        return createdDate;
    }

    public void setCreatedDate(String createdDate) {

        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {

        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {

        this.updatedDate = updatedDate;
    }

    public long getCreateTime() {

        return createTime;
    }

    public void setCreateTime(long createTime) {

        this.createTime = createTime;
    }

    public long getUpdateTime() {

        return updateTime;
    }

    public void setUpdateTime(long updateTime) {

        this.updateTime = updateTime;
    }

    public boolean getDeleted() {

        return deleted;
    }

    public void setDeleted(boolean deleted) {

        this.deleted = deleted;
    }

    public abstract void $saveDB();

    public abstract void $delete(boolean keep);

}
