package me.zeeroooo.materialfb;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by ZeeRooo on 15/04/18
 */

@ReportsCrashes(mailTo = "chavesjuan400@gmail.com")
public class MFB extends Application {
    public static int colorPrimary, colorPrimaryDark, colorAccent, textColor;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this);
    }
}
