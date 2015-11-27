package ys.catcard;

import com.facebook.drawee.backends.pipeline.Fresco;

import ys.catcard.helper.SharedPreferenceHelper;

public class Application extends android.app.Application {

    public static String SHARED_PREF_NAME = "favorite_url";

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLibs();
    }

    private void initializeLibs() {
        Fresco.initialize(getApplicationContext());
        SharedPreferenceHelper.init(SHARED_PREF_NAME, getApplicationContext());
    }

}
