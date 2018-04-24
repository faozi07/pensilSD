package id.suksesit.pensil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import id.suksesit.pensil.helper.Http;

public class HasilActivity extends AppCompatActivity {

    MainActivity H = new MainActivity();

    String URL = Http.url + "penso/profile.php?user_id=" + H.user_id;
    LinearLayout view;
    TextView nama, kesimpulan, hasil,saran;
    CircleImageView fotoSiswa;
    ImageView emot;
    private InterstitialAd mInterstitialAd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil);

        nama = (TextView) findViewById(R.id.tNama);
        hasil = (TextView) findViewById(R.id.hasilPsikologi);
        kesimpulan = (TextView) findViewById(R.id.kesimpulan);
        saran = (TextView) findViewById(R.id.saran);
        fotoSiswa = (CircleImageView) findViewById(R.id.foto_siswa);
        emot = (ImageView) findViewById(R.id.emot);
        nama.setText(H.nama);
        hasil.setText(H.nilai_ss);
        kesimpulan.setText(H.predikat_ss);
        saran.setText(H.saran);
        if (Integer.parseInt(hasil.getText().toString()) < 30) {
            Picasso.with(this)
                    .load(R.drawable.ic_shock)
                    .placeholder(R.drawable.ic_shock)
                    .error(R.drawable.ic_shock)
                    .into(emot);
            kesimpulan.setText(" ----- ");
            hasil.setText("-");
            saran.setText("Kamu belum jawab pertanyaan psikologi");
        }else if (Integer.parseInt(hasil.getText().toString()) >= 30 && Integer.parseInt(hasil.getText().toString()) <= 60) {
            Picasso.with(this)
                    .load(R.drawable.ic_sedih)
                    .placeholder(R.drawable.ic_sedih)
                    .error(R.drawable.ic_sedih)
                    .into(emot);
            kesimpulan.setTextColor(Color.rgb(207, 0, 15));
            hasil.setTextColor(Color.rgb(207, 0, 15));
            saran.setTextColor(Color.rgb(207, 0, 15));
        } else if (Integer.parseInt(hasil.getText().toString()) >= 61 && Integer.parseInt(hasil.getText().toString()) <= 90) {
            Picasso.with(this)
                    .load(R.drawable.ic_sedang)
                    .placeholder(R.drawable.ic_sedang)
                    .error(R.drawable.ic_sedang)
                    .into(emot);
            kesimpulan.setTextColor(Color.rgb(248, 148, 6));
            hasil.setTextColor(Color.rgb(248, 148, 6));
            saran.setTextColor(Color.rgb(248, 148, 6));
        } else if (Integer.parseInt(hasil.getText().toString()) >= 91) {
            Picasso.with(this)
                    .load(R.drawable.ic_tinggi)
                    .placeholder(R.drawable.ic_tinggi)
                    .error(R.drawable.ic_tinggi)
                    .into(emot);
            kesimpulan.setTextColor(Color.rgb(68,108,179));
            hasil.setTextColor(Color.rgb(68,108,179));
            saran.setTextColor(Color.rgb(68,108,179));
        }
        Log.d("log url", URL);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Hasil Psikologi");
        Picasso.with(this)
                .load(Http.picture + H.picture)
                .placeholder(R.drawable.ic_photoprof)
                .error(R.drawable.ic_photoprof)
                .into(fotoSiswa);
        Log.d("Log picture", Http.picture + H.picture);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5730449577374867/5331596856");
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        finish();
        MainActivity.namaBoom = "Beranda";
        startActivity(new Intent(HasilActivity.this,MainActivity.class));
        return;
    }
}
