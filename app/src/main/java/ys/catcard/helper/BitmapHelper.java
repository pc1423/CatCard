package ys.catcard.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ys.catcard.model.listener.ProgressChangeListener;
import ys.catcard.model.Size;

public class BitmapHelper {

    @NonNull
    public static Size bitmapSizeFromUrl(String imageURL, final ProgressChangeListener progressChangeListener){

        try {
            byte[] datas = getImageDataFromUrl(new URL(imageURL), progressChangeListener);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(datas, 0, datas.length, opts);

            int width = opts.outWidth;
            int height = opts.outHeight;

            return new Size(width, height);
        } catch (IOException e) {
            return null;
        }
    }

    @NonNull
    public Bitmap bitmapFromUrl(String imageURL, ProgressChangeListener progressChangeListener){
        try {
            byte[] datas = getImageDataFromUrl(new URL(imageURL), progressChangeListener);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            return BitmapFactory.decodeByteArray(datas, 0, datas.length, opts);
        } catch (IOException e) {
            return null;
        }
    }

    @NonNull
    public static byte[] getImageDataFromUrl(URL url, ProgressChangeListener progressChangeListener) {
        byte[] datas = {};

        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            datas = inputStreamToByteArray(input, connection.getContentLength(), progressChangeListener);

            input.close();
            connection.disconnect();
        } catch (IOException e) {}

        return datas;
    }

    @NonNull
    public static byte[] inputStreamToByteArray(InputStream is, int totalSize, final ProgressChangeListener listener) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        int nBytesReceived = 0;
        byte[] data = new byte[500];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            listener.onProgressChanged(totalSize, nBytesReceived);

            buffer.write(data, 0, nRead);
            nBytesReceived += nRead;

            SystemClock.sleep(6);
        }

        listener.onPostExecute();

        buffer.flush();
        return buffer.toByteArray();
    }


}
