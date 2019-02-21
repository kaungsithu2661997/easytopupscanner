package com.example.mit.easytopupscanner;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private Handler handler;
    private TextView textView;
    private long startTime, currentTime, finishedTime = 0L;
    private int duration = 22000 / 4;
    private int endTime = 0;
    private static int SPLASH_TIME_OUT = 3000;
    SharedPreferences preferences;
    SharedPreferences.Editor editor; public static String myfontss="zaw";
    String sharevalue="";
    String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA
    };
    public static final int request_id_multiple_permissions = 10;
    private boolean multicheckPermissions() {
        int result;
        List<String> list = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                list.add(p);
            }
        }
        if (!list.isEmpty()) {
            ActivityCompat.requestPermissions(this, list.toArray(new String[list.size()]), request_id_multiple_permissions);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case request_id_multiple_permissions: {
                if (grantResults.length > 0) {
                    String permissionDined = "";

                    for (String per : permissions) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            permissionDined = "\n" + per;
                        }
                    }
                 //   checkOperator();
                }
                return;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.mit.easytopupscanner.R.layout.activity_splash);
        //request permissions
        if (multicheckPermissions()) {
        }

        ImageView imageView = (ImageView) findViewById(com.example.mit.easytopupscanner.R.id.splesh_image);
        Glide.with(getApplicationContext()).load(com.example.mit.easytopupscanner.R.raw.mmflag).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(imageView);

        textView = (TextView) findViewById(com.example.mit.easytopupscanner.R.id.splesh_textview);
        textView.setText(getString(com.example.mit.easytopupscanner.R.string.app_name));
        handler = new Handler();

        startTime = Long.valueOf(System.currentTimeMillis());
        currentTime = startTime;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                currentTime = Long.valueOf(System.currentTimeMillis());
                finishedTime = Long.valueOf(currentTime)
                        - Long.valueOf(startTime);
                if (finishedTime >= duration + 30) {

                } else {
                }
            }
        }, 10);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
            }
        }, SPLASH_TIME_OUT);
    }


}