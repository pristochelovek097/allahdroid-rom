package com.android.provision;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class DeviceNameActivity extends Activity {

    private EditText deviceNameInput;
    private Button doneButton;
    private Button randomButton;
    private TextView charCountText;
    
    private String[] randomNames = {
        "allah 1.488", "ebaniy device", "petrosyan", "huesos", "bidlofon",
        "kirkorov", "baskov", "zxc", "shluha", "kaban",
        "zhivotnoe", "suka blyat", "nahuy", "pidaras", "rediska"
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.device_name);
        
        deviceNameInput = (EditText) findViewById(R.id.device_name_input);
        doneButton = (Button) findViewById(R.id.done_button);
        randomButton = (Button) findViewById(R.id.random_button);
        charCountText = (TextView) findViewById(R.id.char_count);
        
        charCountText.setText("0 / 30");
        
        String currentName = getDeviceName();
        if (currentName != null && !currentName.isEmpty() && !currentName.equals("Android")) {
            deviceNameInput.setText(currentName);
            deviceNameInput.setSelection(currentName.length());
        }
        
        deviceNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                charCountText.setText(length + " / 30");
                if (length > 30) {
                    charCountText.setTextColor(0xffff4444);
                } else {
                    charCountText.setTextColor(0xff888888);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                String randomName = randomNames[random.nextInt(randomNames.length)];
                deviceNameInput.setText(randomName);
                deviceNameInput.setSelection(randomName.length());
            }
        });
        
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDeviceNameAndContinue();
            }
        });
    }
    
    private String getDeviceName() {
        try {
            String name = Settings.Global.getString(getContentResolver(), "device_name");
            if (name != null && !name.isEmpty()) return name;
        } catch (Exception e) {}
        
        return Build.MODEL;
    }
    
    private void saveDeviceNameAndContinue() {
        String deviceName = deviceNameInput.getText().toString().trim();
        
        if (deviceName.isEmpty()) {
            deviceName = Build.MODEL;
        }
        
        if (deviceName.length() > 30) {
            deviceName = deviceName.substring(0, 30);
        }
        
        try {
            Settings.Global.putString(getContentResolver(), "device_name", deviceName);
        } catch (Exception e) {}
        
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        
        Toast.makeText(this, deviceName + " теперь ты носишь это имя", Toast.LENGTH_SHORT).show();
        
        Intent intent;
        if (isEmulator()) {
            intent = new Intent(DeviceNameActivity.this, DateTimeSetupActivity.class);
        } else {
            intent = new Intent(DeviceNameActivity.this, WifiSetupActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }
    
    private boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || "goldfish".equals(Build.HARDWARE)
                || "ranchu".equals(Build.HARDWARE);
    }
    
    @Override
    public void onBackPressed() {}
}