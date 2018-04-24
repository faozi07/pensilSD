package id.suksesit.pensil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import id.suksesit.pensil.R;

public class TentangKamiActivity extends AppCompatActivity {

    WebView webView;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang_kami);
        webView = (WebView) findViewById(R.id.ttgkmi);
        String ttg = "Aplikasi Pendeteksi Psikologis Anak ini bertujuan untuk membantu orang tua dan guru untuk senantiasa mengatasi sejak dini kondisi psikologi anak yang biasa berubah-ubah dan mengarah ke hal yang negatif.\n" +
                "<br><br>Dengan aplikasi ini diharapkan mampu mencegah terjadinya psikolgi anak yang salah karena akibat pengaruh lingkungan dan orang-orang terdekatnya sehingga dapat menyelamatkan anak dari perbuatan-perbuatan " +
                "yang dilarang oleh negara, agama, maupun secara adat dan budaya yang baik setempat.<br><br><br>" +
                "Indikator Sikap Sosial : <br>30 ≥ Nilai ≥ 60    =  Rendah <br>61 ≥ Nilai ≥ 90    =  Sedang<br>91 ≥ Nilai ≥ 120    =  Tinggi";
        webView.loadData("<p style=\"text-align:justify; color:#000\">"+ttg+"</p>","text/html","UTF-8");
        getSupportActionBar().setTitle("Tentang Kami");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        startActivity(new Intent(TentangKamiActivity.this, MainActivity.class));
        return;
    }
}
