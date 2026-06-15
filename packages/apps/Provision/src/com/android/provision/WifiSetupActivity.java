package com.android.provision;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class WifiSetupActivity extends Activity {
    
    private WifiManager wifiManager;
    private ListView wifiList;
    private Button skipButton;
    private ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog;
    private Animation buttonClickAnimation;
    
    private String selectedSSID = null;
    private String selectedCapabilities = null;
    
    private Handler scanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            updateWifiList();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_setup);
        
        buttonClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        wifiList = (ListView) findViewById(R.id.wifi_list);
        skipButton = (Button) findViewById(R.id.skip_button);
        
        progressDialog = ProgressDialog.show(this, 
            getString(R.string.scanning_text), 
            getString(R.string.scanning_message), 
            true, false);
        startScan();
        
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter.getItem(position);
                if (item != null && 
                    !item.equals(getString(R.string.scanning_placeholder)) && 
                    !item.equals(getString(R.string.no_wifi_found))) {
                    
                    if (item.contains(" [")) {
                        int bracketStart = item.indexOf(" [");
                        selectedSSID = item.substring(0, bracketStart);
                        selectedCapabilities = item.substring(bracketStart + 2, item.length() - 1);
                    } else {
                        selectedSSID = item;
                        selectedCapabilities = "";
                    }
                    
                    showPasswordDialog();
                }
            }
        });
        
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClickAnimation);
                goToFinal();
            }
        });
    }
    
    private void startScan() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                wifiManager.startScan();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                scanHandler.sendEmptyMessage(0);
            }
        }).start();
    }
    
    private void updateWifiList() {
        List<android.net.wifi.ScanResult> results = wifiManager.getScanResults();
        
        if (results == null || results.size() == 0) {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 
                new String[]{getString(R.string.no_wifi_found)});
            wifiList.setAdapter(adapter);
            return;
        }
        
        String[] networks = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            android.net.wifi.ScanResult result = results.get(i);
            String cap = result.capabilities;
            String security = "";
            if (cap.contains("WPA2") || cap.contains("WPA")) {
                security = getString(R.string.wpa_security);
            } else if (cap.contains("WEP")) {
                security = getString(R.string.wep_security);
            } else {
                security = getString(R.string.open_security);
            }
            networks[i] = result.SSID + security;
        }
        
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, networks);
        wifiList.setAdapter(adapter);
    }
    
    private void showPasswordDialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(String.format(getString(R.string.dialog_title_connect), selectedSSID));
        
        final EditText input = new EditText(this);
        input.setHint(getString(R.string.password_hint));
        
        if (selectedCapabilities != null && selectedCapabilities.contains("OPEN")) {
            connectToWifi(selectedSSID, null);
            return;
        }
        
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.connect_button), 
            new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    String password = input.getText().toString();
                    if (password != null && !password.isEmpty()) {
                        connectToWifi(selectedSSID, password);
                    } else {
                        Toast.makeText(WifiSetupActivity.this, 
                            getString(R.string.password_empty_error), 
                            Toast.LENGTH_SHORT).show();
                    }
                }
            });
        builder.setNegativeButton(getString(R.string.cancel_button), null);
        builder.show();
    }
    
    private void connectToWifi(String ssid, String password) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration config : existingConfigs) {
                if (config.SSID.equals("\"" + ssid + "\"")) {
                    wifiManager.removeNetwork(config.networkId);
                    wifiManager.saveConfiguration();
                }
            }
        }
        
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";
        
        if (password == null || password.isEmpty()) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        }
        
        int netId = wifiManager.addNetwork(config);
        if (netId != -1) {
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
            
            Toast.makeText(this, 
                String.format(getString(R.string.connecting_toast), ssid), 
                Toast.LENGTH_LONG).show();
            
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToFinal();
                }
            }, 5000);
        } else {
            Toast.makeText(this, 
                getString(R.string.add_network_failed), 
                Toast.LENGTH_SHORT).show();
        }
    }
    
    private void goToFinal() {
        Intent intent = new Intent(WifiSetupActivity.this, FinalActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }
    
    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.back_pressed_toast), Toast.LENGTH_SHORT).show();
    }
}