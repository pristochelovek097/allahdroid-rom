package com.android.provision;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

public class DateTimeSetupActivity extends Activity {

    private TextView dateText;
    private TextView timeText;
    private TextView timezoneText;
    private Button doneButton;
    private Animation buttonClickAnimation;
    
    private Calendar calendar;
    private String selectedTimezone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datetime_setup);
        
        calendar = Calendar.getInstance();
        buttonClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click);
        
        dateText = (TextView) findViewById(R.id.date_text);
        timeText = (TextView) findViewById(R.id.time_text);
        timezoneText = (TextView) findViewById(R.id.timezone_text);
        doneButton = (Button) findViewById(R.id.done_button);
        
        dateText.setText("0000-00-00");
        timeText.setText("00:00:00");
        timezoneText.setText("utc");
        
        updateDateDisplay();
        updateTimeDisplay();
        updateTimezoneDisplay();
        
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
        
        timezoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimezonePicker();
            }
        });
        
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClickAnimation);
                saveDateTimeAndFinish();
            }
        });
    }
    
    private void updateDateDisplay() {
        String date = String.format("%d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH));
        dateText.setText(date);
    }
    
    private void updateTimeDisplay() {
        String time = String.format("%02d:%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND));
        timeText.setText(time);
    }
    
    private void updateTimezoneDisplay() {
        selectedTimezone = TimeZone.getDefault().getID();
        String[] parts = selectedTimezone.split("/");
        String display = parts.length > 1 ? parts[1] : selectedTimezone;
        display = display.replace("_", " ");
        timezoneText.setText(display);
    }
    
    private void showDatePicker() {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateDisplay();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    
    private void showTimePicker() {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                updateTimeDisplay();
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }
    
    private void showTimezonePicker() {
        final String[] timezones = TimeZone.getAvailableIDs();
        final String[] displayNames = new String[timezones.length];
        for (int i = 0; i < timezones.length; i++) {
            String[] parts = timezones[i].split("/");
            displayNames[i] = parts.length > 1 ? parts[1].replace("_", " ") : timezones[i];
        }
        
        new android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.timezone_picker_title))
            .setItems(displayNames, new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    selectedTimezone = timezones[which];
                    String[] parts = selectedTimezone.split("/");
                    String display = parts.length > 1 ? parts[1].replace("_", " ") : selectedTimezone;
                    timezoneText.setText(display);
                }
            })
            .show();
    }
    
    private void saveDateTimeAndFinish() {
        long timeInMillis = calendar.getTimeInMillis();
        if (timeInMillis > 100000000000L) {
            Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);
            try {
                Runtime.getRuntime().exec("date -s " + calendar.getTime().toString());
            } catch (Exception e) {}
        }
        
        try {
            Runtime.getRuntime().exec("setprop persist.sys.timezone " + selectedTimezone);
        } catch (Exception e) {}
        TimeZone.setDefault(TimeZone.getTimeZone(selectedTimezone));
        
        Intent intent = new Intent(DateTimeSetupActivity.this, FinalActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }
    
    @Override
    public void onBackPressed() {}
}