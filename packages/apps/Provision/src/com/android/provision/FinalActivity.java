package com.android.provision;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FinalActivity extends Activity {

    private TextView congratsText;
    private TextView subText;
    private Button finishButton;
    private Animation pulseAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.final_screen);

        congratsText = (TextView) findViewById(R.id.congrats_text);
        subText = (TextView) findViewById(R.id.sub_text);
        finishButton = (Button) findViewById(R.id.finish_button);

        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);

        congratsText.startAnimation(pulseAnimation);
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                subText.setVisibility(View.VISIBLE);
                subText.startAnimation(pulseAnimation);
            }
        }, 1000);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishProvisioning();
            }
        });
    }

    private void finishProvisioning() {
        // ставим флаги что устройство настроено
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
        
        // отключаем все активити
        PackageManager pm = getPackageManager();
        
        ComponentName welcome = new ComponentName(this, WelcomeActivity.class);
        pm.setComponentEnabledSetting(welcome, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        
        ComponentName def = new ComponentName(this, DefaultActivity.class);
        pm.setComponentEnabledSetting(def, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        
        ComponentName lang = new ComponentName(this, LanguageSetupActivity.class);
        pm.setComponentEnabledSetting(lang, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        
        ComponentName deviceName = new ComponentName(this, DeviceNameActivity.class);
        pm.setComponentEnabledSetting(deviceName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        
        ComponentName date = new ComponentName(this, DateTimeSetupActivity.class);
        pm.setComponentEnabledSetting(date, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        
        ComponentName wifi = new ComponentName(this, WifiSetupActivity.class);
        pm.setComponentEnabledSetting(wifi, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        
        ComponentName finalAct = new ComponentName(this, FinalActivity.class);
        pm.setComponentEnabledSetting(finalAct, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        
        Toast.makeText(this, "allah akbar", Toast.LENGTH_SHORT).show();
        
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onBackPressed() {}
}