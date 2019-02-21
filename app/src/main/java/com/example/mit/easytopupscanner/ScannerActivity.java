package com.example.mit.easytopupscanner;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

    public class ScannerActivity extends AppCompatActivity implements
        DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private FloatingActionButton switchFlashlightButton;
    private boolean isFlashLightOn = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.mit.easytopupscanner.R.layout.activity_scanner);
        Toolbar toolbar = findViewById(com.example.mit.easytopupscanner.R.id.toolbar);
        toolbar.setNavigationIcon(com.example.mit.easytopupscanner.R.drawable.ic_keyboard_backspace_black_24dp);


        setSupportActionBar(toolbar);

        //Initialize barcode scanner view
        barcodeScannerView = findViewById(com.example.mit.easytopupscanner.R.id.zxing_barcode_scanner);

        //set torch listener
        barcodeScannerView.setTorchListener(this);

        //start capture
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
        }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight() {
        if (isFlashLightOn) {
            barcodeScannerView.setTorchOff();
            isFlashLightOn = false;
        } else {
            barcodeScannerView.setTorchOn();
            isFlashLightOn = true;
        }

    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(com.example.mit.easytopupscanner.R.menu.qr_menu, menu);

            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            switch (id) {
                case com.example.mit.easytopupscanner.R.id.switch_flashlight:
                    switchFlashlight();
                    if(isFlashLightOn){
                        item.setIcon(com.example.mit.easytopupscanner.R.drawable.ic_flash_on_black_24dp);}
                    else{item.setIcon(com.example.mit.easytopupscanner.R.drawable.ic_flash_off_black_24dp);}
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

    @Override
    public void onTorchOn() {
        //switchFlashlightButton.setImageResource(R.drawable.buttonon);
    }

    @Override
    public void onTorchOff() {

        //switchFlashlightButton.setImageResource(R.drawable.buttonoff);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

}