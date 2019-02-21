/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mit.easytopupscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import com.example.mit.easytopupscanner.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mit.easytopupscanner.ui.camera.CameraSource;
import com.example.mit.easytopupscanner.ui.camera.CameraSourcePreview;
import com.example.mit.easytopupscanner.ui.camera.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

/**
 * Activity for the Ocr Detecting app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public final class OcrCaptureActivity extends AppCompatActivity implements MyCardInterface, DialogClickInterface {
    boolean isFlashLightOn = false;
    public static Camera cam = null;// has to be static, otherwise onDestroy() destroys it
    SurfaceView surfaceView;
    private Camera camera;
    private static final int CAMERA_REQUEST = 50;
    boolean flashLightStatus = false;
    private int identifier = 0;
    private static final String TAG = "OcrCaptureActivity";
    OcrDetectorProcessor ocrDetectorProcessor;
    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    Camera.Parameters p;
    boolean status;
    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    private GraphicOverlay<OcrGraphic> graphicOverlay;
    boolean hasCameraFlash;
    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    // A TextToSpeech engine for speaking a String value.
    private TextToSpeech tts;

    boolean flag;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ocr_capture);

        preview = (CameraSourcePreview) findViewById(R.id.preview);
        graphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);


        boolean autoFocus = true;
        boolean useFlash = false;
        flag = false;
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {


            // Permission is not granted
        }


        createCameraSource(true, false);

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        BottomNavigationView navigation = (BottomNavigationView) findViewById(com.example.mit.easytopupscanner.R.id.nvas);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }
    @Override
    public void onBackPressed()
    {
       OcrCaptureActivity.this.finish();
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(this);

        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialogalertbox_ocr);
        String myfont = MainActivity.fonttext;

        TextView title = (TextView) dialog.findViewById(R.id.title_id);
        TextView wayone = (TextView) dialog.findViewById(R.id.one);
        TextView waytwo = (TextView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.two);
        TextView waythree = (TextView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.three);
        ImageButton close = (ImageButton) dialog.findViewById(com.example.mit.easytopupscanner.R.id.close_id);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        initialize();
        String myfonts=preferences.getString("myfont","zaw");
        if (myfonts.equals("zaw")) {
            title.setText("ျမန္မာေငြျဖည့္ကဒ္စကင္နာအသံုးျပဳနည္း");
            wayone.setText("(1) camera ေ႐ွ႕တြင္ လူႀကီးမင္း၏ခဲျခစ္ထားေသာ ေငြျဖည့္ကဒ္ကို လက္ျဖင့္ကိုင္ထားေရြ႔ေသာ္လည္းေကာင္း တစ္စံုတစ္ခုေပၚတြင္တင္ထား၍ေသာ္လည္းေကာင္းထား႐ွိေပးပါ");
            waytwo.setText("(2) လက္ကိုညိမ္ညိမ္ထားပါ Scannerေပၚတြင္ Pin Numberကို ၾကည္လည္ျပတ္သားေအာင္ခ်ိန္ထားေပးပါ");
            waythree.setText("(3)Scanဖတ္ၿပီးေသာအခါMessage Boxေလးေပၚလာပါလိမ့္မည္");
        } else {
            title.setText("မြန်မာငွေဖြည့်ကဒ်စကင်နာအသုံးပြုနည်း");
            wayone.setText("(1) camera ရှေ့တွင် လူကြီးမင်း၏ခဲခြစ်ထားသော ငွေဖြည့်ကဒ်ကို လက်ဖြင့်ကိုင်ထားရွေ့သော်လည်းကောင်း တစ်စုံတစ်ခုပေါ်တွင်တင်ထား၍သော်လည်းကောင်းထားရှိပေးပါ");
            waytwo.setText("(2) လက်ကိုညိမ်ညိမ်ထားပါ Scannerပေါ်တွင် Pin Numberကို ကြည်လည်ပြတ်သားအောင်ချိန်ထားပေးပါ");
            waythree.setText("(3)Scanဖတ်ပြီးသောအခါMessage Boxလေးပေါ်လာပါလိမ့်မည်");
        }

        dialog.show();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case com.example.mit.easytopupscanner.R.id.flash:

                    if (!flashLightStatus) {
                        item.setTitle("Flash ON");
                        item.setIcon(com.example.mit.easytopupscanner.R.drawable.ic_flash_on_black_24dp);
                        flashLightStatus = true;
                        cameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    } else {
                        item.setTitle("Flash Off");
                        item.setIcon(com.example.mit.easytopupscanner.R.drawable.ic_flash_off_black_24dp);
                        flashLightStatus = false;
                        cameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

                    }
                    return true;
                case com.example.mit.easytopupscanner.R.id.guide:
                    //    item.setTitle("");
                    showDialog();
                    return true;

            }
            return false;
        }
    };

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(graphicOverlay, com.example.mit.easytopupscanner.R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(com.example.mit.easytopupscanner.R.string.ok, listener)
                .show();
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);
        boolean c = gestureDetector.onTouchEvent(e);
        return b || c || super.onTouchEvent(e);
    }


    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        ocrDetectorProcessor = new OcrDetectorProcessor(graphicOverlay, this);
        textRecognizer.setProcessor(ocrDetectorProcessor);

        if (!textRecognizer.isOperational()) {
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, com.example.mit.easytopupscanner.R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(com.example.mit.easytopupscanner.R.string.low_storage_error));
            }
        }

        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(1000.0f);


        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        cameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
      cameraSource.doZoom(1000.0f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (preview != null) {
            preview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preview != null) {
            preview.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != 100) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }}

        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource

            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(com.example.mit.easytopupscanner.R.string.no_camera_permission)
                .setPositiveButton(com.example.mit.easytopupscanner.R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    /**
     * onTap is called to speak the tapped TextBlock, if any, out loud.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the tap was on a TextBlock
     */
    private boolean onTap(float rawX, float rawY) {
        OcrGraphic graphic = graphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock text = null;
        if (graphic != null) {
            text = graphic.getTextBlock();
          //  Toast.makeText(getApplicationContext(),text+"",Toast.LENGTH_LONG).show();
            if (text != null && text.getValue() != null) {
                Log.d(TAG, "text data is being spoken! " + text.getValue());
                // Speak the string.
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    tts.speak(text.getValue(), TextToSpeech.QUEUE_ADD, null, "DEFAULT");
//                }
            }
            else {
                Log.d(TAG, "text data is null");
            }
        }
        else {
            Log.d(TAG,"no text detected");
        }
        return text != null;
    }

    @Override
    public void sendData(final String str) {
        Handler handler=new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                vibrationalert();
              showResultDialogue(str);

            }
        });

    }
    public void initialize() {
        //request permissions

        preferences = getApplicationContext().getSharedPreferences("mypref", 0);
        editor = preferences.edit();
    }

    private void shakeItBaby() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }
    private void vibrationalert() {
        Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    private void showResultDialogue(String str) {
        CustomAlertDialog.getInstance().showConfirmDialog("Refill ", str, "Ok", "Cancel", this, identifier);
    }

    @Override
    public void onClickCallButton(DialogInterface pDialog, int pDialogIntefier) {
        if (pDialogIntefier == 0){}
           }

    @Override
    public void onClickShareButton(DialogInterface pDialog, int pDialogIntefier) {
        if (pDialogIntefier == 0){}
            }

    @Override
    public void onClickCancelButton(DialogInterface pDialog, int pDialogIntefier) {
        if (pDialogIntefier == 0)
         pDialog.dismiss();
        finish();
    }
    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (cameraSource != null) {
                cameraSource.doZoom(detector.getScaleFactor());
            }
        }
    }

}
