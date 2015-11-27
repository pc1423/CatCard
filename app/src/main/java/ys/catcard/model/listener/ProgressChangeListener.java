package ys.catcard.model.listener;

import java.io.Serializable;

import ys.catcard.annotation.UiThread;

public interface ProgressChangeListener extends Serializable {

    @UiThread
    void onProgressChanged(int totalAmount, int progress);

    @UiThread
    void onPreExecute();

    @UiThread
    void onPostExecute();
}