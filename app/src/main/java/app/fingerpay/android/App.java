package app.fingerpay.android;

import android.app.Application;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ubuntu.regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}

