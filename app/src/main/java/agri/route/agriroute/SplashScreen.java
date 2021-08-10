package agri.route.agriroute;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    private int SLEEP_TIMER = 2000;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        initializeView();
        animateLogo();
        goToMainActivity();
    }

    private void initializeView()
    {
        imageView = findViewById(R.id.imageView);

    }

    private void animateLogo()
    {
        Animation loadingAnimation = AnimationUtils.loadAnimation(this,R.anim.text_fade_in);

        // loadingAnimation.setDuration(ANIM_TIMER);
        imageView.startAnimation(loadingAnimation);

    }

    private void goToMainActivity()
    {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }, SLEEP_TIMER);

    }
}