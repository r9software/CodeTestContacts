/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights reserved. * * The source
 * code, other & all material, and documentation               * contained herein are, and remains the property of HEX
 * Inc.             * and its suppliers, if any. The intellectual and technical * concepts contained herein are
 * proprietary to NOX Inc. and its          * suppliers and may be covered by U.S. and Foreign Patents, patents      *
 * in process, and are protected by trade secret or copyright law.        * Dissemination of the foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.middevs.local.db.common.sql;


import com.middevs.local.android.sdk.json.JSONArray;
import com.middevs.local.android.sdk.json.JSONException;
import com.middevs.local.android.sdk.json.JSONObject;
import com.middevs.local.db.exception.MidDevsSQLFormatException;

import java.util.List;


/**
 * @author MidDevs
 * @since 1/5/17
 */
public class SQLStatementBuilder {

    public static String buildSQLStatement(JSONObject sql,
                                           String TABLE_NAME)
            throws
            MidDevsSQLFormatException {

        StringBuilder builder = new StringBuilder();
        try {
            String type = sql.getString("type");
            if (type.equals("FIND")) {
                builder.append("SELECT ");
                JSONArray projection;
                if (sql.has("projection")) {
                    projection = sql.getJSONArray("projection");
                    for (int i = 0; i < projection.length(); ++i) {
                        builder.append(projection.get(i));
                        if (i != projection.length() - 1) builder.append(", ");
                        else builder.append(" ");
                    }
                } else {
                    if (sql.has("COUNT") && sql.getBoolean("COUNT")) builder.append(" count(*) ");
                    else builder.append(" * ");
                }
                builder.append("FROM " + TABLE_NAME);

                //###############################################################################################################
                if (sql.has("CE")) {
                    builder.append(" WHERE ");
                    builder.append(buildSQLConditionStatement(sql.getJSONObject("CE")));
                }
                //###############################################################################################################

                if (sql.has("GROUP")) {
                    JSONArray group = sql.getJSONArray("GROUP");
                    builder.append(" GROUP BY ");
                    for (int i = 0; i < group.length(); ++i) {
                        builder.append(group.get(i));
                        if (i != group.length() - 1) builder.append(",");
                        builder.append(" ");
                    }
                }
                if (sql.has("ORDER")) {
                    JSONObject order = sql.getJSONObject("ORDER");
                    builder.append(" ORDER BY ");
                    List<String> orders = order.keys();
                    for (int i = 0; i < orders.size(); ++i) {
                        String key = orders.get(i);
                        builder.append(key + " " + order.getString(key));
                        if (i != orders.size() - 1) builder.append(",");
                        builder.append(" ");
                    }
                }
                if (sql.has("LIMIT")) builder.append(" LIMIT ")
                        .append(sql.getInt("LIMIT"));
                if (sql.has("OFFSET")) builder.append(" OFFSET ")
                        .append(sql.getInt("OFFSET"));
            }
            if (type.equals("DELETE")) {
                builder.append("DELETE FROM " + TABLE_NAME);
                //###############################################################################################################
                if (sql.has("CE")) {
                    builder.append(" WHERE ");
                    builder.append(buildSQLConditionStatement(sql.getJSONObject("CE")));
                }
                //###############################################################################################################
            }

            if (type.equals("UPDATE")) {
                builder.append("UPDATE " + TABLE_NAME + " SET ");
                if (!sql.has("SET")) throw new MidDevsSQLFormatException("SET can't be empty");
                JSONObject set = sql.getJSONObject("SET");
                List<String> columns = set.keys();
                for (int i = 0; i < columns.size(); ++i) {
                    String col = columns.get(i);
                    String value = set.getString(col);
                    builder.append(col + " = " + value);
                    if (i != columns.size() - 1) builder.append(",");
                    builder.append(" ");
                }

                //###############################################################################################################
                if (sql.has("CE")) {
                    builder.append(" WHERE ");
                    builder.append(buildSQLConditionStatement(sql.getJSONObject("CE")));
                }
                //###############################################################################################################
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }


    public static String buildSQLConditionStatement(JSONObject sqlConditionStatementJSON)
            throws
            MidDevsSQLFormatException {

        if (sqlConditionStatementJSON == null) return null;
        Statement masterStatement = buildStatement(sqlConditionStatementJSON, null);
        return masterStatement.toSQLStatement();
    }

    private static Statement buildStatement(JSONObject statement,
                                            String fieldChain)
            throws
            MidDevsSQLFormatException {

        List<String> keys = statement.keys();
        Statement parsedStatement = new AndStatement();
        for (String key : keys) {

            if (key.startsWith("$")) {
                if (key.equals("$OR")) {
                    try {
                        parsedStatement.addStatement(buildOrStatement(statement.getJSONArray("$OR"), fieldChain));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String operator = key.substring(1);
                    try {
                        if (fieldChain == null)
                            throw new MidDevsSQLFormatException("missing field.chain an AND statement ");
                        BasicStatement basicStatement = new BasicStatement(fieldChain + " " + operator + " "
                                + statement.getString(key));
                        parsedStatement.addStatement(basicStatement);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                String fieldChain_ = key;
                try {
                    if (fieldChain != null)
                        throw new MidDevsSQLFormatException("fieldChain JSON inside another fieldChain JSON is "
                                + "illegal");
                    AndStatement _andStatement = buildFieldStatement(fieldChain_,
                            statement.getJSONObject(fieldChain_));
                    parsedStatement.addStatement(_andStatement);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return parsedStatement;
    }

    private static AndStatement buildFieldStatement(String fieldChain,
                                                    JSONObject jsonObject)
            throws
            JSONException,
            MidDevsSQLFormatException {

        if (fieldChain == null) throw new MidDevsSQLFormatException("fieldChain is null");
        List<String> keys = jsonObject.keys();
        AndStatement fieldStatement = new AndStatement();
        for (String key : keys) {
            if (key.startsWith("$")) {
                if (key.equals("$OR")) {
                    Statement orStatement = buildOrStatement(jsonObject.getJSONArray(key), fieldChain);
                    fieldStatement.addStatement(orStatement);
                } else {
                    String operator = key.substring(1);
                    if (operator.equals("IN") || operator.equals("NOT IN")) {
                        JSONArray array = jsonObject.getJSONArray(key);
                        String s = fieldChain + " " + operator + " (";
                        for (int i = 0; i < array.length(); ++i) {
                            s += array.get(i)
                                    .toString();
                            if (i != array.length() - 1) s += ",";
                        }
                        s += ")";
                        fieldStatement.addStatement(new BasicStatement(s));
                        continue;
                    }
                    String value = jsonObject.getString(key);
                    if (operator.equals("LIKE")) value = "'" + value + "'";
                    BasicStatement basicStatement = new BasicStatement(fieldChain + " " + operator + " " + value);
                    fieldStatement.addStatement(basicStatement);
                }
            } else {
                throw new MidDevsSQLFormatException("chain filed query statement must contain $OR or SQL " + "operators");
            }
        }
        return fieldStatement;
    }

    private static Statement buildOrStatement(JSONArray $OR,
                                              String fieldChain)
            throws
            JSONException,
            MidDevsSQLFormatException {

        Statement parsedStatement = new OrStatement();
        for (int i = 0; i < $OR.length(); ++i) {
            JSONObject jsonObject = $OR.getJSONObject(i);
            parsedStatement.addStatement(buildStatement(jsonObject, fieldChain));
        }
        return parsedStatement;
    }

}
