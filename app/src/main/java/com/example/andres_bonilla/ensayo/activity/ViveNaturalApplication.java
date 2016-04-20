package com.example.andres_bonilla.ensayo.activity;

import android.app.Application;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;

import com.batch.android.Batch;
import com.batch.android.Config;
import com.example.andres_bonilla.ensayo.R;

public class ViveNaturalApplication extends Application {
    private static ViveNaturalApplication instance;

    public static ViveNaturalApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        instance = this;

        Batch.setConfig(new Config(com.example.andres_bonilla.ensayo.activity.Config.BATCH_API_KEY));
        Batch.Push.setGCMSenderId(com.example.andres_bonilla.ensayo.activity.Config.GCM_SENDER_ID);
        Batch.Push.setNotificationsColor(ContextCompat.getColor(this, R.color.colorPrimary));
        Batch.Push.setSmallIconResourceId(R.drawable.logo);
        Batch.Push.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher_vive_natural));
    }
}
