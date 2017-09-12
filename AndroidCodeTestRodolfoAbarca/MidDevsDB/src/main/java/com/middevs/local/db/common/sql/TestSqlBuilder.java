/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights reserved. * * The source
 * code, other & all material, and documentation               * contained herein are, and remains the property of HEX
 * Inc.             * and its suppliers, if any. The intellectual and technical * concepts contained herein are
 * proprietary to NOX Inc. and its          * suppliers and may be covered by U.S. and Foreign Patents, patents      *
 * in process, and are protected by trade secret or copyright law.        * Dissemination of the foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.middevs.local.db.common.sql;


import com.middevs.local.android.sdk.json.JSONException;
import com.middevs.local.android.sdk.json.JSONObject;
import com.middevs.local.db.exception.MidDevsSQLFormatException;

/**
 * @author MidDevs
 * @since 1/5/17
 */
public class TestSqlBuilder {

    public static void main(String[] args)
            throws
            JSONException,
            MidDevsSQLFormatException {

        String json = "{\"type\":\"FIND\",\"collection_name\":\"NAME\",\"projection\":[\"col1\",\"col2\",\"col3\"],"
                + "\"CE\":{\"field-chain1\":{\"$OR\":[{\"$=\":\"value1\"},{\"$<=>\":\"value2\"}]},"
                + "\"field-chain2\":{\"$OR\":[{\"$>\":123,\"$<=\":45643},{\"$!=\":8947593}],"
                + "\"$LIKE\":\"_LIKE_STATEMENT_\",\"$IN\":[\"'v1'\","
                + "\"'v2'\",\"'v3'\",\"'v4'\"]},\"field-chain3\":{\"$!=\":\"x\"}},\"LIMIT\":20,\"OFFSET\":100,"
                + "\"COUNT\":true,\"ORDER\":{\"field" + "-name1\":\"DESC\",\"field-name2\":\"ASC\"}}";
        json = "{\n" + "  \"CE\": {\n" + "    \"HASH\": {\n" + "      \"$OR\": [\n" + "        {\n"
                + "          \"$<=\": 1526590196,\n" + "          \"$>=\": 1524856162\n" + "        },\n" + "        {\n"
                + "          \"$<=\": 1903002478,\n" + "          \"$>=\": 1900959174\n" + "        },\n" + "        {\n"
                + "          \"$<=\": 1935436143,\n" + "          \"$>=\": 1933389819\n" + "        }\n" + "      ]\n"
                + "    }\n" + "  },\n" + "  \"type\": \"FIND\",\n" + "  \"GROUP\": [\n" + "    \"MID\"\n" + "  ]\n" + "}";
        JSONObject object = new JSONObject(json);
        String sql = SQLStatementBuilder.buildSQLStatement(object, "MISHA");
        System.out.println(sql);

    }
}
