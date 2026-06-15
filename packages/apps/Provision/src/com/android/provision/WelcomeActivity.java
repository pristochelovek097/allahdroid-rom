package com.android.provision;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

    private TextView titleText;
    private Button startButton;
    private Animation fadeInAnimation;
    private Animation buttonClickAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        titleText = (TextView) findViewById(R.id.title_text);
        startButton = (Button) findViewById(R.id.start_button);

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        buttonClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click);

        titleText.startAnimation(fadeInAnimation);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClickAnimation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(WelcomeActivity.this, DefaultActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    }
                }, 200);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // не выйдем
    }
}