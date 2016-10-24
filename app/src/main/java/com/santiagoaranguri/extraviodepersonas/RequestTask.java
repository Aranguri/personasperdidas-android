package com.santiagoaranguri.extraviodepersonas;

import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask usado para mandar requerimientos HTTP GET.
 */
class RequestTask extends AsyncTask<String, Void, String> {
    Context context;
    OnReadyCallback callback;
    public RequestTask(Context context, OnReadyCallback callback){
        this.context=context;
        this.callback = callback;
    }
    /**
     * Task principal. Manda el requerimiento a la url pasada como parámetro y devuelve la respuesta del servidor.
     * @param url Url a la que se mandará el requerimiento.
     * @return Respuesta del servidor.
     */
    @Override
    protected String doInBackground(String... url) {
        String responseString=null;
        HttpURLConnection connection=null;
        try {
            if(Util.canConnect(context)) {
                connection = (HttpURLConnection) new URL(url[0]).openConnection();
                if (connection.getResponseCode() == 200) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuilder result = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    responseString = result.toString();
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return responseString;
    }

    /**
     * Ejecutado luego del doInBackground.
     * Devuelve la respuesta del servidor al callback.
     * @param result Respuesta del servidor.
     */
    @Override
    protected void onPostExecute(String result) {
        callback.onReady(result);
        super.onPostExecute(result);
    }

    interface OnReadyCallback{
        void onReady(String response);
    }
}