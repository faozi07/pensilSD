package id.suksesit.pensil;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import id.suksesit.pensil.R;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView loading, logo;
    TextView t1, t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        loading = (ImageView) findViewById(R.id.loading);
        logo = (ImageView) findViewById(R.id.logo);
        t1 = (TextView) findViewById(R.id.pensil2);
        t2 = (TextView) findViewById(R.id.pensil3);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                konek();
                t2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        konek();
                        finish();
                        startActivity(getIntent());
                    }
                });
            }

            private void finish() {
                //TODO Auto-generated method stub
            }
        }, 2000);
    }

    private void konek() {
        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            loading.setVisibility(View.GONE);
            logo.setVisibility(View.VISIBLE);
            startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
            this.finish();
        } else {
            loading.setVisibility(View.VISIBLE);
            logo.setVisibility(View.INVISIBLE);
            t1.setText("Anda Tidak Terhubung Internet");
            t2.setText("Klik Disini untuk Reconnect");
            loading.setBackgroundResource(R.drawable.anim_loading);
            AnimationDrawable frameAnimation = (AnimationDrawable) loading.getBackground();
            frameAnimation.start();
            Toast.makeText(getApplication(), "Kamu tidak terhubung dengan internet", Toast.LENGTH_LONG).show();
        }
    }
}
