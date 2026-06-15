package com.android.provision;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.Locale;

public class LanguageSetupActivity extends Activity {

    private ListView languageList;
    private Button skipButton;
    
    private String[] languageNames;
    private String[] languageCodes;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.language_setup);
        
        languageList = (ListView) findViewById(R.id.language_list);
        skipButton = (Button) findViewById(R.id.skip_button);
        
        languageCodes = new String[]{
            "ru", "en", "uk", "de", "fr", "es", "it", "tr", "zh", "ja", "ko", "ar"
        };
        
        languageNames = new String[]{
            "русский", "english", "українська", "deutsch", "français", "español", 
            "italiano", "türkçe", "中文", "日本語", "한국어", "العربية"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
            android.R.layout.simple_list_item_1, languageNames);
        languageList.setAdapter(adapter);
        
        languageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String langCode = languageCodes[position];
                setLocale(langCode);
                goToNext();
            }
        });
        
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNext();
            }
        });
    }
    
    private void setLocale(String langCode) {
        Locale locale;
        if (langCode.equals("zh")) {
            locale = Locale.CHINESE;
        } else if (langCode.equals("ar")) {
            locale = new Locale("ar");
        } else {
            locale = new Locale(langCode);
        }
        
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, 
            getBaseContext().getResources().getDisplayMetrics());
    }
    
    private void goToNext() {
        Intent intent = new Intent(LanguageSetupActivity.this, DeviceNameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }
    
    @Override
    public void onBackPressed() {}
}