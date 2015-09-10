package ys.catcard;

import com.facebook.drawee.backends.pipeline.Fresco;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLibs();
    }

    private void initializeLibs() {
        Fresco.initialize(getApplicationContext());
    }
}
