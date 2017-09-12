/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights
 * reserved. * * The source
 * code, other & all material, and documentation               * contained herein are, and
 * remains the property of HEX
 * Inc.             * and its suppliers, if any. The intellectual and technical * concepts
 * contained herein are
 * proprietary to HEX Inc. and its          * suppliers and may be covered by U.S. and Foreign
 * Patents, patents      *
 * in process, and are protected by trade secret or copyright law.        * Dissemination of the
 * foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.middevs.local.android.sdk.rest.client;

import android.util.Log;

import com.middevs.local.android.sdk.commons.Utils;
import com.middevs.local.android.sdk.json.JSONException;
import com.middevs.local.android.sdk.json.JSONObject;
import com.middevs.local.android.sdk.task.handler.core.engine.ITaskEngine;
import com.middevs.local.android.sdk.task.handler.core.engine.TaskListener;
import com.middevs.local.android.sdk.task.handler.core.task.Task;
import com.middevs.local.android.sdk.task.handler.core.task.callback.Callback;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

/**
 * @author MidDevs
 * @since 10/10/16
 */
public final class WebClient {

    private final static String USER_AGENT = "Misha";
    private static WebClient WEB_CLIENT = null;
    private static ITaskEngine TASK_RUNNER;

    static {

        // Initialize configuration
        String path = "/tmp" + File.separator + ".misha" + File.separator + "cert" + File.separator
                + "misha.jks";
        System.setProperty("javax.net.ssl.trustStore", path);
        System.setProperty("javax.net.ssl.trustStoreType", "jks");
        //for localhost testing only
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            public boolean verify(String hostname,
                                  javax.net.ssl.SSLSession sslSession) {

                return !hostname.equals("localhost");
            }
        });
    }

    private WebClient() {

        TASK_RUNNER = ITaskEngine.buildTaskRunner(-101);
    }

    private WebClient(ITaskEngine ITaskRunner) {

        TASK_RUNNER = ITaskRunner;
    }

    public static WebClient getInstance() {

        if (WEB_CLIENT == null) WEB_CLIENT = new WebClient();
        return WEB_CLIENT;
    }

    private static String buildURL(String url,
                                   HashMap<String, String> urlParams) {

        if (url == null || url.isEmpty()) throw new NullPointerException("url can't be null");
        if (urlParams != null && !urlParams.isEmpty()) {
            StringBuffer builder = new StringBuffer(url);
            builder.append("?");
            for (String key : urlParams.keySet()) {
                builder.append(key);
                builder.append("=");
                builder.append(urlParams.get(key));
                builder.append("&");
            }
            builder.deleteCharAt(builder.length() - 1);
            url = builder.toString();
            url = url.replaceAll(" ", "%20");
        }
        return url;
    }

    private static RESTTask getRESTTask(final HashMap<String, String> headers,
                                        final JSONObject body,
                                        final String final_method,
                                        final String finalUrl,
                                        final Callback<JSONObject> callback) {

        RESTTask task = new RESTTask(callback) {

            @Override
            protected void callBackExecute(ITaskEngine iTaskRunner,
                                           TaskListener listener) {

                JSONObject result = new JSONObject();
                try {

                    URL url = new URL(finalUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(final_method);
                    connection.setRequestProperty("User-Agent", USER_AGENT);
                    connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    connection.setConnectTimeout(30000);
                    if (headers != null && !headers.isEmpty()) {
                        for (String key : headers.keySet())
                            connection.setRequestProperty(key, headers.get(key));
                    }
                    System.out.println("<x>\t" + finalUrl + "   " + final_method);
                    writeBody(body, final_method, connection);
                    int responseCode = connection.getResponseCode();
                    InputStream inputStream = null;
                    try {
                        inputStream = connection.getInputStream();
                    } catch (Exception ignored) {
                    }
                    if (inputStream == null) inputStream = connection.getErrorStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    StringBuilder response = new StringBuilder();
                    byte[] contents = new byte[1024 * 2];
                    int bytesRead;
                    try {
                        while ((bytesRead = bufferedInputStream.read(contents)) != -1) {
                            String x = new String(contents, 0, bytesRead);
                            response.append(x);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String responseString = response.toString();
                    responseString = Utils.convertToUTF8(responseString);
                    bufferedInputStream.close();
                    try {
                        inputStream.close();
                    } catch (Exception ignored) {
                    }
                    result = new JSONObject(responseString);
                    Log.v("RESTTask RESPONSE=", responseString);
                } catch (Exception e) {
                    result = JSONException.exceptionToJSON(e);
                } finally {
                    listener.setResult(result);
                    listener.finish();
                }
            }
        };
        return task;
    }

    private static void writeBody(JSONObject body,
                                  String final_method,
                                  HttpURLConnection connection)
            throws
            IOException {

        if (body != null && !final_method.equals("GET")) {
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(body.toString());
            wr.flush();
            wr.close();
        }
    }

    private static RESTTask getSecureRESTTask(final HashMap<String, String> headers,
                                              final JSONObject body,
                                              final String final_method,
                                              final String finalUrl,
                                              final Callback<JSONObject> callback) {

        RESTTask task = new RESTTask(callback) {

            @Override
            protected void callBackExecute(ITaskEngine iTaskRunner,
                                           TaskListener listener)
                    throws
                    Exception {

                JSONObject result = new JSONObject();
                try {
                    URL url = new URL(finalUrl);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod(final_method);
                    connection.setRequestProperty("User-Agent", USER_AGENT);
                    connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    connection.setConnectTimeout(30000);
                    if (headers != null && !headers.isEmpty()) {
                        for (String key : headers.keySet())
                            connection.setRequestProperty(key, headers.get(key));
                    }
                    writeBody(body, final_method, connection);
                    int responseCode = connection.getResponseCode();
                    InputStream inputStream = null;
                    try {
                        inputStream = connection.getInputStream();
                    } catch (Exception ignored) {
                    }
                    if (inputStream == null) inputStream = connection.getErrorStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    StringBuilder response = new StringBuilder();
                    byte[] contents = new byte[1024 * 2];
                    int bytesRead;
                    try {
                        while ((bytesRead = bufferedInputStream.read(contents)) != -1) {
                            String x = new String(contents, 0, bytesRead);
                            response.append(x);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String responseString = response.toString();
                    responseString = Utils.convertToUTF8(responseString);
                    bufferedInputStream.close();
                    try {
                        inputStream.close();
                    } catch (Exception ignored) {
                    }
                    result = new JSONObject(responseString);
                    Log.v("SecRESTTask RESPONSE=", responseString);
                } catch (Exception e) {
                    result = JSONException.exceptionToJSON(e);
                } finally {
                    listener.setResult(result);
                    listener.finish();
                }
            }
        };
        return task;
    }

    public void cUrl(String url,
                     RESTMethod method,
                     Callback<JSONObject> callback,
                     CUrlParameter... cUrlArgs) {

        TASK_RUNNER.add(new Task() {

            @Override
            public void execute(ITaskEngine iTaskRunner,
                                TaskListener listener) {

                JSONObject result = cUrl(url, method, cUrlArgs);
                callback.onResult(result);
                listener.setResult(result);
                listener.finish();
            }
        });
    }

    public JSONObject cUrl(String url,
                           RESTMethod method,
                           CUrlParameter... cUrlArgs) {

        String args[] = new String[(cUrlArgs != null ? 2 * cUrlArgs.length : 0) + 4];
        args[0] = "curl";
        args[1] = "-X";
        args[2] = method.toString();
        args[3] = url;
        if (cUrlArgs != null) {
            for (int i = 0; i < cUrlArgs.length; i++) {
                args[4 + 2 * i] = cUrlArgs[i].getFlag();
                args[4 + (2 * i + 1)] = cUrlArgs[i].getParameter();
            }
        }
        ProcessBuilder p = new ProcessBuilder(args);
        p.redirectErrorStream(true);
        try {
            final Process shell = p.start();
            InputStream errorStream = shell.getErrorStream();
            InputStream shellIn = shell.getInputStream();
            Scanner scanner = new Scanner(shellIn);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.startsWith("{")) {
                    builder.append(line);
                    break;
                }
            }
            while (scanner.hasNext()) {
                builder.append(scanner.nextLine());
            }
            scanner.close();
            return new JSONObject(builder.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return JSONException.exceptionToJSON(e);
        }
    }

    public WebClient call(RESTMethod method,
                          String url,
                          HashMap<String, String> urlParams,
                          HashMap<String, String> headers,
                          JSONObject body,
                          Callback<JSONObject> callback) {

        boolean https = url.contains("https");
        String _method = "";
        if (method == RESTMethod.GET) _method = "GET";
        if (method == RESTMethod.POST) _method = "POST";
        if (method == RESTMethod.PUT) _method = "PUT";
        if (method == RESTMethod.DELETE) _method = "DELETE";

        final String final_method = _method;
        final String finalUrl = buildURL(url, urlParams);
        if (https)
            TASK_RUNNER.add(getSecureRESTTask(headers, body, final_method, finalUrl, callback));
        else TASK_RUNNER.add(getRESTTask(headers, body, final_method, finalUrl, callback));
        System.out.println(
                "API Call  " + method.toString() + "  URL=" + url + " HEADERS=" + (headers
                        != null ?
                        headers.toString() : headers)
                        + "\nBODY=" + (body != null ? body.toString() : body));
        return this;
    }

    public JSONObject call(RESTMethod method,
                           String url,
                           HashMap<String, String> urlParams,
                           HashMap<String, String> headers,
                           JSONObject body) {

        final MLock LOCK = new MLock();
        final JSONObject[] jsonObject = {null};
        call(method, url, urlParams, headers, body, new Callback<JSONObject>() {

            @Override
            public void onResult(JSONObject result) {

                jsonObject[0] = result;
                LOCK.notifyLock();
            }

            @Override
            public void onError(JSONObject e) {

                jsonObject[0] = e;
                LOCK.notifyLock();
            }
        });
        LOCK.hold();
        return jsonObject[0];
    }

    public void terminate() {

        TASK_RUNNER.stop();
    }

    public synchronized void cancel() {

        synchronized (this) {
            TASK_RUNNER.clearTasks();
        }
    }

    public ITaskEngine getTaskRunner() {

        return TASK_RUNNER;
    }


//	 public
//	 void restart ( )
//	 {
//
//			TASK_RUNNER = ITaskRunner.buildTaskRunner ( -1 );
//	 }

    public enum RESTMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        private String name;

        RESTMethod(String name) {

            this.name = name;
        }

        @Override
        public String toString() {

            return name;
        }
    }
}
