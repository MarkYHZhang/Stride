package io.markzhang.stride;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private boolean isFABOpen = false;
    private FloatingActionButton fab;
    private FloatingActionButton scanQR;
    private FloatingActionButton locationToggle;

    final int REQUEST_CODE_PERMISSIONS = 1545;

    private void showFABMenu(){
        isFABOpen=true;
        scanQR.animate().translationY(-getResources().getDimension(R.dimen.scan_qr_translation));
        locationToggle.animate().translationY(-getResources().getDimension(R.dimen.location_toggle_translation));
        fab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
    }

    private void closeFABMenu(){
        isFABOpen = false;
        scanQR.animate().translationY(0);
        locationToggle.animate().translationY(0);
        new Handler(Looper.getMainLooper()).postDelayed(() -> fab.bringToFront(), 100);
        fab.setImageResource(android.R.drawable.ic_menu_add);
    }

    protected void createLocationRequest() {

    }

    private void requestLocationPermission() {
        boolean foreground = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (foreground) {
            boolean background = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

            if (background) {
                //
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_PERMISSIONS);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);

        scanQR = findViewById(R.id.scan_qr);
        locationToggle = findViewById(R.id.location_toggle);
        fab.setOnClickListener(view -> {

            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            if (sharedPref.getBoolean(getString(R.string.location_service_on), false)) {
                locationToggle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.design_default_color_secondary)));
            } else {
                locationToggle.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }

            if(!isFABOpen){
                showFABMenu();
            }else{
                closeFABMenu();
            }
        });
        scanQR.setOnClickListener(view -> {
            closeFABMenu();
            Toast.makeText(this, "Point at QR code", Toast.LENGTH_SHORT).show();
            // TODO: start QR scan activity
        });
        locationToggle.setOnClickListener(view -> {
            closeFABMenu();

            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            boolean prevState = sharedPref.getBoolean(getString(R.string.location_service_on), false);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.location_service_on), !prevState);
            editor.apply();

            if (prevState) {
                locationToggle.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                Toast.makeText(this, "Location Tracking Disabled", Toast.LENGTH_SHORT).show();

                ComponentName receiver = new ComponentName(this, AutoStart.class);
                PackageManager pm = getPackageManager();
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            } else {
                locationToggle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.design_default_color_secondary)));
                Toast.makeText(this, "Location Tracking Enabled", Toast.LENGTH_SHORT).show();

                ComponentName receiver = new ComponentName(this, AutoStart.class);
                PackageManager pm = getPackageManager();
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            }
        });
    }
}