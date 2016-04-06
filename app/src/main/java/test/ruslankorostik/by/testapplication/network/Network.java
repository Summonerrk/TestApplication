package test.ruslankorostik.by.testapplication.network;


import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;


public class Network extends Loader<Bitmap> {

    private HttpURLConnection urlConnection = null;
    String URL;
    Task task;

    public Network(Context context, Bundle args) {
        super(context);
        if (args != null)
            URL = args.getString("URL");
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();

        if (task != null)
            task.cancel(true);
        task = new Task();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
    }

    @Override
    protected void onReset() {
        super.onReset();
    }

    void getResultFromTask(Bitmap result) {
        deliverResult(result);
    }

    class Task extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap btm = null;

            try {
                java.net.URL url = new java.net.URL (URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.connect();
                InputStream input = urlConnection.getInputStream();
                btm = BitmapFactory.decodeStream(input);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return btm;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            getResultFromTask(result);
        }

    }
}
