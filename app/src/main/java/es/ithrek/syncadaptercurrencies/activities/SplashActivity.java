package es.ithrek.syncadaptercurrencies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import es.ithrek.syncadaptercurrencies.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFullscreen();

        setContentView(R.layout.activity_splash);

        title = (TextView) findViewById(R.id.textViewTitle);
        logo = (ImageView) findViewById(R.id.imageViewLogo);

        setAnimation();
    }

    private void setFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // From Nexus 5.x onwards ()
        getSupportActionBar().hide();
    }

    private void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade);

        title.startAnimation(animation);
        logo.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logo.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
